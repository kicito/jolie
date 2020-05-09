package jolie.lang.parse.util;

import java.util.ArrayList;
import java.util.List;
import jolie.lang.parse.ast.DefinitionNode;
import jolie.lang.parse.ast.EmbeddedServiceNode;
import jolie.lang.parse.ast.ImportStatement;
import jolie.lang.parse.ast.InputPortInfo;
import jolie.lang.parse.ast.InterfaceDefinition;
import jolie.lang.parse.ast.OLSyntaxNode;
import jolie.lang.parse.ast.OutputPortInfo;
import jolie.lang.parse.ast.Program;
import jolie.lang.parse.ast.ServiceNode;
import jolie.lang.parse.ast.SymbolNode;
import jolie.lang.parse.ast.VariablePathNode;
import jolie.lang.parse.ast.expression.ConstantStringExpression;
import jolie.lang.parse.ast.expression.InlineTreeExpressionNode;
import jolie.lang.parse.ast.expression.VariableExpressionNode;

/**
 * A Utility class to handle backward compatibility of Jolie 1
 * 
 */
public class Jolie2Utility
{
    public static boolean isJolie1( Program p )
    {
        for (OLSyntaxNode node : p.children()) {
            if ( node instanceof DefinitionNode ) {
                if ( ((DefinitionNode) node).id().equals( "main" ) ) {
                    return true;
                }
            } else if ( node instanceof ServiceNode ){
                return false;
            }
        }
        return true;
    }

    public static Program removeModuleScopePortsAndEmbeds( Program p )
    {
        ProgramBuilder moduleProgramBuilder = new ProgramBuilder( p.context() );
        for (OLSyntaxNode node : p.children()) {
            if ( !(node instanceof OutputPortInfo) && !(node instanceof InputPortInfo)
                    && !(node instanceof EmbeddedServiceNode) ) {
                moduleProgramBuilder.addChild( node );
            }
        }
        return moduleProgramBuilder.toProgram();
    }

    public static Program transform( Program p )
    {
        ProgramBuilder moduleProgramBuilder = new ProgramBuilder( p.context() );
        ProgramBuilder mainServiceProgramBuilder = new ProgramBuilder( p.context() );
        ServiceNode mainService = new ServiceNode( p.context(), "main" );

        // foreach node s.t. is a SymbolNode but init & main: add to module (file) scope
        // else, add to newly created main service
        for (OLSyntaxNode node : p.children()) {
            if ( node instanceof SymbolNode ) {
                if ( node instanceof DefinitionNode ) {
                    DefinitionNode defNode = (DefinitionNode) node;
                    if ( defNode.id().equals( "main" ) || defNode.id().equals( "init" ) ) {
                        mainServiceProgramBuilder.addChild( node );
                    } else {
                        moduleProgramBuilder.addChild( node );
                    }
                } else {
                    moduleProgramBuilder.addChild( node );
                }
            } else {
                if ( node instanceof ImportStatement ) {
                    moduleProgramBuilder.addChild( node );
                } else {
                    mainServiceProgramBuilder.addChild( node );
                }
            }
        }
        mainService.setProgram( mainServiceProgramBuilder.toProgram() );
        mainService.setPrivate(false);
        mainService.setAcceptParameter(null, null);
        moduleProgramBuilder.addChild( mainService );
        return moduleProgramBuilder.toProgram();
    }


    public static OLSyntaxNode transformProtocolExpression( OLSyntaxNode node )
    {
        // case http -> "http" return ConstantString
        // case http { .... } -> "http" {} return InlineTreeExpression
        // case some.proc -> do noting. return VariablePathNode
        // case "http" {} -> do noting. return InlineTreeExpression

        // case 1

        if ( node instanceof VariableExpressionNode ) {
            VariableExpressionNode varExprNode = (VariableExpressionNode) node;
            VariablePathNode varPathNode = varExprNode.variablePath();
            if ( varPathNode.path().size() == 1 ) {
                return varPathNode.path().get( 0 ).key();
            }
        }

        if ( node instanceof InlineTreeExpressionNode && ((InlineTreeExpressionNode) node)
                .rootExpression() instanceof VariableExpressionNode ) {
            InlineTreeExpressionNode inlineTreeNodeProtocol = (InlineTreeExpressionNode) node;
            if ( inlineTreeNodeProtocol.rootExpression() instanceof VariableExpressionNode ) {
                String protocolSymbolStr =
                        ((VariableExpressionNode) inlineTreeNodeProtocol.rootExpression())
                                .variablePath().toString();
                node = new InlineTreeExpressionNode( inlineTreeNodeProtocol.context(),
                        new ConstantStringExpression( inlineTreeNodeProtocol.context(),
                                protocolSymbolStr ),
                        inlineTreeNodeProtocol.operations() );
            }
        }
        return node;
    }


    public static InterfaceDefinition[] getServiceNodeInterfacesFromInputPortLocal(
            ServiceNode node )
    {
        List< InterfaceDefinition > idef = new ArrayList< InterfaceDefinition >();
        for (OLSyntaxNode n : node.program().children()) {
            if ( n instanceof InputPortInfo ) {
                InputPortInfo ip = (InputPortInfo) n;
                if ( ip.location() instanceof ConstantStringExpression ) {
                    String location = ((ConstantStringExpression) ip.location()).value();
                    if ( location.equals( "local" ) ) {
                        idef.addAll( ip.getInterfaceList() );
                    }
                }
            }
        }
        return idef.toArray( new InterfaceDefinition[idef.size()] );
    }

}
