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
import java.util.logging.Level;
import java.util.logging.Logger;
import jolie.lang.parse.Scanner.Token;
import jolie.lang.parse.ast.ImportStatement;
import jolie.lang.parse.ast.Program;
import jolie.lang.parse.util.ParsingUtils;
import jolie.lang.parse.util.ProgramInspector;

public class ModuleSolverSimple implements ModuleSolver
{

    private Map< URI, ProgramInspector > moduleCache;

    private Map< String, Token > definedConstants;

    private ClassLoader classLoader;

    private String[] includePaths;

    private String charset;

    private String currDirectory;

    protected final ModuleSolverExceptions moduleSolverExceptions = new ModuleSolverExceptions();
    private static final Logger logger = Logger.getLogger( "JOLIE_MODULE" );


    public ModuleSolverSimple( ClassLoader classLoader, String[] includePaths, String charset,
            Map< String, Token > definedConstants )
    {
        this.moduleCache =
                Collections.synchronizedMap( new WeakHashMap< URI, ProgramInspector >() );
        this.classLoader = classLoader;
        this.includePaths = includePaths;
        this.charset = charset;
        this.definedConstants = definedConstants;
    }

    @Override
    public Program solve( Program program, String path ) throws ModuleSolverExceptions
    {
        this.setCurrDirectory( path );
        ModuleSolverVisitor visitor = new ModuleSolverVisitor( this );
        Program importResolvedProgram = visitor.parse( program );

        if ( moduleSolverExceptions.getErrorList().size() > 0 ) {
            logger.severe( "Aborting: Error occur while resolving import statements." );
            for (Exception e : moduleSolverExceptions.getErrorList()) {
                logger.log( Level.SEVERE, e.getMessage(), e );
            }
            throw moduleSolverExceptions;
        }

        return importResolvedProgram;
    }

    private void setCurrDirectory( String path )
    {
        currDirectory = path;
    }

    public ProgramInspector load( File targetFile ) throws ModuleSolverExceptions
    {
        // check if the targetFile already loaded
        if ( moduleCache.containsKey( targetFile.toURI() ) ) {
            return moduleCache.get( targetFile.toURI() );
        }
        try {
            ProgramInspector pi = parseAST( targetFile );
            ImportStatement[] importStatements = pi.getImportStatements();
            if ( importStatements.length > 0 ) {
                System.out.println( importStatements );
            }
            return pi;
        } catch (ModuleSolverExceptions e) {
            this.moduleSolverExceptions
                    .addException( new Exception( "Error occur during load: " + targetFile, e ) );
            throw this.moduleSolverExceptions;
        }
    }


    public ProgramInspector parseAST( File targetFile ) throws ModuleSolverExceptions
    {

        SemanticVerifier.Configuration configuration = new SemanticVerifier.Configuration();
        configuration.setCheckForMain( false );
        try {
            Program program = ParsingUtils.parseProgram( new FileInputStream( targetFile ),
                    targetFile.toURI(), this.charset, this.includePaths, this.classLoader,
                    this.definedConstants, configuration, false, this );
            return ParsingUtils.createInspector( program );
        } catch (IOException | ParserException | SemanticException e) {
            this.moduleSolverExceptions
                    .addException( new Exception( "Error occur during parseAST: " + targetFile, e ) );
            throw this.moduleSolverExceptions;
        }
    }

    public File find( String target ) throws URISyntaxException, FileNotFoundException
    {
        // 1. find in native lib
        // 2. resolve base on path "." , "..", "/"

        // TODO symlink module folder
        Path pTemp = Paths.get( this.currDirectory, target );
        pTemp = pTemp.normalize();
        System.out.println(pTemp);
        File f = pTemp.toFile();
        if ( !f.exists() ) {
            throw new FileNotFoundException( pTemp.toString() );
        }
        return f;
    }

    public void addInspectorToCache( ProgramInspector pi )
    {
        this.moduleCache.put( pi.getSources()[0], pi );
    }

    public ProgramInspector getInspectorFromCache( URI source )
    {
        return this.moduleCache.get( source );
    }
}
