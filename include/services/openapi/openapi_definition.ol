/*
 *   Copyright (C) 2016 by Claudio Guidi <guidiclaudio@gmail.com>         
 *                                                                        
 *   This program is free software; you can redistribute it and/or modify 
 *   it under the terms of the GNU Library General Public License as      
 *   published by the Free Software Foundation; either version 2 of the   
 *   License, or (at your option) any later version.                      
 *                                                                        
 *   This program is distributed in the hope that it will be useful,      
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of       
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the        
 *   GNU General Public License for more details.                         
 *                                                                        
 *   You should have received a copy of the GNU Library General Public    
 *   License along with this program; if not, write to the                
 *   Free Software Foundation, Inc.,                                      
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.            
 *                                                                        
 *   For details about the authors of this software, see the AUTHORS file.
 */

include "json_utils.iol"
include "string_utils.iol"
include "console.iol"
include "runtime.iol"
include "./public/interfaces/OpenApiDefinitionInterface.iol"
include "services/json/jsonschema/JSONSchemaGeneratorInterface.iol"

execution{ concurrent }

constants {
  LOG = false
}

outputPort JSONSchemaGenerator {
  Interfaces: JSONSchemaGeneratorInterface
}

embedded {
  Jolie:
    "services/json/jsonschema/json_schema_generator.ol" in JSONSchemaGenerator
}

outputPort MySelf {
  Interfaces: OpenApiDefinitionInterface
}

inputPort OpenApiDefinition {
  Location: "local"
  Interfaces: OpenApiDefinitionInterface
}

define __clean_name {
    replaceAll@StringUtils( __name_to_clean { .regex = "«|»|-" .replacement = "_" } )( __cleaned_name )
    // put field names starting with digits or containing special chars under "" quotes
    match@StringUtils( __cleaned_name { regex = "(^[0-9](.*)+)|((.*)[!@#$%&*()+=|<>?{}\\[\\]~-](.*))" } )( contains_special_chars )
    if ( contains_special_chars ) {
        __cleaned_name = "\"" + __cleaned_name + "\""
    }
}

define __clean_type_name {
    replaceAll@StringUtils( __name_to_clean { .regex = "«|»|-" .replacement = "_" } )( __cleaned_name )
    // prefix type names starting with digits with an underscore "_"
    match@StringUtils( __cleaned_name { regex = "(^[0-9](.*)+)" } )( contains_special_chars )
    if ( contains_special_chars ) {
        __cleaned_name = "_" + __cleaned_name
    }
}

define __indentation {
    for( i = 0, i < request.indentation, i++ ) {
        indentation = indentation + "\t"
    }
}

init {
  getLocalLocation@Runtime( )( MySelf.location )
  global.DEFINITIONS_PATH = "#/definitions/"
}

main {
  [ setDefinitionsPath( path )( ) {
    global.DEFINITIONS_PATH = path
  } ]

  [ definitionIsArray( request )( response ) {
      response = false
      if ( request.definition.type == "array" ) {
            response = true
      }
  }]

  [  getOpenApiDefinition( request )( response ) {
          with( json ) {
            .swagger = "2.0";
            .info -> request.info;
            .host = request.host;
            .basePath = request.basePath;
            .paths = undefined
            if ( is_defined( request.schemes ) ) {
                .schemes -> request.schemes
            };
            if ( is_defined( request.consumes ) ) {
                .consumes -> request.consumes
            };
            if ( is_defined( request.produces ) ) {
                .produces -> request.produces
            };
            if ( is_defined( request.tags ) ) {
                  if ( #request.tags == 1 ) {
                     __tags._ << request.tags
                  } else {
                     __tags << request.tags
                  };
                  .tags << __tags
            };
            if ( is_defined( request.externalDocs ) ) {
                  .externalDocs -> request.externalDocs
            };
            for( d = 0, d < #request.definitions, d++ ) {
                if ( request.definitions[ d ] instanceof TypeDefinition ) {
                    getTypeDefinition@JSONSchemaGenerator( request.definitions[ d ] )( def )
                } else if ( request.definitions[ d ] instanceof FaultDefinitionForOpenAPI ) {
                    getTypeDefinition@JSONSchemaGenerator( request.definitions[ d ].fault )( def )
                    def.( request.definitions[ d ].fault.name ).properties.fault.pattern = request.definitions[ d ].name 
                }
                .definitions << def
            };
            if ( LOG ) { println@Console("Checking paths...")() };
            for( p = 0, p <#request.paths, p++ ) {

                  __template = request.paths[ p ];
                  if ( LOG ) { println@Console("path:" + __template )() };

                  // paths
                  foreach ( op : request.paths[ p ] ) {
                       undef( __tags );
                       if ( LOG ) { println@Console("method:" + op )() };
                       if ( #request.paths[ p ].( op ).tags == 1 ) {
                          __tags._ -> request.paths[ p ].( op ).tags
                       } else {
                          __tags -> request.paths[ p ].( op ).tags
                       };
                       .paths.(__template ).( op ).tags << __tags;
                       if ( is_defined( request.paths[ p ].( op ).summary ) ) {
                          .paths.(__template ).( op ).summary = request.paths[ p ].( op ).summary
                       };
                       if ( is_defined( request.paths[ p ].( op ).description ) ) {
                          .paths.(__template ).( op ).description = request.paths[ p ].( op ).description
                       };
                       if ( is_defined( request.paths[ p ].( op ).externalDocs ) ) {
                          .paths.(__template ).( op ).externalDocs << request.paths[ p ].( op ).externalDocs
                       };
                       if ( is_defined( request.paths[ p ].( op ).operationId ) ) {
                          .paths.(__template ).( op ).operationId = request.paths[ p ].( op ).operationId
                       };

                       // consumes
                       if ( is_defined( request.paths[ p ].( op ).consumes ) ) {
                          if ( #request.paths[ p ].( op ).consumes == 1 ) {
                            __consumes._ = request.paths[ p ].( op ).consumes
                          } else {
                            __consumes -> request.paths[ p ].( op ).consumes
                          };
                          .paths.(__template ).( op ).consumes << __consumes
                       };

                       // produces
                       if ( is_defined( request.paths[ p ].( op ).produces ) ) {
                         if ( #request.paths[ p ].( op ).produces == 1 ) {
                           __produces._ = request.paths[ p ].( op ).produces
                         } else {
                           __produces -> request.paths[ p ].( op ).produces
                         };
                          .paths.(__template ).( op ).produces << __produces
                       };

                       // responses
                       if ( is_defined( request.paths[ p ].( op ).responses ) ) {
                         for( res = 0, res < #request.paths[ p ].( op ).responses, res++ ) {
                              current_response -> request.paths[ p ].( op ).responses[ res ];
                              status = string( current_response.status )
                              .paths.(__template ).( op ).responses.( status ).description = current_response.description;
                              if ( is_defined( current_response.schema ) ) {
                                   getType@JSONSchemaGenerator( current_response.schema )( .paths.(__template ).( op ).responses.( status ).schema ) 
                              }
                         }
                       };

                       // parameters
                       for( par = 0, par < #request.paths[ p ].( op ).parameters, par++ ) {
                          undef( cur_par );
                          cur_par.ln -> json.paths.(__template ).( op ).parameters[ 0 ]._[ par ]
                          cur_par.ln.name = request.paths[ p ].( op ).parameters[ par ].name;
                          if ( LOG ) { println@Console("parameter:" + cur_par.ln.name )() };
                          if ( is_defined( request.paths[ p ].( op ).parameters[ par ].in.in_body ) ) {
                                cur_par.ln.name = "body"
                                in_body -> request.paths[ p ].( op ).parameters[ par ].in.in_body;
                                cur_par.ln.in = "body";
                                if ( is_defined( in_body.schema_type ) ) {
                                    type -> request.paths[ p ].( op ).parameters[ par ].in.in_body.schema_type;
                                    cur_par.ln.required = true;
                                    getType@JSONSchemaGenerator( type )( generated_type );
                                    cur_par.ln.schema << generated_type
                                } else if ( is_defined( in_body.schema_ref ) ) {
                                    cur_par.ln.schema.("$ref") = "#/definitions/" + in_body.schema_ref
                                    cur_par.ln.required = true
                                }
                          } else if ( is_defined( request.paths[ p ].( op ).parameters[ par ].in.other ) ) {
                                getNativeType@JSONSchemaGenerator( request.paths[ p ].( op ).parameters[ par ].in.other.type.root_type )( resp_root_type );
                                cur_par.ln << resp_root_type
                                if ( request.paths[ p ].( op ).parameters[ par ].required ) {
                                    cur_par.ln.required = true
                                }
                                cur_par.ln.in = request.paths[ p ].( op ).parameters[ par ].in.other;
                                if ( is_defined( request.paths[ p ].( op ).parameters[ par ].in.other.allowEmptyValue ) ) {
                                    cur_par.ln.allowEmptyValue << request.paths[ p ].( op ).parameters[ par ].in.other.allowEmptyValue
                                }
                          }
                     }
                  }
            }

         }
         ;
         if ( LOG ) { println@Console("converting value to JSON string..." )() };
         getJsonString@JsonUtils( json )( response );
         // necessary for converting null into {}
         replaceAll@StringUtils( response { regex = ":null", replacement = ":{}"})( response )
         if ( LOG ) { println@Console("converted!" )() }
    } ]

    [ getJolieTypeFromOpenApiParameters( request )( response ) {
          __name_to_clean = request.name;
          __clean_type_name
          if ( #request.definition.parameters == 1 && is_defined( request.definition.parameters.schema.("$ref") ) ) {
                splreq = request.definition.parameters.schema.("$ref")
                split@StringUtils( splreq { regex = global.DEFINITIONS_PATH } )( splres )
                ref_name = splres.result[ 1 ]
                if ( !(request.array_def_list.( ref_name ) instanceof void ) ) {
                    println@Console("Warning! definition " + ref_name + " directly points to an array that is not portable into a jolie type. Converted as a simple type")()
                } 
                // parameter should be always in the body
                response = "type " + __cleaned_name + ": void {\n"
                response = response + "\t." + request.definition.parameters[ 0 ].name + ": " + ref_name + "\n"
                response = response + "}\n"
          } else {
                response = "type " + __cleaned_name + ": void {\n";
                for( p = 0, p < #request.definition.parameters, p++ ) {
                    cur_par -> request.definition.parameters[ p ];
                    __name_to_clean = cur_par.name 

                    /* path and query parameters must be differentiated from parameters with same name in schemas 
                    Ex: in a post path like /a/b/{userId}
                    with a body schema that contains parameters: userId, name and surname
                    the final jolie types would be
                    
                    type ... {
                        userId
                        userId
                        name
                        surname
                    }

                    userId will be reported twice because it appears both in the body schema and in the path.

                    In order to avoid these cases we use _p or _q prefixes for path and query parameters. The example above will become:

                    type {
                        _puserId
                        userId
                        name
                        surname
                    }
                    */
                    if ( cur_par.in == "path" ) { __name_to_clean = "_p" + __name_to_clean }
                    if ( cur_par.in == "query" ) { __name_to_clean = "_q" + __name_to_clean }
                    __clean_name

                    response = response + "\t." + __cleaned_name
                    if ( is_defined( cur_par.type ) ) {
                        if ( cur_par.type == "array" ) {
                                if (  cur_par.required == "false" ) {
                                    cur_par.schema.items.minItems = 0
                                }

                                undef( rq_arr )
                                rq_arr << {
                                    definition << cur_par,
                                    indentation = 1,
                                    array_def_list -> request.array_def_list
                                }
                                getJolieDefinitionFromOpenApiArray@MySelf( rq_arr )( array );
                                response = response + array.cardinality + ": " + array

                        } else if ( cur_par.type == "file" ) {
                                if (  cur_par.required == "false" ) {
                                    response = response + "?"
                                };
                                response = response + ":raw\n"

                        } else {

                                undef( rq_n )
                                rq_n -> cur_par
                                getJolieNativeTypeFromOpenApiNativeType@MySelf( rq_n )( native );
                                if (  cur_par.required == "false" ) {
                                    response = response + "?"
                                };
                                response = response + ":" + native + "\n"

                        }

                    } else if ( is_defined( cur_par.schema ) ) {

                        undef( rq_arr )
                        rq_arr.schema << cur_par.schema
                        rq_arr.indentation = 1
                        rq_arr.array_def_list -> request.array_def_list
                        getJolieTransformationFromSchema@MySelf( rq_arr )( schema )
                        response = response + schema.cardinality + ": " + schema

                    }
                }
                response = response + "}\n"
          }
         
    }]

    [ getJolieTransformationFromSchema( request )( response ) {
            if ( request.schema instanceof void ) {

                        response = "void\n"

            } else if ( is_defined( request.schema.("$ref") ) ) {

                        split@StringUtils( request.schema.("$ref") { regex = global.DEFINITIONS_PATH } )( splitted_ref )
                        ref_name = splitted_ref.result[ 1 ]
                        if ( request.array_def_list.( ref_name ) instanceof void ) {
                            response = ref_name + "\n"
                        } else {

                            undef( rq_arr )
                            rq_arr << {
                                definition << request.array_def_list.( ref_name ),
                                indentation = request.indentation,
                                array_def_list << request.array_def_list
                            }
                            getJolieDefinitionFromOpenApiArray@MySelf( rq_arr )( array )
                            response.cardinality = array.cardinality
                            response = array

                        }
                        
            } else if ( request.schema.type == "array" ) {

                        undef( rq_arr )
                        rq_arr << {
                            definition << request.schema,
                            indentation = request.indentation,
                            array_def_list << request.array_def_list
                        }
                        getJolieDefinitionFromOpenApiArray@MySelf( rq_arr )( array )
                        response.cardinality = array.cardinality
                        response = array

            } else if ( request.schema.type == "object" ) {

                        if ( #request.schema.properties == 0 ) {
                            response = "undefined \n"
                        } else {

                            undef( rq_obj )
                            rq_obj << {
                                definition << request.schema,
                                indentation = request.indentation,
                                array_def_list << request.array_def_list
                            }
                            getJolieDefinitionFromOpenApiObject@MySelf( rq_obj )( object );
                            response = "void {\n" + object + "}\n"

                        }

            } else if ( is_defined( request.schema.oneOf ) ) {

                    for( _of = 0, _of < #request.schema.oneOf, _of++ ) {
                        branch -> request.schema.oneOf[ _of ]
                        if ( _of == 0 ) {
                            branch_st = ""
                        } else {
                            branch_st = "\n| "
                        }
                        if ( is_defined( branch.type ) ) {

                            undef( rq_obj )
                            rq_obj.definition -> branch
                            rq_obj.indentation = request.indentation
                            rq_obj.array_def_list -> request.array_def_list
                            getJolieDefinitionFromOpenApiObject@MySelf( rq_obj )( object )
                            response = response + branch_st + "void {\n"  + object + "}"

                        } else if ( is_defined( branch.("$ref") ) ) {
                            split@StringUtils( branch.("$ref") { regex = global.DEFINITIONS_PATH } )( splitted_ref )
                            ref_name = splitted_ref.result[ 1 ]
                            if ( !( request.array_def_list.( ref_name ) instanceof void ) ) {
                                println@Console("Warning! definition array " + ref_name + " cannot be converted from a oneOf branch. Converted as a simple type")()
                            } 
                            response = response + branch_st + ref_name                               
                        }
                    }
                    response = response + "\n"

            } else {
                        if ( #request.schema.properties == 0 ) {

                            if ( is_defined( request.schema.anyOf ) ) {

                                response = ""
                                for ( i = 0, i < #request.schema.anyOf, i++ ) {
                                    if ( is_defined( request.schema.anyOf[i].type ) ) {
                                        undef( nt )
                                        nt -> request.schema.anyOf[i]
                                        getJolieNativeTypeFromOpenApiNativeType@MySelf( nt )( native_type )
                                    } else if ( is_defined( request.schema.anyOf[i].("$ref") ) ) {
                                        split@StringUtils( request.schema.anyOf[i].("$ref") { regex = "/" } )( reference )
                                        ref_name = reference.result[ #reference.result - 1 ]

                                        __name_to_clean = ref_name
                                        __clean_type_name
                                        native_type = __cleaned_name
                                    }
                                    if ( i > 0 ) {
                                        response = response + " | "
                                    }
                                    response = response + native_type
                                }
                                response = response + "\n"

                            } else {

                                undef( nt )
                                nt -> request.schema
                                getJolieNativeTypeFromOpenApiNativeType@MySelf( nt )( native_type )
                                response = native_type + "\n"

                            }

                        } else {

                            undef( rq_obj )
                            rq_obj << {
                                definition << request.schema,
                                indentation = request.indentation,
                                array_def_list << request.array_def_list
                            }
                            getJolieDefinitionFromOpenApiObject@MySelf( rq_obj )( object )
                            response = "void {\n" + object + "}\n"

                        }
            }
          
    }] 

    [ getJolieTypeFromOpenApiDefinition( request )( response ) {
        scope( get_definition ) {
            install( DefinitionError => println@Console("Error for type " + __name_to_clean )() );
            __name_to_clean = request.name
            __clean_type_name

            undef( rq_schema )
            rq_schema << {
                schema << request.definition,
                indentation = 1,
                array_def_list << request.array_def_list
            }
            getJolieTransformationFromSchema@MySelf( rq_schema )( schema_rs )

            response = "type " + __cleaned_name + ": " + schema_rs
            if ( !(schema_rs.cardinality instanceof void) ) {
                println@Console("WARNING: definition " + __name_to_clean + " is an array and cannot converted as a jolie type. Converted as a simple type")()
            } 
        }
    }]

    [ getJolieDefinitionFromOpenApiObject( request )( response ) {
        __indentation
        // create required hashmap
        for( isreq in request.definition.required ) {
            is_required.( isreq ) = true
        }

        if ( is_defined( request.definition.additionalProperties )
          && is_defined( request.definition.additionalProperties.("$ref") ) ) {

            split@StringUtils( request.definition.additionalProperties.("$ref") { regex = "/" } )( reference )
            ref_name = reference.result[ #reference.result - 1 ]

            __name_to_clean = ref_name
            __clean_type_name

            println@Console("WARNING " + ref_name + ": currently we are unable to express the map syntax ('additionalProperties') in Jolie. For now generate 'undefined'")()

            response = response + "undefined\n"

        } else {
            foreach( property : request.definition.properties ) {
                __name_to_clean = property
                __clean_name

                response = response + indentation
                response = response + "." + __cleaned_name

                isreq_token = ""
                if ( !is_required.( property ) ) { isreq_token = "?" }
                if ( is_defined( request.definition.properties.( property ).("$ref") ) ) {
                    spl = request.definition.properties.( property ).("$ref");
                    spl.regex = "/";
                    split@StringUtils( spl )( reference );
                    ref_name = reference.result[ #reference.result - 1 ]
                    if ( request.array_def_list.( ref_name ) instanceof void ) {

                            __name_to_clean = ref_name
                            __clean_type_name
                            response = response + isreq_token + ":" + __cleaned_name + "\n"

                    } else {

                            undef( rq_arr )
                            rq_arr << {
                                definition << request.array_def_list.( ref_name ),
                                indentation = 1,
                                array_def_list -> request.array_def_list
                            }
                            getJolieDefinitionFromOpenApiArray@MySelf( rq_arr )( array )
                            response = response + array.cardinality + ": " + array
                    }
                } else {
                    if ( request.definition.properties.( property ).type == "array" ) {

                        undef( rq_arr )
                        rq_arr << {
                            definition << request.definition.properties.( property )
                            indentation = request.indentation + 1
                            array_def_list -> request.array_def_list
                        }
                        getJolieDefinitionFromOpenApiArray@MySelf( rq_arr )( array )
                        response = response + array.cardinality + ": " + array

                    } else if ( request.definition.properties.( property ).type == "object" ) {

                        undef( rq_obj )
                        rq_obj << {
                            definition << request.definition.properties.( property ),
                            indentation = request.indentation + 1,
                            array_def_list << request.array_def_list
                        }
                        getJolieDefinitionFromOpenApiObject@MySelf( rq_obj )( object )
                        response = response + isreq_token +  ": " + object

                    } else {

                        undef( rq_n )
                        rq_n -> request.definition.properties.( property )
                        getJolieNativeTypeFromOpenApiNativeType@MySelf( rq_n )( native );
                        response = response + isreq_token + ":" + native + "\n"

                    }
                }
            }
        }
    }]

    [ getJolieDefinitionFromOpenApiArray( request )( response ) {
        if ( is_defined( request.definition.minItems ) ) { min_cardinality = string( request.definition.minItems ) }
        else { min_cardinality = "0" }
          
        if ( is_defined( request.definition.maxItems ) ) { max_cardinality = string( request.definition.maxItems ) }
        else { max_cardinality = "*" }
          
        response.cardinality = "[" + min_cardinality + "," + max_cardinality + "]"

        if ( is_defined( request.definition.items.("$ref") ) ) {
            spl << request.definition.items.("$ref") { regex = "/" }
            split@StringUtils( spl )( reference );
            ref_name = reference.result[ #reference.result - 1 ]
            if ( request. array_def_list.( ref_name ) instanceof void ) {
                __name_to_clean = ref_name
                __clean_type_name
                response = response + __cleaned_name + "\n"
            } else {
                undef( rq_arr )
                rq_arr << {
                    definition << request.array_def_list.( ref_name ),
                    indentation = 1,
                    array_def_list -> request.array_def_list
                }
                getJolieDefinitionFromOpenApiArray@MySelf( rq_arr )( response )
            }
        } else if ( request.definition.items.type != "object" ) {
              undef( rq_n )
              rq_n -> request.definition.items
              getJolieNativeTypeFromOpenApiNativeType@MySelf( rq_n )( native )
              response = response + native + "\n"
        } else {
              response = response + "undefined\n"
        }
    } ]

    [ getJolieNativeTypeFromOpenApiNativeType( request )( response ) {
        if ( request.type instanceof void ) {
            response = "void"
        } else if ( request.type == "string" ) {
            if ( request.format == "binary" ) {
                response = "raw"
            } else {
                response = "string"

                if ( is_defined( request.pattern ) ) { // regex double-escape
                    response = response + "( regex( \"" + replaceAll@StringUtils( request.pattern { regex = "\\\\" replacement = "\\\\\\\\" } ) + "\" ) )"
                } else if ( is_defined( request.minLength ) && is_defined( request.maxLength ) ) {
                    response = response + "( length( [" + request.minLength + ", " + request.maxLength + "] ) )"
                } else if ( is_defined( request.enum ) ) {
                    if ( request.enum[0] instanceof string ) {
                        response = response + "( enum( ["
                        for ( i = 0, i < #request.enum, i++ ) {
                            response = response + "\"" + request.enum[i] + "\""
                            if ( i != #request.enum - 1 ) {
                                response = response + ", "
                            }
                        }
                        response = response + "] ) )"
                    } else { // null
                        response = response + " | void"
                    }
                }
            }
        } else if ( request.type == "file" ) {
            response = "raw"
        } else if ( request.type == "boolean" ) {
            response = "bool"
        } else if ( request.type == "number" ) {
            response = "double"

            if ( is_defined( request.minimum ) ) {
                if ( is_defined( request.maximum ) ) {
                    response = response + "( ranges( [" + request.minimum + ", " + request.maximum + "] ) )"
                } else {
                    response = response + "( ranges( [" + request.minimum + ", *] ) )"
                }
            }
        } else if ( request.type == "integer" ) {
            if ( is_defined( request.format ) ) {
                if ( request.format == "int32" ) {
                    response = "int"
                } else if ( request.format == "int64" ) {
                    response = "long"
                }
            } else {
                response = "int"
            }
            if ( is_defined( request.minimum ) ) {
                min = long( request.minimum )
                if ( request.format == "int64" ) {
                    min = min + "L"
                }
                if ( is_defined( request.maximum ) ) {
                    max = long( request.format )
                    if ( request.format == "int64" ) {
                        max = max + "L"
                    }
                    response = response + "( ranges( [" + min + ", " + max + "] ) )"
                } else {
                    response = response + "( ranges( [" + min + ", *] ) )"
                }
            }
        }
        if ( is_defined( request.example ) ) {
            response = response + " // Example: " + request.example
        }
    }]

    [ getReferenceName( request )( response ) {
          spl = request;
          spl.regex = "/";
          split@StringUtils( spl )( reference );
          __name_to_clean = reference.result[ #reference.result - 1 ];
          __clean_type_name
          response = __cleaned_name
    }]
}
