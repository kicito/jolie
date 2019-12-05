package jolie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import jolie.lang.parse.ParserException;
import jolie.lang.parse.Scanner.Token;
import jolie.lang.parse.SemanticException;
import jolie.lang.parse.SemanticVerifier;
import jolie.lang.parse.ast.Program;
import jolie.lang.parse.util.ParsingUtils;

class SimpleModuleLoader implements ModuleLoader
{
    private Interpreter parentInterpreter;

    private Map< String, ModuleOOIT > moduleCache;

    private String charset;

    private Map< String, Token > definedConstants;

    public SimpleModuleLoader( Interpreter parentInterpreter, String charset,
            Map< String, Token > definedConstants )
    {
        this.moduleCache = Collections.synchronizedMap( new WeakHashMap< String, ModuleOOIT >() );
        this.parentInterpreter = parentInterpreter;
        this.charset = charset;
        this.definedConstants = definedConstants;
    }

    public File find( String target ) throws URISyntaxException, FileNotFoundException
    {
        File f = new File( target ).getAbsoluteFile();
        if ( f.exists() ) {
            return f;
        } else {
            for (String includePath : this.parentInterpreter.includePaths()) {
                if ( includePath.startsWith( "file:" ) ) {
                    Path p = Paths.get( new URI( includePath ) );
                    p = p.resolve( target );
                    f = p.toFile();
                } else {
                    f = new File( includePath + jolie.lang.Constants.fileSeparator + target );
                }
                if ( f.exists() ) {
                    f = f.getAbsoluteFile();
                    return f;
                }
            }
        }

        throw new FileNotFoundException( target );
    }

    public boolean load( File targetFile ) throws InterpreterException
    {
        // check if the targetFile already loaded
        if ( moduleCache.containsKey( targetFile.toPath().toString() ) ) {
            return true;
        }
        try {
            Program p = parseAST( targetFile );
            ModuleLoaderVisitor visitor = new ModuleLoaderVisitor( targetFile, p );
            System.out.println( "Program: " + p );
            ModuleOOIT ooit = visitor.build(); // TODO look up for usage for
                                               // isConstantMap in OOITBuidler
                                               // since it is required for build method.
            moduleCache.put( targetFile.toPath().toString(), ooit );
        } catch (IOException | ParserException | SemanticException | URISyntaxException e) {
            throw new InterpreterException( e );
        }
        return true;
    }

    private Program parseAST( File targetFile )
            throws IOException, ParserException, SemanticException, URISyntaxException
    {

        SemanticVerifier.Configuration configuration = new SemanticVerifier.Configuration();
        configuration.setCheckForMain( false );

        Program program = ParsingUtils.parseProgram( new FileInputStream( targetFile ),
                targetFile.toURI(), this.charset, this.parentInterpreter.includePaths(),
                this.parentInterpreter.getClassLoader(), this.definedConstants, configuration,
                false );
        return program;
    }

    @Override
    public Object get( String targetString, String identifierName )
    {
        ModuleOOIT ooit = moduleCache.get( targetString ); // TODO check null
        return ooit.get( identifierName );
    }

}
