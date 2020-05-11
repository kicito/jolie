package jolie.lang.parse.module.exceptions;

import jolie.lang.Constants;

public class IllegalAccessSymbolException extends Exception
{

    private static final long serialVersionUID = Constants.serialVersionUID();

    private final String symbolName;
    private final String[] moduleTargets;

    public IllegalAccessSymbolException( String symbolName, String[] moduleTargets )
    {
        super( symbolName );
        this.symbolName = symbolName;
        this.moduleTargets = moduleTargets;
    }

    @Override
    public String getMessage()
    {
        return "Illegal access to symbol " + this.symbolName + " of module " + String.join(".", moduleTargets);
    }
}