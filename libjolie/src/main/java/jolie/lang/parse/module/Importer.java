package jolie.lang.parse.module;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import jolie.lang.parse.ParserException;
import jolie.lang.parse.Scanner;
import jolie.lang.parse.Scanner.Token;
import jolie.lang.parse.SemanticException;
import jolie.lang.parse.SemanticVerifier;
import jolie.lang.parse.ast.ImportStatement;
import jolie.lang.parse.ast.Program;
import jolie.lang.parse.module.ModuleRecord.Status;
import jolie.lang.parse.util.ParsingUtils;
import jolie.lang.parse.util.ProgramInspector;

public class Importer
{

    public static String PACKAGE_FOLDER = "packages";
    public static String JOLIE_HOME = System.getenv( "JOLIE_HOME" );
    private final Map< URI, ModuleRecord > cache;
    private final Configuration config;

    public static class Configuration
    {
        private URI source;
        private String charset;
        private String[] includePaths;
        private ClassLoader classLoader;
        private Map< String, Scanner.Token > definedConstants;

        /**
         * @param source
         * @param charset
         * @param includePaths
         * @param classLoader
         * @param definedConstants
         * @param includeDocumentation
         */
        public Configuration( URI source, String charset, String[] includePaths,
                ClassLoader classLoader, Map< String, Token > definedConstants )
        {
            this.source = source;
            this.charset = charset;
            this.includePaths = includePaths;
            this.classLoader = classLoader;
            this.definedConstants = definedConstants;
        }
    }


    public Importer( Configuration c )
    {
        this.config = c;
        cache = new HashMap<>();
    }

    public String prettyPrintTarget( String[] target )
    {
        String ret = "";
        boolean relativeEnded = false;
        for (String token : target) {
            if ( token.isEmpty() ) {
                ret += ".";
            } else {
                if ( relativeEnded ) {
                    ret += ".";
                }
                relativeEnded = true;
                ret += token;
            }
        }
        return ret;
    }

    private Source moduleLookUp( String[] target ) throws ModuleException
    {
        Finder finder =
                Finder.getFinderForTarget( this.config.source, this.config.includePaths, target );
        Source targetFile = finder.find();
        if ( targetFile == null ) {
            throw new ModuleException( "Unable to locate module " + prettyPrintTarget( target )
                    + ", looked path " + Arrays.toString( finder.lookupedPath() ) );
        }

        return targetFile;
    }


    /**
     * load a target source into module record
     * 
     * @param s Source of import target, an implementation of Source interface.
     * @see Source
     * @throws ModuleException
     */
    private ProgramInspector load( Source s ) throws ModuleException
    {
        System.out.println( "[LOADER] loading " + s.source() );
        SemanticVerifier.Configuration configuration = new SemanticVerifier.Configuration();
        configuration.setCheckForMain( false );
        Program program;
        try {
            Scanner scanner = new Scanner( s.stream(), s.source(), this.config.charset );
            program = ParsingUtils.parseProgram( scanner, this.config.includePaths,
                    this.config.classLoader, this.config.definedConstants, configuration, this );
        } catch (IOException | ParserException | SemanticException e) {
            throw new ModuleException( e );
        }
        ProgramInspector pi = ParsingUtils.createInspector( program );
        return pi;
    }

    public ImportResult importModule( ImportStatement stmt ) throws ModuleException
    {
        // ModuleRecord rc = moduleLookUp(source, stmt.importTarget());
        Source targetSource = moduleLookUp( stmt.importTarget() );

        // perform cache lookup
        ModuleRecord moduleRecord;
        if ( cache.containsKey( targetSource.source() ) ) {
            System.out.println( "[IMPORTER] found " + targetSource.source() + " in cache" );
            moduleRecord = cache.get( targetSource.source() );
            if ( moduleRecord.status() == Status.LOADING ) { // check importStatus is finishes
                throw new ModuleException( "cyclic dependency detected between "
                        + stmt.context().sourceName() + " and " + targetSource.source() );
            }
        } else {
            moduleRecord = new ModuleRecord( targetSource.source() );
            cache.put( targetSource.source(), moduleRecord );
            ProgramInspector pi = load( targetSource );
            moduleRecord.loadFinished( pi );
        }

        if ( stmt.isNamespaceImport() ) {
            return moduleRecord.resolveNameSpace( stmt.context() );
        } else {
            return moduleRecord.resolve( stmt.context(), stmt.importSymbolTargets() );
        }
    }
}
