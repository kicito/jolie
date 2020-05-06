package jolie.lang.parse.ast;

import jolie.lang.Constants;
import jolie.lang.Constants.EmbeddedServiceType;
import jolie.lang.parse.context.ParsingContext;

public class EmbeddedServiceNode2 extends EmbeddedServiceNode
{

    private static final long serialVersionUID = Constants.serialVersionUID();

    private ServiceNode service;

    public EmbeddedServiceNode2( ParsingContext context, String servicePath, String portId )
    {
        super( context, EmbeddedServiceType.SERVICE, servicePath, portId );
    }

    public void setService( ServiceNode node )
    {
        this.service = node;
    }

    public ServiceNode service()
    {
        return this.service;
    }


}
