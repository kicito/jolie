package jolie;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestJolieSimple
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
    void testInterfaceJolie()
    {
        String filePath = "jolie1/interfacePrint.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;

        assertDoesNotThrow(
            () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );
        assertTrue( systemOutContent.toString().contains( "sodep" ) );
    }

    @Test
    void testInternalServiceJolie()
    {
        String filePath = "jolie1/serviceAsATree.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;
        assertDoesNotThrow(
            () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );
        assertTrue( systemOutContent.toString().contains( "pom.xml" ) );
    }

    
    @Test
    void testTwiceService() throws FileNotFoundException, CommandLineException, IOException,
            InterpreterException, InterruptedException
    {
        String serverFilePath = "jolie1/twice/TwiceService.ol";
        String[] serverArgs = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, serverArgs, 0, launcherArgs.length );
        serverArgs[serverArgs.length - 1] = serverFilePath;

        String clientFilePath = "jolie1/twice/TwiceClient.ol";
        String[] clientArgs = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, clientArgs, 0, launcherArgs.length );
        clientArgs[clientArgs.length - 1] = clientFilePath;

        Thread serverThread = new Thread( () -> {
            assertDoesNotThrow(
                () -> JolieRunner.run( serverArgs, this.getClass().getClassLoader(), null ) );
        } );

        Thread clientThread = new Thread( () -> {
            assertDoesNotThrow(
                () -> JolieRunner.run( clientArgs, this.getClass().getClassLoader(), null ) );
        } );
        serverThread.start();
        clientThread.start();
        clientThread.join();
        serverThread.join();
    }
}
