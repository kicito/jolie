package jolie.lang.parse.ast;

import jolie.lang.Constants;
import jolie.lang.Constants.EmbeddedServiceType;
import jolie.lang.parse.context.ParsingContext;

public class EmbeddedServiceNode2 extends EmbeddedServiceNode
{

    private static final long serialVersionUID = Constants.serialVersionUID();

    private ServiceNode service;
    private final OLSyntaxNode passingParam;
    private final OutputPortInfo newOutputPortInfo;

    public EmbeddedServiceNode2( ParsingContext context, String serviceName,
            OutputPortInfo newOutputPortInfo, OLSyntaxNode passingParam )
    {
        super( context, EmbeddedServiceType.SERVICE, serviceName, newOutputPortInfo.id() );
        this.passingParam = passingParam;
        this.newOutputPortInfo = newOutputPortInfo;
    }

    public EmbeddedServiceNode2( ParsingContext context, String serviceName, String portId,
            OLSyntaxNode passingParam )
    {
        super( context, EmbeddedServiceType.SERVICE, serviceName, portId );
        this.passingParam = passingParam;
        this.newOutputPortInfo = null;
    }

    public String serviceName()
    {
        return super.servicePath();
    }

    public void setService( ServiceNode node )
    {
        this.service = node;
    }

    public boolean isCreateNewPort()
    {
        return this.newOutputPortInfo != null;
    }

    public OutputPortInfo outputPortInfo( )
    {
        return this.newOutputPortInfo;
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
