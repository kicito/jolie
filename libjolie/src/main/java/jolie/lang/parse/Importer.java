package jolie.lang.parse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jolie.lang.parse.Scanner.Token;
import jolie.lang.parse.ast.ImportStatement;
import jolie.lang.parse.ast.Program;
import jolie.lang.parse.util.ParsingUtils;
import jolie.lang.parse.util.ProgramInspector;

public class Importer
{
    public static class Configuration
    {
        private String charset;
        private String[] includePaths;
        private ClassLoader classLoader;
        private Map< String, Scanner.Token > definedConstants;
        private boolean includeDocumentation;

        /**
         * @param source
         * @param charset
         * @param includePaths
         * @param classLoader
         * @param definedConstants
         * @param includeDocumentation
         */
        public Configuration( String charset, String[] includePaths, ClassLoader classLoader,
                Map< String, Token > definedConstants, boolean includeDocumentation )
        {
            this.charset = charset;
            this.includePaths = includePaths;
            this.classLoader = classLoader;
            this.definedConstants = definedConstants;
            this.includeDocumentation = includeDocumentation;
        }
    }


    private Map< URI, ModuleRecord > cache;
    private Configuration config;
    private Finder[] finders;
    List< URI > importChain = new ArrayList< URI >();

    public Importer( Configuration c )
    {
        this.config = c;
        cache = new HashMap<>();
    }

    public Importer( Configuration c, Finder[] finders )
    {
        this( c );
        this.finders = finders;
    }

    private ModuleRecord load( Source s ) throws ModuleParsingException
    {
        System.out.println( "[IMPORTER] loading " + s.source() );
        SemanticVerifier.Configuration configuration = new SemanticVerifier.Configuration();
        configuration.setCheckForMain( false );
        Program program;
        try {
            program = ParsingUtils.parseProgram( s.stream(), s.source(), this.config.charset,
                    this.config.includePaths, this.config.classLoader, this.config.definedConstants,
                    configuration, this.config.includeDocumentation, this );
        } catch (IOException | ParserException | SemanticException e) {
            throw new ModuleParsingException( e );
        }
        if ( program == null ) {
            throw new ModuleParsingException( "Program from " + s.source() + " is null" );
        }
        ProgramInspector pi = ParsingUtils.createInspector( program );
        return new ModuleRecord( s.source(), pi );
    }

    public ImportResult importModule( URI source, ImportStatement stmt )
            throws ModuleNotFoundException, ModuleParsingException
    {
        this.finders = Finder.getFindersForTargetString( source, stmt.importTarget() );
        ModuleRecord rc = null;
        for (Finder f : this.finders) {

            try {
                Source targetSource = f.find();

                // perform cache lookup
                if ( cache.containsKey( targetSource.source() ) ) {
                    System.out.println( "[LOADER] found " + targetSource.source() + " in cache" );
                    rc = cache.get( targetSource.source() );
                }
                importChain.add( targetSource.source() );
                System.out.println( "[IMPORTER] ImportChain " + this.importChain );

                rc = load( targetSource );
                importChain.remove( targetSource.source() );
                System.out.println( "[IMPORTER] ImportChain " + this.importChain );

                if ( rc != null ) {
                    break;
                }
            } catch (FileNotFoundException e) {
                throw new ModuleNotFoundException( e );
            } catch (ModuleParsingException e) {
                throw e;
            }
        }

        cache.put( rc.source(), rc );

        boolean importUsingLink = importChain.size() == 0;

        if ( stmt.isNamespaceImport() ) {
            return rc.resolveNameSpace( stmt.context(), importUsingLink );
        } else {
            // retrieve import name and add to OLSyntaxNodes list
            return rc.resolve( stmt.context(), stmt.pathNodes(), importUsingLink );
        }
    }
}
