/***************************************************************************
 *   Copyright (C) 2025 by Monitoring Team                                *
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

package jolie.monitoring.events;

import jolie.monitoring.MonitoringEvent;
import jolie.runtime.Value;

/**
 * Monitoring event triggered when an invalid operation is received at the CommCore level. This
 * event captures messages where the operation is not specified in the input port.
 */
public class InvalidOperationEvent extends MonitoringEvent {

	public InvalidOperationEvent( String operationName, String portName, String messageId, String resourcePath,
		Value message, String internalMessageId ) {

		super( "InvalidOperation", Value.create() );

		data().getFirstChild( "operationName" ).setValue( operationName );
		data().getFirstChild( "portName" ).setValue( portName );
		data().getFirstChild( "messageId" ).setValue( messageId );
		data().getFirstChild( "resourcePath" ).setValue( resourcePath );
		data().getFirstChild( "message" ).deepCopy( message );
		data().getFirstChild( "internalMessageId" ).setValue( internalMessageId );

	}

}
