package jolie.lang.parse;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import jolie.lang.parse.ast.Program;

class TestSemanticVerifier
{

	InputStream is;
	InspectorVisitor iv = new InspectorVisitor();


	static SemanticVerifier.Configuration configuration = new SemanticVerifier.Configuration();

	@BeforeAll
	static void setUpConfig()
	{
		configuration.setCheckForMain( false );
	}


	// @Test
	// void testInvalidInlineTreeParameterizePort() throws Exception
	// {
	// 	URL src = getClass().getClassLoader()
	// 			.getResource( "jolie2/parameterizePort/invalidPortType.ol" );
	// 	is = src.openStream();
	// 	InstanceCreator oc = new InstanceCreator( new String[] {} );

	// 	OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );

	// 	String expectedMessage = "expected parameter to be inlinetree to be type of portInfo";
	// 	Program p = olParser.parse();

	// 	p = OLParseTreeOptimizer.optimize( p );
	// 	SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );;

	// 	SemanticException exception = assertThrows( SemanticException.class,
	// 			() -> semanticVerifier.validate(),
	// 			"Expected validate() to throw, with " + expectedMessage + " but it didn't" );
	// 	assertTrue( exception.getErrorMessages().contains( expectedMessage ) );
	// }

	@Test
	void testParsingParameterizePort() throws Exception
	{
		URL src = getClass().getClassLoader().getResource( "jolie2/parameterizePort/ports.ol" );
		is = src.openStream();
		InstanceCreator oc = new InstanceCreator( new String[] {} );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );

		Program p = olParser.parse();

		p = OLParseTreeOptimizer.optimize( p );
		SemanticVerifier semanticVerifier = new SemanticVerifier( p, configuration );;
		semanticVerifier.validate();
	}

	@AfterEach
	void closeStream() throws IOException
	{
		is.close();
	}
}
