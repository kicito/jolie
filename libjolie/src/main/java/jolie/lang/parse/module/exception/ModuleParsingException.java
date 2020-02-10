package jolie.lang.parse.module.exception;

import jolie.lang.Constants;

public class ModuleParsingException extends Exception
{

    private static final long serialVersionUID = Constants.serialVersionUID();

    public ModuleParsingException( String message )
    {
        super( message );
    }

    public ModuleParsingException(String arg0, Throwable arg1) {
        super(arg0,arg1);
    }
    
    public ModuleParsingException(Throwable arg0) {
        super(arg0);
    }

}
