package jolie.net.ports;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import jolie.Interpreter;
import jolie.net.AggregatedOperation;
import jolie.runtime.VariablePath;
import jolie.runtime.VariablePathBuilder;
import jolie.runtime.expression.Expression;

// public class ParameterizeOutputPort extends OutputPort
// {

//     public static ParameterizeOutputPort create( String name, VariablePath portInfoPath,
//             Expression expr, Interface iface, jolie.process.Process configurationProcess )
//     {
//         VariablePath locationPath =
//                 new VariablePathBuilder( false ).add( "location", 0 ).toVariablePath();
//         VariablePath protocolPath =
//                 new VariablePathBuilder( false ).add( "protocol", 0 ).toVariablePath();
//         Map< String, AggregatedOperation > aggregationMap = new HashMap<>();
//         Map< String, OutputPort > redirectionMap = new HashMap<>();

//         return new ParameterizeOutputPort( name, locationPath, protocolPath, iface, aggregationMap,
//                 redirectionMap, portInfoPath, expr, configurationProcess );
//     }

//     public ParameterizeOutputPort( Interpreter interpreter, String id, String protocolId,
//             Process protocolConfigurationProcess, URI locationURI, Interface iface,
//             boolean isConstant )
//     {
//         super( interpreter, id, protocolId, protocolConfigurationProcess, locationURI, iface,
//                 isConstant );
//     }
// }
