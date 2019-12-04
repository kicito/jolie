
package jolie.lang.parse.ast;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.context.ParsingContext;
import jolie.util.Pair;


public class ImportStatement extends OLSyntaxNode
{
    /**
    *
    */
    private static final long serialVersionUID = 5226504948641693176L;
    private final List< Pair< String, String > > pathNodes; // <target_id, local_id>
    private final String importTarget;
    private final boolean isNamespaceImport;

    // for namespace import
    public ImportStatement( ParsingContext context, String importTarget )
    {
        this( context, importTarget, true, null );
    }

    // for id import
    public ImportStatement( ParsingContext context, String importTarget,
            List< Pair< String, String > > pathNodes )
    {
        this( context, importTarget, false, pathNodes );
    }

    public ImportStatement( ParsingContext context, String importTarget, boolean isNamespaceImport,
            List< Pair< String, String > > pathNodes )
    {
        super( context );
        this.pathNodes = pathNodes;
        this.importTarget = importTarget;
        this.isNamespaceImport = isNamespaceImport;
    }

    public List< Pair< String, String > > pathNodes()
    {
        return pathNodes;
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
        String importID =
                (this.isNamespaceImport) ? "*" : Arrays.toString( this.pathNodes.toArray() );
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
                && ((c.pathNodes == null && this.pathNodes == null) || (c.pathNodes != null
                        && this.pathNodes != null && c.pathNodes.equals( this.pathNodes )));
    }
}
