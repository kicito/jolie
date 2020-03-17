package jolie;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    }

    @Test
    void testService()
            throws FileNotFoundException, CommandLineException, IOException, InterpreterException
    {
        String filePath = "jolie2/services/simple.ol";
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
    }


    @Test
    void testEmbedService()
            throws FileNotFoundException, CommandLineException, IOException, InterpreterException
    {
        String filePath = "jolie2/services/simple_embed.ol";
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
    }

    @Test
    void testEmbedTwiceServiceWithStatic() throws FileNotFoundException, CommandLineException,
            IOException, InterpreterException, InterruptedException
    {
        String serverFilePath = "jolie2/services/twice_static.ol";
        String[] serverArgs = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, serverArgs, 0, launcherArgs.length );
        serverArgs[serverArgs.length - 1] = serverFilePath;
        final Interpreter interpreter =
                new Interpreter( serverArgs, this.getClass().getClassLoader(), null );
        interpreter.run();
        Runtime.getRuntime().addShutdownHook( new Thread() {
            @Override
            public void run()
            {
                interpreter.exit( -1 );
            }
        } );
    }

	@Test
	void simpleEmbedJolieServiceWithStatic() throws Exception
	{

        String serverFilePath = "jolie2/services/twice_static.ol";
        String[] serverArgs = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, serverArgs, 0, launcherArgs.length );
        serverArgs[serverArgs.length - 1] = serverFilePath;
        final Interpreter interpreter =
                new Interpreter( serverArgs, this.getClass().getClassLoader(), null );
        interpreter.run();
        Runtime.getRuntime().addShutdownHook( new Thread() {
            @Override
            public void run()
            {
                interpreter.exit( -1 );
            }
        } );
	}

    @Test
    void testEmbedTwiceServiceWithPortInfo() throws FileNotFoundException, CommandLineException,
            IOException, InterpreterException, InterruptedException
    {
        String serverFilePath = "jolie2/services/twice_portInfo.ol";
        String[] serverArgs = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, serverArgs, 0, launcherArgs.length );
        serverArgs[serverArgs.length - 1] = serverFilePath;
        final Interpreter interpreter =
                new Interpreter( serverArgs, this.getClass().getClassLoader(), null );
        interpreter.run();
        Runtime.getRuntime().addShutdownHook( new Thread() {
            @Override
            public void run()
            {
                interpreter.exit( -1 );
            }
        } );
    }

    @Test
    void testEmbedTwiceService() throws FileNotFoundException, CommandLineException, IOException,
            InterpreterException, InterruptedException
    {
        String serverFilePath = "jolie2/services/twice.ol";
        String[] serverArgs = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, serverArgs, 0, launcherArgs.length );
        serverArgs[serverArgs.length - 1] = serverFilePath;
        final Interpreter interpreter =
                new Interpreter( serverArgs, this.getClass().getClassLoader(), null );
        interpreter.run();
        Runtime.getRuntime().addShutdownHook( new Thread() {
            @Override
            public void run()
            {
                interpreter.exit( -1 );
            }
        } );
    }

    @Test
    void testEmbedConsoleService() throws FileNotFoundException, CommandLineException, IOException,
            InterpreterException, InterruptedException
    {
        String serverFilePath = "jolie2/services/console.ol";
        String[] serverArgs = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, serverArgs, 0, launcherArgs.length );
        serverArgs[serverArgs.length - 1] = serverFilePath;
        final Interpreter interpreter =
                new Interpreter( serverArgs, this.getClass().getClassLoader(), null );
        interpreter.run();
        Runtime.getRuntime().addShutdownHook( new Thread() {
            @Override
            public void run()
            {
                interpreter.exit( -1 );
            }
        } );

        assertTrue( systemOutContent.toString().contains( "Hello" ) );

    }


    @Test
    void testEmbedIDOutputIDInputService() throws FileNotFoundException, CommandLineException,
            IOException, InterpreterException, InterruptedException
    {

        String serverFilePath = "jolie2/services/echo_id_output_id_input.ol";
        String[] serverArgs = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, serverArgs, 0, launcherArgs.length );
        serverArgs[serverArgs.length - 1] = serverFilePath;
        final Interpreter interpreter =
                new Interpreter( serverArgs, this.getClass().getClassLoader(), null );
        interpreter.run();
        Runtime.getRuntime().addShutdownHook( new Thread() {
            @Override
            public void run()
            {
                interpreter.exit( -1 );
            }
        } );
    }

    @Test
    void testEmbedEchoIDOutputLiteralInputService() throws FileNotFoundException,
            CommandLineException, IOException, InterpreterException, InterruptedException
    {
        String serverFilePath = "jolie2/services/echo_id_output_literal_input.ol";
        String[] serverArgs = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, serverArgs, 0, launcherArgs.length );
        serverArgs[serverArgs.length - 1] = serverFilePath;
        final Interpreter interpreter =
                new Interpreter( serverArgs, this.getClass().getClassLoader(), null );
        interpreter.run();
        Runtime.getRuntime().addShutdownHook( new Thread() {
            @Override
            public void run()
            {
                interpreter.exit( -1 );
            }
        } );

    }

    @Test
    void testEmbedEchoStringOutputIDInputService() throws FileNotFoundException,
            CommandLineException, IOException, InterpreterException, InterruptedException
    {
        String serverFilePath = "jolie2/services/echo_string_output_id_input.ol";
        String[] serverArgs = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, serverArgs, 0, launcherArgs.length );
        serverArgs[serverArgs.length - 1] = serverFilePath;
        final Interpreter interpreter =
                new Interpreter( serverArgs, this.getClass().getClassLoader(), null );
        interpreter.run();
        Runtime.getRuntime().addShutdownHook( new Thread() {
            @Override
            public void run()
            {
                interpreter.exit( -1 );
            }
        } );
    }
}
