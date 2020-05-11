package jolie.lang.parse.module.exceptions;

import jolie.lang.Constants;

public class SymbolTypeMismatchException extends Exception
{

    private static final long serialVersionUID = Constants.serialVersionUID();
    private final String symbolName;
    private final String expectType;
    private final String actualType;

    public SymbolTypeMismatchException( String symbolName, String expectType, String actualType )
    {
        super( symbolName );
        this.symbolName = symbolName;
        this.expectType = expectType;
        this.actualType = actualType;
    }

    @Override
    public String getMessage()
    {
        return symbolName + " is not defined as a " + expectType + " found symbol of type " + actualType;
    }
}
