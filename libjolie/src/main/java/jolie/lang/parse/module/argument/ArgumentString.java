package jolie.lang.parse.module.argument;



public class ArgumentString extends Argument
{
    public ArgumentString( String value )
    {
        super( value );
    }

    @Override
    public Class< ? > getObjectType()
    {
        return String.class;
    }
}
