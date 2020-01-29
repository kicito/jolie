package jolie.lang.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import jolie.lang.parse.ast.InputPortInfo;
import jolie.lang.parse.ast.InterfaceDefinition;
import jolie.lang.parse.ast.NullProcessStatement;
import jolie.lang.parse.ast.OLSyntaxNode;
import jolie.lang.parse.ast.OutputPortInfo;
import jolie.lang.parse.ast.Program;
import jolie.lang.parse.ast.RequestResponseOperationDeclaration;
import jolie.lang.parse.ast.types.TypeDefinition;
import jolie.lang.parse.ast.types.TypeInlineDefinition;
import jolie.util.Range;

public class TestDeploymentConstruct
{

	InputStream is;
	InspectorVisitor iv = new InspectorVisitor();


	@Test
	void testInclude() throws Exception
	{
		String code = "include \"date.ol\"";
		this.is = new ByteArrayInputStream( code.getBytes() );
		InstanceCreator oc = new InstanceCreator( new String[] {"simple-import/types/modules"} );
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
		String code =
				"decl service doubleService{}";
		this.is = new ByteArrayInputStream( code.getBytes() );
		InstanceCreator oc = new InstanceCreator( new String[] {} );
		OLParser olParser = oc.createOLParser( is );
		Program p = olParser.parse();
	}

	@Test
	@MethodSource("importStatementTestProvider")

	void testImportStatements( String code, OLSyntaxNode[] expectedArr ) throws Exception
	{
		this.is = new ByteArrayInputStream( code.getBytes() );
		InstanceCreator oc = new InstanceCreator( new String[] {} );
		OLParser olParser = oc.createOLParser( is );
		Program p = olParser.parse();

		for (OLSyntaxNode expected : expectedArr) {
			assertTrue( iv.programHasOLSyntaxNode( p, expected ) );
		}
	}


	@ParameterizedTest
	@MethodSource("importStatementExceptionTestProvider")
	void testImportStatementExceptions( String code, String errorMessage )
			throws RuntimeException, IOException, URISyntaxException
	{
		this.is = new ByteArrayInputStream( code.getBytes() );
		InstanceCreator oc = new InstanceCreator( new String[] {} );
		OLParser olParser = oc.createOLParser( is );

		Exception exception = assertThrows( ParserException.class, () -> olParser.parse() );
		assertTrue( exception.getMessage().contains( errorMessage ) );
	}


	private static Stream< Arguments > importStatementTestProvider()
	{

		TypeInlineDefinition expected1 =
				new TypeInlineDefinition( null, "A", NativeType.INT, Constants.RANGE_ONE_TO_ONE );
		expected1.setDocumentation( "" );

		TypeInlineDefinition expected2 = new TypeInlineDefinition( null, "B", NativeType.STRING,
				Constants.RANGE_ONE_TO_ONE );
		expected1.setDocumentation( "" );

		return Stream.of(
				Arguments.of( "import A from \"simple-import/importstatement-test.ol\"",
						new OLSyntaxNode[] {expected1} ),
				Arguments.of( "import A from \"simple-import/importstatement-test.ol\"",
						new OLSyntaxNode[] {expected1, expected2} ) );
	}

	private static Stream< Arguments > importStatementExceptionTestProvider()
	{
		return Stream.of(
				Arguments.of( "import AA from \"simple-import/importstatement-test.ol\"",
						"AA not found in" ),
				Arguments.of( "import AA from \"somewhere\"", "FileNotFoundException" ),
				Arguments.of( "import AA \"somewhere\"", "expected \"from\" for an import statement" ) );
	}

	@AfterEach
	void closeSteam() throws IOException
	{
		this.is.close();
	}

}
