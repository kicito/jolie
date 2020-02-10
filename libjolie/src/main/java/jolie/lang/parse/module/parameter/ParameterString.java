package jolie.lang.parse.module.parameter;



class ParameterString extends Parameter
{
    protected ParameterString( String value )
    {
        super( value );
    }

    @Override
    public Class< ? > getObjectType()
    {
        return String.class;
    }
}
