package jolie.lang.parse;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import jolie.lang.Constants;
import jolie.lang.NativeType;
import jolie.lang.parse.ast.InterfaceDefinition;
import jolie.lang.parse.ast.Program;
import jolie.lang.parse.ast.RequestResponseOperationDeclaration;
import jolie.lang.parse.ast.types.TypeChoiceDefinition;
import jolie.lang.parse.ast.types.TypeDefinition;
import jolie.lang.parse.ast.types.TypeDefinitionLink;
import jolie.lang.parse.ast.types.TypeInlineDefinition;

class TestModuleResolver
{

	InputStream is;
	InspectorVisitor iv = new InspectorVisitor();

	static SemanticVerifier.Configuration configuration = new SemanticVerifier.Configuration();

	@BeforeAll
	static void setUpConfig()
	{
		configuration.setCheckForMain( false );
	}

	@Test
	void testInstanceOf() throws Exception
	{
		URL src = getClass().getClassLoader().getResource( "simple-import/types/main.ol" );
		is = src.openStream();

		InstanceCreator oc = new InstanceCreator( new String[] {} );
		// ModuleSolver ms = oc.createModuleSolver(
		// Paths.get( URI.create( src.toString() ) ).getParent().toString(),
		// new HashMap< String, Scanner.Token >() );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );

		TypeInlineDefinition expected = new TypeInlineDefinition( null, "Date", NativeType.VOID,
				Constants.RANGE_ONE_TO_ONE );
		TypeInlineDefinition expectedChild1 = new TypeInlineDefinition( null, "month",
				NativeType.INT, Constants.RANGE_ONE_TO_ONE );
		TypeInlineDefinition expectedChild2 =
				new TypeInlineDefinition( null, "day", NativeType.INT, Constants.RANGE_ONE_TO_ONE );
		TypeInlineDefinition expectedChild3 = new TypeInlineDefinition( null, "year",
				NativeType.INT, Constants.RANGE_ONE_TO_ONE );
		expected.setDocumentation( "" );
		expectedChild1.setDocumentation( "" );
		expectedChild2.setDocumentation( "" );
		expectedChild3.setDocumentation( "" );
		expected.putSubType( expectedChild1 );
		expected.putSubType( expectedChild2 );
		expected.putSubType( expectedChild3 );


		Program p = olParser.parse();


		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );

		SemanticVerifier semanticVerifier = new SemanticVerifier( p );
		semanticVerifier.validate();
	}

	@Test
	void testImportTypeLink() throws Exception
	{

		URL src = getClass().getClassLoader().getResource( "simple-import/types-linked/main.ol" );
		is = src.openStream();

		InstanceCreator oc = new InstanceCreator( new String[] {} );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );

		TypeInlineDefinition expected =
				new TypeInlineDefinition( null, "d", NativeType.VOID, Constants.RANGE_ONE_TO_ONE );
		TypeInlineDefinition expectedChild1 = new TypeInlineDefinition( null, "month",
				NativeType.INT, Constants.RANGE_ONE_TO_ONE );
		TypeInlineDefinition expectedChild2 =
				new TypeInlineDefinition( null, "day", NativeType.INT, Constants.RANGE_ONE_TO_ONE );
		TypeInlineDefinition expectedChild3 = new TypeInlineDefinition( null, "year",
				NativeType.INT, Constants.RANGE_ONE_TO_ONE );
		expected.setDocumentation( "" );
		expectedChild1.setDocumentation( "" );
		expectedChild2.setDocumentation( "" );
		expectedChild3.setDocumentation( "" );
		expected.putSubType( expectedChild1 );
		expected.putSubType( expectedChild2 );
		expected.putSubType( expectedChild3 );
		TypeDefinitionLink expected2 =
				new TypeDefinitionLink( null, "date", Constants.RANGE_ONE_TO_ONE, "d" );

		Program p = olParser.parse();

		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );
		assertTrue( iv.programHasOLSyntaxNode( p, expected2 ) );

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();
	}

	@Test
	void testTypeChoiceImport() throws Exception
	{

		URL src = getClass().getClassLoader().getResource( "simple-import/types-sum/main.ol" );
		is = src.openStream();
		InstanceCreator oc = new InstanceCreator( new String[] {} );

		TypeInlineDefinition expectedChild1 = new TypeInlineDefinition( null, "CustomType",
				NativeType.VOID, Constants.RANGE_ONE_TO_ONE );
		TypeInlineDefinition expectedChild2 = new TypeInlineDefinition( null, "CustomType",
				NativeType.BOOL, Constants.RANGE_ONE_TO_ONE );
		TypeInlineDefinition expectedChild3 = new TypeInlineDefinition( null, "CustomType",
				NativeType.INT, Constants.RANGE_ONE_TO_ONE );

		TypeChoiceDefinition expected =
				new TypeChoiceDefinition( null, "CustomType", Constants.RANGE_ONE_TO_ONE,
						expectedChild1, new TypeChoiceDefinition( null, "CustomType",
								Constants.RANGE_ONE_TO_ONE, expectedChild2, expectedChild3 ) );
		expected.setDocumentation( "" );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );

		Program p = olParser.parse();

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();
		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );

	}

	@Test
	void testInterfaceImport() throws Exception
	{
		URL src = getClass().getClassLoader()
				// .getResource( "simple-import/interfaces/modules/twiceInterface.ol" );
				.getResource( "simple-import/interfaces/main.ol" );
		is = src.openStream();
		InstanceCreator oc = new InstanceCreator( new String[] {} );
		InterfaceDefinition expected = new InterfaceDefinition( null, "TwiceInterface" );
		expected.setDocumentation( "" );
		TypeDefinition intType =
				new TypeInlineDefinition( null, "int", NativeType.INT, Constants.RANGE_ONE_TO_ONE );
		RequestResponseOperationDeclaration expectedChild = new RequestResponseOperationDeclaration(
				null, "twice", intType, intType, new HashMap< String, TypeDefinition >() );
		expectedChild.setDocumentation( "" );
		expected.addOperation( expectedChild );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );

		Program p = olParser.parse();

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();

		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );

	}

	@Test
	void testNestedImport() throws Exception
	{
		URL src = getClass().getClassLoader().getResource( "nested-import/types/main.ol" );
		is = src.openStream();
		InstanceCreator oc = new InstanceCreator( new String[] {} );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );
		TypeInlineDefinition expected1 =
				new TypeInlineDefinition( null, "day", NativeType.INT, Constants.RANGE_ONE_TO_ONE );
		expected1.setDocumentation( "" );

		TypeInlineDefinition expected2 = new TypeInlineDefinition( null, "Date", NativeType.VOID,
				Constants.RANGE_ONE_TO_ONE );
		TypeInlineDefinition expectedChild1 = new TypeInlineDefinition( null, "month",
				NativeType.INT, Constants.RANGE_ONE_TO_ONE );
		TypeDefinitionLink expectedChild2 =
				new TypeDefinitionLink( null, "day", Constants.RANGE_ONE_TO_ONE, "day" );
		TypeInlineDefinition expectedChild3 = new TypeInlineDefinition( null, "year",
				NativeType.INT, Constants.RANGE_ONE_TO_ONE );
		expected2.setDocumentation( "" );
		expectedChild1.setDocumentation( "" );
		expectedChild2.setDocumentation( "" );
		expectedChild3.setDocumentation( "" );
		expected2.putSubType( expectedChild1 );
		expected2.putSubType( expectedChild2 );
		expected2.putSubType( expectedChild3 );

		Program p = olParser.parse();

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();

		assertTrue( iv.programHasOLSyntaxNode( p, expected1 ) );
		assertTrue( iv.programHasOLSyntaxNode( p, expected2 ) );

	}


	@Test
	void testDAGImport() throws Exception
	{
		URL src = getClass().getClassLoader().getResource( "dag-import/main.ol" );
		is = src.openStream();
		InstanceCreator oc = new InstanceCreator( new String[] {} );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );

		TypeInlineDefinition expected = new TypeInlineDefinition( null, "Date", NativeType.VOID,
				Constants.RANGE_ONE_TO_ONE );
		TypeInlineDefinition expectedChild1 = new TypeInlineDefinition( null, "month",
				NativeType.INT, Constants.RANGE_ONE_TO_ONE );
		TypeInlineDefinition expectedChild2 =
				new TypeInlineDefinition( null, "day", NativeType.INT, Constants.RANGE_ONE_TO_ONE );
		TypeInlineDefinition expectedChild3 = new TypeInlineDefinition( null, "year",
				NativeType.INT, Constants.RANGE_ONE_TO_ONE );

		TypeDefinitionLink expectedDateLink =
				new TypeDefinitionLink( null, "date", Constants.RANGE_ONE_TO_ONE, "Date" );

		TypeInlineDefinition expected2 = new TypeInlineDefinition( null, "Birthday",
				NativeType.VOID, Constants.RANGE_ONE_TO_ONE );
		TypeInlineDefinition expected2Child = new TypeInlineDefinition( null, "person_name",
				NativeType.STRING, Constants.RANGE_ONE_TO_ONE );

		TypeInlineDefinition expected3 = new TypeInlineDefinition( null, "Holiday", NativeType.VOID,
				Constants.RANGE_ONE_TO_ONE );
		TypeInlineDefinition expected3Child = new TypeInlineDefinition( null, "holiday_name",
				NativeType.STRING, Constants.RANGE_ONE_TO_ONE );

		expected.setDocumentation( "" );
		expected2.setDocumentation( "" );
		expected3.setDocumentation( "" );
		expectedChild1.setDocumentation( "" );
		expectedChild2.setDocumentation( "" );
		expectedChild3.setDocumentation( "" );
		expected2Child.setDocumentation( "" );
		expected3Child.setDocumentation( "" );
		expected.putSubType( expectedChild1 );
		expected.putSubType( expectedChild2 );
		expected.putSubType( expectedChild3 );
		expected2.putSubType( expected2Child );
		expected3.putSubType( expected3Child );
		expected2.putSubType( expectedDateLink );
		expected3.putSubType( expectedDateLink );

		Program p = olParser.parse();

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();

		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );
		assertTrue( iv.programHasOLSyntaxNode( p, expected2 ) );
		assertTrue( iv.programHasOLSyntaxNode( p, expected3 ) );

	}

	@AfterEach
	void closeStream() throws IOException
	{
		is.close();
	}
}
