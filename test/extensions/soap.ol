/***************************************************************************
 *   Copyright (C) 2015 by Matthias Dieter Wallnöfer                       *
 *   Copyright (C) 2018 by Saverio Giallorenzo                             *
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

include "../AbstractTestUnit.iol"
include "console.iol"

include "private/WS-test/lib/WS-testService.iol"

define doTest
{
	loadLocalService;
	start@CalcServiceJoliePort( "http://localhost:14000/" )();

	for( i = 0, i < 50, i++ ) {
		req[ i ] << {
			x = 6
			y = 11
		}
	}

	spawn( x over #req ) in res {
		
		scope( call ) {
			install( default => throw( TestFailed, call.( call.default ) ) )
			sum@CalcServicePort( req[ x ] )( res )
			if ( res[ x ].return != 6+11 ) {
				throw( TestFailed, "Wrong response from the SOAP Service, session " + x + " expected 17, found " + res[ x ].return )
			}
		}
		
	}

	spawn( x over #req ) in res {
		prod@CalcServicePort( req[ x ] )( res )
		if ( res[ x ].return != 6*11 ) {
			throw( TestFailed, "Wrong response from the SOAP Service, session " + x + ", expected 66, found " + res[ x ].return )
		}
	}
	close@CalcServiceJoliePort()()
}
