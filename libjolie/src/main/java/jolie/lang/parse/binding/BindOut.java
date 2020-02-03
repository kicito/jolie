package jolie.lang.parse.binding;

public class BindOut
{
    private String serviceOutputPortName;

    protected BindOut( String serviceOutputPortName )
    {
        this.serviceOutputPortName = serviceOutputPortName;
    }

    public String serviceOutputPortName()
    {
        return serviceOutputPortName;
    }
}