package jolie.lang.parse;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import jolie.lang.parse.ast.Program;
import jolie.lang.parse.util.ProgramInspector;

public interface ModuleSolver
{
    public Program solve(Program p);

    public ProgramInspector load(File targetFile) throws Exception;

    public File find( String target ) throws URISyntaxException, FileNotFoundException;
    
    public void addInspectorToCache( ProgramInspector p );
}