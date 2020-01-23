package jolie.lang.parse;

import jolie.lang.Constants;

public class ModuleNotFoundException extends Exception
{

    private static final long serialVersionUID = Constants.serialVersionUID();

    public ModuleNotFoundException( String message )
    {
        super( message );
    }

    public ModuleNotFoundException(String arg0, Throwable arg1) {
        super(arg0,arg1);
    }
    
    public ModuleNotFoundException(Throwable arg0) {
        super(arg0);
    }

}
