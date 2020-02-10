package jolie.lang.parse.module.argument;

import java.util.Map;

public class ArgumentLiteral extends Argument
{
    public ArgumentLiteral( Map<String, String> value )
    {
        super( value );
    }

    @Override
    public Class< ? > getObjectType()
    {
        return Map.class;
    }
}
