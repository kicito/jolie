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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.context.ParsingContext;



public class SequenceStatement extends OLSyntaxNode
{
	private final List< OLSyntaxNode > children;

	public SequenceStatement( ParsingContext context )
	{
		super( context );
		children = new ArrayList<>();
	}
	
	public List< OLSyntaxNode > children()
	{
		return children;
	}
	
	public void addChild( OLSyntaxNode node )
	{
		children.add( node );
	}
	
	@Override
	public void accept( OLVisitor visitor )
	{
		visitor.visit( this );
	}

	@Override
	public String toString()
	{
		if (children.size() == 1){
			return children.get(0).toString();
		}
		String[] stringArray = Arrays.copyOf(children.toArray(), children.size(), String[].class);
		return String.join(";", stringArray);
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
		result = prime * result + ((children == null) ? 0 : children.hashCode());
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
		SequenceStatement other = (SequenceStatement) obj;
		if ( children == null ) {
			if ( other.children != null ) return false;
		} else if ( !children.equals( other.children ) ) return false;
		return true;
	}
}
