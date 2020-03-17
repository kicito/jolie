package jolie.net.ports;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import jolie.net.AggregatedOperation;
import jolie.runtime.VariablePath;
import jolie.runtime.VariablePathBuilder;
import jolie.runtime.expression.Expression;

public class ParameterizeInputPort extends InputPort
{

    public final VariablePath portInfoPath;
    public final Expression expression;
    public final jolie.process.Process configurationProcess;

    public static ParameterizeInputPort create( String name, VariablePath portInfoPath,
            Expression expr, Interface iface, jolie.process.Process configurationProcess )
    {
        VariablePath locationPath =
                new VariablePathBuilder( false ).add( "location", 0 ).toVariablePath();
        VariablePath protocolPath =
                new VariablePathBuilder( false ).add( "protocol", 0 ).toVariablePath();
        Map< String, AggregatedOperation > aggregationMap = new HashMap<>();
        Map< String, OutputPort > redirectionMap = new HashMap<>();

        return new ParameterizeInputPort( name, locationPath, protocolPath, iface, aggregationMap,
                redirectionMap, portInfoPath, expr, configurationProcess );
    }

    @Override
	public URI location()
	{
		return URI.create( this.portInfoPath.getValue().getChildren("location").first().strValue() );
	}
    
    @Override
	public void setLocation( String location )
	{
        this.portInfoPath.getValue().getChildren("location").first().setValue(location);
    }

    private ParameterizeInputPort( String name, VariablePath locationVariablePath,
            VariablePath protocolConfigurationPath, Interface iface,
            Map< String, AggregatedOperation > aggregationMap,
            Map< String, OutputPort > redirectionMap, VariablePath portInfoPath, Expression expr,
            jolie.process.Process configurationProcess )
    {
        super( name, locationVariablePath, protocolConfigurationPath, iface, aggregationMap,
                redirectionMap );
        this.portInfoPath = portInfoPath;
        this.expression = expr;
        this.configurationProcess = configurationProcess;
    }

}
