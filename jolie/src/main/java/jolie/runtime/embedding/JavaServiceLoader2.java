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

import java.lang.reflect.InvocationTargetException;
import jolie.Interpreter;
import jolie.JolieClassLoader;
import jolie.runtime.JavaService;
import jolie.runtime.expression.Expression;
import jolie.tracer.EmbeddingTraceAction;


public class JavaServiceLoader2 extends EmbeddedServiceLoader
{
	private final String servicePath;
	private final Interpreter interpreter;
	
	public JavaServiceLoader2( String servicePath, Interpreter interpreter )
	{
		super( null );
		this.interpreter = interpreter;
		this.servicePath = servicePath;
	}

	@Override
	public void load()
		throws EmbeddedServiceLoadingException
	{
		try {
			final JolieClassLoader cl = interpreter.getClassLoader();
			final Class<?> c = cl.loadClass( servicePath );
			final Object obj = c.getDeclaredConstructor().newInstance();
			if ( !(obj instanceof JavaService) ) {
				throw new EmbeddedServiceLoadingException( servicePath + " is not a valid JavaService" );
			}
			final JavaService service = (JavaService)obj;
			service.setInterpreter( interpreter );
			setChannel(	new JavaCommChannel( service ) );
			
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