package jolie.runtime.embedding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import jolie.CommandLineException;
import jolie.Interpreter;
import jolie.lang.Constants;
import jolie.lang.parse.ast.JavaServiceNode;
import jolie.lang.parse.ast.ServiceNode;
import jolie.lang.parse.ast.ServiceNode.Technology;
import jolie.net.CommChannel;
import jolie.runtime.JavaService;
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

		Interpreter interpreter = null;
		try {
			Value passingArgument = Value.create();
			if ( acceptedType != null ) {
				acceptedType.check( argumentValue );
				passingArgument.getChildren( this.serviceNode.parameterPath().get() ).first()
						.deepCopy( argumentValue );
			}
			List< String > newArgs = new ArrayList<>();
			newArgs.add( "-i" );
			newArgs.add( parentInterpreter.programDirectory().getAbsolutePath() );

			String[] options = parentInterpreter.optionArgs();
			newArgs.addAll( Arrays.asList( options ) );
			newArgs.add( "#" + serviceNode.name() + ".ol" );
			interpreter =
					new Interpreter( newArgs.toArray( new String[newArgs.size()] ),
							parentInterpreter.getClassLoader(),
							parentInterpreter.programDirectory(), parentInterpreter,
							serviceNode.program(), passingArgument );
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

		if ( this.serviceNode.technology() == Technology.JAVA ) {
			// create redirects from client to java service port
			JavaServiceLoader2 javaServiceLoader = new JavaServiceLoader2( parentInterpreter );
			JavaServiceNode javaServiceNode = (JavaServiceNode) this.serviceNode;
			JavaService javaService =
					javaServiceLoader.loadJavaService( javaServiceNode.javaServicePath() );
			javaService.setInterpreter(interpreter);
			final CommChannel javaChannel = new JavaCommChannel( javaService );

			Value r = interpreter.initThread().state().root();
			r.getFirstChild( "toJava" ).getFirstChild( Constants.LOCATION_NODE_NAME )
					.setValue( javaChannel );
		}
	}

	public String serviceName(){
		return serviceNode.name();
	}

}
