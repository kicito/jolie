package jolie.lang.parse.binding;

public class BindIn
{
    private String serviceInputPortName;
    private String embedderOutputPortName;

    public BindIn( String serviceInputPortName,String embedderOutputPortName )
    {
        this.serviceInputPortName = serviceInputPortName;
        this.embedderOutputPortName = embedderOutputPortName;
    }

    @Override
    public String toString()
    {
        return "BindIn [embedderOutputPortName=" + embedderOutputPortName
                + ", serviceInputPortName=" + serviceInputPortName + "]";
    }

    public String serviceInputPortName()
    {
        return serviceInputPortName;
    }

    public String embedderOutputPortName()
    {
        return embedderOutputPortName;
    }
}