package jolie;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import jolie.runtime.typing.Type;

class ModuleOOIT
{

    private File file;
    private Map< String, Type > types = new HashMap< String, Type >();

    /**
     * 
     */
    public ModuleOOIT( File f )
    {
        this.file = f;
        types = new HashMap< String, Type >();
    }

    public static void warnDuplicateType( String key, Type currType, Type newType )
    {
        // TODO change to interpreter level
        System.out.println( key + " with type " + currType + " is replaced by" + newType );
    }

    public void putType( String key, Type type )
    {
        if ( types.containsKey( key ) ) {
            warnDuplicateType( key, type, types.get( key ) );
        }

        types.put( key, type );
    }

    public Object get( String id )
    {
        return types.get(id);
    }

    @Override
    public String toString()
    {

        StringBuilder sb = new StringBuilder();
        sb.append( this.file.toPath() );
        sb.append( "\n" );
        String typesAsString = types.keySet().stream().map( key -> key + "=" + types.get( key ) )
                .collect( Collectors.joining( ", ", "{", "}" ) );
        sb.append( "types:" + typesAsString );

        return sb.toString();
    }
}
