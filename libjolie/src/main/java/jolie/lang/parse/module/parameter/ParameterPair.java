package jolie.lang.parse.module.parameter;

import jolie.util.Pair;

class ParameterPair extends Parameter
{

    protected ParameterPair( Pair< String, String > value )
    {
        super( value );
    }

    @Override
    public Class< ? > getObjectType()
    {
        return Pair.class;
    }
}
