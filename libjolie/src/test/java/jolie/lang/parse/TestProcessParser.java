package jolie.lang.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import jolie.lang.parse.ast.AssignStatement;
import jolie.lang.parse.ast.DefinitionNode;
import jolie.lang.parse.ast.NullProcessStatement;
import jolie.lang.parse.ast.OLSyntaxNode;
import jolie.lang.parse.ast.ParallelStatement;
import jolie.lang.parse.ast.Program;
import jolie.lang.parse.ast.SolicitResponseOperationStatement;
import jolie.lang.parse.ast.VariablePathNode;
import jolie.lang.parse.ast.expression.ConstantIntegerExpression;
import jolie.lang.parse.ast.expression.ConstantStringExpression;
import jolie.lang.parse.ast.expression.OrConditionNode;
import jolie.lang.parse.ast.expression.ProductExpressionNode;
import jolie.lang.parse.ast.expression.SumExpressionNode;
import jolie.lang.parse.ast.expression.VariableExpressionNode;
import jolie.util.Pair;

public class TestProcessParser
{

	InputStream is;
	InspectorVisitor iv = new InspectorVisitor();

	@Test
	void testDefineStatement() throws Exception
	{
		String code = "define a{ nullProcess }";
		is = new ByteArrayInputStream( code.getBytes() );
        InstanceCreator oc = new InstanceCreator( new String[] {"dist/jolie/include"} );

		OLParser olParser = oc.createOLParser( is );

		ParallelStatement child = OLSyntaxNodeCreator.createNodeBasicProcess(
				new ArrayList< OLSyntaxNode >( Arrays.asList( new NullProcessStatement( null ) ) ) );
		DefinitionNode expected = new DefinitionNode( null, "a", child );

		Program p = olParser.parse();

		assertTrue(iv.programHasOLSyntaxNode(p, expected));
	}

	@Test
	void testSolicitedRequest() throws Exception
	{

		String code = "define a{ println@Console(\"hi\")() }";
		is = new ByteArrayInputStream( code.getBytes() );
        InstanceCreator oc = new InstanceCreator( new String[] {"dist/jolie/include"} );

		OLParser olParser = oc.createOLParser( is );

		OrConditionNode orChild =
				OLSyntaxNodeCreator.createNodeBasicExpression( new ConstantStringExpression( null, "hi" ) );
		SolicitResponseOperationStatement sroChild =
				new SolicitResponseOperationStatement( null, "println", "Console", orChild, null, null );

		ParallelStatement child = OLSyntaxNodeCreator
				.createNodeBasicProcess( new ArrayList< OLSyntaxNode >( Arrays.asList( sroChild ) ) );

		DefinitionNode expected = new DefinitionNode( null, "a", child );

		Program p = olParser.parse();

		assertTrue(iv.programHasOLSyntaxNode(p, expected));
	}


	@Test
	void testAssignment() throws Exception
	{
		String code = "define a {a = b}";
        InstanceCreator oc = new InstanceCreator( new String[] {"dist/jolie/include"} );
		is = new ByteArrayInputStream( code.getBytes() );

		OLParser olParser = oc.createOLParser( is );
		VariablePathNode pathNodeA = new VariablePathNode( null, VariablePathNode.Type.NORMAL );
		pathNodeA.append( new Pair<>( new ConstantStringExpression( null, "a" ), null ) );
		VariablePathNode pathNodeB = new VariablePathNode( null, VariablePathNode.Type.NORMAL );
		pathNodeB.append( new Pair<>( new ConstantStringExpression( null, "b" ), null ) );
		AssignStatement assignmentNode = new AssignStatement( null, pathNodeA, OLSyntaxNodeCreator
				.createNodeBasicExpression( new VariableExpressionNode( null, pathNodeB ) ) );

		ParallelStatement child = OLSyntaxNodeCreator
				.createNodeBasicProcess( new ArrayList< OLSyntaxNode >( Arrays.asList( assignmentNode ) ) );

		DefinitionNode expected = new DefinitionNode( null, "a", child );

		Program p = olParser.parse();

		assertTrue(iv.programHasOLSyntaxNode(p, expected));
	}

	@Test
	void testDynamicVariablePath() throws IOException, URISyntaxException, ParserException
	{
		String code = "define a{person . age = 30;" + "x = person .(\"age\");" + "key = \"age\";"
				+ "y = person .( key );" + "z = person .(\"a\" + \"ge\")}";

		is = new ByteArrayInputStream( code.getBytes() );
        InstanceCreator oc = new InstanceCreator( new String[] {"dist/jolie/include"} );
		OLParser olParser = oc.createOLParser( is );
		List< OLSyntaxNode > nodes = new ArrayList< OLSyntaxNode >();

		// person.age = 30
		VariablePathNode pathNodePersonAge = new VariablePathNode( null, VariablePathNode.Type.NORMAL );
		pathNodePersonAge.append( new Pair<>( new ConstantStringExpression( null, "person" ), null ) );
		pathNodePersonAge.append( new Pair<>( new ConstantStringExpression( null, "age" ), null ) );
		AssignStatement assignmentNodePersonAge =
				new AssignStatement( null, pathNodePersonAge, OLSyntaxNodeCreator
						.createNodeBasicExpression( new ConstantIntegerExpression( null, 30 ) ) );
		nodes.add( assignmentNodePersonAge );

		// x = person .(\"age\")
		VariablePathNode pathNodeX = new VariablePathNode( null, VariablePathNode.Type.NORMAL );
		pathNodeX.append( new Pair<>( new ConstantStringExpression( null, "x" ), null ) );
		VariablePathNode pathNodePersonAgeExp1 =
				new VariablePathNode( null, VariablePathNode.Type.NORMAL );
		pathNodePersonAgeExp1.append( new Pair< OLSyntaxNode, OLSyntaxNode >(
				new ConstantStringExpression( null, "person" ), null ) );
		pathNodePersonAgeExp1.append( new Pair< OLSyntaxNode, OLSyntaxNode >( OLSyntaxNodeCreator
				.createNodeSumProductExpression( new ConstantStringExpression( null, "age" ) ), null ) );
		AssignStatement assignmentNodeX = new AssignStatement( null, pathNodeX, OLSyntaxNodeCreator
				.createNodeBasicExpression( new VariableExpressionNode( null, pathNodePersonAgeExp1 ) ) );
		nodes.add( assignmentNodeX );

		// key = \"age\"
		VariablePathNode pathNodeKey = new VariablePathNode( null, VariablePathNode.Type.NORMAL );
		pathNodeKey.append( new Pair<>( new ConstantStringExpression( null, "key" ), null ) );
		AssignStatement assignmentNodeKey = new AssignStatement( null, pathNodeKey, OLSyntaxNodeCreator
				.createNodeBasicExpression( new ConstantStringExpression( null, "age" ) ) );
		nodes.add( assignmentNodeKey );


		// y = person.(key)
		VariablePathNode pathNodeY = new VariablePathNode( null, VariablePathNode.Type.NORMAL );
		pathNodeY.append( new Pair<>( new ConstantStringExpression( null, "y" ), null ) );
		VariablePathNode pathNodeYExp1 = new VariablePathNode( null, VariablePathNode.Type.NORMAL );
		pathNodeYExp1.append( new Pair< OLSyntaxNode, OLSyntaxNode >(
				new ConstantStringExpression( null, "person" ), null ) );
		pathNodeYExp1.append(
				new Pair< OLSyntaxNode, OLSyntaxNode >( OLSyntaxNodeCreator.createNodeSumProductExpression(
						new VariableExpressionNode( null, pathNodeKey ) ), null ) );
		AssignStatement assignmentNodeY = new AssignStatement( null, pathNodeY, OLSyntaxNodeCreator
				.createNodeBasicExpression( new VariableExpressionNode( null, pathNodeYExp1 ) ) );
		nodes.add( assignmentNodeY );


		// z = person.("a" + "ge")
		VariablePathNode pathNodeZ = new VariablePathNode( null, VariablePathNode.Type.NORMAL );
		ProductExpressionNode stringA = new ProductExpressionNode( null );
		stringA.multiply( new ConstantStringExpression( null, "a" ) );
		ProductExpressionNode stringGE = new ProductExpressionNode( null );
		stringGE.multiply( new ConstantStringExpression( null, "ge" ) );
		SumExpressionNode stringAGE = new SumExpressionNode( null );
		stringAGE.add( stringA );
		stringAGE.add( stringGE );
		pathNodeZ.append( new Pair<>( new ConstantStringExpression( null, "z" ), null ) );
		VariablePathNode pathNodeZExp1 = new VariablePathNode( null, VariablePathNode.Type.NORMAL );
		pathNodeZExp1.append( new Pair< OLSyntaxNode, OLSyntaxNode >(
				new ConstantStringExpression( null, "person" ), null ) );
		pathNodeZExp1.append( new Pair< OLSyntaxNode, OLSyntaxNode >( stringAGE, null ) );
		AssignStatement assignmentNodeZ = new AssignStatement( null, pathNodeZ, OLSyntaxNodeCreator
				.createNodeBasicExpression( new VariableExpressionNode( null, pathNodeZExp1 ) ) );
		nodes.add( assignmentNodeZ );

		ParallelStatement child = OLSyntaxNodeCreator.createNodeBasicProcess( nodes );
		DefinitionNode expected = new DefinitionNode( null, "a", child );

		Program p = olParser.parse();
		assertTrue(iv.programHasOLSyntaxNode(p, expected));

	}



	@Test
	void testInLineTreePath() throws Exception
	{

		String code = "define a{ a << {a=1 b=2 c=3} }";
		is = new ByteArrayInputStream( code.getBytes() );
        InstanceCreator oc = new InstanceCreator( new String[] {"dist/jolie/include"} );

		OLParser olParser = oc.createOLParser( is );


		Program p = olParser.parse();
		assertEquals(((DefinitionNode) p.children().get(0)).body().toString(),  "a << {a=1 b=2 c=3}");
	}


	@Test
	void testInstanceOfPort() throws Exception
	{

		String code = "define a{ b = \"aa\"; c = b instanceof port }";
		is = new ByteArrayInputStream( code.getBytes() );
        InstanceCreator oc = new InstanceCreator( new String[] {"dist/jolie/include"} );

		OLParser olParser = oc.createOLParser( is );


		Program p = olParser.parse();
	}
	// TODO NotificationMessage NDType, iteration, deterministic choice, parallel, sequence,
	// init,
	// main

	@AfterEach
	void closeSteam() throws IOException
	{
		is.close();
	}
}
