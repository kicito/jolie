package jolie.lang.parse;

import jolie.lang.Constants;
import jolie.lang.parse.ast.AddAssignStatement;
import jolie.lang.parse.ast.AssignStatement;
import jolie.lang.parse.ast.CompareConditionNode;
import jolie.lang.parse.ast.CompensateStatement;
import jolie.lang.parse.ast.CorrelationSetInfo;
import jolie.lang.parse.ast.CurrentHandlerStatement;
import jolie.lang.parse.ast.DeepCopyStatement;
import jolie.lang.parse.ast.DefinitionCallStatement;
import jolie.lang.parse.ast.DefinitionNode;
import jolie.lang.parse.ast.DivideAssignStatement;
import jolie.lang.parse.ast.DocumentationComment;
import jolie.lang.parse.ast.EmbeddedServiceNode;
import jolie.lang.parse.ast.ExecutionInfo;
import jolie.lang.parse.ast.ExitStatement;
import jolie.lang.parse.ast.ForEachArrayItemStatement;
import jolie.lang.parse.ast.ForEachSubNodeStatement;
import jolie.lang.parse.ast.ForStatement;
import jolie.lang.parse.ast.IfStatement;
import jolie.lang.parse.ast.ImportStatement;
import jolie.lang.parse.ast.InputPortInfo;
import jolie.lang.parse.ast.InstallFixedVariableExpressionNode;
import jolie.lang.parse.ast.InstallStatement;
import jolie.lang.parse.ast.InterfaceDefinition;
import jolie.lang.parse.ast.InterfaceExtenderDefinition;
import jolie.lang.parse.ast.LinkInStatement;
import jolie.lang.parse.ast.LinkOutStatement;
import jolie.lang.parse.ast.MultiplyAssignStatement;
import jolie.lang.parse.ast.NDChoiceStatement;
import jolie.lang.parse.ast.NotificationOperationStatement;
import jolie.lang.parse.ast.NullProcessStatement;
import jolie.lang.parse.ast.OneWayOperationDeclaration;
import jolie.lang.parse.ast.OneWayOperationStatement;
import jolie.lang.parse.ast.OutputPortInfo;
import jolie.lang.parse.ast.ParallelStatement;
import jolie.lang.parse.ast.PointerStatement;
import jolie.lang.parse.ast.PostDecrementStatement;
import jolie.lang.parse.ast.PostIncrementStatement;
import jolie.lang.parse.ast.PreDecrementStatement;
import jolie.lang.parse.ast.PreIncrementStatement;
import jolie.lang.parse.ast.Program;
import jolie.lang.parse.ast.ProvideUntilStatement;
import jolie.lang.parse.ast.RequestResponseOperationDeclaration;
import jolie.lang.parse.ast.RequestResponseOperationStatement;
import jolie.lang.parse.ast.RunStatement;
import jolie.lang.parse.ast.Scope;
import jolie.lang.parse.ast.SequenceStatement;
import jolie.lang.parse.ast.SolicitResponseOperationStatement;
import jolie.lang.parse.ast.SpawnStatement;
import jolie.lang.parse.ast.SubtractAssignStatement;
import jolie.lang.parse.ast.SynchronizedStatement;
import jolie.lang.parse.ast.ThrowStatement;
import jolie.lang.parse.ast.TypeCastExpressionNode;
import jolie.lang.parse.ast.UndefStatement;
import jolie.lang.parse.ast.ValueVectorSizeExpressionNode;
import jolie.lang.parse.ast.VariablePathNode;
import jolie.lang.parse.ast.WhileStatement;
import jolie.lang.parse.ast.courier.CourierChoiceStatement;
import jolie.lang.parse.ast.courier.CourierDefinitionNode;
import jolie.lang.parse.ast.courier.NotificationForwardStatement;
import jolie.lang.parse.ast.courier.SolicitResponseForwardStatement;
import jolie.lang.parse.ast.expression.AndConditionNode;
import jolie.lang.parse.ast.expression.ConstantBoolExpression;
import jolie.lang.parse.ast.expression.ConstantDoubleExpression;
import jolie.lang.parse.ast.expression.ConstantIntegerExpression;
import jolie.lang.parse.ast.expression.ConstantLongExpression;
import jolie.lang.parse.ast.expression.ConstantStringExpression;
import jolie.lang.parse.ast.expression.FreshValueExpressionNode;
import jolie.lang.parse.ast.expression.InlineTreeExpressionNode;
import jolie.lang.parse.ast.expression.InstanceOfExpressionNode;
import jolie.lang.parse.ast.expression.IsTypeExpressionNode;
import jolie.lang.parse.ast.expression.NotExpressionNode;
import jolie.lang.parse.ast.expression.OrConditionNode;
import jolie.lang.parse.ast.expression.ProductExpressionNode;
import jolie.lang.parse.ast.expression.SumExpressionNode;
import jolie.lang.parse.ast.expression.VariableExpressionNode;
import jolie.lang.parse.ast.expression.VoidExpressionNode;
import jolie.lang.parse.ast.types.TypeChoiceDefinition;
import jolie.lang.parse.ast.types.TypeDefinitionImport;
import jolie.lang.parse.ast.types.TypeDefinitionLink;
import jolie.lang.parse.ast.types.TypeInlineDefinition;
import jolie.lang.parse.ast.OLSyntaxNode;
import jolie.util.Pair;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Field;

class InspectorVisitor implements OLVisitor
{
    private boolean isFound = false;
    private OLSyntaxNode target;
    private Class< ? > targetClass;
    private Map< Class< ? >, Boolean > inpectLog;

    /**
     * getInstanceNestedOLSyntaxNode traverse through fields of receiving parameter
     * using reflection, returns all OLSyntax node associate to it
     * 
     * @param object
     * @return List< OLSyntaxNode >
     */
    private static List< OLSyntaxNode > getInstanceNestedOLSyntaxNode( OLSyntaxNode object )
    {
        Class< ? > cls = object.getClass();
        List< OLSyntaxNode > ret = new ArrayList<>();
        for (Field field : cls.getDeclaredFields()) {
            field.setAccessible( true );
            Class c = field.getType();
            try {
                if ( OLSyntaxNode.class.isAssignableFrom( c ) ) {
                    if ( field.get( object ) != null ) {
                        ret.add( (OLSyntaxNode) field.get( object ) );
                    }
                } else if ( c == List.class ) {
                    if ( field.get( object ) == null ) {
                        continue;
                    }
                    List< ? > list = (List< ? >) field.get( object );
                    if ( list.size() > 0 ) {
                        if ( list.get( 0 ) instanceof OLSyntaxNode ) {
                            ret.addAll( (List< OLSyntaxNode >) list );
                        }
                        if ( list.get( 0 ) instanceof Pair ) {
                            for (Pair p : (List< Pair >) list) {
                                if ( p.key() instanceof OLSyntaxNode ) {
                                    ret.add( (OLSyntaxNode) p.key() );
                                }
                                if ( p.value() instanceof OLSyntaxNode ) {
                                    ret.add( (OLSyntaxNode) p.value() );
                                }
                            }
                        }
                    }
                } else if ( c == Map.class ) {
                    if ( field.get( object ) == null ) {
                        continue;
                    }
                    Map< Object, Object > m = (Map) field.get( object );
                    for (Map.Entry< Object, Object > entry : m.entrySet()) {
                        if ( entry.getKey() instanceof OLSyntaxNode ) {
                            ret.add( (OLSyntaxNode) entry.getKey() );
                        }
                        if ( entry.getValue() instanceof OLSyntaxNode ) {
                            ret.add( (OLSyntaxNode) entry.getValue() );
                        }
                    }
                } else if ( c == Pair.class ) {
                    if ( field.get( object ) == null ) {
                        continue;
                    }
                    Pair p = (Pair) field.get( object );
                    if ( p.key() instanceof OLSyntaxNode ) {
                        ret.add( (OLSyntaxNode) p.key() );
                    }
                    if ( p.value() instanceof OLSyntaxNode ) {
                        ret.add( (OLSyntaxNode) p.value() );
                    }
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * removeNodeContext set context field of the receiving parameter to null, include it's
     * children/ operand or body
     */
    public static boolean removeNodeContext( OLSyntaxNode n )
    {
        try {
            List< OLSyntaxNode > children = getInstanceNestedOLSyntaxNode( n );
            if ( children != null ) {
                for (OLSyntaxNode child : children) {
                    removeNodeContext( child );
                }
            }
            Class< ? > cls = n.getClass();
            while (cls != null && cls != OLSyntaxNode.class) {
                cls = cls.getSuperclass();
            }
            final Field field = cls.getDeclaredField( "context" );
            field.setAccessible( true );
            field.set( n, null );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean programHasOLSyntaxNode( Program p, OLSyntaxNode node )
    {
        this.target = node;
        this.targetClass = node.getClass();
        // TODO make this method accept array
        this.inpectLog = new HashMap<>();
        this.visit( p );
        return isFound;
    }

    @Override
    public void visit( Program n )
    {
        for (OLSyntaxNode node : n.children()) {
            InspectorVisitor.removeNodeContext( node );
            if ( isFound ) {
                return;
            }
            node.accept( this );
        }
    }

    @Override
    public void visit( OneWayOperationDeclaration n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( RequestResponseOperationDeclaration n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( DefinitionNode n )
    {
        isFound = n.equals( target );
        if ( isFound ) {
            return;
        }
        n.body().accept( this );
    }

    @Override
    public void visit( ParallelStatement n )
    {
        isFound = n.equals( target );
        if ( isFound ) {
            return;
        }
        for (OLSyntaxNode child : n.children()) {
            child.accept( this );
        }
    }

    @Override
    public void visit( SequenceStatement n )
    {
        isFound = n.equals( target );
        if ( isFound ) {
            return;
        }
        for (OLSyntaxNode child : n.children()) {
            child.accept( this );
        }
    }

    @Override
    public void visit( NDChoiceStatement n )
    {
        isFound = n.equals( target );
        if ( isFound ) {
            return;
        }
        for (Pair< OLSyntaxNode, OLSyntaxNode > child : n.children()) {
            child.key().accept( this );
            if ( isFound ) {
                return;
            }
            child.value().accept( this );
            if ( isFound ) {
                return;
            }
        }
    }

    @Override
    public void visit( OneWayOperationStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( RequestResponseOperationStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( NotificationOperationStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( SolicitResponseOperationStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( LinkInStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( LinkOutStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( AssignStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( AddAssignStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( SubtractAssignStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( MultiplyAssignStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( DivideAssignStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( IfStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( DefinitionCallStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( WhileStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( OrConditionNode n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( AndConditionNode n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( NotExpressionNode n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( CompareConditionNode n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( ConstantIntegerExpression n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( ConstantDoubleExpression n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( ConstantBoolExpression n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( ConstantLongExpression n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( ConstantStringExpression n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( ProductExpressionNode n )
    {
        isFound = n.equals( target );
        if ( isFound ) {
            return;
        }
        for (Pair< Constants.OperandType, OLSyntaxNode > operand : n.operands()) {
            operand.value().accept( this );
        }
    }

    @Override
    public void visit( SumExpressionNode n )
    {
        isFound = n.equals( target );
        if ( isFound ) {
            return;
        }
        for (Pair< Constants.OperandType, OLSyntaxNode > operand : n.operands()) {
            operand.value().accept( this );
        }
    }

    @Override
    public void visit( VariableExpressionNode n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( NullProcessStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( Scope n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( InstallStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( CompensateStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( ThrowStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( ExitStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( ExecutionInfo n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( CorrelationSetInfo n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( InputPortInfo n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( OutputPortInfo n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( PointerStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( DeepCopyStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( RunStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( UndefStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( ValueVectorSizeExpressionNode n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( PreIncrementStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( PostIncrementStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( PreDecrementStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( PostDecrementStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( ForStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( ForEachSubNodeStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( ForEachArrayItemStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( SpawnStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( IsTypeExpressionNode n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( InstanceOfExpressionNode n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( TypeCastExpressionNode n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( SynchronizedStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( CurrentHandlerStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( EmbeddedServiceNode n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( InstallFixedVariableExpressionNode n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( VariablePathNode n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( TypeInlineDefinition n )
    {
        if ( n.getClass() == targetClass ) {
            isFound = n.equals( target );
        }
    }

    @Override
    public void visit( TypeDefinitionLink n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( InterfaceDefinition n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( DocumentationComment n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( FreshValueExpressionNode n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( CourierDefinitionNode n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( CourierChoiceStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( NotificationForwardStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( SolicitResponseForwardStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( InterfaceExtenderDefinition n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( InlineTreeExpressionNode n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( VoidExpressionNode n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( ProvideUntilStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( TypeChoiceDefinition n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( ImportStatement n )
    {
        isFound = n.equals( target );
    }

    @Override
    public void visit( TypeDefinitionImport n )
    {
        isFound = n.equals( target );
    }

}
