package jolie.lang.parse.binding;

public class BindOutInputPort extends BindOut
{
    private String embedderInputPort;

    public BindOutInputPort( String serviceOutputPortName, String ip )
    {
        super( serviceOutputPortName );
        this.embedderInputPort = ip;
    }

    public String embedderInputPort()
    {
        return embedderInputPort;
    }

    @Override
    public String toString()
    {
        return "BindOutInputPort [embedderInputPort=" + embedderInputPort + "]";
    }

}