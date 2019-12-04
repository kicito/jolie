package jolie.lang.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import jolie.lang.parse.ast.ImportStatement;
import jolie.lang.parse.ast.Program;
import jolie.util.Pair;

public class TestModule
{

    @ParameterizedTest
    @DisplayName("Import syntax")
    @CsvSource({"import A from \"A.ol\", false, 'A', A.ol", "import * from \"A.ol\", true, , A.ol",
            "'import A,B,C,D from \"mul_ID.ol\"', false, 'A,B,C,D' , mul_ID.ol",
            "import A as local from \"mul_ID.ol\", false, 'A as local' , mul_ID.ol",
            "'import A as local, B as localB from \"mul_ID.ol\"', false, 'A as local, B as localB' , mul_ID.ol"})
    void testImportStatements( String code, boolean expectedNameSpaceImport,
            String expectedPathNodes, String expectedTargetFile ) throws IOException
    {
        System.out.println( code );

        InputStream targetStream = new ByteArrayInputStream( code.getBytes() );
        try {
            List< Pair< String, String > > pathNodes;
            if ( expectedPathNodes != null && !expectedPathNodes.isEmpty() ) {
                String[] expectedPaths = expectedPathNodes.split( "," );
                pathNodes = new ArrayList<>();
                for (String path : expectedPaths) {
                    String[] pathSplited = path.split( "as" );
                    String targetID = pathSplited[0];
                    String localID = pathSplited.length == 2 ? pathSplited[1].trim() : pathSplited[0].trim();
                    pathNodes.add( new Pair< String, String >( targetID, localID ) );
                }
            } else {
                pathNodes = null;
            }
            ImportStatement expected = new ImportStatement( null, expectedTargetFile,
                    expectedNameSpaceImport, pathNodes );
            OLParser olParser = new OLParser(
                    new Scanner( targetStream, new URI( "testImportStatements" ),
                            StandardCharsets.UTF_8.name(), false ),
                    new String[] {}, TestModule.class.getClassLoader() );
            Program p = olParser.parse();
            assertEquals( p.children().get( 0 ), expected );
        } catch (Exception e) {
            e.printStackTrace();
            fail( "Should not have thrown any exception" );
        } finally {
            targetStream.close();
        }
    }
}
