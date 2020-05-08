package jolie.runtime.embedding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import jolie.CommandLineException;
import jolie.Interpreter;
import jolie.lang.parse.ast.ServiceNode;
import jolie.process.DeepCopyProcess;
import jolie.process.Process;
import jolie.runtime.VariablePathBuilder;
import jolie.runtime.expression.Expression;

public class ServiceNodeLoader extends EmbeddedServiceLoader
{
	private final Interpreter interpreter;

    protected ServiceNodeLoader( 
        Expression channelDest,
        Interpreter currInterpreter,
        ServiceNode serviceNode,
        Expression serviceArgument
    )
		throws IOException, CommandLineException
    {
        super( channelDest );
		List< String > newArgs = new ArrayList<>();
		newArgs.add( "-i" );
		newArgs.add( currInterpreter.programDirectory().getAbsolutePath() );
		
		String[] options = currInterpreter.optionArgs();
		newArgs.addAll( Arrays.asList( options ) );
        newArgs.add( "#" + serviceNode.name() + ".ol" );

		interpreter = new Interpreter(
			newArgs.toArray( new String[ newArgs.size() ] ),
			currInterpreter.getClassLoader(),
			currInterpreter.programDirectory(),
			currInterpreter,
			serviceNode.program(),
			serviceNode.parameterPath(),
            serviceArgument.evaluate()
		);
    }

    @Override
    public void load() 
        throws EmbeddedServiceLoadingException
    {
		Future< Exception > f = interpreter.start();
		try {
			Exception e = f.get();
			if ( e == null ) {
				setChannel( interpreter.commCore().getLocalCommChannel() );
			} else {
				throw new EmbeddedServiceLoadingException( e );
			}
		} catch( InterruptedException | ExecutionException | EmbeddedServiceLoadingException e ) {
			throw new EmbeddedServiceLoadingException( e );
		}
	}

	public Interpreter interpreter()
	{
		return interpreter;
	}
    
}