package jolie.lang.parse;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import jolie.lang.Constants;
import jolie.lang.NativeType;
import jolie.lang.parse.ast.AssignStatement;
import jolie.lang.parse.ast.InterfaceDefinition;
import jolie.lang.parse.ast.OLSyntaxNode;
import jolie.lang.parse.ast.Program;
import jolie.lang.parse.ast.RequestResponseOperationDeclaration;
import jolie.lang.parse.ast.VariablePathNode;
import jolie.lang.parse.ast.VariablePathNode.Type;
import jolie.lang.parse.ast.expression.ConstantIntegerExpression;
import jolie.lang.parse.ast.expression.ConstantStringExpression;
import jolie.lang.parse.ast.types.TypeChoiceDefinition;
import jolie.lang.parse.ast.types.TypeDefinition;
import jolie.lang.parse.ast.types.TypeInlineDefinition;
import jolie.util.Pair;

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
	void testSimpleImport() throws Exception
	{
		URL src = getClass().getClassLoader().getResource( "jolie2/import/simple-import/types/main.ol" );
		is = src.openStream();
		// String importPrefix = src.getPath() + "#";
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
		expected.setDocumentation( "" );
		expectedChild1.setDocumentation( "" );
		expectedChild2.setDocumentation( "" );
		expectedChild3.setDocumentation( "" );
		expected.putSubType( expectedChild1 );
		expected.putSubType( expectedChild2 );
		expected.putSubType( expectedChild3 );

		Program p = olParser.parse();


		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();
	}

	@Test
	void testSimpleImportAs() throws Exception
	{
		URL src = getClass().getClassLoader().getResource( "jolie2/import/simple-import/types/main-as.ol" );
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

		Program p = olParser.parse();


		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();
	}

	@Test
	void testSimpleImportUrl() throws Exception
	{
		URL src = getClass().getClassLoader().getResource( "jolie2/import/simple-import/types/main-url.ol" );
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
		expected.setDocumentation( "" );
		expectedChild1.setDocumentation( "" );
		expectedChild2.setDocumentation( "" );
		expectedChild3.setDocumentation( "" );
		expected.putSubType( expectedChild1 );
		expected.putSubType( expectedChild2 );
		expected.putSubType( expectedChild3 );

		Program p = olParser.parse();


		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();
	}

	@Test
	void testImportTypeLink() throws Exception
	{

		URL src = getClass().getClassLoader().getResource( "jolie2/import/simple-import/types-linked/main.ol" );
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
		expected.setDocumentation( "" );
		expectedChild1.setDocumentation( "" );
		expectedChild2.setDocumentation( "" );
		expectedChild3.setDocumentation( "" );
		expected.putSubType( expectedChild1 );
		expected.putSubType( expectedChild2 );
		expected.putSubType( expectedChild3 );

		Program p = olParser.parse();

		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();
	}

	@Test
	void testTypeChoiceImport() throws Exception
	{

		URL src = getClass().getClassLoader().getResource( "jolie2/import/simple-import/types-sum/main.ol" );
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
		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();

	}

	@Test
	void testInterfaceImport() throws Exception
	{
		URL src = getClass().getClassLoader().getResource( "jolie2/import/simple-import/interfaces/main.ol" );
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

		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();

	}



	@Test
	void testProcedureDefinitionImport() throws Exception
	{
		URL src = getClass().getClassLoader()
				.getResource( "jolie2/import/simple-import/define/main.ol" );
		is = src.openStream();
		InstanceCreator oc = new InstanceCreator( new String[] {} );
		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );

		VariablePathNode aPath = new VariablePathNode( null, Type.NORMAL );
		aPath.append( new Pair< OLSyntaxNode, OLSyntaxNode >(
				new ConstantStringExpression( null, "a" ), null ) );
		OLSyntaxNode expectedExpression = OLSyntaxNodeCreator
				.createNodeBasicExpression( new ConstantIntegerExpression( null, 2 ) );
		AssignStatement expected = new AssignStatement( null, aPath, expectedExpression );

		Program p = olParser.parse();

		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();

	}


	@Test
	void testServiceImport() throws Exception
	{
		URL src = getClass().getClassLoader().getResource( "jolie2/import/simple-import/service/main.ol" );
		is = src.openStream();
		InstanceCreator oc = new InstanceCreator( new String[] {} );
		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );

		Program p = olParser.parse();
		configuration.setCheckForMain( false );

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();
	}

	@Test
	void testNestedImport() throws Exception
	{
		URL src = getClass().getClassLoader().getResource( "jolie2/import/nested-import/types/main.ol" );
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
		expected.setDocumentation( "" );
		expectedChild1.setDocumentation( "" );
		expectedChild2.setDocumentation( "" );
		expectedChild3.setDocumentation( "" );
		expected.putSubType( expectedChild1 );
		expected.putSubType( expectedChild2 );
		expected.putSubType( expectedChild3 );

		Program p = olParser.parse();


		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();

	}


	@Test
	void testDAGImport() throws Exception
	{
		URL src = getClass().getClassLoader().getResource( "jolie2/import/dag-import/main.ol" );
		is = src.openStream();
		InstanceCreator oc = new InstanceCreator( new String[] {} );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );

		TypeInlineDefinition expectedDate = new TypeInlineDefinition( null, "date", NativeType.VOID,
				Constants.RANGE_ONE_TO_ONE );
		TypeInlineDefinition expectedDateChild1 = new TypeInlineDefinition( null, "month",
				NativeType.INT, Constants.RANGE_ONE_TO_ONE );
		TypeInlineDefinition expectedDateChild2 =
				new TypeInlineDefinition( null, "day", NativeType.INT, Constants.RANGE_ONE_TO_ONE );
		TypeInlineDefinition expectedDateChild3 = new TypeInlineDefinition( null, "year",
				NativeType.INT, Constants.RANGE_ONE_TO_ONE );
		expectedDate.setDocumentation( "" );
		expectedDateChild1.setDocumentation( "" );
		expectedDateChild2.setDocumentation( "" );
		expectedDateChild3.setDocumentation( "" );
		expectedDate.putSubType( expectedDateChild1 );
		expectedDate.putSubType( expectedDateChild2 );
		expectedDate.putSubType( expectedDateChild3 );


		TypeInlineDefinition expected1 = new TypeInlineDefinition( null, "Birthday",
				NativeType.VOID, Constants.RANGE_ONE_TO_ONE );
		TypeInlineDefinition expected1Child = new TypeInlineDefinition( null, "person_name",
				NativeType.STRING, Constants.RANGE_ONE_TO_ONE );
		expected1.setDocumentation( "" );
		expected1Child.setDocumentation( "" );


		TypeInlineDefinition expected2 = new TypeInlineDefinition( null, "Holiday", NativeType.VOID,
				Constants.RANGE_ONE_TO_ONE );
		TypeInlineDefinition expected2Child = new TypeInlineDefinition( null, "holiday_name",
				NativeType.STRING, Constants.RANGE_ONE_TO_ONE );
		expected2.setDocumentation( "" );
		expected2Child.setDocumentation( "" );

		expected1.putSubType( expected1Child );
		expected1.putSubType( expectedDate );
		expected2.putSubType( expected2Child );
		expected2.putSubType( expectedDate );

		Program p = olParser.parse();

		assertTrue( iv.programHasOLSyntaxNode( p, expected1 ) );
		assertTrue( iv.programHasOLSyntaxNode( p, expected2 ) );


		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();
	}



	@Test
	void testNameSpaceImport() throws Exception
	{
		URL src = getClass().getClassLoader().getResource( "jolie2/import/simple-import/namespace/main.ol" );

		is = src.openStream();
		InstanceCreator oc = new InstanceCreator( new String[] {} );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );

		TypeInlineDefinition expected = new TypeInlineDefinition( null, "A", NativeType.STRING,
				Constants.RANGE_ONE_TO_ONE );

		InterfaceDefinition expected2 = new InterfaceDefinition( null, "B" );
		TypeDefinition intType =
				new TypeInlineDefinition( null, "int", NativeType.INT, Constants.RANGE_ONE_TO_ONE );
		RequestResponseOperationDeclaration expectedChild = new RequestResponseOperationDeclaration(
				null, "twice", intType, intType, new HashMap< String, TypeDefinition >() );
		expectedChild.setDocumentation( "" );
		expected2.addOperation( expectedChild );

		expected.setDocumentation( "" );
		expected2.setDocumentation( "" );
		Program p = olParser.parse();

		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );
		assertTrue( iv.programHasOLSyntaxNode( p, expected2 ) );

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();
	}

	@AfterEach
	void closeStream() throws IOException
	{
		is.close();
	}
}
