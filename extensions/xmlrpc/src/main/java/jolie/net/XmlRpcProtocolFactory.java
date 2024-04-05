/***************************************************************************
 *   Copyright (C) 2009 by Fabrizio Montesi                                *
 *   Copyright (C) 2009 by Claudio Guidi                                   *
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


package jolie.net;

import java.io.IOException;
import java.net.URI;
import jolie.net.ext.CommProtocolFactory;
import jolie.net.protocols.CommProtocol;
import jolie.runtime.VariablePath;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import jolie.runtime.AndJarDeps;

@AndJarDeps( { "jolie-xml.jar" } )
public class XmlRpcProtocolFactory extends CommProtocolFactory {
	final private Transformer transformer;
	final private DocumentBuilderFactory docBuilderFactory;
	final private DocumentBuilder docBuilder;

	public XmlRpcProtocolFactory( CommCore commCore )
		throws ParserConfigurationException, TransformerConfigurationException {
		super( commCore );
		docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setNamespaceAware( true );
		docBuilder = docBuilderFactory.newDocumentBuilder();
		transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
	}

	@Override
	public CommProtocol createInputProtocol( VariablePath configurationPath, URI location )
		throws IOException {
		return new XmlRpcProtocol(
			configurationPath,
			location,
			true,
			transformer,
			docBuilder,
			commCore().interpreter() );
	}

	@Override
	public CommProtocol createOutputProtocol( VariablePath configurationPath, URI location )
		throws IOException {
		return new XmlRpcProtocol(
			configurationPath,
			location,
			false,
			transformer,
			docBuilder,
			commCore().interpreter() );
	}
}
