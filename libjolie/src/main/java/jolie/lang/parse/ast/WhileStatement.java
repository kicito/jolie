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


public class WhileStatement extends OLSyntaxNode
{
	private final OLSyntaxNode condition, body;

	public WhileStatement( ParsingContext context, OLSyntaxNode condition, OLSyntaxNode body )
	{
		super( context );
		this.condition = condition;
		this.body = body;
	}

	public OLSyntaxNode condition()
	{
		return condition;
	}

	public OLSyntaxNode body()
	{
		return body;
	}

	@Override
	public void accept( OLVisitor visitor )
	{
		visitor.visit( this );
	}

	@Override
	public String toString()
	{
		return "while(" + this.condition + "){ " + this.body + "}";
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
		result = prime * result + ((condition == null) ? 0 : condition.hashCode());
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
		WhileStatement other = (WhileStatement) obj;
		if ( body == null ) {
			if ( other.body != null ) return false;
		} else if ( !body.equals( other.body ) ) return false;
		if ( condition == null ) {
			if ( other.condition != null ) return false;
		} else if ( !condition.equals( other.condition ) ) return false;
		return true;
	}
}
