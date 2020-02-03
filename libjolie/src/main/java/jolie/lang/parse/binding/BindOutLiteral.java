package jolie.lang.parse.binding;

public class BindOutLiteral extends BindOut
{
    private String locationLiteral;

    public BindOutLiteral( String serviceOutputPortName, String locationLiteral )
    {
        super( serviceOutputPortName );
        this.locationLiteral = locationLiteral;
    }

    public String locationLiteral()
    {
        return locationLiteral;
    }

    @Override
    public String toString()
    {
        return "BindOutLiteral [locationLiteral=" + locationLiteral + "]";
    }

}