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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.context.ParsingContext;
import jolie.util.Pair;



public class IfStatement extends OLSyntaxNode
{
	private final List< Pair< OLSyntaxNode, OLSyntaxNode > > children;
	private OLSyntaxNode elseProcess;

	public IfStatement( ParsingContext context )
	{
		super( context );
		children = new LinkedList<>();
		elseProcess = null;
	}
	
	public void setElseProcess( OLSyntaxNode elseProcess )
	{
		this.elseProcess = elseProcess;
	}
	
	public OLSyntaxNode elseProcess()
	{
		return elseProcess;
	}
	
	public List< Pair< OLSyntaxNode, OLSyntaxNode > > children()
	{
		return children;
	}
	
	public void addChild( Pair< OLSyntaxNode, OLSyntaxNode > node )
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
		StringBuilder sb = new StringBuilder();
		for (Pair< OLSyntaxNode, OLSyntaxNode > child : children) {
			sb.append( "if( " );
			sb.append( child.key() );
			sb.append( "){" );
			sb.append( child.value() );
			sb.append( "}" );
		}
		if ( this.elseProcess != null ) {
			sb.append( "else{" );
			sb.append( this.elseProcess );
			sb.append( "}" );
		}
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
		int result = 1;
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((elseProcess == null) ? 0 : elseProcess.hashCode());
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
		IfStatement other = (IfStatement) obj;
		if ( children == null ) {
			if ( other.children != null ) return false;
		} else if ( !children.equals( other.children ) ) return false;
		if ( elseProcess == null ) {
			if ( other.elseProcess != null ) return false;
		} else if ( !elseProcess.equals( other.elseProcess ) ) return false;
		return true;
	}
}
