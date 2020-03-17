package jolie.net;

import java.io.IOException;
import jolie.Interpreter;
import jolie.net.ports.ParameterizeInputPort;

public class ParameterizeCommListener extends CommListener
{

	public ParameterizeCommListener( Interpreter interpreter, ParameterizeInputPort inputPort ) throws IOException
	{
		super( interpreter, inputPort );
	}

	@Override
	public void shutdown()
	{
		// not use
	}

	@Override
	public void run()
	{
		// not use
	}
}
