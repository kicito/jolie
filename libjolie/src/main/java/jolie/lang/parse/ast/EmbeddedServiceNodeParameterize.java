package jolie.lang.parse.ast;

import jolie.lang.Constants;
import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.ast.servicenode.ServiceNodeParameterize;
import jolie.lang.parse.context.ParsingContext;

public class EmbeddedServiceNodeParameterize extends OLSyntaxNode
{

    private ServiceNodeParameterize embedService;
    private OLSyntaxNode expression;
    private static final long serialVersionUID = Constants.serialVersionUID();


    public EmbeddedServiceNodeParameterize( ParsingContext context,
            ServiceNodeParameterize embedService, OLSyntaxNode expression )
    {
        super( context );
        this.embedService = embedService;
        this.expression = expression;
    }

    @Override
    public void accept( OLVisitor visitor )
    {
        visitor.visit(this);
    }

    public ServiceNodeParameterize embedService()
    {
        return embedService;
    }

    public OLSyntaxNode expression()
    {
        return expression;
    }

    public void setExpression( OLSyntaxNode expression )
    {
        this.expression = expression;
    }
}
