package jolie.lang.parse;

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
import jolie.lang.parse.Scanner.Token;
import jolie.lang.parse.ast.Program;
import jolie.lang.parse.util.ParsingUtils;
import jolie.lang.parse.util.ProgramInspector;

public class ModuleSolverSimple implements ModuleSolver
{

    private Map< String, ProgramInspector > moduleCache;

    private Map< String, Token > definedConstants;

    private ClassLoader classLoader;

    private String[] includePaths;

    private String charset;

    public ModuleSolverSimple( ClassLoader classLoader, String[] includePaths, String charset,
            Map< String, Token > definedConstants )
    {
        this.moduleCache =
                Collections.synchronizedMap( new WeakHashMap< String, ProgramInspector >() );
        this.classLoader = classLoader;
        this.includePaths = includePaths;
        this.charset = charset;
        this.definedConstants = definedConstants;

    }

    @Override
    public Program solve( Program p )
    {

        // foreach program p
        ModuleSolverVisitor visitor = new ModuleSolverVisitor( this );
        Program importResolvedProgram = visitor.parse(p);

        return importResolvedProgram;
    }

    public ProgramInspector load( File targetFile ) throws Exception
    {
        // check if the targetFile already loaded
        if ( moduleCache.containsKey( targetFile.toPath().toString() ) ) {
            return moduleCache.get(targetFile.toPath().toString());
        }
        ProgramInspector pi = null;
        try {
            pi = parseAST( targetFile );
            System.out.println( "Program: " + pi );
        } catch (IOException | ParserException | SemanticException | URISyntaxException e) {
            e.printStackTrace();
            throw new Exception( e );
        }
        return pi;
    }


    public ProgramInspector parseAST( File targetFile )
            throws IOException, ParserException, SemanticException, URISyntaxException
    {

        SemanticVerifier.Configuration configuration = new SemanticVerifier.Configuration();
        configuration.setCheckForMain( false );

        Program program = ParsingUtils.parseProgram( new FileInputStream( targetFile ),
                targetFile.toURI(), this.charset, this.includePaths,
                this.classLoader, this.definedConstants, configuration,
                false );
        return ParsingUtils.createInspector(program);
    }

    public File find( String target ) throws URISyntaxException, FileNotFoundException
    {
        File f = new File( target ).getAbsoluteFile();
        if ( f.exists() ) {
            return f;
        } else {
            for (String includePath : this.includePaths) {
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

    public void addInspectorToCache( ProgramInspector pi){
        this.moduleCache.put( pi.getSources()[0].getPath(), pi );
    }
}
