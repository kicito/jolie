package jolie.lang.parse.ast;

import java.net.URI;
import java.util.Map;
import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.context.ParsingContext;

public class ParameterizeInputPortInfo extends InputPortInfo
{

    private OLSyntaxNode parameter;

    public ParameterizeInputPortInfo( ParsingContext context, String id, AggregationItemInfo[] aggregationList,
    Map< String, String > redirectionMap)
    {
        super( context, id, null, null, new NullProcessStatement( context ), aggregationList, redirectionMap );
    }
    

    public void setParameter( OLSyntaxNode parameter )
    {
        this.parameter = parameter;
    }

    @Override
    public void accept( OLVisitor visitor )
    {
        visitor.visit( this );
    }

    public OLSyntaxNode parameter()
    {
        return parameter;
    }
    

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj ) return true;
        if ( !super.equals( obj ) ) return false;
        if ( getClass() != obj.getClass() ) return false;
        ParameterizeInputPortInfo other = (ParameterizeInputPortInfo) obj;
        if ( parameter == null ) {
            if ( other.parameter != null ) return false;
        } else if ( !parameter.equals( other.parameter ) ) return false;
        return true;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((parameter == null) ? 0 : parameter.hashCode());
        return result;
    }

    @Override
    public String toString()
    {
        return "inputPort " + this.id() + " ( " + this.parameter + " )";
    }

    public void setLocation( URI location )
    {
        super.location = location;
    }

    public void setProtocolId( String protocolId )
    {
        super.protocolId = protocolId;
    }
}