package jolie.lang.parse;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import jolie.lang.Constants;
import jolie.lang.Constants.EmbeddedServiceType;
import jolie.lang.parse.ast.DefinitionNode;
import jolie.lang.parse.ast.InputPortInfo;
import jolie.lang.parse.ast.InterfaceDefinition;
import jolie.lang.parse.ast.OLSyntaxNode;
import jolie.lang.parse.ast.OutputPortInfo;
import jolie.lang.parse.ast.ServiceNode;
import jolie.lang.parse.ast.types.TypeChoiceDefinition;
import jolie.lang.parse.ast.types.TypeDefinition;
import jolie.lang.parse.ast.types.TypeDefinitionLink;
import jolie.lang.parse.ast.types.TypeInlineDefinition;
import jolie.lang.parse.context.ParsingContext;
import jolie.lang.parse.util.ProgramInspector;
import jolie.util.Pair;

public class ModuleRecord
{

    private URI source;
    private ProgramInspector programInspector;

    public ModuleRecord( URI source, ProgramInspector programInspector )
    {
        this.source = source;
        this.programInspector = programInspector;
    }


    private TypeDefinition findType( String id )
    {
        for (TypeDefinition td : programInspector.getTypes()) {
            if ( td.id().equals( id ) ) {
                return td;
            }
        }
        return null;
    }

    private InterfaceDefinition findInterface( String id )
    {
        for (InterfaceDefinition ifaceDef : programInspector.getInterfaces()) {
            if ( ifaceDef.name().equals( id ) ) {
                return ifaceDef;
            }
        }
        return null;
    }

    private DefinitionNode findProcedureDefinition( String id )
    {
        for (DefinitionNode procedureDef : programInspector.getProcedureDefinitions()) {
            if ( procedureDef.id().equals( id ) ) {
                return procedureDef;
            }
        }
        return null;
    }

    private ServiceNode findService( String id )
    {
        for (ServiceNode service : programInspector.getServices()) {
            if ( service.name().equals( id ) ) {
                return service;
            }
        }
        return null;
    }

    public OLSyntaxNode find( String id )
    {
        TypeDefinition moduleTypeDef = this.findType( id );
        if ( moduleTypeDef != null ) {
            return moduleTypeDef;
        }
        InterfaceDefinition moduleInterfaceDef = this.findInterface( id );
        if ( moduleInterfaceDef != null ) {
            return moduleInterfaceDef;
        }
        DefinitionNode moduleProcedureDef = this.findProcedureDefinition( id );
        if ( moduleProcedureDef != null ) {
            return moduleProcedureDef;
        }
        ServiceNode moduleService = this.findService( id );
        if ( moduleService != null ) {
            return moduleService;
        }
        return null;
    }



    public ImportResult resolveNameSpace( ParsingContext ctx, boolean importUsingLink )
            throws ModuleParsingException
    {
        List< Pair< String, String > > importPaths = new ArrayList< Pair< String, String > >();
        for (TypeDefinition td : this.programInspector.getTypes()) {
            importPaths.add( new Pair< String, String >( td.id(), td.id() ) );
        }

        for (InterfaceDefinition interfaceDef : this.programInspector.getInterfaces()) {
            importPaths
                    .add( new Pair< String, String >( interfaceDef.name(), interfaceDef.name() ) );
        }

        for (DefinitionNode definitionNode : this.programInspector.getProcedureDefinitions()) {
            importPaths
                    .add( new Pair< String, String >( definitionNode.id(), definitionNode.id() ) );
        }

        for (ServiceNode service : this.programInspector.getServices()) {
            importPaths.add( new Pair< String, String >( service.name(), service.name() ) );
        }
        return resolve( ctx, importPaths, importUsingLink );
    }

    public ImportResult resolve( ParsingContext ctx, List< Pair< String, String > > pathNodes,
            boolean importUsingLink ) throws ModuleParsingException
    {
        ImportResult res = new ImportResult();
        for (Pair< String, String > pathNode : pathNodes) {
            OLSyntaxNode moduleNode = this.find( pathNode.key() );
            if ( moduleNode == null ) {
                throw new ModuleParsingException( "unable to find " + pathNode.key() + " in "
                        + Arrays.toString( this.programInspector.getSources() ) );
            }
            ImportResult importResult = null;
            if ( moduleNode instanceof TypeDefinition ) {
                if ( importUsingLink ) {
                    importResult = resolveTypeUsingLink( ctx, (TypeDefinition) moduleNode,
                            pathNode.value(), false );
                } else {
                    importResult = resolveType( ctx, (TypeDefinition) moduleNode, pathNode.value(),
                            false );
                }
                res.addResult( importResult );
            } else if ( moduleNode instanceof InterfaceDefinition ) {
                importResult =
                        resolveInterface( ctx, (InterfaceDefinition) moduleNode, pathNode.value() );
                res.addResult( importResult );
            } else if ( moduleNode instanceof DefinitionNode ) {
                importResult = resolveProcedureDefinition( ctx, (DefinitionNode) moduleNode,
                        pathNode.value() );
                res.addResult( importResult );
            } else if ( moduleNode instanceof ServiceNode ) {
                importResult = resolveService( ctx, (ServiceNode) moduleNode, pathNode.value() );
                res.addResult( importResult );
            }
        }
        return res;
    }


    private ImportResult resolveType( ParsingContext ctx, TypeDefinition td, String localName,
            boolean isSubType ) throws ModuleParsingException
    {
        ImportResult typeResult = new ImportResult();
        if ( td instanceof TypeChoiceDefinition ) {
            TypeChoiceDefinition moduleType = (TypeChoiceDefinition) td;
            TypeChoiceDefinition localType = new TypeChoiceDefinition( ctx, localName,
                    moduleType.cardinality(), moduleType.left(), moduleType.right() );
            localType.setDocumentation( moduleType.getDocumentation() );
            if ( !moduleType.left().id().equals( moduleType.id() ) ) {
                ImportResult choiceResult =
                        resolveType( ctx, moduleType.left(), moduleType.left().id(), true );
                typeResult.prependResult( choiceResult );
            }
            if ( !moduleType.right().id().equals( moduleType.id() ) ) {
                ImportResult choiceResult =
                        resolveType( ctx, moduleType.left(), moduleType.left().id(), true );
                typeResult.prependResult( choiceResult );
            }
            typeResult.addNode( localType );
            typeResult.addType( localType );
            return typeResult;
        } else if ( td instanceof TypeDefinitionLink ) {
            TypeDefinitionLink moduleType = (TypeDefinitionLink) td;
            if ( !isSubType ) {
                TypeDefinitionLink localType = new TypeDefinitionLink( ctx, localName,
                        moduleType.cardinality(), moduleType.linkedTypeName() );
                localType.setDocumentation( moduleType.getDocumentation() );
                typeResult.addNode( localType );
                typeResult.addType( localType );
            }
            TypeDefinition linkedType = this.findType( moduleType.linkedTypeName() );
            typeResult.addNode( linkedType );
            return typeResult;
        } else if ( td instanceof TypeInlineDefinition ) {
            TypeInlineDefinition moduleType = (TypeInlineDefinition) td;
            if ( isSubType && moduleType.nativeType() != null ) {
                return typeResult;
            }
            TypeInlineDefinition localType = new TypeInlineDefinition( ctx, localName,
                    moduleType.nativeType(), moduleType.cardinality() );
            if ( moduleType.subTypes() != null ) {
                for (Map.Entry< String, TypeDefinition > entry : moduleType.subTypes()) {
                    ImportResult subTypeResult =
                            resolveType( ctx, entry.getValue(), entry.getKey(), true );
                    for (OLSyntaxNode n : subTypeResult.nodes()) {
                        typeResult.addNode( n );
                        TypeDefinition subTD = (TypeDefinition) n;
                        typeResult.addType( subTD );
                    }
                    localType.putSubType( entry.getValue() );
                }
            }
            localType.setDocumentation( moduleType.getDocumentation() );
            typeResult.addNode( localType );
            typeResult.addType( localType );
        } else {
            throw new ModuleParsingException( td.id() + " of type " + td.getClass().getSimpleName()
                    + " is not support for module system" );
        }
        return typeResult;
    }

    private ImportResult resolveTypeUsingLink( ParsingContext ctx, TypeDefinition td,
            String localName, boolean isADependencyType ) throws ModuleParsingException
    {
        ImportResult typeResult = new ImportResult();
        String localTypeNamePrefix = ctx.sourceName() + "#";
        if ( td instanceof TypeChoiceDefinition ) {
            TypeChoiceDefinition moduleType = (TypeChoiceDefinition) td;
            if ( !moduleType.left().id().equals( moduleType.id() ) ) {
                ImportResult choiceResult = resolveTypeUsingLink( ctx, moduleType.left(),
                        moduleType.left().id(), true );
                typeResult.prependResult( choiceResult );
            }
            if ( !moduleType.right().id().equals( moduleType.id() ) ) {
                ImportResult choiceResult = resolveTypeUsingLink( ctx, moduleType.left(),
                        moduleType.left().id(), true );
                typeResult.prependResult( choiceResult );
            }
            TypeChoiceDefinition localType =
                    new TypeChoiceDefinition( ctx, localTypeNamePrefix + localName,
                            moduleType.cardinality(), moduleType.left(), moduleType.right() );
            localType.setDocumentation( moduleType.getDocumentation() );
            typeResult.addNode( localType );
            typeResult.addType( localType );
        } else if ( td instanceof TypeDefinitionLink ) {
            TypeDefinitionLink moduleType = (TypeDefinitionLink) td;

            // find and resolve linked type
            TypeDefinition linkedType = this.findType( moduleType.linkedTypeName() );
            ImportResult linkedTypeNameResult =
                    resolveTypeUsingLink( ctx, linkedType, linkedType.id(), false );
            typeResult.addResult( linkedTypeNameResult );

            if ( !isADependencyType ) {
                TypeDefinitionLink localTypeLink =
                        new TypeDefinitionLink( ctx, localTypeNamePrefix + localName,
                                Constants.RANGE_ONE_TO_ONE, localTypeNamePrefix + linkedType.id() );
                typeResult.addNode( localTypeLink );
                typeResult.addType( localTypeLink );
            }

        } else if ( td instanceof TypeInlineDefinition ) {
            TypeInlineDefinition moduleType = (TypeInlineDefinition) td;
            if ( isADependencyType && !moduleType.hasSubTypes() ) {
                return typeResult;
            }
            TypeInlineDefinition localType =
                    new TypeInlineDefinition( ctx, localTypeNamePrefix + localName,
                            moduleType.nativeType(), moduleType.cardinality() );

            if ( moduleType.subTypes() != null ) {
                for (Map.Entry< String, TypeDefinition > entry : moduleType.subTypes()) {
                    ImportResult subTypeResult =
                            resolveTypeUsingLink( ctx, entry.getValue(), entry.getKey(), true );
                    for (OLSyntaxNode n : subTypeResult.nodes()) {
                        typeResult.addNode( n );
                        typeResult.addType( (TypeDefinition) n );
                    }
                    localType.putSubType( entry.getValue() );
                }
            }
            localType.setDocumentation( moduleType.getDocumentation() );
            typeResult.addNode( localType );
            typeResult.addType( localType );
        } else {
            throw new ModuleParsingException( td.id() + " of type " + td.getClass().getSimpleName()
                    + " is not support for module system" );
        }

        if ( !isADependencyType ) {
            TypeDefinitionLink localTypeLink = new TypeDefinitionLink( ctx, localName,
                    Constants.RANGE_ONE_TO_ONE, localTypeNamePrefix + localName );
            typeResult.addNode( localTypeLink );
            typeResult.addType( localTypeLink );
        }

        return typeResult;
    }

    private ImportResult resolveInterface( ParsingContext ctx, InterfaceDefinition id,
            String localName ) throws ModuleParsingException
    {
        ImportResult interfaceResult = new ImportResult();
        InterfaceDefinition localIface = new InterfaceDefinition( ctx, localName );
        localIface.setDocumentation( id.getDocumentation() );

        id.copyTo( localIface );
        interfaceResult.addNode( localIface );
        interfaceResult.addInterface( localIface );

        return interfaceResult;
    }

    private ImportResult resolveProcedureDefinition( ParsingContext ctx, DefinitionNode id,
            String localName ) throws ModuleParsingException
    {
        ImportResult procedureDefinitionResult = new ImportResult();
        DefinitionNode localProcedureDefinition = new DefinitionNode( ctx, localName, id.body() );

        procedureDefinitionResult.addNode( localProcedureDefinition );

        return procedureDefinitionResult;
    }

    private ImportResult resolveService( ParsingContext ctx, ServiceNode moduleService,
            String localName ) throws ModuleParsingException
    {
        ImportResult serviceResult = new ImportResult();
        ServiceNode localService = new ServiceNode( ctx, localName, moduleService.type() );
        if ( moduleService.type() == EmbeddedServiceType.JAVA ) {
            localService.putParameter( "packageName", moduleService.getParameter( "packageName" ) );
        }
        moduleService.getInputPortInfos().forEach(
                ( String name, InputPortInfo ip ) -> localService.addInputPortInfo( ip ) );

        moduleService.getOutputPortInfos().forEach(
            ( String name, OutputPortInfo ip ) -> localService.addOutputPortInfo( ip ) );

        localService.setProgram(moduleService.program());

        serviceResult.addNode(localService);
        serviceResult.addService(localService);

        return serviceResult;
    }


    /**
     * @return the source of imported module
     */
    public URI source()
    {
        return source;
    }

}
