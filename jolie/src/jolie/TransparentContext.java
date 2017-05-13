/***************************************************************************
 *   Copyright (C) 2015 by Fabrizio Montesi <famontesi@gmail.com>          *
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

package jolie;

import java.util.Map;
import java.util.function.Consumer;
import jolie.behaviours.Behaviour;
import jolie.net.SessionMessage;
import jolie.runtime.InputOperation;

/**
 *
 * @author Fabrizio Montesi
 */
public abstract class TransparentContext extends StatefulContext
{
	public TransparentContext( Behaviour process, ExecutionContext parent )
	{
		super( process, parent );
	}

	@Override
	public jolie.State state()
	{
		return parent.state();
	}

	@Override
	public void requestMessage( InputOperation operation, ExecutionContext ctx, Consumer< SessionMessage > then )
	{
		parent.requestMessage( operation, ctx, then );
	}

	@Override
	public void requestMessage( Map< String, InputOperation> operations, ExecutionContext ctx, Consumer< SessionMessage > then )
	{
		parent.requestMessage( operations, ctx, then );
	}

	@Override
	public String getSessionId()
	{
		return parent.getSessionId();
	}
}