package jolie;

import jolie.lang.Constants;
import jolie.lang.parse.context.ParsingContext;

public class ModuleLoaderException extends Exception
{
    final private static long serialVersionUID = Constants.serialVersionUID();


    public ModuleLoaderException( String message )
    {
        super( message );
    }

    public ModuleLoaderException( ParsingContext context, String message )
    {
        super( context.sourceName() + ":" + context.line() + ": " + message );
    }

}
