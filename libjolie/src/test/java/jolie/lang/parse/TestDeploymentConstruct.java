package jolie.lang.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import jolie.lang.Constants;
import jolie.lang.NativeType;
import jolie.lang.parse.ast.ImportStatement;
import jolie.lang.parse.ast.InputPortInfo;
import jolie.lang.parse.ast.InterfaceDefinition;
import jolie.lang.parse.ast.NullProcessStatement;
import jolie.lang.parse.ast.OLSyntaxNode;
import jolie.lang.parse.ast.OutputPortInfo;
import jolie.lang.parse.ast.Program;
import jolie.lang.parse.ast.RequestResponseOperationDeclaration;
import jolie.lang.parse.ast.types.TypeDefinition;
import jolie.lang.parse.ast.types.TypeInlineDefinition;
import jolie.util.Pair;
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

		assertTrue( iv.programHasOLSyntaxNode(p, expected) );
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

		assertTrue( iv.programHasOLSyntaxNode(p, expected) );
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

		assertTrue( iv.programHasOLSyntaxNode(p, expected) );
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

		assertTrue( iv.programHasOLSyntaxNode(p, expected) );
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

		assertTrue( iv.programHasOLSyntaxNode(p, expected) );
	}

	@ParameterizedTest
	@DisplayName("Import syntax")
	@CsvSource({"import A from \"A.ol\", false, 'A', A.ol", "import * from \"A.ol\", true, , A.ol",
			"'import A,B,C,D from \"mul_ID.ol\"', false, 'A,B,C,D' , mul_ID.ol",
			"import A as local from \"mul_ID.ol\", false, 'A as local' , mul_ID.ol",
			"'import A as local, B as localB from \"mul_ID.ol\"', false, 'A as local, B as localB' , mul_ID.ol"})
	void testImportStatements( String code, boolean expectedNameSpaceImport,
			String expectedPathNodes, String expectedTargetFile ) throws Exception
	{
		ImportStatement expected;
		if ( expectedPathNodes != null && !expectedPathNodes.isEmpty() ) {
			String[] expectedPaths = expectedPathNodes.split( "," );
			List< Pair< String, String > > pathNodes = new ArrayList<>();
			for (String path : expectedPaths) {
				String[] pathSplited = path.split( "as" );
				String targetID = pathSplited[0].trim();
				String localID =
						pathSplited.length == 2 ? pathSplited[1].trim() : pathSplited[0].trim();
				pathNodes.add( new Pair< String, String >( targetID, localID ) );
			}
			expected = new ImportStatement( null, expectedTargetFile, expectedNameSpaceImport,
					pathNodes );
		} else {
			expected = new ImportStatement( null, expectedTargetFile );
		}
		this.is = new ByteArrayInputStream( code.getBytes() );
		InstanceCreator oc = new InstanceCreator( new String[] {} );
		OLParser olParser = oc.createOLParser( is );

		Program p = olParser.parse();
		assertTrue( iv.programHasOLSyntaxNode(p, expected) );
	}

	@AfterEach
	void closeSteam() throws IOException
	{
		this.is.close();
	}

}
