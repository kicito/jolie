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
import jolie.runtime.Value;
import jolie.runtime.expression.Expression;
import jolie.runtime.typing.Type;
import jolie.runtime.typing.TypeCheckingException;

public class ServiceNodeLoader extends EmbeddedServiceLoader
{
	private final Interpreter parentInterpreter;
	private final ServiceNode serviceNode;
	private final Type acceptedType;

	protected ServiceNodeLoader( Expression channelDest, Interpreter parentInterpreter,
			ServiceNode serviceNode, Type acceptedType ) throws IOException, CommandLineException
	{
		super( channelDest );
		this.parentInterpreter = parentInterpreter;
		this.serviceNode = serviceNode;
		this.acceptedType = acceptedType;
	}

	@Override
	public void load( Value argumentValue ) throws EmbeddedServiceLoadingException
	{
		try {
			Value passingArgument = Value.create();
			if (acceptedType != null){
				acceptedType.check( argumentValue );
				passingArgument.getChildren(this.serviceNode.parameterPath().get()).first().deepCopy(argumentValue);
			}
			List< String > newArgs = new ArrayList<>();
			newArgs.add( "-i" );
			newArgs.add( parentInterpreter.programDirectory().getAbsolutePath() );

			String[] options = parentInterpreter.optionArgs();
			newArgs.addAll( Arrays.asList( options ) );
			newArgs.add( "#" + serviceNode.name() + ".ol" );
			Interpreter interpreter =
					new Interpreter( newArgs.toArray( new String[newArgs.size()] ),
							parentInterpreter.getClassLoader(),
							parentInterpreter.programDirectory(), parentInterpreter,
							serviceNode.program(), serviceNode.parameterPath().get(), passingArgument );
			Future< Exception > f = interpreter.start();
			Exception e = f.get();
			if ( e == null ) {
				setChannel( interpreter.commCore().getLocalCommChannel() );
			} else {
				throw new EmbeddedServiceLoadingException( e );
			}
		} catch (IOException | InterruptedException | ExecutionException
				| EmbeddedServiceLoadingException | CommandLineException
				| TypeCheckingException e) {
			throw new EmbeddedServiceLoadingException( e );
		}
	}

	public String serviceName(){
		return serviceNode.name();
	}

}
