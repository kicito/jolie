package jolie.lang.parse.module.argument;

public abstract class Argument
{
    private Object value;

    protected Argument( Object value )
    {
        this.value = value;
    }

    public Object value()
    {
        return value;
    }

    public abstract Class< ? > getObjectType();
}
