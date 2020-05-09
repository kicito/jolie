package jolie.lang.parse.ast;

import jolie.lang.Constants;
import jolie.lang.parse.context.ParsingContext;

public class JavaServiceNode extends ServiceNode
{
    private static final long serialVersionUID = Constants.serialVersionUID();
    private final String javaServicePath;

    public JavaServiceNode( ParsingContext context, String name, String javaServicePath, Program p )
    {
        super( context, name, p, Technology.JAVA );
        this.javaServicePath = javaServicePath;
    }

    public String javaServicePath(){
        return javaServicePath;
    }


}
