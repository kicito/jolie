package jolie.lang.parse.module;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import jolie.lang.parse.module.exception.ModuleNotFoundException;
import jolie.lang.parse.module.exception.ModuleParsingException;


public interface Finder
{
    public Source find( URI baseUri, String target ) throws ModuleNotFoundException;

    static Finder[] getFindersForTargetString( String target ) throws ModuleParsingException
    {
        // check protocol
        if ( target.startsWith( "http" ) || target.startsWith( "https" ) ) {
            return new Finder[] {URLFinder.getInstance()};
        } else if ( target.startsWith( "./" ) ) {
            return new Finder[] {ProjectDirFinder.getInstance()};
        } else {
            return new Finder[] {LibDirFinder.getInstance(), ProjectDirFinder.getInstance()};
        }
    }
}


class ProjectDirFinder implements Finder
{
    private static ProjectDirFinder finder = null;

    private ProjectDirFinder()
    {
    }

    @Override
    public Source find( URI baseUri, String target )
    {
        File targetFile = new File( baseUri.resolve( target ).normalize() );
        System.out.println( "[ProjectDirFinder] Perform look up for " + targetFile );

        if ( !targetFile.exists() ) {
            return null;
        }
        return new FileSource( targetFile );
    }

    public static ProjectDirFinder getInstance()
    {
        if ( finder == null ) finder = new ProjectDirFinder();

        return finder;
    }

}


class LibDirFinder implements Finder
{
    private static LibDirFinder finder = null;

    private static String libDir = System.getenv( "JOLIE_HOME" );

    private LibDirFinder()
    {
    }

    @Override
    public Source find( URI baseUri, String target ) throws ModuleNotFoundException
    {
        if ( libDir.isEmpty() ) {
            throw new ModuleNotFoundException(
                    "[LibDirFinder] JOLIE_HOME is not defined in environment" );
        }
        File targetFile = Paths.get( libDir, "include", "modules", target ).toFile();

        System.out.println( "[LibDirFinder] Perform look up for " + targetFile );
        if ( !targetFile.exists() ) {
            return null;
        }
        return new FileSource( targetFile );
    }

    public static LibDirFinder getInstance()
    {
        if ( finder == null ) finder = new LibDirFinder();

        return finder;
    }

}


class URLFinder implements Finder
{
    private static URLFinder finder = null;

    private URLFinder()
    {
    }

    @Override
    public Source find( URI baseUri, String targetURL ) throws ModuleNotFoundException
    {
        System.out.println( "[URLFinder] Perform look up for " + targetURL );
        try {
            return new URLSource( new URL( targetURL ) );
        } catch (MalformedURLException e) {
            throw new ModuleNotFoundException( "unable to locate " + targetURL, e );
        }
    }

    public static URLFinder getInstance()
    {
        if ( finder == null ) finder = new URLFinder();
        return finder;
    }
}


interface Source
{
    URI source();

    InputStream stream() throws FileNotFoundException, IOException;
}


class FileSource implements Source
{

    File file;

    public FileSource( File f )
    {
        this.file = f;
    }

    @Override
    public URI source()
    {
        return this.file.toURI();
    }

    @Override
    public InputStream stream()
    {
        try {
            return new FileInputStream( this.file );
        } catch (FileNotFoundException e) {
        }
        return null;
    }
}


class URLSource implements Source
{

    URL source;

    public URLSource( URL s )
    {
        this.source = s;
    }

    @Override
    public URI source()
    {
        try {
            return this.source.toURI();
        } catch (URISyntaxException e) {
        }
        return null;
    }

    @Override
    public InputStream stream() throws IOException
    {
        return this.source.openStream();
    }
}
