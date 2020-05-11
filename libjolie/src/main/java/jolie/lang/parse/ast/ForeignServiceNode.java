package jolie.lang.parse.ast;

import jolie.lang.Constants;
import jolie.lang.parse.context.ParsingContext;

public class ForeignServiceNode extends ServiceNode
{
    private static final long serialVersionUID = Constants.serialVersionUID();
    private final String servicePath;

    public ForeignServiceNode( ParsingContext context, String name, Constants.EmbeddedServiceType tech, String servicePath, Program p )
    {
        super( context, name, p, tech );
        this.servicePath = servicePath;
    }

    public String servicePath(){
        return servicePath;
    }

}
