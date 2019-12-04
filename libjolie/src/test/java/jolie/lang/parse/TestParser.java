package jolie.lang.parse;

import static org.junit.jupiter.api.Assertions.fail;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import jolie.lang.parse.ast.Program;

public class TestParser
{

    @ParameterizedTest
    @DisplayName("define")
    @CsvSource({"define a { b = c }"})
    void testImportStatements( String code ) throws IOException
    {
        System.out.println( code );
        InputStream targetStream = new ByteArrayInputStream( code.getBytes() );
        try {
            OLParser olParser = new OLParser(
                    new Scanner( targetStream, new URI( "test_define" ),
                            StandardCharsets.UTF_8.name(), false ),
                    new String[] {}, TestParser.class.getClassLoader() );
            Program p = olParser.parse();
            System.out.println( p );

            // assertEquals( p.children().get( 0 ), expected );
        } catch (Exception e) {
            e.printStackTrace();
            fail( "Should not have thrown any exception" );
        } finally {
            targetStream.close();
        }
    }
    
    @ParameterizedTest
    @CsvSource({"main{println@Console(\"hi\")()}"})
    void testSolicitedRequest( String code ) throws IOException
    {
        System.out.println( code );
        InputStream targetStream = new ByteArrayInputStream( code.getBytes() );
        try {
            OLParser olParser = new OLParser(
                    new Scanner( targetStream, new URI( "test_define" ),
                            StandardCharsets.UTF_8.name(), false ),
                    new String[] {}, TestParser.class.getClassLoader() );
            Program p = olParser.parse();
            System.out.println( p );

            // assertEquals( p.children().get( 0 ), expected );
        } catch (Exception e) {
            e.printStackTrace();
            fail( "Should not have thrown any exception" );
        } finally {
            targetStream.close();
        }
    }
}
