package jolie.lang.parse.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jolie.lang.parse.ast.DefinitionNode;
import jolie.lang.parse.ast.InterfaceDefinition;
import jolie.lang.parse.ast.OLSyntaxNode;
import jolie.lang.parse.ast.ServiceNode;
import jolie.lang.parse.ast.servicenode.ServiceNodeParameterize;
import jolie.lang.parse.ast.types.TypeDefinition;



public class ImportResult
{
    private final List< OLSyntaxNode > nodes;
    private final Map< String, TypeDefinition > types;
    private final Map< String, InterfaceDefinition > interfaces;
    private final Map< String, DefinitionNode > procedures;
    private final Map< String, ServiceNode > services;
    private final Map< String, ServiceNodeParameterize > paramServices;

    public ImportResult()
    {
        this.nodes = new ArrayList<>();
        this.types = new HashMap< String, TypeDefinition >();
        this.interfaces = new HashMap< String, InterfaceDefinition >();
        this.procedures = new HashMap< String, DefinitionNode >();
        this.services = new HashMap< String, ServiceNode >();
        this.paramServices = new HashMap< String, ServiceNodeParameterize >();
    }

    public void prependResult( ImportResult re )
    {
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
        for (Map.Entry< String, ServiceNode > entry : re.services.entrySet()) {
            this.services.put( entry.getKey(), entry.getValue() );
        }
        for (Map.Entry< String, ServiceNodeParameterize > entry : re.paramServices.entrySet()) {
            this.paramServices.put( entry.getKey(), entry.getValue() );
        }
    }

    public void addNode( OLSyntaxNode n )
    {
        if ( n instanceof TypeDefinition ) {
            this.addType( (TypeDefinition) n );
        }
        if ( n instanceof InterfaceDefinition ) {
            this.addInterface( (InterfaceDefinition) n );
        }
        if ( n instanceof DefinitionNode ) {
            this.addProcedure( (DefinitionNode) n );
        }
        if ( n instanceof ServiceNode ) {
            this.addService( (ServiceNode) n );
        }
        if ( n instanceof ServiceNodeParameterize ) {
            this.addParamService( (ServiceNodeParameterize) n );
        }
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

    public void addService( ServiceNode s )
    {
        this.services.put( s.name(), s );
    }

    /**
     * @return the Services
     */
    public Map< String, ServiceNode > services()
    {
        return services;
    }

    public void addParamService( ServiceNodeParameterize s )
    {
        this.paramServices.put( s.name(), s );
    }

    /**
     * @return the parameterize Services
     */
    public Map< String, ServiceNodeParameterize > paramServices()
    {
        return paramServices;
    }
}
