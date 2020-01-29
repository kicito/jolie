package jolie.lang.parse;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import jolie.lang.parse.ast.DefinitionNode;
import jolie.lang.parse.ast.InterfaceDefinition;
import jolie.lang.parse.ast.OLSyntaxNode;
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
        return null;
    }



    public ImportResult resolveNameSpace( ParsingContext ctx )
    {
        ImportResult res = new ImportResult();
        for (TypeDefinition td : this.programInspector.getTypes()) {
            res.nodes.add( td );
            res.types.put( td.id(), td );
        }

        for (InterfaceDefinition interfaceDef : this.programInspector.getInterfaces()) {
            res.nodes.add( interfaceDef );
            res.interfaces.put( interfaceDef.name(), interfaceDef );
        }
        return res;
    }

    public ImportResult resolve( ParsingContext ctx, List< Pair< String, String > > pathNodes )
            throws ModuleParsingException
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
                importResult =
                        resolveType( ctx, (TypeDefinition) moduleNode, pathNode.value(), false );
                res.addResult( importResult );
            } else if ( moduleNode instanceof InterfaceDefinition ) {
                importResult =
                        resolveInterface( ctx, (InterfaceDefinition) moduleNode, pathNode.value() );
                res.addResult( importResult );
            }else if ( moduleNode instanceof DefinitionNode ) {
                importResult =
                        resolveProcedureDefinition( ctx, (DefinitionNode) moduleNode, pathNode.value() );
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
            typeResult.nodes.add( localType );
            typeResult.types.put( localType.id(), localType );
            return typeResult;
        } else if ( td instanceof TypeDefinitionLink ) {
            TypeDefinitionLink moduleType = (TypeDefinitionLink) td;
            if ( !isSubType ) {
                TypeDefinitionLink localType = new TypeDefinitionLink( ctx, localName,
                        moduleType.cardinality(), moduleType.linkedTypeName() );
                localType.setDocumentation( moduleType.getDocumentation() );
                typeResult.nodes.add( localType );
                typeResult.types.put( localType.id(), localType );
            }
            TypeDefinition linkedType = this.findType( moduleType.linkedTypeName() );
            typeResult.nodes.add( linkedType );
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
                    for (OLSyntaxNode n : subTypeResult.nodes) {
                        typeResult.nodes.add( n );
                        TypeDefinition subTD = (TypeDefinition) n;
                        typeResult.types.put( subTD.id(), subTD );
                    }
                    localType.putSubType( entry.getValue() );
                }
            }
            localType.setDocumentation( moduleType.getDocumentation() );
            typeResult.nodes.add( localType );
            typeResult.types.put( localType.id(), localType );
        } else {
            throw new ModuleParsingException( td.id() + " of type " + td.getClass().getSimpleName()
                    + " is not support for module system" );
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
        interfaceResult.nodes.add( localIface );
        interfaceResult.interfaces.put( localName, localIface );

        return interfaceResult;
    }

    private ImportResult resolveProcedureDefinition( ParsingContext ctx, DefinitionNode id,
            String localName ) throws ModuleParsingException
    {
        ImportResult procedureDefinitionResult = new ImportResult();
        DefinitionNode localProcedureDefinition = new DefinitionNode( ctx, localName, id.body() );

        procedureDefinitionResult.nodes.add( localProcedureDefinition );

        return procedureDefinitionResult;
    }


    /**
     * @return the source of imported module
     */
    public URI source()
    {
        return source;
    }

}
