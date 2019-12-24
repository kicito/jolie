package jolie.lang.parse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import jolie.lang.parse.ast.Program;

class TestModuleResolver
{

    InputStream is;

    @Test
    void testInstanceOf() throws IOException, URISyntaxException, ParserException
    {
        is = getClass().getClassLoader().getResourceAsStream( "simple-import/types/main.ol" );
        String resourcePath =
                getClass().getClassLoader().getResource( "." ).getPath() + "simple-import/types/";
        InstanceCreator oc = new InstanceCreator( new String[] {resourcePath} );
        OLParser olParser = oc.createOLParser( is );
        Program p = olParser.parse();

        ModuleSolver ms = oc.createModuleSolver( new HashMap< String, Scanner.Token >() );

        p = ms.solve( p );
    }

    @AfterEach
    void closeStream() throws IOException
    {
        is.close();
    }
}
