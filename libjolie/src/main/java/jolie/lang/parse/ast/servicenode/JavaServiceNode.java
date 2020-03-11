package jolie.lang.parse.ast.servicenode;

import jolie.lang.Constants;
import jolie.lang.Constants.ServiceType;
import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.context.ParsingContext;

public class JavaServiceNode extends ServiceNodeParameterize
{

    private static final long serialVersionUID = Constants.serialVersionUID();
    private String javaClass;

    public JavaServiceNode( ParsingContext context, String javaClass, String name )
    {
        super( context, name );
        this.javaClass = javaClass;
    }

    @Override
    public void accept( OLVisitor visitor )
    {
        visitor.visit( this );
    }

    @Override
    public String getTarget()
    {
        return javaClass;
    }

    @Override
    public String toString()
    {
        return "decl service Java(\"" + javaClass + "\") " + super.name() + "( + "
                + super.parameterType() + " )";
    }

    @Override
    public ServiceType getType()
    {
        return ServiceType.JAVA;
    }
    
}