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

import jolie.lang.Constants;
import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.context.ParsingContext;

public class EmbeddedServiceNode extends OLSyntaxNode
{
	private final String servicePath;
	private final String portId;
	private final Constants.EmbeddedServiceType type;

	private Program program = null;

	public EmbeddedServiceNode(
			ParsingContext context,
			Constants.EmbeddedServiceType type,
			String servicePath,
			String portId )
	{
		super( context );
		this.type = type;
		this.servicePath = servicePath;
		this.portId = portId;
	}
	
	public Constants.EmbeddedServiceType type()
	{
		return type;
	}
	
	public String servicePath()
	{
		return servicePath;
	}
	
	public String portId()
	{
		return portId;
	}

	public void setProgram( Program program )
	{
		this.program = program;
	}

	public Program program()
	{
		return program;
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
		sb.append( "embedded {" );
		sb.append( this.type + ": " + "\"" + this.servicePath + "\"" );
		if ( this.portId != null ) {
			sb.append( "in: " + this.portId );
		}
		sb.append( "}" );
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
		result = prime * result + ((portId == null) ? 0 : portId.hashCode());
		result = prime * result + ((program == null) ? 0 : program.hashCode());
		result = prime * result + ((servicePath == null) ? 0 : servicePath.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		EmbeddedServiceNode other = (EmbeddedServiceNode) obj;
		if ( portId == null ) {
			if ( other.portId != null ) return false;
		} else if ( !portId.equals( other.portId ) ) return false;
		if ( program == null ) {
			if ( other.program != null ) return false;
		} else if ( !program.equals( other.program ) ) return false;
		if ( servicePath == null ) {
			if ( other.servicePath != null ) return false;
		} else if ( !servicePath.equals( other.servicePath ) ) return false;
		if ( type != other.type ) return false;
		return true;
	}
}
