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
public class TestService
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

        systemOutContent = new ByteArrayOutputStream();
        System.setOut( new PrintStream( systemOutContent ) );
    }

    @AfterEach
    void restoreSystemOutStream()
    {
        System.setOut( originalSystemOut );
        // print buffer
        System.out.println( systemOutContent.toString() );
        JolieRunner.stop();
    }

    @Test
    void testService()
    {
        String filePath = "jolie2/services/simple.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;
        
        assertDoesNotThrow(
            () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );
    }


    @Test
    void testEmbedService()
    {
        String filePath = "jolie2/services/simple_embed.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;
        
        assertDoesNotThrow(
            () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );
    }

    @Test
    void testEmbedTwiceServiceWithStatic() 
    {
        String serverFilePath = "jolie2/services/twice_static.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = serverFilePath;
        
        assertDoesNotThrow(
            () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );
    }

	@Test
	void simpleEmbedJolieServiceWithStatic()
	{
        String serverFilePath = "jolie2/services/twice_static.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = serverFilePath;
        
        assertDoesNotThrow(
            () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );
	}

    @Test
    void testEmbedTwiceServiceWithPortInfo()
    {
        String serverFilePath = "jolie2/services/twice_portInfo.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = serverFilePath;
        
        assertDoesNotThrow(
            () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );
    }

    @Test
    void testEmbedTwiceService()
    {
        String serverFilePath = "jolie2/services/twice.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = serverFilePath;
        
        assertDoesNotThrow(
            () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );
    }

    @Test
    void testEmbedConsoleService()
    {
        String serverFilePath = "jolie2/services/console_jolie.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = serverFilePath;
        
        assertDoesNotThrow(
            () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );
    }

    @Test
    void testEmbedConsoleJavaService() 
    {
        String serverFilePath = "jolie2/services/console_java.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = serverFilePath;
        assertDoesNotThrow(
            () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );

        assertTrue( systemOutContent.toString().contains( "Hello" ) );

    }
}
