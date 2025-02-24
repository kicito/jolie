/*
 * Copyright (C) 2020 Narongrit Unwerawattana <narongrit.kie@gmail.com>
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

package jolie.lang.parse.module;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * an Interface of Joile module Source
 */
public interface ModuleSource {

	/**
	 * @return URI location of the module
	 */
	URI uri();

	/**
	 * @return an optional include path for parsing this module
	 */
	Optional< String > includePath();

	/**
	 * @return an InputStream of source
	 */
	InputStream openStream() throws IOException;

	/**
	 * @return name of module
	 */
	String name();

	/**
	 * @return name of module
	 */
	Optional< URI > parentURI();


	static ModuleSource create( URI uri, Optional< InputStream > is ) throws FileNotFoundException {
		switch( uri.getScheme() ) {
		case "jap":
			return new JapSource( uri );
		case "file":
			return new PathSource( Paths.get( uri ) );
		default:
			if( is.isPresent() ) {
				return new InputStreamSource( is.get(), uri );
			} else {
				return new ProgramSource( null, uri );
			}
		}
	}

	public static ModuleSource create( URI uri ) throws FileNotFoundException {
		return ModuleSource.create( uri, Optional.empty() );
	}
}
