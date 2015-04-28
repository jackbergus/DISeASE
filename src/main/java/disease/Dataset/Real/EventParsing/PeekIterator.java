/*
 * Copyright (C) 2015 Giacomo Bergami <giacomo@openmailbox.org>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package disease.Dataset.Real.EventParsing;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates over a collection while permitting to peek the result, without 
 * incrementing the next counter. 
 * 
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public class PeekIterator<T> implements Iterator<T> {

    private final Iterator<T> iterator;
    private T nextitem;
    private boolean hasCurrentElemStored; //detects if nextitem represents the current item

    public PeekIterator(Iterator<T> iterator) {
        this.iterator = iterator;
        this.nextitem = null;
        this.hasCurrentElemStored = false;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
    
    public T peek() {
        if (!this.hasCurrentElemStored) {
            if (!hasNext()) {
                throw (new NoSuchElementException("Iterator has no elements left."));
            }
            this.nextitem = this.iterator.next();
            this.hasCurrentElemStored = true;
        }

        return nextitem;
    }
    
    public void discard() {
        nextitem = null;
        hasCurrentElemStored = false;
    }
    
    @Override
    public T next() {
        T toReturn = peek();
        discard();
        return toReturn;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}