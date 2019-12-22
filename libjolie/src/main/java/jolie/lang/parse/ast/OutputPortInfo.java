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

import java.net.URI;

import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.context.ParsingContext;

public class OutputPortInfo extends PortInfo
{	
	private String protocolId = null;
	private OLSyntaxNode protocolConfiguration = null;
	private URI location = null;
	
	public OutputPortInfo( ParsingContext context, String id )
	{
		super( context, id );
	}
	
	public void setProtocolId( String protocolId )
	{
		this.protocolId = protocolId;
	}
	
	public void setProtocolConfiguration( OLSyntaxNode protocolConfiguration )
	{
		this.protocolConfiguration = protocolConfiguration;
	}
	
	public void setLocation( URI location )
	{
		this.location = location;
	}
	
	@Override
	public void accept( OLVisitor visitor )
	{
		visitor.visit( this );
	}
	
	public String protocolId()
	{
		return protocolId;
	}
	
	public OLSyntaxNode protocolConfiguration()
	{
		return protocolConfiguration;
	}
	
	public URI location()
	{
		return location;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("outputPort " + super.id());
		sb.append("{");
		sb.append("location: " + this.location);
		sb.append("protocol: " + this.protocolId);
		sb.append("config: " + this.protocolConfiguration);
		sb.append("}");
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
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result
				+ ((protocolConfiguration == null) ? 0 : protocolConfiguration.hashCode());
		result = prime * result + ((protocolId == null) ? 0 : protocolId.hashCode());
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
		OutputPortInfo other = (OutputPortInfo) obj;
		if ( location == null ) {
			if ( other.location != null ) return false;
		} else if ( !location.equals( other.location ) ) return false;
		if ( protocolConfiguration == null ) {
			if ( other.protocolConfiguration != null ) return false;
		} else if ( !protocolConfiguration.equals( other.protocolConfiguration ) ) return false;
		if ( protocolId == null ) {
			if ( other.protocolId != null ) return false;
		} else if ( !protocolId.equals( other.protocolId ) ) return false;
		return true;
	}
}
