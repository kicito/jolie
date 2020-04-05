/*
 * Copyright (C) 2015 Martin Wolf <mw@martinwolf.eu>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package jolie.runtime.embedding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import jolie.CommandLineException;
import jolie.Interpreter;
import jolie.lang.parse.ast.Program;
import jolie.runtime.Value;
import jolie.runtime.typing.Type;
import jolie.runtime.typing.TypeCheckingException;
import jolie.util.Pair;


public class JolieServiceLoader2 extends EmbeddedServiceLoader
{
	private final Interpreter parentInterpreter;
	private Interpreter thisInterpreter;
	private final String paramPath;
	private final Value argumentValue;
	private final Type parameterType;
	private final String[] args;
	private final Program program;

	public JolieServiceLoader2( Interpreter currInterpreter, String serviceName, Program program,
			Pair< String, Value > argument, Type parameterType )
	{
		super( null );
		if ( argument != null ) {
			this.paramPath = argument.key();
			this.argumentValue = argument.value();
			this.parameterType = parameterType;
		} else {
			this.paramPath = null;
			this.argumentValue = null;
			this.parameterType = null;
		}
		List< String > newArgs = new ArrayList<>();
		newArgs.add( "-i" );
		newArgs.add( currInterpreter.programDirectory().getAbsolutePath() );

		String[] options = currInterpreter.optionArgs();
		newArgs.addAll( Arrays.asList( options ) );
		newArgs.add( "#" + serviceName + ".ol" );
		this.args = newArgs.toArray( new String[newArgs.size()] );
		this.program = program;
		this.parentInterpreter = currInterpreter; 
	}

	@Override
	public void load() throws EmbeddedServiceLoadingException
	{
		if ( parameterType != null ) {
			try {
				Type.assignDefault( this.argumentValue, this.parameterType );
				parameterType.check( this.argumentValue );
			} catch (TypeCheckingException e1) {
				throw new EmbeddedServiceLoadingException( e1 );
			}
		}
		try {
			thisInterpreter = new Interpreter( this.args, this.parentInterpreter.getClassLoader(),
					this.parentInterpreter.programDirectory(), this.parentInterpreter, program,
					new Pair< String, Value >( this.paramPath, this.argumentValue ) );
		} catch (CommandLineException | IOException e1) {
			throw new EmbeddedServiceLoadingException( e1 );
		}
		Future< Exception > f = thisInterpreter.start2();
		try {
			Exception e = f.get();
			if ( e == null ) {
				setChannel( thisInterpreter.commCore().getLocalCommChannel() );
			} else {
				throw new EmbeddedServiceLoadingException( e );
			}
		} catch (InterruptedException | ExecutionException | EmbeddedServiceLoadingException e) {
			throw new EmbeddedServiceLoadingException( e );
		}
	}

	public Interpreter interpreter()
	{
		return thisInterpreter;
	}
}
