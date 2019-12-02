
package jolie.lang.parse.ast;

import java.net.URI;
import java.util.Arrays;
import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.context.ParsingContext;


public class ImportStatement extends OLSyntaxNode
{
    /**
    *
    */
    private static final long serialVersionUID = 5226504948641693176L;
    private final String[] localPathNodes;
    private final String importTarget;
    private final boolean isNamespaceImport;

    public ImportStatement( ParsingContext context, String[] localPathNodes, String importTarget,
            boolean isNamespaceImport )
    {
        super( context );
        this.localPathNodes = localPathNodes;
        this.importTarget = importTarget;
        this.isNamespaceImport = isNamespaceImport;
    }

    public String[] localPathNodes()
    {
        return localPathNodes;
    }

    public String importTarget()
    {
        return importTarget;
    }

    public boolean isNamespaceImport()
    {
        return isNamespaceImport;
    }

    @Override
    public void accept( OLVisitor visitor )
    {
        visitor.visit( this );
    }

    @Override
    public String toString()
    {
        String importID = (this.isNamespaceImport) ? "*" : Arrays.toString( this.localPathNodes );
        return "import " + importID + " from '" + this.importTarget + "'";
    }

    @Override
    public boolean equals( Object o )
    {
        // compare ignore context
        if ( o == this ) {
            return true;
        }

        if ( !(o instanceof ImportStatement) ) {
            return false;
        }

        // typecast o to ImportStatement so that we can compare data members
        ImportStatement c = (ImportStatement) o;
        return c.isNamespaceImport == this.isNamespaceImport
                && c.importTarget.equals( this.importTarget )
                && Arrays.equals( c.localPathNodes, this.localPathNodes );
    }
}
