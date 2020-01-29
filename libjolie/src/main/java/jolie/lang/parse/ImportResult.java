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
    public List< OLSyntaxNode > nodes;
    public Map< String, TypeDefinition > types;
    public Map< String, InterfaceDefinition > interfaces;
    public Map< String, DefinitionNode > procedures;

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
}
