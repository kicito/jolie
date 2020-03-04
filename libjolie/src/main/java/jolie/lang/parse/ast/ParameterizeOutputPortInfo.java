package jolie.lang.parse.ast;

import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.context.ParsingContext;

public class ParameterizeOutputPortInfo extends PortInfo
{

    public ParameterizeOutputPortInfo( ParsingContext context, String id )
    {
        super( context, id );
        // TODO Auto-generated constructor stub
    }

    @Override
    public void accept( OLVisitor visitor )
    {
        visitor.visit(this);
    }
}
