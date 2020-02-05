package jolie.lang.parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

interface Source
{
    URI source();

    InputStream stream() throws FileNotFoundException, IOException;
}


public interface Finder
{
    public Source find() throws ModuleNotFoundException;

    public URI target();

    static Finder[] getFindersForTargetString( URI source, String target )
            throws ModuleParsingException
    {
        // check protocol
        try {
            if ( target.startsWith( "http" ) || target.startsWith( "https" ) ) {
                return new Finder[] {new URLFinder( target )};
            } else {
                return new Finder[] {new ProjectDirFinder( source, target )};
            }
        } catch (NullPointerException | IllegalArgumentException | MalformedURLException e) {
            throw new ModuleParsingException( "[FINDER] Unable to resolve " + target, e );
        }
    }
}


class ProjectDirFinder implements Finder
{

    private File targetFile;

    public ProjectDirFinder( URI baseUri, String target )
    {
        this.targetFile = new File( baseUri.resolve( target ).normalize() );
    }

    @Override
    public Source find() throws ModuleNotFoundException
    {
        System.out.println( "[ProjectDirFinder] Perform look up for " + this.targetFile );
        if ( !this.targetFile.exists() ) {
            throw new ModuleNotFoundException( "unable to locate " + this.targetFile.toPath().toString() );
        }
        return new FileSource( this.targetFile );
    }

    @Override
    public URI target()
    {
        return this.targetFile.toURI();
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
            } catch (FileNotFoundException e) { }
            return null;
        }
    }

}


class URLFinder implements Finder
{

    private String targetURL;

    public URLFinder( String targetURL ) throws MalformedURLException
    {
        this.targetURL = targetURL;
    }

    @Override
    public Source find() throws ModuleNotFoundException
    {
        System.out.println( "[URLFinder] Perform look up for " + targetURL );
        try {
            return new URLSource( new URL( this.targetURL ) );
        } catch (MalformedURLException e) {
            throw new ModuleNotFoundException( "unable to locate " + this.targetURL , e);
        }
    }

    @Override
    public URI target()
    {
        try {
            return new URI( this.targetURL );
        } catch (URISyntaxException e) {
        }
        return null;
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

}
