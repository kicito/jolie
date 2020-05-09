package jolie;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestJolieImportSystem
{

	static {
		JolieURLStreamHandlerFactory.registerInVM();
	}
    private static final String[] launcherArgs = new String[] {"-l",
    "../lib/*:../dist/jolie/lib:../dist/jolie/javaServices/*:../dist/jolie/extensions/*",
    "-i", "../include"};
    
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
    void testEmbeddedJavaServiceNode()
    {
        String filePath = "src/test/resources/imports/import_console.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;

        assertDoesNotThrow(
            () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );
    }

    @Test
    void testEmbeddedServiceNodeNewPrimitive()
    {
        String filePath = "src/test/resources/imports/test_service_new_primitive.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;

        assertDoesNotThrow(
            () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );
    }

    @Test
    void testImportServiceInternalLocalIP()
    {
        String filePath = "src/test/resources/imports/test_service_internal_local_input.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;

        assertDoesNotThrow(
            () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );
    }


    @Test
    void testImportServiceWithoutBindingPort()
    {
        String filePath = "src/test/resources/imports/test_service_no_binding.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;

        assertDoesNotThrow(
            () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );
    }

    @Test
    void testImport()
    {
        String filePath = "src/test/resources/imports/A.ol";
        String[] args = new String[launcherArgs.length + 1];
        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
        args[args.length - 1] = filePath;

        assertDoesNotThrow(
            () -> JolieRunner.run( args, this.getClass().getClassLoader(), null ) );
    }

}
