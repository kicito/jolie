package jolie;

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
    void testInterfaceJolie() throws Exception
    {
        String filePath = "jolie-simple/interfacePrint.ol";
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
        assertTrue( systemOutContent.toString().contains( "sodep" ) );
    }



    @Test
    void testInternalServiceJolie() throws Exception
    {
        String filePath = "jolie-simple/serviceAsATree.ol";
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
        assertTrue( systemOutContent.toString().contains( "target/jolie-1.9.0-git.jar" ) );
    }

    
    @Test
    void testTwiceService() throws FileNotFoundException, CommandLineException, IOException,
            InterpreterException, InterruptedException
    {
        String serverFilePath = "jolie-simple/twice/TwiceService.ol";
        String clientFilePath = "jolie-simple/twice/TwiceClient.ol";
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
