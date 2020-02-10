package jolie.lang.parse.module;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import jolie.lang.parse.ast.DefinitionNode;
import jolie.lang.parse.ast.InterfaceDefinition;
import jolie.lang.parse.ast.OLSyntaxNode;
import jolie.lang.parse.ast.ServiceNode;
import jolie.lang.parse.ast.types.TypeDefinition;
import jolie.lang.parse.context.ParsingContext;
import jolie.lang.parse.module.exception.ModuleParsingException;
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

    public Importable find( String id )
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

    public ImportResult resolveNameSpace( ParsingContext ctx ) throws ModuleParsingException
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
        return resolve( ctx, importPaths );
    }

    public ImportResult resolve( ParsingContext ctx, List< Pair< String, String > > pathNodes ) throws ModuleParsingException
    {
        ImportResult res = new ImportResult();
        for (Pair< String, String > pathNode : pathNodes) {
            Importable moduleNode = this.find( pathNode.key() );
            if (moduleNode == null){
                throw new ModuleParsingException( "unable to find " + pathNode.key() + " in " + this.programInspector.getSources());
            }
            OLSyntaxNode node = moduleNode.resolve(ctx, this.programInspector, pathNode.value());
            res.addNode(node);
        }

        return res;

    }


    /**
     * @return the source of imported module
     */
    public URI source()
    {
        return source;
    }

}
