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
package it.jackbergus.pickiterator;

import java.util.Optional;

/**
 *
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public class PickIteratorConcatenation<K> implements IPickIterator<K> {

    private IPickIterator<K> current;
    private IPickIterator<K> next;
    
    public PickIteratorConcatenation(IPickIterator<K> first, IPickIterator<K> second) {
        this.current = first;
        this.next = second;
        if (this.current==null)
            this.current = this.next;
    }
    
    @Override
    public boolean hasNext() {
        if (current==null || !current.hasNext()) {
            if (next ==null || !next.hasNext())
                return false;
            else {
                current = next;
                next = null;
            }
        } 
        return (current!=null && current.hasNext());
    }

    @Override
    public Optional<K> next() {
        if (hasNext())
            return current.next();
        else
            return Optional.empty();
    }

    @Override
    public Optional<K> pick() {
        if (!current.pick().isPresent())
            current = next;
        return current.pick();
    }
    
}
