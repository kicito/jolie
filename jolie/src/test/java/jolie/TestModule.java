package jolie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.ValueSource;
import util.StringArrayConverter;

public class TestModule
{
        private static final String[] launcherArgs = new String[] {"-l",
                        "dist/jolie/extensions/*:dist/jolie/javaServices/*:dist/jolie/lib/*", "-i",
                        "dist/jolie/include"};

        @TestTemplate
        @ParameterizedTest
        @ValueSource(strings = {"simple-import/types/main.ol","simple-import/interfaces/main.ol"})
        void testSimpleImportJolie( @ConvertWith(StringArrayConverter.class) String[] testArgs )
        {

                try {

                        String[] args = new String[launcherArgs.length + testArgs.length];
                        System.arraycopy( launcherArgs, 0, args, 0, launcherArgs.length );
                        System.arraycopy( testArgs, 0, args, launcherArgs.length, testArgs.length );
                        final Interpreter interpreter = new Interpreter( args,
                                        this.getClass().getClassLoader(), null );
                        // Thread.currentThread()
                        // .setContextClassLoader( interpreter.getClassLoader() );
                        interpreter.run();
                        // ooitBuilder.environmentObj();
                        System.out.println( "break" );
                } catch (Exception e) {
                        e.printStackTrace();
                        fail( "Should not have thrown any exception" );
                }


        }


}
