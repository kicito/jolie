package jolie.lang.parse.module;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import jolie.lang.parse.ParserException;
import jolie.lang.parse.Scanner;
import jolie.lang.parse.Scanner.Token;
import jolie.lang.parse.SemanticException;
import jolie.lang.parse.SemanticVerifier;
import jolie.lang.parse.ast.ImportStatement;
import jolie.lang.parse.ast.Program;
import jolie.lang.parse.module.exception.ModuleNotFoundException;
import jolie.lang.parse.module.exception.ModuleParsingException;
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
        System.out.println( "[LOADER] loading " + s.source() );
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

    public ModuleRecord moduleLookUp(URI source, String target) 
        throws ModuleParsingException, ModuleNotFoundException
    {
        ModuleRecord rc = null;
        Finder[] finders = Finder.getFindersForTargetString( target );

        for (Finder f : finders) {
            Source targetSource = f.find(source, target);
            if (targetSource == null){
                continue;
            }
            // perform cache lookup
            if ( cache.containsKey( targetSource.source() ) ) {
                System.out.println( "[LOADER] found " + targetSource.source() + " in cache" );
                rc = cache.get( targetSource.source() );
            }else{
                rc = load( targetSource );
            }
        }

        return rc;
    }

    public ImportResult importModule( URI source, ImportStatement stmt )
            throws ModuleNotFoundException, ModuleParsingException
    {
        ModuleRecord rc = moduleLookUp(source, stmt.importTarget());
        if ( rc == null ) {
            throw new ModuleParsingException("unable to locate from " + source + " with target" + stmt.importTarget());
        }
        cache.put( rc.source(), rc );
        if ( stmt.isNamespaceImport() ) {
            return rc.resolveNameSpace( stmt.context() );
        } else {
            return rc.resolve( stmt.context(), stmt.pathNodes() );
        }
    }
}
