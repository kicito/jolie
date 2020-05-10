package jolie.lang.parse.module.exceptions;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jolie.lang.Constants;

public class ModuleNotFoundException extends FileNotFoundException
{

    private static final long serialVersionUID = Constants.serialVersionUID();

    private List< String > lookedPath;

    private final String moduleName;

    public ModuleNotFoundException( String moduleName )
    {
        super( moduleName );
        this.moduleName = moduleName;
        this.lookedPath = new ArrayList< String >();
    }

    public ModuleNotFoundException( String moduleName, String lookedPath )
    {
        super( moduleName );
        this.moduleName = moduleName;
        this.lookedPath = new ArrayList< String >();
        this.lookedPath.add(lookedPath);
    }

    public void addLookedPath( String path )
    {
        this.lookedPath.add( path );
    }


    @Override
    public String getMessage()
    {
        return "Module \"" + this.moduleName + "\" not found from lookup path "
                + Arrays.toString( this.lookedPath.toArray() );
    }

}
