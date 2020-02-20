/***************************************************************************
 *   Copyright (C) 2008 by Fabrizio Montesi                                *
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

import java.util.HashMap;
import java.util.Map;
import jolie.lang.parse.DocumentedNode;
import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.ast.types.TypeDefinition;
import jolie.lang.parse.ast.types.TypeInlineDefinition;
import jolie.lang.parse.context.ParsingContext;
import jolie.lang.parse.module.Importable;
import jolie.lang.parse.util.ProgramInspector;

/**
 *
 * @author Fabrizio Montesi
 */
public class InterfaceDefinition extends OLSyntaxNode implements OperationCollector, DocumentedNode, Importable
{
	private final Map<String, OperationDeclaration> operationsMap =
		new HashMap<>();
	private final String name;
	private String documentation;

	public InterfaceDefinition( ParsingContext context, String name )
	{
		super( context );
		this.name = name;
	}

	@Override
	public Map< String, OperationDeclaration > operationsMap()
	{
		return operationsMap;
	}

	public String name()
	{
		return name;
	}

	@Override
	public void addOperation( OperationDeclaration decl )
	{
		operationsMap.put( decl.id(), decl );
	}

	public void copyTo( OperationCollector oc )
	{
		oc.operationsMap().putAll( operationsMap );
	}

	@Override
	public void accept( OLVisitor visitor )
	{
		visitor.visit( this );
	}

	@Override
	public void setDocumentation( String documentation )
	{
		this.documentation = documentation;
	}

	@Override
	public String getDocumentation()
	{
		return this.documentation;
	}

	@Override
	public InterfaceDefinition resolve( ParsingContext ctx, ProgramInspector pi, String localID )
	{
        InterfaceDefinition localIface = new InterfaceDefinition( ctx, localID );
		localIface.setDocumentation( this.getDocumentation() );
		this.operationsMap.values().forEach((op) -> localIface.addOperation(op));
        this.copyTo( localIface );
		return localIface;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( "INTERFACE:" + name + "{");
		this.operationsMap.entrySet().forEach((e)->{ sb.append("\n\t").append(e.getValue()); });
		sb.append( "\n}");
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
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((operationsMap == null) ? 0 : operationsMap.hashCode());
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
		InterfaceDefinition other = (InterfaceDefinition) obj;
		if ( name == null ) {
			if ( other.name != null ) return false;
		} else if ( !name.equals( other.name ) ) return false;
		if ( operationsMap == null ) {
			if ( other.operationsMap != null ) return false;
		} else if ( !operationsMap.equals( other.operationsMap ) ) return false;
		return true;
	}
}
