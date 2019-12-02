package jolie.lang.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import jolie.lang.parse.ast.ImportStatement;
import jolie.lang.parse.ast.Program;

public class TestModule
{

    @ParameterizedTest
    @DisplayName("Import syntax")
    @CsvSource({"import A from \"A.ol\", false, 'A', A.ol", "import * from \"A.ol\", true, , A.ol",
            "'import A,B,C,D from \"mul_ID.ol\"', false, 'A,B,C,D' , mul_ID.ol"})
    void testImportStatements( String code, boolean expectedNameSpaceImport,
            String expectedLocalPathNodes, String expectedTargetFile ) throws IOException
    {
        System.out.println(code);
        InputStream targetStream = new ByteArrayInputStream( code.getBytes() );
        try {
            String[] expectedLocalPaths = new String[] {};
            if ( expectedLocalPathNodes != null ) {
                expectedLocalPaths = expectedLocalPathNodes.split( "," );
            } else {
                expectedLocalPaths = null;
            }
            ImportStatement expected = new ImportStatement( null, expectedLocalPaths,
                    expectedTargetFile, expectedNameSpaceImport );
            OLParser olParser = new OLParser(
                    new Scanner( targetStream, new URI( "testImportStatements" ),
                            StandardCharsets.UTF_8.name(), false ),
                    new String[] {}, TestModule.class.getClassLoader() );
            Program p = olParser.parse();
            assertEquals( p.children().get( 0 ), expected );
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not have thrown any exception");
        } finally {
            targetStream.close();
        }
    }
}
