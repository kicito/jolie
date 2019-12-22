/***************************************************************************
 *   Copyright (C) by Fabrizio Montesi                                     *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Library General Public License as       *
 *   published by the Free Software Foundation; either version 2 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU Library General Public     *
 *   License along with this program; if not, write to the                 *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 *                                                                         *
 *   For details about the authors of this software, see the AUTHORS file. *
 ***************************************************************************/

package jolie.lang.parse.ast;

import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.ast.expression.VariableExpressionNode;
import jolie.lang.parse.context.ParsingContext;


public class DeepCopyStatement extends OLSyntaxNode
{
	private final VariablePathNode leftPath;
	private final OLSyntaxNode rightExpression;
	private final boolean copyLinks;

	public DeepCopyStatement( ParsingContext context, VariablePathNode leftPath, OLSyntaxNode rightExpression, boolean copyLinks )
	{
		super( context );
		if ( rightExpression instanceof VariableExpressionNode ) {
			VariablePathNode.levelPaths( leftPath, ((VariableExpressionNode) rightExpression).variablePath() );
		}
		this.leftPath = leftPath;
		this.rightExpression = rightExpression;
		this.copyLinks = copyLinks;
	}
	
	public VariablePathNode leftPath()
	{
		return leftPath;
	}
	
	public OLSyntaxNode rightExpression()
	{
		return rightExpression;
	}
	
	public boolean copyLinks()
	{
		return copyLinks;
	}
	
	@Override
	public void accept( OLVisitor visitor )
	{
		visitor.visit( this );
	}
	
	@Override
	public String toString()
	{
		String sp = "<<";
		if ( this.copyLinks ) {
			sp += "-";
		}
		return this.leftPath + " " + sp + " " + this.rightExpression;
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
		result = prime * result + (copyLinks ? 1231 : 1237);
		result = prime * result + ((leftPath == null) ? 0 : leftPath.hashCode());
		result = prime * result + ((rightExpression == null) ? 0 : rightExpression.hashCode());
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
		DeepCopyStatement other = (DeepCopyStatement) obj;
		if ( copyLinks != other.copyLinks ) return false;
		if ( leftPath == null ) {
			if ( other.leftPath != null ) return false;
		} else if ( !leftPath.equals( other.leftPath ) ) return false;
		if ( rightExpression == null ) {
			if ( other.rightExpression != null ) return false;
		} else if ( !rightExpression.equals( other.rightExpression ) ) return false;
		return true;
	}
}
