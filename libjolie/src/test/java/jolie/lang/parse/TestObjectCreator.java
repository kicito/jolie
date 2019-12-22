package jolie.lang.parse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import jolie.lang.parse.Scanner.Token;

class TestObjectCreator
{

    String[] includePaths;

    public TestObjectCreator( String[] includePaths )
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
                this.includePaths, TestObjectCreator.class.getClassLoader() );
    }

    ModuleSolverSimple createModuleSolver( Map< String, Token > constants )
    {
        return new ModuleSolverSimple( TestObjectCreator.class.getClassLoader(), this.includePaths,
                StandardCharsets.UTF_8.name(), constants );
    }

}
