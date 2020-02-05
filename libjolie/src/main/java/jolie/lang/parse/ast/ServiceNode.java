package jolie.lang.parse.ast;

import java.util.HashMap;
import java.util.Map;
import jolie.lang.Constants;
import jolie.lang.Constants.EmbeddedServiceType;
import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.context.ParsingContext;
import jolie.lang.parse.module.Importable;
import jolie.lang.parse.util.ProgramInspector;

public class ServiceNode extends OLSyntaxNode implements Importable
{
    private static final long serialVersionUID = Constants.serialVersionUID();
    private String name;
    private Program program = null;
    private Constants.EmbeddedServiceType type;
    private Map< String, Object > parameters;
    private Map< String, InputPortInfo > inputPortInfos;
    private Map< String, OutputPortInfo > outputPortInfos;

    public ServiceNode( ParsingContext context, String name, Constants.EmbeddedServiceType type )
    {
        super( context );
        this.name = name;
        this.type = type;
        this.parameters = new HashMap< String, Object >();
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

    public void setType( Constants.EmbeddedServiceType type )
    {
        this.type = type;
    }

    public Constants.EmbeddedServiceType type()
    {
        return this.type;
    }

    public void putParameter( String key, Object content )
    {
        this.parameters.put( key, content );
    }

    public Object getParameter( String key )
    {
        return this.parameters.get( key );
    }

    public String name()
    {
        return this.name;
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
        if ( this.type() == EmbeddedServiceType.JAVA ) {
            localService.putParameter( "packageName", this.getParameter( "packageName" ) );
        }
        this.getInputPortInfos().forEach(
                ( String name, InputPortInfo ip ) -> localService.addInputPortInfo( ip ) );

        this.getOutputPortInfos().forEach(
            ( String name, OutputPortInfo ip ) -> localService.addOutputPortInfo( ip ) );

        localService.setProgram(this.program());
        return localService;
	}

    @Override
    public void accept( OLVisitor visitor )
    {
        visitor.visit( this );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((program == null) ? 0 : program.hashCode());
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj ) return true;
        if ( !super.equals( obj ) ) return false;
        if ( getClass() != obj.getClass() ) return false;
        ServiceNode other = (ServiceNode) obj;
        if ( name == null ) {
            if ( other.name != null ) return false;
        } else if ( !name.equals( other.name ) ) return false;
        if ( program == null ) {
            if ( other.program != null ) return false;
        } else if ( !program.equals( other.program ) ) return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "ServiceNode [name=" + name + ", program=" + program + "]";
    }

}
