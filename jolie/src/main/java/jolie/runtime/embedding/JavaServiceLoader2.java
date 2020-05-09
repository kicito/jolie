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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import jolie.Interpreter;
import jolie.JolieClassLoader;
import jolie.lang.parse.ast.InterfaceDefinition;
import jolie.lang.parse.ast.OperationDeclaration;
import jolie.lang.parse.ast.RequestResponseOperationDeclaration;
import jolie.lang.parse.ast.types.TypeDefinition;
import jolie.lang.parse.ast.types.TypeDefinitionUndefined;
import jolie.lang.parse.context.URIParsingContext;
import jolie.runtime.JavaService;


public class JavaServiceLoader2
{

	private final Interpreter interpreter;

	public JavaServiceLoader2(Interpreter interpreter){
		this.interpreter = interpreter;
	}

	public JavaService loadJavaService( String servicePath )
			throws EmbeddedServiceLoadingException
	{

		final JolieClassLoader cl = interpreter.getClassLoader();
		try {
			final Class< ? > c = cl.loadClass( servicePath );
			final Object obj = c.getDeclaredConstructor().newInstance();

			if ( !(obj instanceof JavaService) ) {
				throw new EmbeddedServiceLoadingException(
						servicePath + " is not a valid JavaService" );
			}
			return (JavaService) obj;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new EmbeddedServiceLoadingException( e );
		}
	}
	public static InterfaceDefinition buildInterfaceFromJavaService( JavaService service )
	{

		List< Method > requestResponses =
				getMethodsAnnotatedWith( service.getClass(), RequestResponse.class );
		InterfaceDefinition ifaceDef = new InterfaceDefinition( URIParsingContext.DEFAULT, service.getClass().getSimpleName() + "Interface" );
		for (Method rr : requestResponses) {
			String opName = rr.getName();
			OperationDeclaration op = new RequestResponseOperationDeclaration( URIParsingContext.DEFAULT, opName,
					TypeDefinitionUndefined.getInstance(), TypeDefinitionUndefined.getInstance(),
					new HashMap< String, TypeDefinition >() );
			System.out.println(op.id());
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
}
