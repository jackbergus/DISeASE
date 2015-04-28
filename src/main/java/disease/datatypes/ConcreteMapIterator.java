/*
 * Copyright (C) 2015 Giacomo Bergami
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
package disease.datatypes;

import disease.utils.datatypes.Pair;
import java.util.Iterator;
import java.util.Map;

/**
 * Implements an iterator through a map, without allocating all the elements in
 * the entry set.
 * @author Giacomo Bergami
 * @param <K>
 * @param <V>
 */
public class ConcreteMapIterator<K,V> implements MapIterator<K,V> {
    
    private Map<K,V> elem;
    private Iterator<K> it;
    public ConcreteMapIterator(Map<K,V> toiterate) {
        this.elem = toiterate;
        if (elem==null)
            this.it = null;
        else
            this.it = this.elem.keySet().iterator();
    }

    @Override
    public boolean hasNext() {
        if (it==null)
            return false;
        return it.hasNext();
    }

    @Override
    public Pair<K, V> next() {
        K e = it.next();
        return new Pair<>(e,elem.get(e));
    }

    @Override
    public Iterator<Pair<K, V>> iterator() {
        return this;
    }

}
