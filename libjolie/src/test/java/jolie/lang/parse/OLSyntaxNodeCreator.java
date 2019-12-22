package jolie.lang.parse;

import java.util.List;
import jolie.lang.parse.ast.OLSyntaxNode;
import jolie.lang.parse.ast.ParallelStatement;
import jolie.lang.parse.ast.SequenceStatement;
import jolie.lang.parse.ast.expression.AndConditionNode;
import jolie.lang.parse.ast.expression.OrConditionNode;
import jolie.lang.parse.ast.expression.ProductExpressionNode;
import jolie.lang.parse.ast.expression.SumExpressionNode;

class OLSyntaxNodeCreator
{

    static SumExpressionNode createNodeSumProductExpression( OLSyntaxNode child )
    {
        ProductExpressionNode productExpression = new ProductExpressionNode( null );
        productExpression.multiply( child );
        SumExpressionNode sumExpression = new SumExpressionNode( null );
        sumExpression.add( productExpression );
        return sumExpression;
    }

    static OrConditionNode createNodeBasicExpression( OLSyntaxNode child )
    {

        AndConditionNode andExpression = new AndConditionNode( null );
        andExpression.addChild( createNodeSumProductExpression( child ) );
        OrConditionNode orExpression = new OrConditionNode( null );
        orExpression.addChild( andExpression );
        return orExpression;
    }

    static ParallelStatement createNodeBasicProcess( List< OLSyntaxNode > child )
    {
        SequenceStatement seqStatement = new SequenceStatement( null );
        child.forEach( ( c ) -> seqStatement.addChild( c ) );
        ParallelStatement parStatement = new ParallelStatement( null );
        parStatement.addChild( seqStatement );
        return parStatement;
    }
}