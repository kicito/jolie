package jolie.lang.parse.module.exceptions;

import jolie.lang.Constants;

public class DuplicateSymbolException extends Exception
{

    private static final long serialVersionUID = Constants.serialVersionUID();
    private final String symbolName;

    public DuplicateSymbolException( String symbolName )
    {
        super( symbolName );
        this.symbolName = symbolName;
    }

    @Override
    public String getMessage()
    {
        return "detected duplicate declaration of symbol " + symbolName;
    }


}
