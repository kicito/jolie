package jolie.lang.parse.ast.types;

import jolie.lang.Constants;
import jolie.lang.NativeType;
import jolie.lang.parse.ast.expression.ConstantStringExpression;
import jolie.lang.parse.ast.expression.InlineTreeExpressionNode;
import jolie.lang.parse.ast.expression.VoidExpressionNode;
import jolie.lang.parse.ast.expression.InlineTreeExpressionNode.Operation;
import jolie.lang.parse.context.ParsingContext;
import jolie.lang.parse.context.URIParsingContext;
import jolie.lang.parse.util.ProgramInspector;

public class TypeDefinitionPort extends TypeInlineDefinition
{
    public static final String PORT_KEYWORD = "portInfo";

    private static class LazyHolder
    {
        private LazyHolder()
        {
        }

        private final static TypeDefinitionPort instance = new TypeDefinitionPort();
    }

    private TypeDefinitionPort()
    {
        super( URIParsingContext.DEFAULT, TypeDefinitionPort.PORT_KEYWORD, NativeType.VOID,
                Constants.RANGE_ONE_TO_ONE );
        TypeDefinition protocol = new TypeInlineDefinition( URIParsingContext.DEFAULT, "protocol",
                NativeType.STRING, Constants.RANGE_ONE_TO_ONE );
        TypeDefinition location = new TypeInlineDefinition( URIParsingContext.DEFAULT, "location",
                NativeType.STRING, Constants.RANGE_ONE_TO_ONE );
        super.putSubType( protocol );
        super.putSubType( location );
        super.putSubType( TypeDefinitionInterface.getInstance() );
    }

    public static TypeDefinitionPort getInstance()
    {
        return LazyHolder.instance;
    }

    public static boolean isPortType( InlineTreeExpressionNode n )
    {
        if ( !(n.rootExpression() instanceof VoidExpressionNode) ){
            return false;
        }
        for (Operation o : n.operations()) {
            if( o instanceof InlineTreeExpressionNode.AssignmentOperation ){
                InlineTreeExpressionNode.AssignmentOperation assignment = (InlineTreeExpressionNode.AssignmentOperation)o;
                TypeInlineDefinition subType = (TypeInlineDefinition) LazyHolder.instance.getSubType(assignment.path().toString());
                if (!(assignment.expression() instanceof ConstantStringExpression && subType.nativeType() == NativeType.STRING)){
                    return false;
                }
            }else if ( o instanceof InlineTreeExpressionNode.DeepCopyOperation ){
                InlineTreeExpressionNode.DeepCopyOperation deepCopy = (InlineTreeExpressionNode.DeepCopyOperation)o;
                TypeInlineDefinition subType = (TypeInlineDefinition) LazyHolder.instance.getSubType(deepCopy.path().toString());

            }
            System.out.println("operation = " + o);
        }
        return true;
    }

    @Override
    public String toString()
    {
        return TypeDefinitionPort.PORT_KEYWORD;
    }

    @Override
    public boolean equals( Object obj )
    {
        return super.equals( obj );
    }
}
