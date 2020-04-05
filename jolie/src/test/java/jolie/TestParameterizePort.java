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
public class TestParameterizePort
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
    void testInstanceOfPortInfo()
    {
        String filePath = "jolie2/parameterizePort/portType.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;

        assertDoesNotThrow(
                () -> JolieRunner.run(args, this.getClass().getClassLoader(), null) );

        assertTrue( systemOutContent.toString().contains( "true" ) );
    }



    @Test
    void testParameterizeOutputPortInlineTreeParamIface() throws Exception
    {
        String serverFilePath = "jolie2/parameterizePort/outputPort/server.ol";

        String[] serverArgs = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, serverArgs, 0, launcherArgs.length );
        serverArgs[serverArgs.length - 1] = serverFilePath;


        String filePath = "jolie2/parameterizePort/outputPort/InlineTreeParamIface.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;

        Thread serverThread = new Thread( () -> {
            assertDoesNotThrow(
                    () -> JolieRunner.run( serverArgs, this.getClass().getClassLoader(), null ) );
        } );

        Thread clientThread = new Thread( () -> {
            assertDoesNotThrow(
                    () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );
        } );

        serverThread.start();
        clientThread.start();
        clientThread.join();
        serverThread.join();

    }

    @Test
    void testParameterizeInputPortInlineTreeParamIface() throws Exception
    {
        String serverFilePath = "jolie2/parameterizePort/inputPort/InlineTreeParamIface.ol";

        String[] serverArgs = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, serverArgs, 0, launcherArgs.length );
        serverArgs[serverArgs.length - 1] = serverFilePath;

        String filePath = "jolie2/parameterizePort/inputPort/client.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;

        Thread serverThread = new Thread( () -> {
            assertDoesNotThrow(
                    () -> JolieRunner.run( serverArgs, this.getClass().getClassLoader(), null ) );
        } );

        Thread clientThread = new Thread( () -> {
            assertDoesNotThrow(
                    () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );
        } );

        serverThread.start();
        clientThread.start();
        clientThread.join();
        serverThread.join();

    }

    @Test
    void testParameterizePort() throws Exception
    {
        String serverFilePath = "jolie2/parameterizePort/parameterizeInputPort.ol";

        String[] serverArgs = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, serverArgs, 0, launcherArgs.length );
        serverArgs[serverArgs.length - 1] = serverFilePath;

        String filePath = "jolie2/parameterizePort/parameterizePort.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;

        Thread serverThread = new Thread( () -> {
            assertDoesNotThrow(
                    () -> JolieRunner.run( serverArgs, this.getClass().getClassLoader(), null ) );
        } );

        Thread clientThread = new Thread( () -> {
            assertDoesNotThrow(
                    () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );
        } );

        serverThread.start();
        clientThread.start();
        clientThread.join();
        serverThread.join();

    }

    @Test
    void testParameterizeInputPortLocalLocation()
    {
        String filePath = "jolie2/parameterizePort/localLocation.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;

        assertDoesNotThrow(
            () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );
    }

}
