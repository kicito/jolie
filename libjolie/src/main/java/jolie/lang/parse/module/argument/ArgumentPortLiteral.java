package jolie.lang.parse.module.argument;

import java.net.URI;
import java.util.Map;
import jolie.lang.parse.ast.InputPortInfo;
import jolie.lang.parse.ast.PortInfo;

public class ArgumentPortLiteral extends Argument
{

    private InputPortInfo port;
    
    public ArgumentPortLiteral( InputPortInfo port)
    {
        super( port );
    }

    @Override
    public Class< ? > getObjectType()
    {
        return InputPortInfo.class;
    }
    // private PortInfo info;
	// private URI location;
    // private String protocolId;
    
    // public ArgumentPortLiteral( PortInfo info, URI location, String protocolId)
    // {
    //     super( info );
    // }

    // @Override
    // public Class< ? > getObjectType()
    // {
    //     return PortInfo.class;
    // }

    // @Override
    // public Object value(){
    //     return null;
    // }
}
