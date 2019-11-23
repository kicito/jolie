package jolie.lang.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.commons.util.StringUtils;
import jolie.lang.parse.ast.Program;
import jolie.lang.parse.util.ParsingUtils;

public class TestModule
{

    @Test
    @DisplayName("1 + 1 = 2")
    void addsTwoNumbers()
    {
        assertEquals( 2, 1 + 2, "1 + 1 should equal 2" );
    }

    @ParameterizedTest
    @ValueSource(strings = {"include \"console.iol\""})
    void testImportStatements( String code ) throws IOException
    {
        InputStream targetStream = new ByteArrayInputStream( code.getBytes() );
        try {
            OLParser olParser = new OLParser(
                    new Scanner( targetStream, new URI( "testImportStatements" ),
                            StandardCharsets.UTF_8.name(), false ),
                    new String[] {},
                    TestModule.class.getClassLoader() );
            Program p = olParser.parse();

            System.out.println( p );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            targetStream.close();
        }


    }
}
