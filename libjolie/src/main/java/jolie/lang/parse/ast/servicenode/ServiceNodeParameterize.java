package jolie.lang.parse.ast.servicenode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jolie.lang.Constants;
import jolie.lang.Constants.ServiceType;
import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.ast.DefinitionNode;
import jolie.lang.parse.ast.InputPortInfo;
import jolie.lang.parse.ast.OLSyntaxNode;
import jolie.lang.parse.ast.OutputPortInfo;
import jolie.lang.parse.ast.Program;
import jolie.lang.parse.ast.SequenceStatement;
import jolie.lang.parse.ast.VariablePathNode;
import jolie.lang.parse.ast.types.TypeDefinition;
import jolie.lang.parse.context.ParsingContext;
import jolie.util.Pair;

public abstract class ServiceNodeParameterize extends OLSyntaxNode
{
    private String name;
    private Pair< TypeDefinition, VariablePathNode > parameter; // type variablePath
    private Map< String, InputPortInfo > inputPortInfos;
    private Map< String, OutputPortInfo > outputPortInfos;
    private List< OLSyntaxNode > deploymentInstructions = new ArrayList< OLSyntaxNode >();
    private SequenceStatement init = null;
    private OLSyntaxNode main = null;

    private static final long serialVersionUID = Constants.serialVersionUID();

    public ServiceNodeParameterize( ParsingContext context, String name )
    {
        super( context );
        this.name = name;
        this.inputPortInfos = new HashMap<>();
        this.outputPortInfos = new HashMap<>();
    }

    public String name()
    {
        return this.name;
    }

    public TypeDefinition parameterType()
    {
        if (this.parameter == null ) return null;
        return this.parameter.key();
    }

    public VariablePathNode parameterPath()
    {
        if (this.parameter == null ) return null;
        return this.parameter.value();
    }

    public void setParameter( Pair< TypeDefinition, VariablePathNode > param )
    {
        this.parameter = param;
    }

    public void setParameter( TypeDefinition t, VariablePathNode path )
    {
        this.parameter = new Pair< TypeDefinition, VariablePathNode >( t, path );
    }

    public List< OLSyntaxNode > deploymentInstructions()
    {
        return this.deploymentInstructions;
    }

    public void addDeploymentInstruction( OLSyntaxNode n )
    {
        deploymentInstructions.add( n );
    }

    public void addInit( OLSyntaxNode init )
    {
        if ( this.init == null ) {
            this.init = new SequenceStatement( init.context() );
        }
        this.init.addChild( init );
    }

    public OLSyntaxNode init()
    {
        if (this.init == null){
            return null;
        }
        return new DefinitionNode( this.init.context(), "init", this.init );
    }

    public OLSyntaxNode main()
    {
        return this.main;
    }

    public void setMain( OLSyntaxNode main )
    {
        this.main = main;
    }

    public void addInputPortInfo( InputPortInfo ip )
    {
        this.inputPortInfos.put( ip.id(), ip );
    }

    public InputPortInfo getInputPortInfo( String name )
    {
        return this.inputPortInfos.get( name );
    }

    public Map< String, InputPortInfo > getInputPortInfos()
    {
        return this.inputPortInfos;
    }

    public void addOutputPortInfo( OutputPortInfo op )
    {
        this.outputPortInfos.put( op.id(), op );
    }

    public OutputPortInfo getOutputPortInfo( String name )
    {
        return this.outputPortInfos.get( name );
    }

    public Map< String, OutputPortInfo > getOutputPortInfos()
    {
        return this.outputPortInfos;
    }


    public Program program()
    {
        List< OLSyntaxNode > children = new ArrayList< OLSyntaxNode >();
        if ( this.deploymentInstructions.size() > 0 ) {
            children.addAll( this.deploymentInstructions );
        }
        if ( this.inputPortInfos.values().size() > 0 ) {
            children.addAll( this.inputPortInfos.values() );
        }
        if ( this.outputPortInfos.values().size() > 0 ) {
            children.addAll( this.outputPortInfos.values() );
        }
        // if ( this.embeddings.size() > 0 ) {
        //     children.addAll( this.embeddings );
        // }
        if ( this.init != null ) {
            children.add( new DefinitionNode( this.init.context(), "init", this.init ) );
        }
        if ( this.main != null ) {
            children.add( this.main );
        }
        return new Program( this.context(), children );
    }


    public abstract String getTarget();

    public abstract ServiceType getType();

    public abstract void accept( OLVisitor visitor );
}
