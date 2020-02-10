package jolie.lang.parse.module.parameter;

public abstract class Parameter
{
    private Object value;

    protected Parameter( Object value )
    {
        this.value = value;
    }

    public Object value()
    {
        return value;
    }

    public abstract Class< ? > getObjectType();
}
