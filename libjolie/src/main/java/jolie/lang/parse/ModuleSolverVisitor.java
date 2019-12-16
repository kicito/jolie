package jolie.lang.parse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import jolie.lang.parse.ast.types.TypeDefinitionImport;
import jolie.lang.parse.ast.types.TypeDefinitionLink;
import jolie.lang.parse.ast.types.TypeInlineDefinition;
import jolie.lang.parse.util.ProgramInspector;
import jolie.util.Pair;

class ModuleSolverVisitor implements OLVisitor
{

    private ModuleSolver ms;

    private final List< OLSyntaxNode > programChildren = new ArrayList<>();

    private final Map<String,TypeDefinition> importedTypes = new HashMap<>();

    private OLSyntaxNode currNode;
    private TypeDefinition currType;
        
    public ModuleSolverVisitor(ModuleSolver ms){
        this.ms = ms;
    }

    public Program parse(Program p){
        this.visit(p);
        return new Program(p.context(), programChildren);
    }

    private OLSyntaxNode parse( OLSyntaxNode n )
    {
        n.accept( this );
        return currNode;
    }


    @Override
    public void visit( Program n )
    {
		for( OLSyntaxNode node : n.children() ) {
            if( node instanceof ImportStatement){
                node.accept( this );
            }else if (node instanceof DefinitionNode){
                node.accept( this );
            }else{
                programChildren.add(node);
            }
		}
    }

    @Override
    public void visit( OneWayOperationDeclaration decl ){ }

    @Override
    public void visit( RequestResponseOperationDeclaration decl ){ }

    @Override
    public void visit( DefinitionNode n )
    {
        programChildren.add( new DefinitionNode( n.context(), n.id(), parse( n.body() ) ) );
    }

    @Override
    public void visit( ParallelStatement n ){
        ParallelStatement tmp = new ParallelStatement( n.context() );
        for (OLSyntaxNode node : n.children()) {
            node.accept( this );
            tmp.addChild( currNode );
        }
        currNode = tmp;
     }

    @Override
    public void visit( SequenceStatement n )
    {
        SequenceStatement tmp = new SequenceStatement( n.context() );
        for (OLSyntaxNode node : n.children()) {
            node.accept( this );
            tmp.addChild( currNode );
        }
        currNode = tmp;
    }

    @Override
    public void visit( NDChoiceStatement n )
    {
        NDChoiceStatement tmp = new NDChoiceStatement( n.context() );
        for (Pair< OLSyntaxNode, OLSyntaxNode > pair : n.children()) {
            pair.key().accept( this );
            OLSyntaxNode node = currNode;
            pair.value().accept( this );
            tmp.addChild( new Pair<>( node, currNode ) );
        }
        currNode = tmp;
    }

    @Override
    public void visit( OneWayOperationStatement n ){ }

    @Override
    public void visit( RequestResponseOperationStatement n ){ }

    @Override
    public void visit( NotificationOperationStatement n ){ }

    @Override
    public void visit( SolicitResponseOperationStatement n )
    {
        OLSyntaxNode outputExpression = null;
        if ( n.outputExpression() != null ) {
            n.outputExpression().accept( this );
            outputExpression = currNode;
        }
        currNode = new SolicitResponseOperationStatement( n.context(), n.id(), n.outputPortId(),
                outputExpression, n.inputVarPath(),
                n.handlersFunction() );
    }
    
    @Override
    public void visit( LinkInStatement n ){ currNode = n; }

    @Override
    public void visit( LinkOutStatement n ){ currNode = n; }

    @Override
    public void visit( AssignStatement n )
    {
        
        currNode = new AssignStatement( n.context(), n.variablePath(), parse( n.expression() ) );
    }

    @Override
    public void visit( AddAssignStatement n ){ }

    @Override
    public void visit( SubtractAssignStatement n ){ }

    @Override
    public void visit( MultiplyAssignStatement n ){ }

    @Override
    public void visit( DivideAssignStatement n ){ }

    @Override
    public void visit( IfStatement n )
    {
        IfStatement stm = new IfStatement( n.context() );
        OLSyntaxNode condition;
        for (Pair< OLSyntaxNode, OLSyntaxNode > pair : n.children()) {
            pair.key().accept( this );
            condition = currNode;
            pair.value().accept( this );
            stm.addChild( new Pair<>( condition, currNode ) );
        }

        if ( n.elseProcess() != null ) {
            n.elseProcess().accept( this );
            stm.setElseProcess( currNode );
        }

        currNode = stm;
    }

    @Override
    public void visit( DefinitionCallStatement n ){ 
        currNode = n;
    }

    @Override
    public void visit( WhileStatement n ){ }

    @Override
    public void visit( OrConditionNode n )
    {
        OrConditionNode ret = new OrConditionNode( n.context() );
        for (OLSyntaxNode child : n.children()) {
            ret.addChild( parse(child) );
        }
        currNode = ret;
    }

    @Override
    public void visit( AndConditionNode n )
    {
        AndConditionNode ret = new AndConditionNode( n.context() );
        for (OLSyntaxNode child : n.children()) {
            ret.addChild( parse(child) );
        }
        currNode = ret;
    }

    @Override
    public void visit( NotExpressionNode n )
    {
        currNode = new NotExpressionNode( n.context(), parse( currNode ) );
    }

    @Override
    public void visit( CompareConditionNode n )
    {
        n.leftExpression().accept( this );
        OLSyntaxNode leftExpression = currNode;
        n.rightExpression().accept( this );
        currNode = new CompareConditionNode( n.context(), leftExpression, currNode, n.opType() );
    }

    @Override
    public void visit( ConstantIntegerExpression n ) { currNode = n; }
    
    @Override
    public void visit( ConstantLongExpression n ) { currNode = n; }
    
    @Override
    public void visit( ConstantBoolExpression n ) { currNode = n; }

    @Override
    public void visit( ConstantDoubleExpression n ) { currNode = n; }

    @Override
    public void visit( ConstantStringExpression n ){ 
        currNode = new ConstantStringExpression( n.context(), n.value().intern() );

    }

    @Override
    public void visit( ProductExpressionNode n )
    {
        ProductExpressionNode ret = new ProductExpressionNode( n.context() );
        for (Pair< Constants.OperandType, OLSyntaxNode > pair : n.operands()) {
            if ( pair.key() == Constants.OperandType.MULTIPLY ) {
                ret.multiply( parse( pair.value() ) );
            } else if ( pair.key() == Constants.OperandType.DIVIDE ) {
                ret.divide( parse( pair.value() ) );
            } else if ( pair.key() == Constants.OperandType.MODULUS ) {
                ret.modulo( parse( pair.value() ) );
            }
        }
        currNode = ret;
    }

    @Override
    public void visit( SumExpressionNode n )
    {
        SumExpressionNode ret = new SumExpressionNode( n.context() );
        for (Pair< Constants.OperandType, OLSyntaxNode > pair : n.operands()) {
            if ( pair.key() == Constants.OperandType.ADD ) {
                ret.add( parse( pair.value() ) );
            } else {
                ret.subtract( parse( pair.value() ) );
            }
        }
        currNode = ret;
    }

    @Override
    public void visit( VariableExpressionNode n ){ 
        currNode = new VariableExpressionNode(
            n.context(),
            n.variablePath()
        );
    }

    @Override
    public void visit( NullProcessStatement n ){ currNode = n; }

    @Override
    public void visit( Scope n )
    {
        n.body().accept( this );
        currNode = new Scope( n.context(), n.id(), currNode );

    }

    @Override
    public void visit( InstallStatement n )
    {
        currNode = n;
    }

    @Override
    public void visit( CompensateStatement n ){ currNode = n; }

    @Override
    public void visit( ThrowStatement n ){ currNode = n; }

    @Override
    public void visit( ExitStatement n ){ currNode = n; }

    @Override
    public void visit( ExecutionInfo n ){ 
        programChildren.add( n );
    }

    @Override
    public void visit( CorrelationSetInfo n ){ }

    @Override
    public void visit( InputPortInfo n ){ }

    @Override
    public void visit( OutputPortInfo n ){ }

    @Override
    public void visit( PointerStatement n ){ }

    @Override
    public void visit( DeepCopyStatement n ){ }

    @Override
    public void visit( RunStatement n ){ currNode = n; }

    @Override
    public void visit( UndefStatement n ){ }

    @Override
    public void visit( ValueVectorSizeExpressionNode n ){ }

    @Override
    public void visit( PreIncrementStatement n ){ }

    @Override
    public void visit( PostIncrementStatement n ){ }

    @Override
    public void visit( PreDecrementStatement n ){ }

    @Override
    public void visit( PostDecrementStatement n ){ }

    @Override
    public void visit( ForStatement n ){ }

    @Override
    public void visit( ForEachSubNodeStatement n ){ }

    @Override
    public void visit( ForEachArrayItemStatement n ){ }

    @Override
    public void visit( SpawnStatement n ){ }

    @Override
    public void visit( IsTypeExpressionNode n ){ }

    @Override
    public void visit( InstanceOfExpressionNode n )
    {
        if (n.type() instanceof TypeDefinitionImport){
            n.type().accept(this);
            currNode = new InstanceOfExpressionNode( n.context(), n.expression() , currType );
        }else{
            currNode = n;
        }
    }

    @Override
    public void visit( TypeCastExpressionNode n ){ }

    @Override
    public void visit( SynchronizedStatement n ){ }

    @Override
    public void visit( CurrentHandlerStatement n ){ }

    @Override
    public void visit( EmbeddedServiceNode n ){
        programChildren.add( n );
    }

    @Override
    public void visit( InstallFixedVariableExpressionNode n ){ }

    @Override
    public void visit( VariablePathNode n ){ }

    @Override
    public void visit( TypeInlineDefinition n ){ }

    @Override
    public void visit( TypeDefinitionLink n ){ }

    @Override
    public void visit( InterfaceDefinition n ){ }

    @Override
    public void visit( DocumentationComment n ){ }

    @Override
    public void visit( FreshValueExpressionNode n ){ }

    @Override
    public void visit( CourierDefinitionNode n ){ }

    @Override
    public void visit( CourierChoiceStatement n ){ }

    @Override
    public void visit( NotificationForwardStatement n ){ }

    @Override
    public void visit( SolicitResponseForwardStatement n ){ }

    @Override
    public void visit( InterfaceExtenderDefinition n ){ }

    @Override
    public void visit( InlineTreeExpressionNode n ){ }

    @Override
    public void visit( VoidExpressionNode n ){ }

    @Override
    public void visit( ProvideUntilStatement n ){ }

    @Override
    public void visit( TypeChoiceDefinition n ){ }

    @Override
    public void visit( ImportStatement n )
    {
        ProgramInspector pi = null;
        Map<String, ImportStatement.IDType> expectedIDTypeMap=n.expectedIDTypeMap();
        try {
            File f = ms.find( n.importTarget() );
            pi = ms.load( f );
            ms.addInspectorToCache(pi);
            System.out.println(pi);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (pi == null){
            throw new Error("programinspector is null");
        }

        for(Pair<String, String> importPair:n.pathNodes()){
            String moduleSymbol = importPair.key();
            String localSymbol = importPair.value();
            ImportStatement.IDType expectedType = expectedIDTypeMap.get(localSymbol);
            switch(expectedType){
                case TYPE:
                for(TypeDefinition td : pi.getTypes()){
                    if (td.id().equals(moduleSymbol)){
                        programChildren.add(td);
                        importedTypes.put(localSymbol, td);
                    }
                }
                
                break; 
                default: throw new Error("expected type of " + localSymbol + "< " +expectedType+ "> " + " is not support");
            }

        }
    }
    
	public void visit( TypeDefinitionImport n){
        TypeDefinition td = importedTypes.get(n.id());
        currType = td;
    }

}