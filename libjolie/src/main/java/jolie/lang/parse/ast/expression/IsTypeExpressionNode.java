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

package jolie.lang.parse.ast.expression;

import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.ast.OLSyntaxNode;
import jolie.lang.parse.ast.VariablePathNode;
import jolie.lang.parse.context.ParsingContext;


public class IsTypeExpressionNode extends OLSyntaxNode
{
	public enum CheckType {
		DEFINED,
		INT,
		STRING,
		DOUBLE,
		LONG,
		BOOL
	}
	
	private final VariablePathNode variablePath;
	private final CheckType type;

	public IsTypeExpressionNode( ParsingContext context, CheckType type, VariablePathNode variablePath )
	{
		super( context );
		this.type = type;
		this.variablePath = variablePath;
	}
	
	public CheckType type()
	{
		return type;
	}
	
	public VariablePathNode variablePath()
	{
		return variablePath;
	}
	
	@Override
	public void accept( OLVisitor visitor )
	{
		visitor.visit( this );
	}

	@Override
	public String toString()
	{
		return "is_"+this.type.toString().toLowerCase() + "( " + this.variablePath + " )";
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
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if ( obj == null ) return false;
		if ( getClass() != obj.getClass() ) return false;
		IsTypeExpressionNode other = (IsTypeExpressionNode) obj;
		if ( type != other.type ) return false;
		if ( variablePath == null ) {
			if ( other.variablePath != null ) return false;
		} else if ( !variablePath.equals( other.variablePath ) ) return false;
		return true;
	}
}
