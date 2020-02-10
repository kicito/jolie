package jolie.lang.parse.ast;

import jolie.lang.Constants;
import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.context.ParsingContext;
import jolie.lang.parse.module.argument.Argument;

public class EmbeddedServiceNode2 extends OLSyntaxNode
{
    private static final long serialVersionUID = Constants.serialVersionUID();
    private ServiceNode clientService, embedingService;
    private Argument[] args;

    public EmbeddedServiceNode2( ParsingContext context, ServiceNode clientService, ServiceNode embedingService )
    {
        super( context );
        this.clientService = clientService;
        this.embedingService = embedingService;
    }

    public ServiceNode clientService()
    {
        return clientService;
    }
    public ServiceNode embedingService()
    {
        return embedingService;
    }

    public Argument[] getArgs()
    {
        return args;
    }

    public void setArgs( Argument[] args )
    {
        this.args = args;
    }

    @Override
    public void accept( OLVisitor visitor )
    {
        visitor.visit( this );
    }

}


