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

		URL src = getClass().getClassLoader().getResource( "service/simple/main.ol" );
		is = src.openStream();

		InstanceCreator oc = new InstanceCreator( new String[] {} );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );

		olParser.parse();

		assertEquals( olParser.services.size(), 2 );
	}

	@Test
	void simpleEmbedService() throws Exception
	{

		URL src = getClass().getClassLoader().getResource( "service/embed/main.ol" );
		is = src.openStream();

		InstanceCreator oc = new InstanceCreator( new String[] {} );

		OLParser olParser = oc.createOLParser( new Scanner( is, src.toURI(), null ) );

		olParser.parse();

		assertEquals( olParser.services.size(), 2 );
	}


	@AfterEach
	void closeStream() throws IOException
	{
		is.close();
	}
}
