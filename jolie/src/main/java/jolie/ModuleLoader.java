package jolie;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

interface ModuleLoader
{
    public File find( String target ) throws URISyntaxException, FileNotFoundException;
    // TODO change to Moduleloader execption

    // public Object parse(URI source);
    public boolean load( File target ) throws InterpreterException;


    public Object get( String targetString, String identifierName );
}
