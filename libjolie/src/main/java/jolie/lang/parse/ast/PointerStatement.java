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


public class PointerStatement extends OLSyntaxNode
{
	private final VariablePathNode leftPath, rightPath;

	public PointerStatement( ParsingContext context, VariablePathNode leftPath, VariablePathNode rightPath )
	{
		super( context );
		VariablePathNode.levelPaths( leftPath, rightPath );
		this.leftPath = leftPath;
		this.rightPath = rightPath;
	}
	
	public VariablePathNode leftPath()
	{
		return leftPath;
	}
	
	public VariablePathNode rightPath()
	{
		return rightPath;
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
		result = prime * result + ((leftPath == null) ? 0 : leftPath.hashCode());
		result = prime * result + ((rightPath == null) ? 0 : rightPath.hashCode());
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
		PointerStatement other = (PointerStatement) obj;
		if ( leftPath == null ) {
			if ( other.leftPath != null ) return false;
		} else if ( !leftPath.equals( other.leftPath ) ) return false;
		if ( rightPath == null ) {
			if ( other.rightPath != null ) return false;
		} else if ( !rightPath.equals( other.rightPath ) ) return false;
		return true;
	}
}
