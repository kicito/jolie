package jolie.lang.parse.ast;

import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.context.ParsingContext;

/**
 * @author Karoly Szanto
 */
public class SubtractAssignStatement extends OLSyntaxNode
{
	private final VariablePathNode variablePath;
	private final OLSyntaxNode expression;

	public SubtractAssignStatement( ParsingContext context, VariablePathNode path, OLSyntaxNode expression )
	{
		super( context );
		this.variablePath = path;
		this.expression = expression;
	}

	public VariablePathNode variablePath()
	{
		return variablePath;
	}

	public OLSyntaxNode expression()
	{
		return expression;
	}

	@Override
	public void accept( OLVisitor visitor )
	{
		visitor.visit( this );
	}

	@Override
	public String toString()
	{
		return this.variablePath + " -= " + this.expression;
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
		int result = super.hashCode();
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + ((variablePath == null) ? 0 : variablePath.hashCode());
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
		if ( !super.equals( obj ) ) return false;
		if ( getClass() != obj.getClass() ) return false;
		SubtractAssignStatement other = (SubtractAssignStatement) obj;
		if ( expression == null ) {
			if ( other.expression != null ) return false;
		} else if ( !expression.equals( other.expression ) ) return false;
		if ( variablePath == null ) {
			if ( other.variablePath != null ) return false;
		} else if ( !variablePath.equals( other.variablePath ) ) return false;
		return true;
	}
}
