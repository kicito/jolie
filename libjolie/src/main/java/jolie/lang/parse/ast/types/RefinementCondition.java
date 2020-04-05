package jolie.lang.parse.ast.types;

import java.util.Arrays;
import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.ast.OLSyntaxNode;
import jolie.lang.parse.context.ParsingContext;

public class RefinementCondition extends OLSyntaxNode
{
    private String name;
    private OLSyntaxNode[] arguments;

    public RefinementCondition( ParsingContext ctx, String name, OLSyntaxNode[] arguments )
    {
        super( ctx );
        this.name = name;
        this.arguments = arguments;
    }

    public String name()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public OLSyntaxNode[] arguments()
    {
        return arguments;
    }

    public void setArguments( OLSyntaxNode[] arguments )
    {
        this.arguments = arguments;
    }

    @Override
    public String toString()
    {
        return name + "(" + Arrays.toString( this.arguments ) + ")";
    }

    @Override
    public void accept( OLVisitor visitor )
    {
        visitor.visit( this );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Arrays.hashCode( arguments );
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj ) return true;
        if ( !super.equals( obj ) ) return false;
        if ( getClass() != obj.getClass() ) return false;
        RefinementCondition other = (RefinementCondition) obj;
        if ( !Arrays.equals( arguments, other.arguments ) ) return false;
        if ( name == null ) {
            if ( other.name != null ) return false;
        } else if ( !name.equals( other.name ) ) return false;
        return true;
    }

}
