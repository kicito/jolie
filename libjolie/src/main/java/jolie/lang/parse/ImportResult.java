package jolie.lang.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jolie.lang.parse.ast.DefinitionNode;
import jolie.lang.parse.ast.InterfaceDefinition;
import jolie.lang.parse.ast.OLSyntaxNode;
import jolie.lang.parse.ast.types.TypeDefinition;



public class ImportResult
{
    private final List< OLSyntaxNode > nodes;
    private final Map< String, TypeDefinition > types;
    private final Map< String, InterfaceDefinition > interfaces;
    private final Map< String, DefinitionNode > procedures;

    public ImportResult()
    {
        this.nodes = new ArrayList<>();
        this.types = new HashMap< String, TypeDefinition >();
        this.interfaces = new HashMap< String, InterfaceDefinition >();
        this.procedures = new HashMap< String, DefinitionNode >();
    }

    public void prependResult( ImportResult re )
    {
        // TODO check name crash for each type
        for (OLSyntaxNode n : re.nodes) {
            this.nodes.add( 0, n );
        }
        for (Map.Entry< String, TypeDefinition > entry : re.types.entrySet()) {
            this.types.put( entry.getKey(), entry.getValue() );
        }
        for (Map.Entry< String, InterfaceDefinition > entry : re.interfaces.entrySet()) {
            this.interfaces.put( entry.getKey(), entry.getValue() );
        }
        for (Map.Entry< String, DefinitionNode > entry : re.procedures.entrySet()) {
            this.procedures.put( entry.getKey(), entry.getValue() );
        }
    }

    public void addResult( ImportResult re )
    {
        // TODO check name crash for each type
        for (OLSyntaxNode n : re.nodes) {
            this.nodes.add( n );
        }
        for (Map.Entry< String, TypeDefinition > entry : re.types.entrySet()) {
            this.types.put( entry.getKey(), entry.getValue() );
        }
        for (Map.Entry< String, InterfaceDefinition > entry : re.interfaces.entrySet()) {
            this.interfaces.put( entry.getKey(), entry.getValue() );
        }
        for (Map.Entry< String, DefinitionNode > entry : re.procedures.entrySet()) {
            this.procedures.put( entry.getKey(), entry.getValue() );
        }
    }

    public void addNode( OLSyntaxNode n )
    {
        this.nodes.add( n );
    }

    /**
     * @return the nodes
     */
    public List< OLSyntaxNode > nodes()
    {
        return nodes;
    }

    public void addType( TypeDefinition td )
    {
        this.types.put( td.id(), td );
    }

    /**
     * @return the types
     */
    public Map< String, TypeDefinition > types()
    {
        return types;
    }

    public void addInterface( InterfaceDefinition id )
    {
        this.interfaces.put( id.name(), id );
    }

    /**
     * @return the interfaces
     */
    public Map< String, InterfaceDefinition > interfaces()
    {
        return interfaces;
    }

    public void addProcedure( DefinitionNode dn )
    {
        this.procedures.put( dn.id(), dn );
    }

    /**
     * @return the procedures
     */
    public Map< String, DefinitionNode > procedures()
    {
        return procedures;
    }
}
