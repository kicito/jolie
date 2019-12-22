/***************************************************************************
 *   Copyright (C) 2006-2015 by Fabrizio Montesi <famontesi@gmail.com>     *
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

package jolie.util;

import java.io.Serializable;

/**
 * A convenience class for expressing a pair of objects.
 * @author Fabrizio Montesi
 * @param <K> The type of the first element in the pair
 * @param <V> The type of the second element in the pair
 */
public final class Pair< K, V > implements Serializable
{
	public final static long serialVersionUID = jolie.lang.Constants.serialVersionUID();

	private final K key;
	private final V value;
	
	public Pair( K key, V value )
	{
		this.key = key;
		this.value = value;
	}
	
	public final K key()
	{
		return key;
	}
	
	public final V value()
	{
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj ) return true;
		if ( obj == null ) return false;
		if ( getClass() != obj.getClass() ) return false;
		Pair other = (Pair) obj;
		if ( key == null ) {
			if ( other.key != null ) return false;
		} else if ( !key.equals( other.key ) ) return false;
		if ( value == null ) {
			if ( other.value != null ) return false;
		} else if ( !value.equals( other.value ) ) return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "< " + this.key+", "+ this.value + " >";
	}
}
