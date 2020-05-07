package jolie.lang.parse.ast;

import jolie.lang.Constants;
import jolie.lang.Constants.EmbeddedServiceType;
import jolie.lang.parse.context.ParsingContext;

public class EmbeddedServiceNode2 extends EmbeddedServiceNode
{

    private static final long serialVersionUID = Constants.serialVersionUID();

    private ServiceNode service;
    private OLSyntaxNode passingParam;

    public EmbeddedServiceNode2( ParsingContext context, String serviceName, String portId,
            OLSyntaxNode passingParam )
    {
        super( context, EmbeddedServiceType.SERVICE, serviceName, portId );
        this.passingParam = passingParam;
    }

    public String serviceName()
    {
        return super.servicePath();
    }

    public void setService( ServiceNode node )
    {
        this.service = node;
    }

    public ServiceNode service()
    {
        return this.service;
    }

    public OLSyntaxNode passingParam()
    {
        return this.passingParam;
    }

}
