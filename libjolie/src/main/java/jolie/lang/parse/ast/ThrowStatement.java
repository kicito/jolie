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


public class ThrowStatement extends OLSyntaxNode
{
	private final String id;
	private final OLSyntaxNode expression;

	public ThrowStatement( ParsingContext context, String id )
	{
		super( context );
		this.id = id;
		this.expression = null;
	}
	
	public ThrowStatement( ParsingContext context, String id, OLSyntaxNode expression )
	{
		super( context );
		this.id = id;
		this.expression = expression;
	}
	
	public OLSyntaxNode expression()
	{
		return expression;
	}
	
	public String id()
	{
		return id;
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
		sb.append("throw(" + this.id);
		if(this.expression != null){
			sb.append(", "+ this.expression);
		}
		sb.append(")");
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
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		ThrowStatement other = (ThrowStatement) obj;
		if ( expression == null ) {
			if ( other.expression != null ) return false;
		} else if ( !expression.equals( other.expression ) ) return false;
		if ( id == null ) {
			if ( other.id != null ) return false;
		} else if ( !id.equals( other.id ) ) return false;
		return true;
	}
}
