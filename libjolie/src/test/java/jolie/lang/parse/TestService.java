package jolie.lang.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import jolie.lang.parse.ast.Program;

class TestService
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
	void simpleService() throws Exception
	{

		URL src = getClass().getClassLoader().getResource( "jolie2/services/simple/main.ol" );
		is = src.openStream();

		InstanceCreator oc = new InstanceCreator( new String[] {} );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );

		Program p = olParser.parse();
		p = OLParseTreeOptimizer.optimize( p );

		assertEquals( 2, olParser.services.size() );

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();
	}

	@Test
	void simpleEmbedJolieService() throws Exception
	{

		URL src = getClass().getClassLoader().getResource( "jolie2/services/embed/twice.ol" );
		is = src.openStream();

		InstanceCreator oc = new InstanceCreator( new String[] {} );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );

		Program p = olParser.parse();

		assertEquals( olParser.services.size(), 2 );

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();
	}

	@Test
	void simpleEmbedStringOutputPortService() throws Exception
	{

		URL src = getClass().getClassLoader().getResource( "jolie2/services/embed/embed-output-string.ol" );
		is = src.openStream();

		InstanceCreator oc = new InstanceCreator( new String[] {} );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );

		Program p = olParser.parse();

		assertEquals( olParser.services.size(), 5 );

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();
	}

	@Test
	void simpleEmbedIDOutputPortService() throws Exception
	{

		URL src = getClass().getClassLoader().getResource( "jolie2/services/embed/embed-output-id.ol" );
		is = src.openStream();

		InstanceCreator oc = new InstanceCreator( new String[] {} );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );

		Program p = olParser.parse();

		assertEquals( olParser.services.size(), 2 );

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();
	}

	@Test
	void simpleEmbedLiteralService() throws Exception
	{

		URL src = getClass().getClassLoader().getResource( "jolie2/services/embed/embed-input-literal.ol" );
		is = src.openStream();

		InstanceCreator oc = new InstanceCreator( new String[] {} );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );

		Program p = olParser.parse();

		assertEquals( olParser.services.size(), 2 );

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();
	}


	@Test
	void simpleEmbedMultiArgsService() throws Exception
	{

		URL src = getClass().getClassLoader().getResource( "jolie2/services/embed/embed-multiple-args.ol" );
		is = src.openStream();

		InstanceCreator oc = new InstanceCreator( new String[] {} );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );

		Program p = olParser.parse();

		assertEquals( olParser.services.size(), 2 );

		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );
		semanticVerifier.validate();
	}

	@AfterEach
	void closeStream() throws IOException
	{
		is.close();
	}
}
