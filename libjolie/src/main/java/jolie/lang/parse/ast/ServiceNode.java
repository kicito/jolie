package jolie.lang.parse.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jolie.lang.Constants;
import jolie.lang.Constants.EmbeddedServiceType;
import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.context.ParsingContext;
import jolie.lang.parse.module.Importable;
import jolie.lang.parse.module.parameter.Parameter;
import jolie.lang.parse.util.ProgramInspector;
import jolie.util.Pair;

public class ServiceNode extends OLSyntaxNode implements Importable
{

    public static class Bind
    {
        public String localPort;
        public String callerPort;

        public Bind( String localPort, String callerPort )
        {
            this.localPort = localPort;
            this.callerPort = callerPort;
        }
    }


    private static final long serialVersionUID = Constants.serialVersionUID();
    private String name;
    private SequenceStatement init = null;
    private OLSyntaxNode main = null;
    private Constants.EmbeddedServiceType type;
    private List< Parameter > parameters;
    private List< Bind > binds;
    private Program program = null;
    private Map< String, InputPortInfo > inputPortInfos;
    private Map< String, OutputPortInfo > outputPortInfos;
    private List< OLSyntaxNode > deploymentInstructions = new ArrayList< OLSyntaxNode >();
    private List< EmbeddedServiceNode2 > embeddings = new ArrayList< EmbeddedServiceNode2 >();

    public ServiceNode( ParsingContext context, String name, Constants.EmbeddedServiceType type )
    {
        super( context );
        this.name = name;
        this.type = type;
        this.parameters = new ArrayList< Parameter >();
        this.binds = new ArrayList< Bind >();
        this.inputPortInfos = new HashMap<>();
        this.outputPortInfos = new HashMap<>();
    }


    public void setProgram( Program p )
    {
        this.program = p;
    }

    public Program program()
    {
        return this.program;
    }

    // public Program program()
    // {
    // List< OLSyntaxNode > children = new ArrayList< OLSyntaxNode >();
    // if (this.deploymentInstructions.size() > 0){
    // children.addAll( this.deploymentInstructions );
    // }
    // if (this.inputPortInfos.values().size() > 0){
    // children.addAll( this.inputPortInfos.values() );
    // }
    // if (this.outputPortInfos.values().size() > 0){
    // children.addAll( this.outputPortInfos.values() );
    // }
    // if (this.embeddings.size() > 0){
    // children.addAll( this.embeddings );
    // }
    // if ( this.init != null ) {
    // children.add( new DefinitionNode( this.init.context(), "init", this.init ) );
    // }
    // if ( this.main != null ) {
    // children.add( this.main );
    // }
    // return new Program( this.context(), children );
    // }

    public void addDeploymentInstruction( OLSyntaxNode n )
    {
        deploymentInstructions.add( n );
    }

    public void addEmbedding( EmbeddedServiceNode2 n )
    {
        embeddings.add( n );
    }

    public List< OLSyntaxNode > deploymentInstructions()
    {
        return this.deploymentInstructions;
    }

    public List< EmbeddedServiceNode2 > embeddings()
    {
        return this.embeddings;
    }

    public void setType( Constants.EmbeddedServiceType type )
    {
        this.type = type;
    }

    public Constants.EmbeddedServiceType type()
    {
        return this.type;
    }

    public void addParameter( Parameter arg )
    {
        this.parameters.add( arg );
    }

    public void addParameters( Parameter[] args )
    {
        this.parameters.addAll( Arrays.asList( args ) );
    }

    public Parameter getParameter( int index )
    {
        return this.parameters.get( index );
    }

    private Parameter[] getParameters()
    {
        return this.parameters.toArray( new Parameter[0] );
    }

    public Parameter[] getAssignableParameters()
    {
        if ( this.type == EmbeddedServiceType.JAVA ) {
            return this.parameters.subList( 1, this.parameters.size() ).toArray( new Parameter[0] );
        }
        return this.parameters.toArray( new Parameter[0] );
    }

    @SuppressWarnings("unchecked")
    public boolean isInputPortNameExistsInParameters( String inputPortName )
    {
        for (Parameter param : this.getParameters()) {
            if ( param.getObjectType() == Pair.class ) {
                Pair< String, String > argPair = (Pair< String, String >) param.value();
                if ( argPair.key() == "inputPort" && argPair.value().equals( inputPortName ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public boolean isOutputPortNameExistsInParameters( String outputPortName )
    {
        for (Parameter param : this.getParameters()) {
            if ( param.getObjectType() == Pair.class ) {
                Pair< String, String > argPair = (Pair< String, String >) param.value();
                if ( argPair.key() == "outputPort" && argPair.value().equals( outputPortName ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public String name()
    {
        return this.name;
    }

    public void addBinding( Bind bindInfo )
    {
        this.binds.add( bindInfo );
    }

    public void addBindings( Bind[] bindInfos )
    {
        this.binds.addAll( Arrays.asList( bindInfos ) );
    }

    public Bind getBinding( int index )
    {
        return this.binds.get( index );
    }

    public PortInfo getLocalBindingPortFromParamName( String portName )
    {
        for (Bind b : getBindings()) {
            if ( b.callerPort.equals( portName ) ) {
                if ( this.inputPortInfos.containsKey( b.localPort ) ) {
                    return this.inputPortInfos.get( b.localPort );
                }
                if ( this.outputPortInfos.containsKey( b.localPort ) ) {
                    return this.outputPortInfos.get( b.localPort );
                }
            }
        }
        return null;
    }

    public Bind[] getBindings()
    {
        return this.binds.toArray( new Bind[0] );
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

    @Override
    public ServiceNode resolve( ParsingContext ctx, ProgramInspector pi, String localID )
    {
        ServiceNode localService = new ServiceNode( ctx, localID, this.type() );
        localService.addParameters( this.getParameters() );
        localService.addBindings( this.getBindings() );
        this.getInputPortInfos().forEach(
                ( String name, InputPortInfo ip ) -> localService.addInputPortInfo( ip ) );

        this.getOutputPortInfos().forEach(
                ( String name, OutputPortInfo ip ) -> localService.addOutputPortInfo( ip ) );
        if (this.main != null){
            localService.setMain( this.main );
        }
        if (this.init != null){
            localService.addInit( init );
        }
        this.deploymentInstructions.forEach( di -> localService.addDeploymentInstruction( di ) );
        // localService.setProgram( this.program() );
        return localService;
    }

    @Override
    public void accept( OLVisitor visitor )
    {
        visitor.visit( this );
    }

    @Override
    public String toString()
    {
        return "ServiceNode [name=" + name + "]";
    }

    public void addInit( OLSyntaxNode init )
    {
        if ( this.init == null ) {
            this.init = new SequenceStatement( init.context() );
        }
        this.init.addChild( init );
    }


    public OLSyntaxNode initSequence()
    {
        return this.init;
    }

    public void setMain( OLSyntaxNode main )
    {
        this.main = main;
    }

    public OLSyntaxNode main()
    {
        return this.main;
    }

}
