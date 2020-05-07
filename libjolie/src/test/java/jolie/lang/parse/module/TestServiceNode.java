package jolie.lang.parse.module;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import jolie.lang.parse.Scanner;

public class TestServiceNode
{

    private static String BASE_DIR = "imports/";
    private static URL baseDir = TestModuleParser.class.getClassLoader().getResource( BASE_DIR );
    private static String[] includePaths = new String[0];

    @Test
    void testParser() throws IOException, URISyntaxException
    {
        String code = "service console(loc : string){} service console(){} service console{}";
        InputStream is = new ByteArrayInputStream( code.getBytes() );

        ModuleParser parser = new ModuleParser( StandardCharsets.UTF_8.name(), new String[0],
                this.getClass().getClassLoader() );

        Scanner s = new Scanner( is, baseDir.toURI(), null );

        assertDoesNotThrow( () -> parser.parse( s ) );
    }

}
