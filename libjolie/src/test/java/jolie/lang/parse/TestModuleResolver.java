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
import jolie.lang.parse.ast.types.TypeDefinitionLink;
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
		URL src = getClass().getClassLoader().getResource( "simple-import/types/main.ol" );
		is = src.openStream();
		String importPrefix = src.getPath() + "#";
		InstanceCreator oc = new InstanceCreator( new String[] {} );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );
		TypeDefinitionLink expected = new TypeDefinitionLink( null, "Date",
				Constants.RANGE_ONE_TO_ONE, importPrefix + "Date" );

		Program p = olParser.parse();


		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );

		SemanticVerifier semanticVerifier = new SemanticVerifier( p );
		semanticVerifier.validate();
	}

	@Test
	void testSimpleImportAs() throws Exception
	{
		URL src = getClass().getClassLoader().getResource( "simple-import/types/main-as.ol" );
		is = src.openStream();
		String importPrefix = src.getPath() + "#";
		InstanceCreator oc = new InstanceCreator( new String[] {} );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );
		TypeDefinitionLink expected =
				new TypeDefinitionLink( null, "d", Constants.RANGE_ONE_TO_ONE, importPrefix + "d" );

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
		String importPrefix = src.getPath() + "#";

		InstanceCreator oc = new InstanceCreator( new String[] {} );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );

		TypeInlineDefinition expected = new TypeInlineDefinition( null, importPrefix + "d",
				NativeType.VOID, Constants.RANGE_ONE_TO_ONE );
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

		TypeDefinitionLink expected2 = new TypeDefinitionLink( null, importPrefix + "date",
				Constants.RANGE_ONE_TO_ONE, importPrefix + "d" );
		TypeDefinitionLink expected3 = new TypeDefinitionLink( null, "date",
				Constants.RANGE_ONE_TO_ONE, importPrefix + "date" );

		Program p = olParser.parse();

		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );
		assertTrue( iv.programHasOLSyntaxNode( p, expected2 ) );
		assertTrue( iv.programHasOLSyntaxNode( p, expected3 ) );

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();
	}

	@Test
	void testTypeChoiceImport() throws Exception
	{

		URL src = getClass().getClassLoader().getResource( "simple-import/types-sum/main.ol" );
		is = src.openStream();
		String importPrefix = src.getPath() + "#";

		InstanceCreator oc = new InstanceCreator( new String[] {} );

		TypeInlineDefinition expectedChild1 = new TypeInlineDefinition( null, "CustomType",
				NativeType.VOID, Constants.RANGE_ONE_TO_ONE );
		TypeInlineDefinition expectedChild2 = new TypeInlineDefinition( null, "CustomType",
				NativeType.BOOL, Constants.RANGE_ONE_TO_ONE );
		TypeInlineDefinition expectedChild3 = new TypeInlineDefinition( null, "CustomType",
				NativeType.INT, Constants.RANGE_ONE_TO_ONE );

		TypeChoiceDefinition expected = new TypeChoiceDefinition( null, importPrefix + "CustomType",
				Constants.RANGE_ONE_TO_ONE, expectedChild1,
				new TypeChoiceDefinition( null, "CustomType", Constants.RANGE_ONE_TO_ONE,
						expectedChild2, expectedChild3 ) );
		expected.setDocumentation( "" );
		TypeDefinitionLink expected2 = new TypeDefinitionLink( null, "CustomType",
				Constants.RANGE_ONE_TO_ONE, importPrefix + "CustomType" );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );

		Program p = olParser.parse();
		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );
		assertTrue( iv.programHasOLSyntaxNode( p, expected2 ) );

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();

	}

	@Test
	void testInterfaceImport() throws Exception
	{
		URL src = getClass().getClassLoader().getResource( "simple-import/interfaces/main.ol" );
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
	void testProcedureDefinitionImport() throws Exception
	{
		URL src = getClass().getClassLoader()
				// .getResource( "simple-import/define/modules/define.ol" );
				.getResource( "simple-import/define/main.ol" );
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

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();

		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );
	}


	@Test
	void testServiceImport() throws Exception
	{
		URL src = getClass().getClassLoader()
				.getResource( "simple-import/service/main.ol" );
		is = src.openStream();
		InstanceCreator oc = new InstanceCreator( new String[] {} );
		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );
		
		Program p = olParser.parse();


		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();

		// assertTrue( iv.programHasOLSyntaxNode( p, expected ) );
	}

	@Test
	void testNestedImport() throws Exception
	{
		URL src = getClass().getClassLoader().getResource( "nested-import/types/main.ol" );
		is = src.openStream();
		String importPrefix = src.getPath() + "#";

		InstanceCreator oc = new InstanceCreator( new String[] {} );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );
		TypeInlineDefinition expected1 = new TypeInlineDefinition( null, importPrefix + "day",
				NativeType.INT, Constants.RANGE_ONE_TO_ONE );
		expected1.setDocumentation( "" );

		TypeInlineDefinition expected2 = new TypeInlineDefinition( null, importPrefix + "Date",
				NativeType.VOID, Constants.RANGE_ONE_TO_ONE );
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


		assertTrue( iv.programHasOLSyntaxNode( p, expected1 ) );
		assertTrue( iv.programHasOLSyntaxNode( p, expected2 ) );

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();

	}


	@Test
	void testDAGImport() throws Exception
	{
		URL src = getClass().getClassLoader().getResource( "dag-import/main.ol" );
		is = src.openStream();
		String importPrefix = src.getPath() + "#";
		InstanceCreator oc = new InstanceCreator( new String[] {} );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );

		TypeDefinitionLink expected1 = new TypeDefinitionLink( null, "Birthday",
				Constants.RANGE_ONE_TO_ONE, importPrefix + "Birthday" );
		TypeDefinitionLink expected2 = new TypeDefinitionLink( null, "Holiday",
				Constants.RANGE_ONE_TO_ONE, importPrefix + "Holiday" );

		Program p = olParser.parse();

		assertTrue( iv.programHasOLSyntaxNode( p, expected1 ) );
		assertTrue( iv.programHasOLSyntaxNode( p, expected2 ) );


		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();
	}



	@Test
	void testNameSpaceImport() throws Exception
	{
		URL src = getClass().getClassLoader().getResource( "simple-import/namespace/main.ol" );
		String importPrefix = src.getPath() + "#";

		is = src.openStream();
		InstanceCreator oc = new InstanceCreator( new String[] {} );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );

		TypeInlineDefinition expected = new TypeInlineDefinition( null, importPrefix + "A", NativeType.STRING,
				Constants.RANGE_ONE_TO_ONE );

		InterfaceDefinition expected2 = new InterfaceDefinition( null, importPrefix +  "B" );
		TypeDefinition intType =
				new TypeInlineDefinition( null, "int", NativeType.INT, Constants.RANGE_ONE_TO_ONE );
		RequestResponseOperationDeclaration expectedChild = new RequestResponseOperationDeclaration(
				null, "twice", intType, intType, new HashMap< String, TypeDefinition >() );
		expectedChild.setDocumentation( "" );
		expected2.addOperation( expectedChild );

		expected.setDocumentation( "" );
		expected2.setDocumentation( "" );
		Program p = olParser.parse();

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();

		assertTrue( iv.programHasOLSyntaxNode( p, expected ) );
		assertTrue( iv.programHasOLSyntaxNode( p, expected2 ) );
	}

	@AfterEach
	void closeStream() throws IOException
	{
		is.close();
	}
}
