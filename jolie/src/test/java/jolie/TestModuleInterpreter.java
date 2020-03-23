package jolie;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestModuleInterpreter
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
    void testImportTypesJolie()
    {
        String filePath = "jolie2/import/simple/types/main.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;

        assertDoesNotThrow( () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );
        assertTrue( systemOutContent.toString().contains( "true" ) );
    }

    @Test
    void testImportTypeLink()
    {
        String filePath = "jolie2/import/simple/types-linked/main.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;
        assertDoesNotThrow( () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );
        assertTrue( systemOutContent.toString().contains( "true" ) );
    }

    @Test
    void testTypeChoiceImport()
    {
        String filePath = "jolie2/import/simple/types-sum/main.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;
        assertDoesNotThrow( () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );
        assertTrue( systemOutContent.toString().contains( "true" ) );
    }


    @Test
    void testDAGImport()
    {
        String filePath = "jolie2/import/dag/main.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;
        assertDoesNotThrow( () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );
        assertTrue( systemOutContent.toString().contains( "bDay = true" ) );
        assertTrue( systemOutContent.toString().contains( "hDay = true" ) );
    }

    @Test
    void testInterfaceImport()
    {

        String serverFilePath = "jolie2/import/simple/interfaces/server.ol";
        String clientFilePath = "jolie2/import/simple/interfaces/client.ol";
        String[] serverArgs = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, serverArgs, 0, launcherArgs.length );
        serverArgs[serverArgs.length - 1] = serverFilePath;

        String[] clientArgs = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, clientArgs, 0, launcherArgs.length );
        clientArgs[clientArgs.length - 1] = clientFilePath;

        Thread serverThread = new Thread( () -> {
            assertDoesNotThrow(
                    () -> JolieRunner.run( serverArgs, this.getClass().getClassLoader(), null ) );
            assertTrue(
                    systemOutContent.toString().contains( "received " + 5 + ", return " + 10 ) );
        } );

        Thread clientThread = new Thread( () -> {
            assertDoesNotThrow(
                    () -> JolieRunner.run( clientArgs, this.getClass().getClassLoader(), null ) );
            assertTrue( systemOutContent.toString().contains( "10" ) );
        } );
        serverThread.start();
        clientThread.start();
        assertDoesNotThrow( () -> clientThread.join() );
        assertDoesNotThrow( () -> serverThread.join() );
    }

    // @Test
    // void testEmbedConsoleService()
    //         throws CommandLineException, IOException, InterpreterException, InterruptedException
    // {

    //     String filePath = "jolie2/import/services/import_lib_module_console.ol";
    //     String[] args = new String[launcherArgs.length + 1];
    //     System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
    //     args[args.length - 1] = filePath;


    //     assertDoesNotThrow( () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );
    //     assertTrue( systemOutContent.toString().contains( "Hello" ) );

    // }
}
