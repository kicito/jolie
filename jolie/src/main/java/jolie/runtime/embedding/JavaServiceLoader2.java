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
import java.util.Map.Entry;
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
import jolie.runtime.JavaService;
import jolie.runtime.Value;
import jolie.runtime.typing.Type;
import jolie.runtime.typing.TypeCheckingException;
import jolie.tracer.EmbeddingTraceAction;
import jolie.util.Pair;


public class JavaServiceLoader2 extends EmbeddedServiceLoader
{
	private final String serviceName;
	private final Interpreter interpreter;

	private String fromClientPortName = null;

	private final JavaService service;
	private final Value argumentValue;
	private final Type parameterType;

	public JavaServiceLoader2( String serviceName, String servicePath, Interpreter currInterpreter,
			Program program, Pair< String, Value > argument, Type parameterType )
			throws EmbeddedServiceLoaderCreationException
	{
		super( null );
		this.serviceName = "JAVA_" + serviceName;
		this.argumentValue = argument.value();
		this.parameterType = parameterType;

		final JolieClassLoader cl = currInterpreter.getClassLoader();
		try {
			final Class< ? > c = cl.loadClass( servicePath );
			final Object obj = c.getDeclaredConstructor().newInstance();

			if ( !(obj instanceof JavaService) ) {
				throw new EmbeddedServiceLoaderCreationException(
						servicePath + " is not a valid JavaService" );
			}
			this.service = (JavaService) obj;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new EmbeddedServiceLoaderCreationException( e );
		}


		InterfaceDefinition ifaceDef = buildInterfaceFromJavaService( service );

		ParameterizeInputPortInfo ip = null;
		Program p = program;
		int portIndex;
		for (portIndex = 0; portIndex < p.children().size(); portIndex++) {
			OLSyntaxNode node = p.children().get( portIndex );
			// there shall be only one inputport
			if ( node instanceof ParameterizeInputPortInfo ) {
				ip = (ParameterizeInputPortInfo) node;
				break;
			}
		}

		if ( ip == null ) {
			throw new EmbeddedServiceLoaderCreationException(
					"inputPort to communicate with embedder is not defined" );
		}

		// create new outputPort for communicate with embedder via aggregation
		OutputPortInfo opInfo = new OutputPortInfo( ip.context(), this.serviceName );
		fromClientPortName = ip.id();

		Map< String, String > redirection = new HashMap< String, String >();

		opInfo.addInterface( ifaceDef );
		for (Entry< String, OperationDeclaration > op : ifaceDef.operationsMap().entrySet()) {
			opInfo.addOperation( op.getValue() );
		}

		// set create new aggregation for existed inputPort
		InputPortInfo.AggregationItemInfo[] aggregationList =
				new InputPortInfo.AggregationItemInfo[] {new InputPortInfo.AggregationItemInfo(
						new String[] {this.serviceName}, null )};

		ParameterizeInputPortInfo newIp = new ParameterizeInputPortInfo( ip.context(), ip.id(),
				aggregationList, redirection );
		newIp.setParameter( ip.parameter() );

		if ( ip.getDocumentation() != null && ip.getDocumentation().length() > 0 ) {
			newIp.setDocumentation( ip.getDocumentation() );
		}
		p.children().set( portIndex, newIp );
		p.children().add( portIndex - 1, opInfo );

		List< String > newArgs = new ArrayList<>();
		newArgs.add( "-i" );
		newArgs.add( currInterpreter.programDirectory().getAbsolutePath() );

		String[] options = currInterpreter.optionArgs();
		newArgs.addAll( Arrays.asList( options ) );
		newArgs.add( "#" + servicePath + ".ol" );
		try {
			interpreter = new Interpreter( newArgs.toArray( new String[newArgs.size()] ),
					currInterpreter.getClassLoader(), currInterpreter.programDirectory(),
					currInterpreter, p, argument );
		} catch (CommandLineException | IOException e) {
			throw new EmbeddedServiceLoaderCreationException(
					"unable to create interpreter instance" );
		}
	}

	private InterfaceDefinition buildInterfaceFromJavaService( JavaService service )
	{

		List< Method > requestResponses =
				getMethodsAnnotatedWith( service.getClass(), RequestResponse.class );
		InterfaceDefinition ifaceDef = new InterfaceDefinition( null, this.serviceName );
		for (Method rr : requestResponses) {
			String opName = rr.getName();
			OperationDeclaration op = new RequestResponseOperationDeclaration( null, opName,
					TypeDefinitionUndefined.getInstance(), TypeDefinitionUndefined.getInstance(),
					new HashMap< String, TypeDefinition >() );
			ifaceDef.addOperation( op );
		}
		return ifaceDef;
	}

	private static List< Method > getMethodsAnnotatedWith( final Class< ? > type,
			final Class< ? extends Annotation > annotation )
	{
		final List< Method > methods = new ArrayList< Method >();
		Class< ? > klass = type;
		while (klass != Object.class) { 
			final List< Method > allMethods =
					new ArrayList< Method >( Arrays.asList( klass.getDeclaredMethods() ) );
			for (final Method method : allMethods) {
				if ( method.isAnnotationPresent( RequestResponse.class ) ) {
					methods.add( method );
				}
			}
			// move to the upper class in the hierarchy in search for more methods
			klass = klass.getSuperclass();
		}
		return methods;
	}

	@Override
	public void load() throws EmbeddedServiceLoadingException
	{
		try {
			parameterType.check( argumentValue );
		} catch (TypeCheckingException e1) {
			throw new EmbeddedServiceLoadingException( e1 );
		}

		Future< Exception > f = interpreter.start2();
		try {
			Exception e = f.get();

			if ( e == null ) {
				setChannel( interpreter.commCore().getLocalCommChannel() );
			} else {
				throw new EmbeddedServiceLoadingException( e );
			}
		} catch (InterruptedException | ExecutionException | EmbeddedServiceLoadingException e) {
			throw new EmbeddedServiceLoadingException( e );
		}

		service.setInterpreter( interpreter );
		final CommChannel javaChannel = new JavaCommChannel( service );

		Value l;
		Value r = interpreter.initThread().state().root();
		l = r.getFirstChild( serviceName ).getFirstChild( Constants.LOCATION_NODE_NAME );
		l.setValue( javaChannel );

		CommListener cListener =
				interpreter.commCore().getListenerByInputPortName( fromClientPortName );
		CommChannel cc = interpreter.commCore().getLocalCommChannel( cListener );
		cc.setRedirectionChannel( javaChannel );

		interpreter.tracer()
				.trace( () -> new EmbeddingTraceAction( EmbeddingTraceAction.Type.SERVICE_LOAD,
						"Java Service Loader", service.getClass().getCanonicalName(), null ) );

	}
}
