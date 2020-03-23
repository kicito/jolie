/***************************************************************************
 *   Copyright (C) by Fabrizio Montesi                                     *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Library General Public License as       *
 *   published by the Free Software Foundation; either version 2 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU Library General Public     *
 *   License along with this program; if not, write to the                 *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 *                                                                         *
 *   For details about the authors of this software, see the AUTHORS file. *
 ***************************************************************************/


package jolie.runtime.embedding;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import jolie.CommandLineException;
import jolie.Interpreter;
import jolie.JolieClassLoader;
import jolie.lang.Constants;
import jolie.lang.parse.ast.InputPortInfo;
import jolie.lang.parse.ast.InterfaceDefinition;
import jolie.lang.parse.ast.OLSyntaxNode;
import jolie.lang.parse.ast.OperationDeclaration;
import jolie.lang.parse.ast.OutputPortInfo;
import jolie.lang.parse.ast.ParameterizeInputPortInfo;
import jolie.lang.parse.ast.Program;
import jolie.lang.parse.ast.RequestResponseOperationDeclaration;
import jolie.lang.parse.ast.types.TypeDefinition;
import jolie.lang.parse.ast.types.TypeDefinitionUndefined;
import jolie.net.CommChannel;
import jolie.net.CommListener;
import jolie.net.ports.OutputPort;
import jolie.runtime.JavaService;
import jolie.runtime.Value;
import jolie.tracer.EmbeddingTraceAction;
import jolie.util.Pair;


public class JavaServiceLoader2 extends EmbeddedServiceLoader
{
	private final String serviceName;
	private final String servicePath;
	private final Interpreter interpreter;

	private String fromClientPortName = null;
    private final Pair < String ,Value > argumentValue;
	
	public JavaServiceLoader2( String serviceName, String servicePath, Interpreter currInterpreter ,Program program, Pair < String ,Value > argumentValue)
		throws IOException, CommandLineException, ClassNotFoundException
	{
		super( null );
		this.serviceName = "JAVA_" + serviceName;
		this.servicePath = servicePath;
		this.argumentValue = argumentValue;
		

		Program p = program;
		for ( int i = 0; i < p.children().size(); i++ ){
			OLSyntaxNode node = p.children().get(i);
			// there shall be only one inputport
			if (node instanceof ParameterizeInputPortInfo){
				ParameterizeInputPortInfo ip = (ParameterizeInputPortInfo) node;
				OutputPortInfo opInfo = new OutputPortInfo(ip.context(), this.serviceName);

				if (ip.id().equals( "dummy" )){
					continue;
				}
				fromClientPortName = ip.id();
				Map< String, String > redirection = new HashMap<String, String>();
				redirection.put(serviceName, this.serviceName );


				final JolieClassLoader cl = currInterpreter.getClassLoader();
				final Class<?> c = cl.loadClass( servicePath );
				List<Method> requestResponses = getMethodsAnnotatedWith(c, RequestResponse.class);
				InterfaceDefinition ifaceDef = new InterfaceDefinition(ip.context(), this.serviceName);
				for ( Method rr : requestResponses ){
					String opName = rr.getName();
					OperationDeclaration op = new RequestResponseOperationDeclaration(
						ip.context(),
						opName,
						TypeDefinitionUndefined.getInstance(),
						TypeDefinitionUndefined.getInstance(),
						new HashMap<String,TypeDefinition>()
					);
					opInfo.addOperation(op);
					ifaceDef.addOperation(op);
				}
				opInfo.addInterface(ifaceDef);

				InputPortInfo.AggregationItemInfo[] aggregationList =
						new InputPortInfo.AggregationItemInfo[] {
								new InputPortInfo.AggregationItemInfo( new String[] {this.serviceName},
										null )};

				ParameterizeInputPortInfo newIp = new ParameterizeInputPortInfo( ip.context(),
						ip.id(), aggregationList, redirection );
				newIp.setParameter( ip.parameter() );

				if ( ip.getDocumentation() != null && ip.getDocumentation().length() > 0 ) {
					newIp.setDocumentation( ip.getDocumentation() );
				}
				p.children().set( i, newIp );
				p.children().add( i-1, opInfo );
				break;
			}
		}
        
		List< String > newArgs = new ArrayList<>();
		newArgs.add( "-i" );
		newArgs.add( currInterpreter.programDirectory().getAbsolutePath() );
		
		String[] options = currInterpreter.optionArgs();
		newArgs.addAll( Arrays.asList( options ) );
        newArgs.add( "#" + servicePath + ".ol" );
		interpreter = new Interpreter(
			newArgs.toArray( new String[ newArgs.size() ] ),
			currInterpreter.getClassLoader(),
			currInterpreter.programDirectory(),
			currInterpreter,
            p,
            argumentValue
        );
	}

	public static List<Method> getMethodsAnnotatedWith(final Class<?> type, final Class<? extends Annotation> annotation) {
		final List<Method> methods = new ArrayList<Method>();
		Class<?> klass = type;
		while (klass != Object.class) { // need to iterated thought hierarchy in order to retrieve methods from above the current instance
			// iterate though the list of methods declared in the class represented by klass variable, and add those annotated with the specified annotation
			final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(klass.getDeclaredMethods()));       
			for (final Method method : allMethods) {
				if (method.isAnnotationPresent(RequestResponse.class)) {
					Annotation annotInstance = method.getAnnotation(annotation);
					// TODO process annotInstance
					methods.add(method);
				}
			}
			// move to the upper class in the hierarchy in search for more methods
			klass = klass.getSuperclass();
		}
		return methods;
	}

	@Override
	public void load()
		throws EmbeddedServiceLoadingException
	{
		// OutputPort javaOP = new OutputPort(interpreter, serviceName);
		// interpreter.register(serviceName, javaOP);
		
		Future< Exception > f = interpreter.start2();
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
		
		try {
			final JolieClassLoader cl = interpreter.getClassLoader();
			final Class<?> c = cl.loadClass( servicePath );
			final Object obj = c.getDeclaredConstructor().newInstance();
			if ( !(obj instanceof JavaService) ) {
				throw new EmbeddedServiceLoadingException( servicePath + " is not a valid JavaService" );
			}
			final JavaService service = (JavaService)obj;
			// List<Method> requestResponses = getMethodsAnnotatedWith(service.getClass(), RequestResponse.class);
			service.setInterpreter( interpreter );
			final CommChannel javaChannel = new JavaCommChannel( service );

			Value l;
			Value r = interpreter.initThread().state().root();
			l = r.getFirstChild( serviceName ).getFirstChild( Constants.LOCATION_NODE_NAME );
			l.setValue(javaChannel);

			CommListener cListener = interpreter.commCore().getListenerByInputPortName(fromClientPortName);
			CommChannel cc = interpreter.commCore().getLocalCommChannel(cListener);
			cc.setRedirectionChannel(javaChannel);

			interpreter.tracer().trace(	() -> new EmbeddingTraceAction(
				EmbeddingTraceAction.Type.SERVICE_LOAD,
				"Java Service Loader",
				c.getCanonicalName(),
				null
			) );
		} catch( InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e ) {
			throw new EmbeddedServiceLoadingException( e );
		}
	}
}