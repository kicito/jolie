package jolie.lang.parse.ast.servicenode;

import jolie.lang.Constants;
import jolie.lang.Constants.ServiceType;
import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.context.ParsingContext;

public class JolieServiceNode extends ServiceNodeParameterize
{

    private static final long serialVersionUID = Constants.serialVersionUID();

    public JolieServiceNode( ParsingContext context, String name )
    {
        super( context, name );
    }

    @Override
    public void accept( OLVisitor visitor )
    {
        visitor.visit( this );
    }

    @Override
    public String getTarget()
    {
        return null;
    }

    @Override
    public String toString()
    {
        return "decl service " + super.name() + "( + " + super.parameterType() + " )";
    }

    @Override
    public ServiceType getType()
    {
        return ServiceType.JOLIE;
    }
}
