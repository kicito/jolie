
package jolie.lang.parse.ast;

import java.net.URI;
import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.context.ParsingContext;


public class ImportStatement extends OLSyntaxNode
{
    /**
    *
    */
    private static final long serialVersionUID = 5226504948641693176L;
    private final VariablePathNode[] localPathNodes;
    private final URI importTarget;
    private final boolean isNamespaceImport;

    public ImportStatement( ParsingContext context, VariablePathNode[] localPathNodes,
            URI importTarget, boolean isNamespaceImport )
    {
        super( context );
        this.localPathNodes = localPathNodes;
        this.importTarget = importTarget;
        this.isNamespaceImport = isNamespaceImport;
    }

    public VariablePathNode[] localPathNodes()
    {
        return localPathNodes;
    }

    public URI importTarget()
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
}
