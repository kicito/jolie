package jolie.lang.parse.ast;

import java.util.ArrayList;
import java.util.List;
import jolie.lang.Constants;
import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.binding.BindIn;
import jolie.lang.parse.binding.BindOut;
import jolie.lang.parse.context.ParsingContext;

public class EmbeddedServiceNode2 extends OLSyntaxNode
{
    private static final long serialVersionUID = Constants.serialVersionUID();
    private ServiceNode service;
    private List< BindIn > bindIns;
    private List< BindOut > bindOuts;

    public EmbeddedServiceNode2( ParsingContext context, ServiceNode service )
    {
        super( context );
        this.service = service;
    }


    public ServiceNode service()
    {
        return service;
    }

    public List< BindIn > bindIns()
    {
        return bindIns;
    }

    public List< BindOut > bindOuts()
    {
        return bindOuts;
    }


    public void addBindIns( List< BindIn > bindIns )
    {
        if ( this.bindIns == null ) {
            this.bindIns = new ArrayList< BindIn >();
        }
        this.bindIns.addAll( bindIns );
    }


    public void addBindOut( List< BindOut > bindOuts )
    {
        if ( this.bindOuts == null ) {
            this.bindOuts = new ArrayList< BindOut >();
        }
        this.bindOuts.addAll( bindOuts );
    }

    @Override
    public void accept( OLVisitor visitor )
    {
        visitor.visit( this );
    }

}


