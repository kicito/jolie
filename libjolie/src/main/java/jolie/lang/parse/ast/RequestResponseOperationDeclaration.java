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


import java.util.Map;
import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.ast.types.TypeDefinition;
import jolie.lang.parse.context.ParsingContext;


public class RequestResponseOperationDeclaration extends OperationDeclaration
{
	private final Map< String, TypeDefinition > faults;
	private final TypeDefinition requestType, responseType;

	public RequestResponseOperationDeclaration(
			ParsingContext context,
			String id,
			TypeDefinition requestType,
			TypeDefinition responseType,
			Map< String, TypeDefinition > faults
		)
	{
		super( context, id );
		this.requestType = requestType;
		this.responseType = responseType;
		this.faults = faults;
	}

	public TypeDefinition requestType()
	{
		return requestType;
	}

	public TypeDefinition responseType()
	{
		return responseType;
	}
	
	public Map< String, TypeDefinition > faults()
	{
		return faults;
	}
	
	@Override
	public void accept( OLVisitor visitor )
	{
		visitor.visit( this );
	}

	@Override
	public String toString()
	{
		return super.id() + "( " + this.requestType + " )" + "( " + this.responseType + " )";
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
		result = prime * result + ((faults == null) ? 0 : faults.hashCode());
		result = prime * result + ((requestType == null) ? 0 : requestType.hashCode());
		result = prime * result + ((responseType == null) ? 0 : responseType.hashCode());
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
		RequestResponseOperationDeclaration other = (RequestResponseOperationDeclaration) obj;
		if ( faults == null ) {
			if ( other.faults != null ) return false;
		} else if ( !faults.equals( other.faults ) ) return false;
		if ( requestType == null ) {
			if ( other.requestType != null ) return false;
		} else if ( !requestType.equals( other.requestType ) ) return false;
		if ( responseType == null ) {
			if ( other.responseType != null ) return false;
		} else if ( !responseType.equals( other.responseType ) ) return false;
		return true;
	}
}
