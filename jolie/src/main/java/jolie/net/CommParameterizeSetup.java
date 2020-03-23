package jolie.net;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Future;
import jolie.ExecutionThread;
import jolie.Interpreter;
import jolie.lang.Constants;
import jolie.net.ext.CommListenerFactory;
import jolie.net.ext.CommProtocolFactory;
import jolie.net.ports.InputPort;
import jolie.net.ports.ParameterizeInputPort;
import jolie.runtime.ExitingException;
import jolie.runtime.FaultException;
import jolie.runtime.InputOperation;
import jolie.runtime.Value;
import jolie.runtime.VariablePath;

public class CommParameterizeSetup extends ExecutionThread
{
    InputPort inputPort;
    Interpreter interpreter;
    VariablePath inputPortInfoPath;
    CommListener listener;

    public CommParameterizeSetup( ParameterizeInputPort inputPort, Interpreter interpreter )
    {
        super( inputPort.configurationProcess, interpreter.initThread() );
        this.inputPort = inputPort;
        this.inputPortInfoPath = inputPort.portInfoPath;
        this.interpreter = interpreter;
    }

    public jolie.State state()
    {
        return parent.state();
    }

    @Override
    public Future< SessionMessage > requestMessage( InputOperation operation,
            ExecutionThread ethread )
    {
        return null;
    }

    @Override
    public Future< SessionMessage > requestMessage( Map< String, InputOperation > operations,
            ExecutionThread ethread )
    {
        return null;
    }

    @Override
    public String getSessionId()
    {
        return parent.getSessionId();
    }

    @Override
    public void runProcess()
    {
        try {
            process().run();
            Value v = inputPortInfoPath.getValue();
            String location = v.getChildren( "location" ).first().strValue();
            String protocol = v.getChildren( "protocol" ).first().strValue();

            inputPort.setLocation( location );
            String medium = inputPort.location().getScheme();

            CommProtocolFactory protocolFactory = null;
            CommListenerFactory factory = null;


            try {
                protocolFactory = this.interpreter.commCore().getCommProtocolFactory( protocol );
            } catch (IOException e1) {
                throw new FaultException( e1 );
            }

            if ( location.equals( Constants.LOCAL_LOCATION_KEYWORD ) ) {
                final LocalListener l = this.interpreter.commCore().localListener();
                l.mergeInterface( inputPort.getInterface() );
                l.addAggregations( inputPort.aggregationMap() );
                l.addRedirections( inputPort.redirectionMap() );
                listener = l;
            } else if ( protocolFactory != null || inputPort.location().getScheme()
                    .equals( Constants.LOCAL_LOCATION_KEYWORD ) ) {
                try {
                    factory = this.interpreter.commCore().getCommListenerFactory( medium );
                    if ( factory == null ) {
                        throw new UnsupportedCommMediumException( medium );
                    }
                    listener = factory.createListener( interpreter, protocolFactory, inputPort );
                } catch (IOException e) {
                    throw new FaultException( e );
                }
            } else {
                throw new FaultException( "Communication protocol extension for protocol "
                        + protocol + " not found." );
            }

        } catch (FaultException | ExitingException e) {
            e.printStackTrace();
        }
    }
}
