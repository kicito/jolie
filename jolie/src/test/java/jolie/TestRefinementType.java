package jolie;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class TestRefinementType
{

    static {
        JolieURLStreamHandlerFactory.registerInVM();
    }
    private static final String[] launcherArgs = new String[] {"-l",
            "../lib/*:../dist/jolie/lib:../dist/jolie/javaServices/*:../dist/jolie/extensions/*"};

    private PrintStream originalSystemOut;
    private ByteArrayOutputStream systemOutContent;

    @BeforeEach
    void redirectSystemOutStream()
    {

        originalSystemOut = System.out;

        // given
        systemOutContent = new ByteArrayOutputStream();
        System.setOut( new PrintStream( systemOutContent ) );
    }

    @AfterEach
    void restoreSystemOutStream()
    {
        System.setOut( originalSystemOut );
        // print buffer
        System.out.println( systemOutContent.toString() );
    }


    @Test
    void testNumberRefinementType()
    {
        String filePath = "jolie2/refinedType/refinedTypeNumber.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;

        assertDoesNotThrow(
                () -> JolieRunner.run(args, this.getClass().getClassLoader(), null) );

        // assertTrue( systemOutContent.toString().contains( "true" ) );
    }

    @Test
    void testStringRefinementType()
    {
        String filePath = "jolie2/refinedType/refinedTypeString.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;

        assertDoesNotThrow(
                () -> JolieRunner.run(args, this.getClass().getClassLoader(), null) );

        assertTrue( systemOutContent.toString().contains( "true" ) );
    }

    @Test
    void testRefinementTypeDefault()
    {
        String filePath = "jolie2/refinedType/serviceTypeDemo.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;

        assertDoesNotThrow(
                () -> JolieRunner.run(args, this.getClass().getClassLoader(), null) );

        assertTrue( systemOutContent.toString().contains( "true" ) );
    }
}
