package jolie.lang.parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;

interface Source
{
    URI source();

    InputStream stream() throws FileNotFoundException;
}


public interface Finder
{
    public Source find() throws FileNotFoundException;

    public URI target();

    static Finder[] getFindersForTargetString( URI source, String target )
            throws ModuleParsingException
    {
        // check protocol
        File file = new File( resolveTargetStringToURI( source, target ) );
        System.out.println( "File is: " + file.toString() );

        return new Finder[] {new ProjectDirFinder( source, target )};
    }

    static URI resolveTargetStringToURI( URI source, String target )
    {
        // if string start with ./
        if ( target.startsWith( "./" ) ) {
            return source.resolve( target );
        } else if ( target.startsWith( "/" ) ) {
            // if string start with /

        } else {
            // if string start with arbitary string
            return source.resolve( target );

        }
        return null;
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
    public Source find() throws FileNotFoundException
    {
        if ( !targetFile.exists() ) {
            throw new FileNotFoundException( targetFile.toPath().toString() );
        }
        return new FileSource( targetFile );
    }

    @Override
    public URI target(){
        return targetFile.toURI();
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
        public InputStream stream() throws FileNotFoundException
        {
            return new FileInputStream( this.file );
        }
    }

}
