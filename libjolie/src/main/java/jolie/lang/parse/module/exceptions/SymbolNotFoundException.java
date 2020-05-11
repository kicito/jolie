package jolie.lang.parse.module.exceptions;

import jolie.lang.Constants;

public class SymbolNotFoundException extends Exception
{

    private static final long serialVersionUID = Constants.serialVersionUID();
    private final String symbolName;
    private final String[] moduleTargets;

    public SymbolNotFoundException( String symbolName )
    {
        super( symbolName );
        this.symbolName = symbolName;
        this.moduleTargets = null;
    }

    public SymbolNotFoundException( String symbolName, String[] moduleTargets )
    {
        super( symbolName );
        this.symbolName = symbolName;
        this.moduleTargets = moduleTargets;
    }

    @Override
    public String getMessage()
    {
        if (this.moduleTargets == null)
            return this.symbolName + " is not defined in symbolTable";
        else
            return this.symbolName + " is not defined in " + String.join(".", this.moduleTargets); 
    }
}
