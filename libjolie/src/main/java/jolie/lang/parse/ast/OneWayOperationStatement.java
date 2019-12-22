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


public class OneWayOperationStatement extends OLSyntaxNode
{
	private final VariablePathNode inputVarPath;
	private final String id;
	
	public OneWayOperationStatement( ParsingContext context, String id, VariablePathNode inputVarPath )
	{
		super( context );
		this.id = id;
		this.inputVarPath = inputVarPath;
	}
	
	public String id()
	{
		return id;
	}
	
	public VariablePathNode inputVarPath()
	{
		return inputVarPath;
	}
	
	@Override
	public void accept( OLVisitor visitor )
	{
		visitor.visit( this );
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((inputVarPath == null) ? 0 : inputVarPath.hashCode());
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
		OneWayOperationStatement other = (OneWayOperationStatement) obj;
		if ( id == null ) {
			if ( other.id != null ) return false;
		} else if ( !id.equals( other.id ) ) return false;
		if ( inputVarPath == null ) {
			if ( other.inputVarPath != null ) return false;
		} else if ( !inputVarPath.equals( other.inputVarPath ) ) return false;
		return true;
	}
}
