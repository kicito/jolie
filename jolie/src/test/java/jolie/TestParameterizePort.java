package jolie;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void testInstanceOfPortInfo() throws Exception
    {
        String filePath = "jolie2/parameterizePort/portType.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;
        final Interpreter interpreter =
                new Interpreter( args, this.getClass().getClassLoader(), null );
        // Thread.currentThread().setContextClassLoader( interpreter.getClassLoader() );
        interpreter.run();

        Runtime.getRuntime().addShutdownHook( new Thread() {
            @Override
            public void run()
            {
                interpreter.exit( -1 );
            }
        } );
        assertTrue( systemOutContent.toString().contains( "true" ) );
    }



    @Test
    void testParameterizeOutputPort() throws Exception
    {
        String serverFilePath = "jolie2/parameterizePort/SumService.ol";

        String[] serverArgs = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, serverArgs, 0, launcherArgs.length );
        serverArgs[serverArgs.length - 1] = serverFilePath;
        final Interpreter serverInterpreter =
                new Interpreter( serverArgs, this.getClass().getClassLoader(), null );


        String filePath = "jolie2/parameterizePort/parameterizePort.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;
        final Interpreter interpreter =
                new Interpreter( args, this.getClass().getClassLoader(), null );


        Thread serverThread = new Thread( () -> {
            try {
                serverInterpreter.run();
                assertTrue( systemOutContent.toString()
                        .contains( "receive value hello from parameterize port" ) );

            } catch (InterpreterException | IOException e) {
                e.printStackTrace();
            }
        } );

        Thread clientThread = new Thread( () -> {
            try {
                interpreter.run();
                assertTrue( systemOutContent.toString().contains( "3" ) );
            } catch (InterpreterException | IOException e) {
                e.printStackTrace();
            }
        } );

        serverThread.start();
        clientThread.start();
        clientThread.join();
        serverThread.join();

    }

    @Test
    void testParameterizeInputPort() throws Exception
    {
        String serverFilePath = "jolie2/parameterizePort/parameterizeInputPort.ol";

        String[] serverArgs = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, serverArgs, 0, launcherArgs.length );
        serverArgs[serverArgs.length - 1] = serverFilePath;
        final Interpreter serverInterpreter =
                new Interpreter( serverArgs, this.getClass().getClassLoader(), null );


        String filePath = "jolie2/parameterizePort/parameterizePort.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;
        final Interpreter interpreter =
                new Interpreter( args, this.getClass().getClassLoader(), null );


        Thread serverThread = new Thread( () -> {
            try {
                serverInterpreter.run();
                assertTrue( systemOutContent.toString()
                        .contains( "receive value hello from parameterize port" ) );

            } catch (InterpreterException | IOException e) {
                e.printStackTrace();
            }
        } );

        Thread clientThread = new Thread( () -> {
            try {
                interpreter.run();
                assertTrue( systemOutContent.toString().contains( "3" ) );
            } catch (InterpreterException | IOException e) {
                e.printStackTrace();
            }
        } );

        serverThread.start();
        clientThread.start();
        clientThread.join();
        serverThread.join();

    }

}
