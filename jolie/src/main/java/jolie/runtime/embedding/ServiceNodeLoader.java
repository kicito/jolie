package jolie.runtime.embedding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import jolie.CommandLineException;
import jolie.Interpreter;
import jolie.lang.Constants;
import jolie.lang.parse.ast.ForeignServiceNode;
import jolie.lang.parse.ast.ServiceNode;
import jolie.lang.parse.ast.types.TypeChoiceDefinition;
import jolie.lang.parse.ast.types.TypeDefinition;
import jolie.lang.parse.ast.types.TypeDefinitionLink;
import jolie.lang.parse.ast.types.TypeInlineDefinition;
import jolie.runtime.Value;
import jolie.runtime.expression.Expression;
import jolie.runtime.typing.Type;
import jolie.runtime.typing.TypeCheckingException;

public class ServiceNodeLoader extends EmbeddedServiceLoader
{
	private final Interpreter parentInterpreter;
	private final ServiceNode serviceNode;

	protected ServiceNodeLoader( Expression channelDest, Interpreter parentInterpreter,
			ServiceNode serviceNode ) throws IOException, CommandLineException
	{
		super( channelDest );
		this.parentInterpreter = parentInterpreter;
		this.serviceNode = serviceNode;
	}

	@Override
	public void load( Value argumentValue ) throws EmbeddedServiceLoadingException
	{

		Interpreter interpreter = null;
		try {
			Value passingArgument = Value.create();
			if ( this.serviceNode.parameterType().isPresent() ) {
				Type acceptedType = buildType( this.serviceNode.parameterType().get() );
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
			interpreter = new Interpreter( newArgs.toArray( new String[newArgs.size()] ),
					parentInterpreter.getClassLoader(), parentInterpreter.programDirectory(),
					parentInterpreter, serviceNode.program(), passingArgument );
			Future< Exception > f = interpreter.start();
			Exception e = f.get();
			if ( e == null ) {
				setChannel( interpreter.commCore().getLocalCommChannel() );
			} else {
				throw new EmbeddedServiceLoadingException( e );
			}

			final EmbeddedServiceLoader foreignLoader;
			final Expression channelDest;
			switch (serviceNode.type()) {
				case JOLIE:
					// do nothing
					return;
				case JAVA:
					ForeignServiceNode javaServiceNode = (ForeignServiceNode) this.serviceNode;
					channelDest =
							interpreter.initThread().state().root().getFirstChild( "toForeign" )
									.getFirstChild( Constants.LOCATION_NODE_NAME );
					foreignLoader = new JavaServiceLoader( channelDest,
							javaServiceNode.servicePath(), interpreter );
					foreignLoader.load( null );
					break;
				default:
					String serviceType = serviceNode.type().toString();
					ForeignServiceNode foreignServiceNode = (ForeignServiceNode) this.serviceNode;
					EmbeddedServiceLoaderFactory factory =
							interpreter.getEmbeddedServiceLoaderFactory( serviceType );
					channelDest =
							interpreter.initThread().state().root().getFirstChild( "toForeign" )
									.getFirstChild( Constants.LOCATION_NODE_NAME );
					if ( factory == null ) {
						throw new IOException( "Could not find extension to load services of type "
								+ serviceType );
					}
					foreignLoader = factory.createLoader( interpreter, serviceType,
							foreignServiceNode.servicePath(), channelDest );
					foreignLoader.load( null );
					break;
			}

		} catch (IOException | InterruptedException | ExecutionException
				| EmbeddedServiceLoadingException | CommandLineException | TypeCheckingException
				| EmbeddedServiceLoaderCreationException e) {
			throw new EmbeddedServiceLoadingException( e );
		}
	}

	public String serviceName()
	{
		return serviceNode.name();
	}

	private Type buildType( TypeDefinition typeDefinition )
	{
		if ( typeDefinition instanceof TypeDefinitionLink ) {
			return buildType( (TypeDefinitionLink) typeDefinition );
		} else if ( typeDefinition instanceof TypeInlineDefinition ) {
			return buildType( (TypeInlineDefinition) typeDefinition );
		} else if ( typeDefinition instanceof TypeChoiceDefinition ) {
			return buildType( (TypeChoiceDefinition) typeDefinition );
		}
		return null; // dead code
	}

	private Type buildType( TypeDefinitionLink n )
	{
		return buildType( n.linkedType() );
	}


	private Type buildType( TypeInlineDefinition n )
	{
		Type t;
		if ( n.untypedSubTypes() ) {
			t = Type.create( n.nativeType(), n.cardinality(), true, null );
		} else {
			Map< String, Type > subTypes = new HashMap< String, Type >();
			if ( n.subTypes() != null ) {
				for (Entry< String, TypeDefinition > entry : n.subTypes()) {
					subTypes.put( entry.getKey(), buildType( entry.getValue() ) );
				}
			}
			t = Type.create( n.nativeType(), n.cardinality(), false, subTypes );
		}
		return t;
	}

	private Type buildType( TypeChoiceDefinition n )
	{
		return Type.createChoice( n.cardinality(), buildType( n.left() ), buildType( n.right() ) );
	}

}
