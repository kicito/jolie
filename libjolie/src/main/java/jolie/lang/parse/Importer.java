package jolie.lang.parse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import jolie.lang.parse.Scanner.Token;
import jolie.lang.parse.ast.ImportStatement;
import jolie.lang.parse.ast.OLSyntaxNode;
import jolie.lang.parse.ast.Program;
import jolie.lang.parse.ast.types.TypeDefinition;
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

    public Importer( Configuration c )
    {
        this.config = c;
        cache = new HashMap<>();
    }

    private ModuleRecord load( Source s ) throws ModuleParsingException
    {
        System.out.println("loading " + s.source());
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
        Finder[] finders = Finder.getFindersForTargetString( source, stmt.importTarget() );
        ModuleRecord rc = null;
        for (Finder f : finders) {

            // perform cache lookup
            if ( cache.containsKey( f.target() ) ) {
                rc = cache.get( f.target() );
            }

            try {
                Source targetSource = f.find();
                rc = load( targetSource );

                if ( rc != null ) {
                    break;
                }
            } catch (FileNotFoundException e) {
                throw new ModuleNotFoundException( e );
            } catch (ModuleParsingException e) {
                throw e;
            }
        }

        // retrieve import name and add to OLSyntaxNodes list
        return rc.resolve(stmt.context(), stmt.pathNodes());
    }
}
