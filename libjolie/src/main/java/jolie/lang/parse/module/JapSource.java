/*
 * Copyright (C) 2025 Narongrit Unwerawattana <narongrit.kie@gmail.com>
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
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import jolie.lang.Constants;



public class JapSource implements ModuleSource {

	private final JarFile japFile;
	private final URI uri;
	private final String filePath;
	private final Path parentPath;
	private final ZipEntry moduleEntry;

	public JapSource( URI uri ) throws FileNotFoundException {
		if( !uri.getScheme().equals( "jap" ) ) {
			throw new IllegalArgumentException( "Passing uri is scheme is invalid, expected 'jap'" );
		}
		this.uri = uri;
		try {
			URI fileURI = new URI( uri.getSchemeSpecificPart() );
			if( fileURI.toString().contains( "!" ) ) {
				// uri contains entry point
				String japURIStrings[] = fileURI.toString().split( "!" );
				this.japFile = new JarFile( Paths.get( new URI( japURIStrings[ 0 ] ) ).toFile() );
				if( this.japFile.getEntry( japURIStrings[ 1 ] ) != null ) {
					this.filePath = japURIStrings[ 1 ];
					this.moduleEntry = this.japFile.getEntry( this.filePath );
				} else {
					if( japURIStrings[ 1 ].startsWith( "/" ) ) { // try with/without trailing slash
						this.filePath = japURIStrings[ 1 ].substring( 1 );
						this.moduleEntry = this.japFile.getEntry( this.filePath );
					} else {
						this.filePath = "/" + japURIStrings[ 1 ];
						this.moduleEntry = this.japFile.getEntry( this.filePath );
					}
				}
			} else {
				// lookup entrypoint from Manifest or guess by name
				this.japFile = new JarFile( Paths.get( fileURI ).toFile() );
				Manifest manifest = this.japFile.getManifest();
				if( manifest != null ) {
					// See if a main program is defined through a Manifest attribute
					Attributes attrs = manifest.getMainAttributes();
					this.filePath = attrs.getValue( Constants.Manifest.MAIN_PROGRAM );
					this.moduleEntry = japFile.getEntry( this.filePath );
				} else {
					// guess by name
					this.filePath = this.japFile.getName() + ".ol";
					this.moduleEntry = this.japFile.getEntry( this.filePath );
				}
			}
			if( this.moduleEntry == null ) {
				throw new FileNotFoundException( uri.toString() );
			}
		} catch( IOException | URISyntaxException e ) {
			throw new FileNotFoundException( uri.toString() );
		}

		this.parentPath = Paths.get( this.filePath ).getParent();
	}

	public JapSource( Path f, List< String > path ) throws FileNotFoundException {
		if( !f.toFile().exists() ) {
			throw new FileNotFoundException( f.toString() );
		}
		try {
			this.japFile = new JarFile( f.toFile() );
			this.uri = f.toUri();
			this.filePath = String.join( "/", path );
			moduleEntry = japFile.getEntry( this.filePath + ".ol" );
			if( moduleEntry == null ) {
				throw new FileNotFoundException(
					this.filePath + " in " + f.toString() );
			}
			this.parentPath = Paths.get( this.filePath ).getParent();
		} catch( IOException e ) {
			throw new FileNotFoundException( f.toString() );
		}
	}

	/**
	 * additional includePath of JAP source is a parent path of the main execution file defined at main
	 * program
	 */
	@Override
	public Optional< String > includePath() {
		return Optional
			.of( "jap:" + this.uri.toString() + "!/" + (this.parentPath != null ? this.parentPath.toString() : "") );
	}

	@Override
	public URI uri() {
		return this.uri;
	}

	@Override
	public InputStream openStream() throws IOException {
		return this.japFile.getInputStream( this.moduleEntry );
	}

	@Override
	public String name() {
		return this.japFile.getName() + "!/" + this.moduleEntry.getName();
	}

	@Override
	public Optional< URI > parentURI() {
		return Optional
			.of( URI.create(
				"jap:" + this.uri.toString() + "!/" + (this.parentPath != null ? this.parentPath.toString() : "") ) );
	}
}
