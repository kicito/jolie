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


public class SpawnStatement extends OLSyntaxNode
{
	private final VariablePathNode indexVariablePath;
	private final VariablePathNode inVariablePath; // may be null
	private final OLSyntaxNode upperBoundExpression, body;

	public SpawnStatement(
			ParsingContext context,
			VariablePathNode indexVariablePath,
			OLSyntaxNode upperBoundExpression,
			VariablePathNode inVariablePath,
			OLSyntaxNode body
	)
	{
		super( context );
		this.indexVariablePath = indexVariablePath;
		this.inVariablePath = inVariablePath;
		this.upperBoundExpression = upperBoundExpression;
		this.body = body;
	}
	
	public OLSyntaxNode body()
	{
		return body;
	}

	public OLSyntaxNode upperBoundExpression()
	{
		return upperBoundExpression;
	}

	public VariablePathNode indexVariablePath()
	{
		return indexVariablePath;
	}
	
	public VariablePathNode inVariablePath()
	{
		return inVariablePath;
	}
	
	@Override
	public void accept( OLVisitor visitor )
	{
		visitor.visit( this );
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( "spawn" );
		sb.append( "( " + this.indexVariablePath + " over " + this.upperBoundExpression + " ) in "
				+ this.inVariablePath );
		sb.append( "{ " + this.body + " }" );
		return sb.toString();
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
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((inVariablePath == null) ? 0 : inVariablePath.hashCode());
		result = prime * result + ((indexVariablePath == null) ? 0 : indexVariablePath.hashCode());
		result = prime * result
				+ ((upperBoundExpression == null) ? 0 : upperBoundExpression.hashCode());
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
		SpawnStatement other = (SpawnStatement) obj;
		if ( body == null ) {
			if ( other.body != null ) return false;
		} else if ( !body.equals( other.body ) ) return false;
		if ( inVariablePath == null ) {
			if ( other.inVariablePath != null ) return false;
		} else if ( !inVariablePath.equals( other.inVariablePath ) ) return false;
		if ( indexVariablePath == null ) {
			if ( other.indexVariablePath != null ) return false;
		} else if ( !indexVariablePath.equals( other.indexVariablePath ) ) return false;
		if ( upperBoundExpression == null ) {
			if ( other.upperBoundExpression != null ) return false;
		} else if ( !upperBoundExpression.equals( other.upperBoundExpression ) ) return false;
		return true;
	}
}
