package jolie.lang.parse;

import java.util.ArrayList;
import java.util.List;
import jolie.lang.Constants;
import jolie.lang.parse.context.ParsingContext;

public class ModuleSolverExceptions extends Exception
{

    private static final long serialVersionUID = Constants.serialVersionUID();

    private final List< Exception > errorList = new ArrayList<>();

    public static class ModuleException extends Exception
    {
        private static final long serialVersionUID = ModuleSolverExceptions.serialVersionUID;

        public ModuleException( ParsingContext context, String message )
        {
            super( new StringBuilder().append( context.sourceName() ).append( ':' )
                    .append( context.line() ).append( ": error: " ).append( message ).toString() );
        }

        public ModuleException( ParsingContext context, String message, Throwable cause )
        {
            this( new StringBuilder().append( context.sourceName() ).append( ':' )
                    .append( context.line() ).append( ": error: " ).append( message ).toString(),
                    cause );
        }

        private ModuleException( String message, Throwable cause )
        {
            super( message, cause );
        }
    }

    public void addException( Exception e )
    {
        errorList.add( e );
    }

    public List< Exception > getErrorList()
    {
        return errorList;
    }

    public String getErrorMessages()
    {
        StringBuilder message = new StringBuilder();
        for (Exception error : errorList) {
            message.append( error.getMessage() ).append( '\n' );
        }
        return message.toString();
    }

}
