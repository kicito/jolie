/*
 * Copyright (C) 2020 Narongrit Unwerawattana <narongrit.kie@gmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package jolie.lang.parse.module;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import jolie.lang.Constants.OperandType;
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
import jolie.lang.parse.ast.InputPortInfo.AggregationItemInfo;
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
import jolie.lang.parse.ast.OperationDeclaration;
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
import jolie.lang.parse.ast.SequenceStatement;
import jolie.lang.parse.ast.ServiceNode;
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
import jolie.lang.parse.ast.courier.CourierChoiceStatement.InterfaceOneWayBranch;
import jolie.lang.parse.ast.courier.CourierChoiceStatement.InterfaceRequestResponseBranch;
import jolie.lang.parse.ast.courier.CourierChoiceStatement.OperationOneWayBranch;
import jolie.lang.parse.ast.courier.CourierChoiceStatement.OperationRequestResponseBranch;
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
import jolie.lang.parse.ast.expression.InlineTreeExpressionNode.Operation;
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
import jolie.lang.parse.ast.types.TypeDefinitionUndefined;
import jolie.lang.parse.ast.types.TypeInlineDefinition;
import jolie.lang.parse.module.SymbolInfo.Privacy;
import jolie.lang.parse.module.SymbolInfo.Scope;
import jolie.util.Pair;

public class GlobalSymbolReferenceResolver
{
    private final Map< URI, ModuleRecord > moduleMap;
    private final Map< URI, SymbolTable > symbolTables;

    public GlobalSymbolReferenceResolver( Set< ModuleRecord > moduleMap )
    {
        this.moduleMap = new HashMap<>();
        this.symbolTables = new HashMap<>();
        for (ModuleRecord mr : moduleMap) {
            this.moduleMap.put( mr.source(), mr );
            this.symbolTables.put( mr.source(), mr.symbolTable() );
        }
    }

    private class SymbolReferenceResolverVisitor implements OLVisitor
    {

        private final Map< URI, ModuleRecord > moduleMap;
        private URI currentURI;
        private boolean valid = true;
        private ModuleException error;

        /**
         * @param moduleMap a map of module source URI and it's ModuleRecord object, result
         *                  form resolving all pointer of external Symbols
         */
        protected SymbolReferenceResolverVisitor( Map< URI, ModuleRecord > moduleMap )
        {
            this.moduleMap = moduleMap;
        }


        /**
         * Walk through the Jolie AST tree and resolve the call of external Symbols.
         */
        public void resolve( Program p ) throws ModuleException
        {
            currentURI = p.context().source();
            visit( p );
            if ( !this.valid ) {
                throw error;
            }
            return;
        }

        @Override
        public void visit( Program n )
        {
            for (OLSyntaxNode node : n.children()) {
                if ( !this.valid ) {
                    return;
                }
                node.accept( this );
            }
        }

        @Override
        public void visit( OneWayOperationDeclaration decl )
        {
            decl.requestType().accept( this );
        }

        @Override
        public void visit( RequestResponseOperationDeclaration decl )
        {
            decl.requestType().accept( this );
            decl.responseType().accept( this );
            for (TypeDefinition fault : decl.faults().values()) {
                fault.accept( this );
            }
        }

        @Override
        public void visit( DefinitionNode n )
        {
            n.body().accept( this );
        }

        @Override
        public void visit( ParallelStatement n )
        {
            for (OLSyntaxNode node : n.children()) {
                node.accept( this );
            }
        }

        @Override
        public void visit( SequenceStatement n )
        {
            for (OLSyntaxNode node : n.children()) {
                node.accept( this );
            }
        }

        @Override
        public void visit( NDChoiceStatement n )
        {
            for (Pair< OLSyntaxNode, OLSyntaxNode > child : n.children()) {
                child.key().accept( this );
                child.value().accept( this );
            }
        }

        @Override
        public void visit( OneWayOperationStatement n )
        {
        }

        @Override
        public void visit( RequestResponseOperationStatement n )
        {
            n.process().accept( this );
        }

        @Override
        public void visit( NotificationOperationStatement n )
        {
        }

        @Override
        public void visit( SolicitResponseOperationStatement n )
        {
            if ( n.handlersFunction() != null ) {
                for (Pair< String, OLSyntaxNode > handler : n.handlersFunction().pairs()) {
                    handler.value().accept( this );
                }
            }
        }

        @Override
        public void visit( LinkInStatement n )
        {
        }

        @Override
        public void visit( LinkOutStatement n )
        {
        }

        @Override
        public void visit( AssignStatement n )
        {
            n.expression().accept( this );
        }

        @Override
        public void visit( AddAssignStatement n )
        {
            n.expression().accept( this );
        }

        @Override
        public void visit( SubtractAssignStatement n )
        {
            n.expression().accept( this );
        }

        @Override
        public void visit( MultiplyAssignStatement n )
        {
            n.expression().accept( this );
        }

        @Override
        public void visit( DivideAssignStatement n )
        {
            n.expression().accept( this );
        }

        @Override
        public void visit( IfStatement n )
        {
            for (Pair< OLSyntaxNode, OLSyntaxNode > child : n.children()) {
                child.key().accept( this );
                child.value().accept( this );
            }
            if ( n.elseProcess() != null ) {
                n.elseProcess().accept( this );
            }
        }

        @Override
        public void visit( DefinitionCallStatement n )
        {

            Optional< SymbolInfo > symbol = this.moduleMap.get( currentURI ).symbol( n.id() );
            if ( !symbol.isPresent() ) {
                this.valid = false;
                this.error = new ModuleException( n.context(),
                        n.id() + " is not defined in symbolTable" );
                return;
            }
            if ( !(symbol.get().node() instanceof DefinitionNode) ) {
                this.valid = false;
                this.error = new ModuleException( n.context(),
                        n.id() + " is not defined as an procedure definition" );
                return;
            }

            DefinitionNode linkedNode = (DefinitionNode) symbol.get().node();
            if ( linkedNode == null ) {
                this.valid = false;
                this.error = new ModuleException(
                        "procedure " + n.id() + " points to an undefined procedure" );
                return;
            }
            n.setDefinitionLink( linkedNode );
        }

        @Override
        public void visit( WhileStatement n )
        {
            n.condition().accept( this );
            n.body().accept( this );
        }

        @Override
        public void visit( OrConditionNode n )
        {
            for (OLSyntaxNode node : n.children()) {
                node.accept( this );
            }
        }

        @Override
        public void visit( AndConditionNode n )
        {
            for (OLSyntaxNode node : n.children()) {
                node.accept( this );
            }
        }

        @Override
        public void visit( NotExpressionNode n )
        {
            n.expression().accept( this );
        }

        @Override
        public void visit( CompareConditionNode n )
        {
            n.leftExpression().accept( this );
            n.rightExpression().accept( this );
        }

        @Override
        public void visit( ConstantIntegerExpression n )
        {
        }

        @Override
        public void visit( ConstantDoubleExpression n )
        {
        }

        @Override
        public void visit( ConstantBoolExpression n )
        {
        }

        @Override
        public void visit( ConstantLongExpression n )
        {
        }

        @Override
        public void visit( ConstantStringExpression n )
        {
        }

        @Override
        public void visit( ProductExpressionNode n )
        {
            for (Pair< OperandType, OLSyntaxNode > node : n.operands()) {
                node.value().accept( this );
            }
        }

        @Override
        public void visit( SumExpressionNode n )
        {
            for (Pair< OperandType, OLSyntaxNode > node : n.operands()) {
                node.value().accept( this );
            }
        }

        @Override
        public void visit( VariableExpressionNode n )
        {
        }

        @Override
        public void visit( NullProcessStatement n )
        {
        }

        @Override
        public void visit( jolie.lang.parse.ast.Scope n )
        {
            n.body().accept( this );
        }

        @Override
        public void visit( InstallStatement n )
        {
            for (Pair< String, OLSyntaxNode > handlerFunction : n.handlersFunction().pairs()) {
                handlerFunction.value().accept( this );
            }
        }

        @Override
        public void visit( CompensateStatement n )
        {
        }

        @Override
        public void visit( ThrowStatement n )
        {
            if ( n.expression() != null ) {
                n.expression().accept( this );
            }
        }

        @Override
        public void visit( ExitStatement n )
        {
        }

        @Override
        public void visit( ExecutionInfo n )
        {
        }

        @Override
        public void visit( CorrelationSetInfo n )
        {
        }

        @Override
        public void visit( InputPortInfo n )
        {
            // resolve interface definition
            for (InterfaceDefinition iface : n.getInterfaceList()) {
                Optional< SymbolInfo > symbol =
                        this.moduleMap.get( currentURI ).symbol( iface.name() );
                if ( !symbol.isPresent() ) {
                    this.valid = false;
                    this.error = new ModuleException( n.context(),
                            n.id() + " is not defined in symbolTable" );
                    return;
                }
                if ( !(symbol.get().node() instanceof InterfaceDefinition) ) {
                    this.valid = false;
                    this.error = new ModuleException( n.context(),
                            n.id() + " is not defined as an interface definition" );
                    return;
                }
                InterfaceDefinition ifaceDeclFromSymbol = (InterfaceDefinition) symbol.get().node();
                ifaceDeclFromSymbol.operationsMap().values().forEach( op -> {
                    iface.addOperation( op );
                    n.addOperation( op );
                } );
                iface.setDocumentation( ifaceDeclFromSymbol.getDocumentation() );
            }
            for (OperationDeclaration op : n.operations()) {
                op.accept( this );
            }
            for (AggregationItemInfo aggregationItem : n.aggregationList()) {
                if ( aggregationItem.interfaceExtender() != null ) {
                    aggregationItem.interfaceExtender().accept( this );
                }
            }
        }

        @Override
        public void visit( OutputPortInfo n )
        {
            // resolve interface definition
            for (InterfaceDefinition iface : n.getInterfaceList()) {
                Optional< SymbolInfo > symbol =
                        this.moduleMap.get( currentURI ).symbol( iface.name() );
                if ( !symbol.isPresent() ) {
                    this.valid = false;
                    this.error = new ModuleException( n.context(),
                            n.id() + " is not defined in symbolTable" );
                    return;
                }
                if ( !(symbol.get().node() instanceof InterfaceDefinition) ) {
                    this.valid = false;
                    this.error = new ModuleException( n.context(),
                            n.id() + " is not defined as an interface defition" );
                    return;
                }
                InterfaceDefinition ifaceDeclFromSymbol = (InterfaceDefinition) symbol.get().node();
                ifaceDeclFromSymbol.operationsMap().values().forEach( op -> {
                    iface.addOperation( op );
                    n.addOperation( op );
                } );
                iface.setDocumentation( ifaceDeclFromSymbol.getDocumentation() );
            }
            for (OperationDeclaration op : n.operations()) {
                op.accept( this );
            }
        }

        @Override
        public void visit( PointerStatement n )
        {
        }

        @Override
        public void visit( DeepCopyStatement n )
        {
            n.rightExpression().accept( this );
        }

        @Override
        public void visit( RunStatement n )
        {
        }

        @Override
        public void visit( UndefStatement n )
        {
        }

        @Override
        public void visit( ValueVectorSizeExpressionNode n )
        {
        }

        @Override
        public void visit( PreIncrementStatement n )
        {
        }

        @Override
        public void visit( PostIncrementStatement n )
        {
        }

        @Override
        public void visit( PreDecrementStatement n )
        {
        }

        @Override
        public void visit( PostDecrementStatement n )
        {
        }

        @Override
        public void visit( ForStatement n )
        {
            n.body().accept( this );
        }

        @Override
        public void visit( ForEachSubNodeStatement n )
        {
            n.body().accept( this );
        }

        @Override
        public void visit( ForEachArrayItemStatement n )
        {
            n.body().accept( this );
        }

        @Override
        public void visit( SpawnStatement n )
        {
            n.body().accept( this );
        }

        @Override
        public void visit( IsTypeExpressionNode n )
        {
        }

        @Override
        public void visit( InstanceOfExpressionNode n )
        {
            n.type().accept( this );
        }

        @Override
        public void visit( TypeCastExpressionNode n )
        {
        }

        @Override
        public void visit( SynchronizedStatement n )
        {
            n.body().accept( this );
        }

        @Override
        public void visit( CurrentHandlerStatement n )
        {
        }

        @Override
        public void visit( EmbeddedServiceNode n )
        {
            if (n.program() != null){
                n.program().accept(this);
            }
        }

        @Override
        public void visit( InstallFixedVariableExpressionNode n )
        {
        }

        @Override
        public void visit( VariablePathNode n )
        {
        }

        @Override
        public void visit( TypeInlineDefinition n )
        {
            if ( n.hasSubTypes() ) {
                for (Map.Entry< String, TypeDefinition > subType : n.subTypes()) {
                    subType.getValue().accept( this );
                }
            }
        }

        @Override
        public void visit( TypeDefinitionLink n )
        {
            TypeDefinition linkedType = null;
            if ( n.linkedTypeName().equals( TypeDefinitionUndefined.UNDEFINED_KEYWORD ) ) {
                linkedType = TypeDefinitionUndefined.getInstance();
            } else {
                Optional< SymbolInfo > targetSymbolInfo =
                        this.moduleMap.get( currentURI ).symbol( n.linkedTypeName() );
                if ( !targetSymbolInfo.isPresent() ) {
                    this.valid = false;
                    this.error = new ModuleException( n.context(),
                            n.id() + " is not defined in symbolTable" );
                    return;
                }
                if ( !(targetSymbolInfo.get().node() instanceof TypeDefinition) ) {
                    this.valid = false;
                    this.error = new ModuleException( n.context(),
                            n.id() + " is not defined as a type definition" );
                    return;
                }
                linkedType = (TypeDefinition) targetSymbolInfo.get().node();
            }
            n.setLinkedType( linkedType );
        }

        @Override
        public void visit( InterfaceDefinition n )
        {
            for (OperationDeclaration op : n.operationsMap().values()) {
                op.accept( this );
            }
        }

        @Override
        public void visit( DocumentationComment n )
        {
        }

        @Override
        public void visit( FreshValueExpressionNode n )
        {
        }

        @Override
        public void visit( CourierDefinitionNode n )
        {
            n.body().accept( this );
        }

        @Override
        public void visit( CourierChoiceStatement n )
        {
            for (InterfaceOneWayBranch owIfaceBranch : n.interfaceOneWayBranches()) {
                owIfaceBranch.interfaceDefinition.accept( this );
                owIfaceBranch.body.accept( this );
            }

            for (InterfaceRequestResponseBranch rrIfaceBranch : n
                    .interfaceRequestResponseBranches()) {
                rrIfaceBranch.interfaceDefinition.accept( this );
                rrIfaceBranch.body.accept( this );
            }

            for (OperationOneWayBranch owBranch : n.operationOneWayBranches()) {
                owBranch.body.accept( this );
            }

            for (OperationRequestResponseBranch rrBranch : n.operationRequestResponseBranches()) {
                rrBranch.body.accept( this );
            }
        }

        @Override
        public void visit( NotificationForwardStatement n )
        {
        }

        @Override
        public void visit( SolicitResponseForwardStatement n )
        {
        }

        @Override
        public void visit( InterfaceExtenderDefinition n )
        {
            if ( n.defaultOneWayOperation() != null ) {
                n.defaultOneWayOperation().accept( this );
            }
            if ( n.defaultRequestResponseOperation() != null ) {
                n.defaultRequestResponseOperation().accept( this );
            }
            for (OperationDeclaration op : n.operationsMap().values()) {
                op.accept( this );
            }
        }

        @Override
        public void visit( InlineTreeExpressionNode n )
        {
            for (Operation operation : n.operations()) {
                if ( operation instanceof InlineTreeExpressionNode.AssignmentOperation ) {
                    ((InlineTreeExpressionNode.AssignmentOperation) operation).expression()
                            .accept( this );;
                } else if ( operation instanceof InlineTreeExpressionNode.DeepCopyOperation ) {
                    ((InlineTreeExpressionNode.DeepCopyOperation) operation).expression()
                            .accept( this );;
                }
            }
        }

        @Override
        public void visit( VoidExpressionNode n )
        {
        }

        @Override
        public void visit( ProvideUntilStatement n )
        {
            n.provide().accept( this );
            n.until().accept( this );
        }

        @Override
        public void visit( TypeChoiceDefinition n )
        {
            n.left().accept( this );
            if ( n.right() != null ) {
                n.right().accept( this );
            }
        }

        @Override
        public void visit( ImportStatement n )
        {
        }
        
        @Override
        public void visit( ServiceNode n ) {
            n.program().accept(this);
        }
    }

    private SymbolInfo symbolLookup( SymbolInfoExternal symbolInfo ) throws ModuleException
    {
        ModuleRecord externalSourceRecord =
                this.moduleMap.get( symbolInfo.moduleSource().get().source() );
        SymbolInfo externalSourceSymbol =
                externalSourceRecord.symbol( symbolInfo.moduleSymbol() ).get();
        if ( externalSourceSymbol == null ) {
            throw new ModuleException(
                    symbolInfo.name() + " is not defined in " + externalSourceRecord.source() );
        }
        if ( externalSourceSymbol.scope() == Scope.LOCAL ) {
            return externalSourceSymbol;
        } else {
            return symbolLookup( (SymbolInfoExternal) externalSourceSymbol );
        }
    }

    /**
     * Find and set a pointer of externalSymbol to it's corresponding AST node by
     * perform lookup at ModuleRecord Map, a result from ModuleCrawler.
     * 
     * @throws ModuleException when the target module or the target Symbol is not found. adding
     *                         wildcard symbol failed
     */
    public void resolveExternalSymbols() throws ModuleException
    {
        for (ModuleRecord md : moduleMap.values()) {
            for (SymbolInfoExternal si : md.externalSymbols()) {
                SymbolInfoExternal localSymbol = (SymbolInfoExternal) si;
                if ( si instanceof SymbolWildCard ) {
                    ModuleRecord wildcardImportedRecord =
                            this.moduleMap.get( localSymbol.moduleSource().get().source() );
                    md.addWildcardImportedRecord( (SymbolWildCard) si,
                            wildcardImportedRecord.symbols() );
                } else {
                    SymbolInfo targetSymbol = symbolLookup( localSymbol );
                    if ( targetSymbol.privacy() == Privacy.PRIVATE ) {
                        throw new ModuleException( si.context(),
                                "cannot refer to private name " + si.name() + " of module "
                                        + si.moduleTargets()[si.moduleTargets().length - 1] );
                    }
                    si.setPointer( targetSymbol.node() );
                }
            }
        }
    }

    /**
     * resolve LinkedType of each ModuleRecord AST node in the Map.
     * 
     * @throws ModuleException if the linked type cannot find it's referencing node
     */
    public void resolveLinkedType() throws ModuleException
    {
        SymbolReferenceResolverVisitor resolver =
                new SymbolReferenceResolverVisitor( this.moduleMap );
        for (ModuleRecord md : moduleMap.values()) {
            resolver.resolve( md.program() );
        }
    }

    /**
     * Resolve symbols the is imported from external modules and resolve linked type's pointer
     * 
     * @throws ModuleException if the process is failed
     */
    public void resolve() throws ModuleException
    {
        this.resolveExternalSymbols();
        this.resolveLinkedType();
    }

    public Map< URI, SymbolTable > symbolTables()
    {
        return this.symbolTables;
    }
}
