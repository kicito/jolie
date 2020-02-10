package jolie.lang.parse.module.parameter;

import jolie.util.Pair;

public class ParameterFactory
{
    @SuppressWarnings("unchecked")
    public static Parameter createParameter( Object value )
    {
        if ( value instanceof String ) {
            return new ParameterString( (String) value );
        } else if ( value instanceof Pair< ?, ? > ) {
            return new ParameterPair( (Pair< String, String >) value );
        }
        return null;
    }
}
