/***************************************************************************
 *   Copyright (C) 2015 by Fabrizio Montesi <famontesi@gmail.com>          *
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
import jolie.lang.parse.ast.types.TypeInlineDefinition;
import jolie.lang.parse.context.ParsingContext;

import java.io.Serializable;
import java.util.Arrays;


public class InlineTreeExpressionNode extends OLSyntaxNode
{
	public static interface Operation
	{
	}

	public static class AssignmentOperation implements Operation, Serializable
	{
		private final VariablePathNode path;
		private final OLSyntaxNode expression;

		public AssignmentOperation( VariablePathNode path, OLSyntaxNode expression )
		{
			this.path = path;
			this.expression = expression;
		}

		public VariablePathNode path()
		{
			return path;
		}

		public OLSyntaxNode expression()
		{
			return expression;
		}

		@Override
		public String toString()
		{
			return path.toString() + "=" + expression.toString();
		}
		
	}

	public static class DeepCopyOperation implements Operation, Serializable
	{
		private final VariablePathNode path;
		private final OLSyntaxNode expression;

		public DeepCopyOperation( VariablePathNode path, OLSyntaxNode expression )
		{
			this.path = path;
			this.expression = expression;
		}

		public VariablePathNode path()
		{
			return path;
		}

		public OLSyntaxNode expression()
		{
			return expression;
		}

		@Override
		public String toString()
		{
			return path.toString() + "<<" + expression.toString();
		}
	}

	public static class PointsToOperation implements Operation, Serializable
	{
		private final VariablePathNode path;
		private final VariablePathNode target;

		public PointsToOperation( VariablePathNode path, VariablePathNode target )
		{
			this.path = path;
			this.target = target;
		}

		public VariablePathNode path()
		{
			return path;
		}

		public VariablePathNode target()
		{
			return target;
		}

		@Override
		public String toString()
		{
			return path.toString() + "->" + target.toString();
		}
	}

	private final OLSyntaxNode rootExpression;
	private final Operation[] operations;

	public InlineTreeExpressionNode( ParsingContext context, OLSyntaxNode rootExpression,
			Operation[] operations )
	{
		super( context );
		this.rootExpression = rootExpression;
		this.operations = operations;
	}

	public OLSyntaxNode rootExpression()
	{
		return rootExpression;
	}

	public Operation[] operations()
	{
		return operations;
	}

	public boolean isType( TypeInlineDefinition td )
	{

		if ( (((NativeLiteral) this.rootExpression()).type() != td.nativeType()) ) {
			return false;
		}
		
		// TODO use better approach to test! this pass if tree only has some subtype of a type
        for (Operation o : this.operations()) {
            if( o instanceof AssignmentOperation ){
                AssignmentOperation assignment = (AssignmentOperation)o;
				String typeKey = ((ConstantStringExpression)assignment.path().path().get(0).key()).value();
				TypeInlineDefinition subType = (TypeInlineDefinition) td.getSubType(typeKey);
				
				if ( assignment.expression() instanceof NativeLiteral){
					NativeLiteral literal = (NativeLiteral)assignment.expression();
					if (literal.type() != subType.nativeType()){
						return false;
					}
				} else {
					return false;
				}
            }else if ( o instanceof DeepCopyOperation ){
				DeepCopyOperation deepCopy = (DeepCopyOperation)o;
				String typeKey = ((ConstantStringExpression)deepCopy.path().path().get(0).key()).value();
				TypeInlineDefinition subType = (TypeInlineDefinition) td.getSubType(typeKey);

				if ( deepCopy.expression() instanceof InlineTreeExpressionNode){
					InlineTreeExpressionNode inLineTree = (InlineTreeExpressionNode)deepCopy.expression();
					if (!inLineTree.isType(subType)){
						return false;
					}
				} else {
					return false;
				}
            } else{
				return false;
			}
        }

		return true;
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
		for (Operation o : operations){
			sb.append(o);
			sb.append(' ');
		}
		sb.delete(sb.length()-1, sb.length());

		return rootExpression.toString() + "{" + sb + "}";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode( operations );
		result = prime * result + ((rootExpression == null) ? 0 : rootExpression.hashCode());
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj ) return true;
		if ( obj == null ) return false;
		if ( getClass() != obj.getClass() ) return false;
		InlineTreeExpressionNode other = (InlineTreeExpressionNode) obj;
		if ( !Arrays.equals( operations, other.operations ) ) return false;
		if ( rootExpression == null ) {
			if ( other.rootExpression != null ) return false;
		} else if ( !rootExpression.equals( other.rootExpression ) ) return false;
		return true;
	}
}
