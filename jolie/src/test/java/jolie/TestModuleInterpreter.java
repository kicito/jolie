package jolie;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

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
    void testImportTypesJolie() throws Exception
    {
        String filePath = "simple-import/types/main.ol";
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
    void testImportTypeLink() throws Exception
    {
        String filePath = "simple-import/types-linked/main.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;
        final Interpreter interpreter =
                new Interpreter( args, this.getClass().getClassLoader(), null );
        Thread.currentThread().setContextClassLoader( interpreter.getClassLoader() );

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
    void testTypeChoiceImport() throws Exception
    {
        String filePath = "simple-import/types-sum/main.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;
        final Interpreter interpreter =
                new Interpreter( args, this.getClass().getClassLoader(), null );
        Thread.currentThread().setContextClassLoader( interpreter.getClassLoader() );

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
    void testInterfaceImport() throws Exception
    {

        String serverFilePath = "simple-import/interfaces/server.ol";
        String clientFilePath = "simple-import/interfaces/client.ol";
        String[] serverArgs = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, serverArgs, 0, launcherArgs.length );
        serverArgs[serverArgs.length - 1] = serverFilePath;
        final Interpreter serverInterpreter =
                new Interpreter( serverArgs, this.getClass().getClassLoader(), null );

        String[] clientArgs = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, clientArgs, 0, launcherArgs.length );
        clientArgs[clientArgs.length - 1] = clientFilePath;
        final Interpreter clientInterpreter =
                new Interpreter( clientArgs, this.getClass().getClassLoader(), null );

        Thread serverThread = new Thread( () -> {
            try {
                serverInterpreter.run();
                assertTrue( systemOutContent.toString()
                        .contains( "received " + 5 + ", return " + 10 ) );
            } catch (InterpreterException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } );

        Thread clientThread = new Thread( () -> {
            try {
                clientInterpreter.run();
                assertTrue( systemOutContent.toString().contains( "10" ) );
            } catch (InterpreterException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } );
        serverThread.start();
        clientThread.start();
        clientThread.join();
        serverThread.join();
    }

}
