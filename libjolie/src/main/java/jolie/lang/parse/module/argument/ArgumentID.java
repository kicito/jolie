package jolie.lang.parse.module.argument;


public class ArgumentID extends Argument
{
    public ArgumentID( String value )
    {
        super( value );
    }

    @Override
    public Class< ? > getObjectType()
    {
        return String.class;
    }
}
