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
package disease.utils;

import com.blogspot.mydailyjava.guava.cache.jackbergus.CacheMap;
import disease.utils.datatypes.Pair;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.tweetsmining.model.matrices.MapMap;

/**
 *
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public class MapMapRefSet implements Set<Pair<Long,Long>> {

    private MapMap ref;
    private Iterator<Pair<Long,Long>> l;
    private boolean empty;
    
    public MapMapRefSet(MapMap ref) {
        this.ref = ref;
        this.l = ref.iterator();
        empty = l.hasNext();
    }

    @Override @Deprecated
    public int size() {
        return ref.size();
    }

    @Override
    public boolean isEmpty() {
        return empty;
    }

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Pair))
            return false;
        Pair<Long,Long> p = (Pair<Long,Long>)o;
        return (ref.get(p.getFirst()).equals(p.getSecond()));
    }

    @Override
    public Iterator<Pair<Long, Long>> iterator() {
        return l;
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean add(Pair<Long, Long> e) {
        return ref.put(e.getFirst(), e.getSecond(), 1.0)!=1.0;
    }

    @Override
    public boolean remove(Object o) {
        if (!(o instanceof Pair))
            return false;
        Pair<Long,Long> p = (Pair<Long,Long>)o;
        return ref.remove(p.getFirst(), p.getSecond())!=0.0;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    

    @Override
    public boolean addAll(Collection<? extends Pair<Long, Long>> c) {
        boolean toret = true;
        for (Pair<Long, Long> x:c) {
            toret = toret & add(x);
        }
        return toret;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean toret = true;
        for (Object x:c) {
            if (x instanceof Pair)
                toret = toret & remove((Pair<Long,Long>)x);
            else 
                toret = false;
        }
        return toret;
    }
    

    @Override
    public void clear() {
        ref.clear();
    }
    
    

    
}
