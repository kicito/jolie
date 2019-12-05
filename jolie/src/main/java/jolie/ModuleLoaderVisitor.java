package jolie;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import jolie.lang.parse.OLVisitor;
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
import jolie.lang.parse.ast.OLSyntaxNode;
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
import jolie.lang.parse.ast.types.TypeDefinition;
import jolie.lang.parse.ast.types.TypeDefinitionLink;
import jolie.lang.parse.ast.types.TypeInlineDefinition;
import jolie.runtime.expression.Expression;
import jolie.runtime.typing.Type;

class ModuleLoaderVisitor implements OLVisitor
{

    private ModuleOOIT ooit;
    private Program program;

    // variables for visiting
    private boolean insideOperationDeclaration = false;
    private Expression currExpression;
    private Type currType;
    boolean insideType = false;

    public ModuleLoaderVisitor( File targetName, Program p )
    {
        this.ooit = new ModuleOOIT( targetName );
        this.program = p;
    }

    public ModuleOOIT build()
    {
        this.visit( this.program );
        return ooit;
    }

    @Override
    public void visit( Program n )
    {
        for (OLSyntaxNode node : n.children())
            node.accept( this );
    }

    @Override
    public void visit( OneWayOperationDeclaration decl )
    {
        System.out.println( decl.getClass().toString() + decl );

    }

    @Override
    public void visit( RequestResponseOperationDeclaration decl )
    {

        System.out.println( decl.getClass().toString() + decl );

    }

    @Override
    public void visit( DefinitionNode n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( ParallelStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( SequenceStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( NDChoiceStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( OneWayOperationStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( RequestResponseOperationStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( NotificationOperationStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( SolicitResponseOperationStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( LinkInStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( LinkOutStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( AssignStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( AddAssignStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( SubtractAssignStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( MultiplyAssignStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( DivideAssignStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( IfStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( DefinitionCallStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( WhileStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( OrConditionNode n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( AndConditionNode n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( NotExpressionNode n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( CompareConditionNode n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( ConstantIntegerExpression n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( ConstantDoubleExpression n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( ConstantBoolExpression n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( ConstantLongExpression n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( ConstantStringExpression n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( ProductExpressionNode n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( SumExpressionNode n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( VariableExpressionNode n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( NullProcessStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( Scope n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( InstallStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( CompensateStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( ThrowStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( ExitStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( ExecutionInfo n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( CorrelationSetInfo n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( InputPortInfo n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( OutputPortInfo n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( PointerStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( DeepCopyStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( RunStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( UndefStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( ValueVectorSizeExpressionNode n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( PreIncrementStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( PostIncrementStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( PreDecrementStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( PostDecrementStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( ForStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( ForEachSubNodeStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( ForEachArrayItemStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( SpawnStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( IsTypeExpressionNode n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( InstanceOfExpressionNode n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( TypeCastExpressionNode n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( SynchronizedStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( CurrentHandlerStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( EmbeddedServiceNode n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( InstallFixedVariableExpressionNode n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( VariablePathNode n )
    {
        System.out.println( n.getClass().toString() + n );

    }


    @Override
    public void visit( TypeInlineDefinition n )
    {
        boolean backupInsideType = insideType;
        insideType = true;

        if ( n.untypedSubTypes() ) {
            currType = Type.create( n.nativeType(), n.cardinality(), true, null );
        } else {
            Map< String, Type > subTypes = new HashMap< String, Type >();
            if ( n.subTypes() != null ) {
                for (Entry< String, TypeDefinition > entry : n.subTypes()) {
                    subTypes.put( entry.getKey(), buildType( entry.getValue() ) );
                }
            }
            currType = Type.create( n.nativeType(), n.cardinality(), false, subTypes );
        }

        insideType = backupInsideType;

        if ( insideType == false && insideOperationDeclaration == false ) {
            ooit.putType( n.id(), currType );
        }
    }

    private Type buildType( OLSyntaxNode n )
    {
        if ( n == null ) {
            return null;
        }
        n.accept( this );
        return currType;
    }

    @Override
    public void visit( TypeDefinitionLink n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( InterfaceDefinition n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( DocumentationComment n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( FreshValueExpressionNode n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( CourierDefinitionNode n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( CourierChoiceStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( NotificationForwardStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( SolicitResponseForwardStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( InterfaceExtenderDefinition n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( InlineTreeExpressionNode n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( VoidExpressionNode n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( ProvideUntilStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( TypeChoiceDefinition n )
    {
        System.out.println( n.getClass().toString() + n );

    }

    @Override
    public void visit( ImportStatement n )
    {
        System.out.println( n.getClass().toString() + n );

    }

}
