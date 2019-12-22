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
import jolie.lang.parse.context.ParsingContext;
import jolie.lang.parse.Scanner;


public class CompareConditionNode extends OLSyntaxNode
{
	private final OLSyntaxNode leftExpression, rightExpression;
	private final Scanner.TokenType opType;

	public CompareConditionNode( ParsingContext context, OLSyntaxNode leftExpr, OLSyntaxNode rightExpr, Scanner.TokenType opType )
	{
		super( context );
		this.leftExpression = leftExpr;
		this.rightExpression = rightExpr;
		this.opType = opType;
	}
	
	public OLSyntaxNode leftExpression()
	{
		return leftExpression;
	}
	
	public OLSyntaxNode rightExpression()
	{
		return rightExpression;
	}
	
	public Scanner.TokenType opType()
	{
		return opType;
	}
	
	@Override
	public void accept( OLVisitor visitor )
	{
		visitor.visit( this );
	}

	@Override
	public String toString()
	{
		return this.leftExpression + " " + this.opType + " " + this.rightExpression;
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
		result = prime * result + ((leftExpression == null) ? 0 : leftExpression.hashCode());
		result = prime * result + ((opType == null) ? 0 : opType.hashCode());
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
		CompareConditionNode other = (CompareConditionNode) obj;
		if ( leftExpression == null ) {
			if ( other.leftExpression != null ) return false;
		} else if ( !leftExpression.equals( other.leftExpression ) ) return false;
		if ( opType != other.opType ) return false;
		if ( rightExpression == null ) {
			if ( other.rightExpression != null ) return false;
		} else if ( !rightExpression.equals( other.rightExpression ) ) return false;
		return true;
	}
}
