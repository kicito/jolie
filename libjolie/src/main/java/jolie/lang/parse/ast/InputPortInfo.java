/***************************************************************************
 *   Copyright (C) 2007-2011 by Fabrizio Montesi <famontesi@gmail.com>     *
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

import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.context.ParsingContext;

public class InputPortInfo extends PortInfo
{
	public static class AggregationItemInfo implements Serializable
	{
		private final String[] outputPortList;
		private final InterfaceExtenderDefinition interfaceExtender;

		public AggregationItemInfo( String[] outputPortList, InterfaceExtenderDefinition extender )
		{
			this.outputPortList = outputPortList;
			this.interfaceExtender = extender;
		}

		public String[] outputPortList()
		{
			return outputPortList;
		}

		public InterfaceExtenderDefinition interfaceExtender()
		{
			return interfaceExtender;
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
			result = prime * result
					+ ((interfaceExtender == null) ? 0 : interfaceExtender.hashCode());
			result = prime * result + Arrays.hashCode( outputPortList );
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
			AggregationItemInfo other = (AggregationItemInfo) obj;
			if ( interfaceExtender == null ) {
				if ( other.interfaceExtender != null ) return false;
			} else if ( !interfaceExtender.equals( other.interfaceExtender ) ) return false;
			if ( !Arrays.equals( outputPortList, other.outputPortList ) ) return false;
			return true;
		}
	}

	protected URI location;
	protected String protocolId;
	protected OLSyntaxNode protocolConfiguration;
	protected final AggregationItemInfo[] aggregationList;
	protected Map< String, String > redirectionMap;

	public InputPortInfo( ParsingContext context, String id, URI location, String protocolId,
			OLSyntaxNode protocolConfiguration, AggregationItemInfo[] aggregationList,
			Map< String, String > redirectionMap )
	{
		super( context, id );
		this.location = location;
		this.protocolId = protocolId;
		this.protocolConfiguration = protocolConfiguration;
		this.aggregationList = aggregationList;
		this.redirectionMap = redirectionMap;
	}

	public AggregationItemInfo[] aggregationList()
	{
		return aggregationList;
	}

	public Map< String, String > redirectionMap()
	{
		return redirectionMap;
	}

	public OLSyntaxNode protocolConfiguration()
	{
		return protocolConfiguration;
	}

	public String protocolId()
	{
		return protocolId;
	}

	public URI location()
	{
		return location;
	}

	public void bindLocationAndProtocol( OutputPortInfo op, boolean force )
	{
		if ( force ) {
			this.protocolId = op.protocolId();
			if ( op.protocolConfiguration() == null ) {
				this.protocolConfiguration = new NullProcessStatement( this.context() );
			} else {
				this.protocolConfiguration = op.protocolConfiguration();
			}
			this.protocolId = op.protocolId();
			this.location = op.location();
		} else {
			if ( this.protocolId == null ) {
				this.protocolId = op.protocolId();
			}
			if ( this.protocolConfiguration == null ) {
				this.protocolConfiguration = op.protocolConfiguration();
			}
			if ( this.protocolId == null ) {
				this.protocolId = op.protocolId();
			}
			if ( this.location == null ) {
				this.location = op.location();
			}
		}
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
		sb.append( "inputPort " + super.id() );
		sb.append( "{" );
		sb.append( "location: " + this.location );
		sb.append( "protocol: " + this.protocolId );
		sb.append( "config: " + this.protocolConfiguration );
		sb.append( "}" );
		// TODO agregration and redirection
		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode( aggregationList );
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result
				+ ((protocolConfiguration == null) ? 0 : protocolConfiguration.hashCode());
		result = prime * result + ((protocolId == null) ? 0 : protocolId.hashCode());
		result = prime * result + ((redirectionMap == null) ? 0 : redirectionMap.hashCode());
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj ) return true;
		if ( obj == null ) return false;
		if ( getClass() != obj.getClass() ) return false;
		InputPortInfo other = (InputPortInfo) obj;
		if ( !Arrays.equals( aggregationList, other.aggregationList ) ) return false;
		if ( location == null ) {
			if ( other.location != null ) return false;
		} else if ( !location.equals( other.location ) ) return false;
		if ( protocolConfiguration == null ) {
			if ( other.protocolConfiguration != null ) return false;
		} else if ( !protocolConfiguration.equals( other.protocolConfiguration ) ) return false;
		if ( protocolId == null ) {
			if ( other.protocolId != null ) return false;
		} else if ( !protocolId.equals( other.protocolId ) ) return false;
		if ( redirectionMap == null ) {
			if ( other.redirectionMap != null ) return false;
		} else if ( !redirectionMap.equals( other.redirectionMap ) ) return false;
		return true;
	}
}
