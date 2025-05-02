/*
 * Copyright (C) 2025 Kasper Okumu <kaspokumu@gmail.com>
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

package jolie.lang.parse.util;

import java.util.ArrayDeque;
import java.util.Deque;


/**
 * Fifo structure that discards the oldest element on insertion if it is full.
 */
public class SaveLastN< T > {
	private final int capacity;
	private final Deque< T > deque;

	public SaveLastN( int capacity ) {
		this.capacity = capacity;
		this.deque = new ArrayDeque< T >( capacity );
	}

	/**
	 * Saves the element, if the SaveLastN is at capacity, the oldest element is removed.
	 *
	 * @param element
	 */
	public void put( T element ) {
		if( full() )
			deque.removeFirst();
		deque.addLast( element );
	}

	/**
	 * Returns the oldest element.
	 *
	 * @return The oldest element
	 */
	public T get() {
		return deque.getFirst();
	}

	/**
	 * Returns whether the SaveLastN is at capacity.
	 *
	 * @return true if this SaveLastN is at capacity, false if it is not at capacity.
	 */
	public boolean full() {
		return deque.size() >= capacity;
	}

	@Override
	public String toString() {
		return deque.toString();
	}
}
