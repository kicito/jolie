package jolie.lang.parse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import jolie.lang.parse.Scanner.Token;

class InstanceCreator
{

    String[] includePaths;

    public InstanceCreator( String[] includePaths )
    {
        if ( this.includePaths == null ) {
            this.includePaths = new String[] {};
        }
        this.includePaths = includePaths;
    }

    OLParser createOLParser( InputStream codeSteam ) throws IOException, URISyntaxException
    {
        return new OLParser(
                new Scanner( codeSteam, new URI( "test" ), StandardCharsets.UTF_8.name(), false ),
                this.includePaths, InstanceCreator.class.getClassLoader() );
    }

    OLParser createOLParser( Scanner source ) throws IOException, URISyntaxException
    {
        return new OLParser( source, this.includePaths, InstanceCreator.class.getClassLoader() );
    }

    ModuleSolverSimple createModuleSolver( Map< String, Token > constants )
    {
        return new ModuleSolverSimple( InstanceCreator.class.getClassLoader(), this.includePaths,
                StandardCharsets.UTF_8.name(), constants );
    }

}
