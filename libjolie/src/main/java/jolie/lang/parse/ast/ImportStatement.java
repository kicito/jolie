
package jolie.lang.parse.ast;

import java.util.Arrays;
import java.util.HashMap;
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

    public static enum IDType {
        TYPE, INTERFACE, UNDEFINED
    }


    private static final long serialVersionUID = 5226504948641693176L;
    private final List< Pair< String, String > > pathNodes; // <target_id, local_id>
    private final Map< String, IDType > expectedIDTypeMap;// <local_id, expectedType>
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
        this.expectedIDTypeMap = new HashMap< String, IDType >();
        if (pathNodes != null){
            for (Pair< String, String > node : pathNodes) {
                this.expectedIDTypeMap.put( node.value(), IDType.UNDEFINED );
            }
        }
    }

    public void setExpectedType( String id, IDType type )
    {
        this.expectedIDTypeMap.put( id, type );
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

    public Map< String, IDType > expectedIDTypeMap()
    {
        return expectedIDTypeMap;
    }

    @Override
    public String toString()
    {
        String importID =
                (this.isNamespaceImport) ? "*" : Arrays.toString( this.pathNodes.toArray() );
        return "import " + importID + " from '" + this.importTarget + "'";
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expectedIDTypeMap == null) ? 0 : expectedIDTypeMap.hashCode());
        result = prime * result + ((importTarget == null) ? 0 : importTarget.hashCode());
        result = prime * result + (isNamespaceImport ? 1231 : 1237);
        result = prime * result + ((pathNodes == null) ? 0 : pathNodes.hashCode());
        return result;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        ImportStatement other = (ImportStatement) obj;
        if ( expectedIDTypeMap == null ) {
            if ( other.expectedIDTypeMap != null ) return false;
        } else if ( !expectedIDTypeMap.equals( other.expectedIDTypeMap ) ) return false;
        if ( importTarget == null ) {
            if ( other.importTarget != null ) return false;
        } else if ( !importTarget.equals( other.importTarget ) ) return false;
        if ( isNamespaceImport != other.isNamespaceImport ) return false;
        if ( pathNodes == null ) {
            if ( other.pathNodes != null ) return false;
        } else if ( !pathNodes.equals( other.pathNodes ) ) return false;
        return true;
    }

    // ImportStatement is resolved at OLParser.parse(), thus this function is unused.
    @Override
    public void accept( OLVisitor visitor )
    { }
}
