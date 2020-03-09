package jolie.lang.parse;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import jolie.lang.Constants;
import jolie.lang.NativeType;
import jolie.lang.parse.ast.AssignStatement;
import jolie.lang.parse.ast.InputPortInfo;
import jolie.lang.parse.ast.InterfaceDefinition;
import jolie.lang.parse.ast.NullProcessStatement;
import jolie.lang.parse.ast.OLSyntaxNode;
import jolie.lang.parse.ast.OutputPortInfo;
import jolie.lang.parse.ast.Program;
import jolie.lang.parse.ast.RequestResponseOperationDeclaration;
import jolie.lang.parse.ast.SequenceStatement;
import jolie.lang.parse.ast.VariablePathNode;
import jolie.lang.parse.ast.expression.ConstantIntegerExpression;
import jolie.lang.parse.ast.expression.ConstantStringExpression;
import jolie.lang.parse.ast.types.TypeDefinition;
import jolie.lang.parse.ast.types.TypeInlineDefinition;
import jolie.util.Pair;
import jolie.util.Range;

public class TestDeploymentConstruct
{

	InputStream is;
	InspectorVisitor iv = new InspectorVisitor();



	static SemanticVerifier.Configuration configuration = new SemanticVerifier.Configuration();

	@Test
	void testInclude() throws Exception
	{
		String code = "include \"date.ol\"";
		this.is = new ByteArrayInputStream( code.getBytes() );
		InstanceCreator oc = new InstanceCreator( new String[] {"jolie2/import/simple-import/types/modules"} );
		OLParser olParser = oc.createOLParser( is );

		TypeInlineDefinition expected =
				new TypeInlineDefinition( null, "Date", NativeType.VOID, new Range( 1, 1 ) );
		TypeInlineDefinition expectedChild1 =
				new TypeInlineDefinition( null, "month", NativeType.INT, new Range( 1, 1 ) );
		TypeInlineDefinition expectedChild2 =
				new TypeInlineDefinition( null, "day", NativeType.INT, new Range( 1, 1 ) );
		TypeInlineDefinition expectedChild3 =
				new TypeInlineDefinition( null, "year", NativeType.INT, new Range( 1, 1 ) );
		expected.setDocumentation( "" );
		expectedChild1.setDocumentation( "" );
		expectedChild2.setDocumentation( "" );
		expectedChild3.setDocumentation( "" );
		expected.putSubType( expectedChild1 );
		expected.putSubType( expectedChild2 );
		expected.putSubType( expectedChild3 );
		Program p = olParser.parse();

		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );
	}

	@Test
	void testType() throws Exception
	{
		String code = "type Date {day:int month:int year:int}";
		this.is = new ByteArrayInputStream( code.getBytes() );
		InstanceCreator oc = new InstanceCreator( new String[] {} );
		OLParser olParser = oc.createOLParser( is );
		TypeInlineDefinition expected =
				new TypeInlineDefinition( null, "Date", NativeType.VOID, new Range( 1, 1 ) );
		TypeInlineDefinition expectedChild1 =
				new TypeInlineDefinition( null, "month", NativeType.INT, new Range( 1, 1 ) );
		TypeInlineDefinition expectedChild2 =
				new TypeInlineDefinition( null, "day", NativeType.INT, new Range( 1, 1 ) );
		TypeInlineDefinition expectedChild3 =
				new TypeInlineDefinition( null, "year", NativeType.INT, new Range( 1, 1 ) );
		expected.setDocumentation( "" );
		expectedChild1.setDocumentation( "" );
		expectedChild2.setDocumentation( "" );
		expectedChild3.setDocumentation( "" );
		expected.putSubType( expectedChild1 );
		expected.putSubType( expectedChild2 );
		expected.putSubType( expectedChild3 );


		Program p = olParser.parse();

		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );
	}

	@Test
	void testInterface() throws Exception
	{
		String code = "interface SumInterface { RequestResponse: sum( int )( int )}";
		this.is = new ByteArrayInputStream( code.getBytes() );
		InstanceCreator oc = new InstanceCreator( new String[] {} );
		OLParser olParser = oc.createOLParser( is );

		TypeDefinition intTD =
				new TypeInlineDefinition( null, "int", NativeType.INT, Constants.RANGE_ONE_TO_ONE );
		InterfaceDefinition expected = new InterfaceDefinition( null, "SumInterface" );
		RequestResponseOperationDeclaration od = new RequestResponseOperationDeclaration( null,
				"sum", intTD, intTD, new HashMap<>() );
		expected.addOperation( od );
		od.setDocumentation("");
		expected.setDocumentation( "" );

		Program p = olParser.parse();

		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );
	}

	@Test
	void testInputPort() throws Exception
	{
		String code =
				"inputPort SumInput { Location: \"socket://localhost:8000/\" Protocol: soap RequestResponse:sum(int)(int) }";
		this.is = new ByteArrayInputStream( code.getBytes() );
		InstanceCreator oc = new InstanceCreator( new String[] {} );
		OLParser olParser = oc.createOLParser( is );


		InputPortInfo expected =
				new InputPortInfo( null, "SumInput", new URI( "socket://localhost:8000/" ), "soap",
						((OLSyntaxNode) new NullProcessStatement( null )),
						new InputPortInfo.AggregationItemInfo[] {}, new HashMap<>() );
		TypeDefinition intTD =
				new TypeInlineDefinition( null, "int", NativeType.INT, Constants.RANGE_ONE_TO_ONE );

		// expected.addInterface( iface );
		expected.setDocumentation( "" );
		expected.addOperation( new RequestResponseOperationDeclaration( null, "sum", intTD, intTD,
				new HashMap<>() ) );


		Program p = olParser.parse();

		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );
	}


	@Test
	void testOutputPort() throws Exception
	{
		String code =
				"outputPort SumServ { Location: \"socket://localhost:8000/\" Protocol: soap }";
		this.is = new ByteArrayInputStream( code.getBytes() );
		InstanceCreator oc = new InstanceCreator( new String[] {} );
		OLParser olParser = oc.createOLParser( is );


		OutputPortInfo expected = new OutputPortInfo( null, "SumServ" );
		expected.setLocation( new URI( "socket://localhost:8000/" ) );
		expected.setProtocolId( "soap" );
		expected.setDocumentation( "" );

		Program p = olParser.parse();

		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );
	}

	@Test
	void testService() throws Exception
	{
		String code = "decl service doubleService (){ init{ a = 1 } main{a=2} }";
		this.is = new ByteArrayInputStream( code.getBytes() );
		InstanceCreator oc = new InstanceCreator( new String[] {} );
		OLParser olParser = oc.createOLParser( is );
		SequenceStatement expected = new SequenceStatement( null );
		VariablePathNode pathNodeA = new VariablePathNode( null, VariablePathNode.Type.NORMAL );
		pathNodeA.append( new Pair<>( new ConstantStringExpression( null, "a" ), null ) );

		AssignStatement assignmentNode = new AssignStatement( null, pathNodeA, OLSyntaxNodeCreator
				.createNodeBasicExpression( new ConstantIntegerExpression( null, 1 ) ) );
		SequenceStatement expected2 = new SequenceStatement( null );

		AssignStatement assignmentNode2 = new AssignStatement( null, pathNodeA, OLSyntaxNodeCreator
				.createNodeBasicExpression( new ConstantIntegerExpression( null, 2 ) ) );

		expected.addChild( assignmentNode );
		expected2.addChild( assignmentNode2 );

		Program p = olParser.parse();

		assertNotNull( olParser.services );
		Program program = olParser.services.get( "doubleService" ).program();
		assertTrue( iv.programHasOLSyntaxNode( program, expected ) );
		assertTrue( iv.programHasOLSyntaxNode( program, expected2 ) );


		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();

	}

	@Test
	void testJavaService() throws Exception
	{
		StringBuilder code = new StringBuilder();
		code.append("decl service Java ConsoleService (\"joliex.io.ConsoleService\", outputPort fromClient, inputPort toClient)");
		code.append("{");
		code.append("inputPort IP { OneWay: test(int)}");
		code.append("outputPort Receiver { location: \"local\" OneWay: test(int) }");
		code.append("binding {IP -> fromClient Receiver -> toClient}");
		code.append("}");

		this.is = new ByteArrayInputStream( code.toString().getBytes() );
		InstanceCreator oc = new InstanceCreator( new String[] {} );
		OLParser olParser = oc.createOLParser( is );

		Program p = olParser.parse();

		assertNotNull( olParser.services );

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();

	}

	@Test
	void testGlobalVar() throws IOException, URISyntaxException, ParserException
	{
		
		StringBuilder code = new StringBuilder();
		code.append("var a = 1");
		code.append("var a << {a=1 b=2 c= \"test\"}");

		this.is = new ByteArrayInputStream( code.toString().getBytes() );
		InstanceCreator oc = new InstanceCreator( new String[] {} );
		OLParser olParser = oc.createOLParser( is );

		Program p = olParser.parse();

		// assertNotNull( olParser.services );

		// SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		// semanticVerifier.validate();
	}

	@Test
	void testParameterizeOutputPort() throws IOException, URISyntaxException, ParserException,
			SemanticException
	{
		
		// variable node path
		StringBuilder code = new StringBuilder();
		code.append("outputPort myOP( a )");

		this.is = new ByteArrayInputStream( code.toString().getBytes() );
		InstanceCreator oc = new InstanceCreator( new String[] {} );
		OLParser olParser = oc.createOLParser( is );

		Program p = olParser.parse();

		p = OLParseTreeOptimizer.optimize( p );

		configuration.setCheckForMain(false);
		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();

		// inlinetree

		StringBuilder code2 = new StringBuilder();
		code2.append("outputPort myOP( { location = \"socket://localhost:3000\" protocol = \"http\"			interfaces << \"SumInterface\"{ operations[0] << \"notice\"{ reqType = \"string\" } } } )");

		this.is = new ByteArrayInputStream( code2.toString().getBytes() );
		OLParser olParser2 = oc.createOLParser( is );
		
		Program p2 = olParser2.parse();

		p2 = OLParseTreeOptimizer.optimize( p2 );
		semanticVerifier = new SemanticVerifier( p2, configuration );
		semanticVerifier.validate();

		// assertNotNull( olParser.services );
	}

	@Test
	void testParameterizeInputPort() throws IOException, URISyntaxException, ParserException,
			SemanticException
	{
		
		// variable node path
		StringBuilder code = new StringBuilder();
		code.append("inputPort myIP( a )");

		this.is = new ByteArrayInputStream( code.toString().getBytes() );
		InstanceCreator oc = new InstanceCreator( new String[] {} );
		OLParser olParser = oc.createOLParser( is );

		Program p = olParser.parse();

		p = OLParseTreeOptimizer.optimize( p );

		configuration.setCheckForMain(false);
		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();

		// inlinetree
		StringBuilder code2 = new StringBuilder();
		code2.append("inputPort myIP( { location = \"socket://localhost:3000\" protocol = \"http\"			interfaces << \"SumInterface\"{ operations[0] << \"notice\"{ reqType = \"string\" } } } )");

		this.is = new ByteArrayInputStream( code2.toString().getBytes() );
		OLParser olParser2 = oc.createOLParser( is );
		
		Program p2 = olParser2.parse();

		p2 = OLParseTreeOptimizer.optimize( p2 );
		semanticVerifier = new SemanticVerifier( p2, configuration );
		semanticVerifier.validate();

		// assertNotNull( olParser.services );
	}


	@ParameterizedTest
	@MethodSource("importStatementExceptionTestProvider")
	void testImportStatementExceptions( String code, String errorMessage )
			throws RuntimeException, IOException, URISyntaxException
	{
		this.is = new ByteArrayInputStream( code.getBytes() );
		InstanceCreator oc = new InstanceCreator( new String[] {} );
		OLParser olParser = oc.createOLParser( is );

		Exception exception = assertThrows( ParserException.class, () -> olParser.parse(),
				"Expected parse() to throw, with " + errorMessage + " but it didn't" );
		assertTrue( exception.getMessage().contains( errorMessage ) );
	}

	private static Stream< Arguments > importStatementExceptionTestProvider()
	{
		return Stream.of(
				Arguments.of( "import AA from \"jolie2/import/simple-import/importstatement-test.ol\"",
						"unable to find AA in" ),
				Arguments.of( "import AA from \"somewhere\"", "unable to locate" ), Arguments.of(
						"import AA \"somewhere\"", "expected \"from\" for an import statement" ) );
	}

	@AfterEach
	void closeSteam() throws IOException
	{
		this.is.close();
	}

}
