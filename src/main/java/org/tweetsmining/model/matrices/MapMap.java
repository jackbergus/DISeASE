/*
 * Copyright (C) 2015 vasistas
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
package org.tweetsmining.model.matrices;

import com.blogspot.mydailyjava.guava.cache.jackbergus.CacheMap;
import disease.datatypes.ConcreteMapIterator;
import disease.utils.datatypes.Pair;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.tweetsmining.model.graph.database.cache.CacheBuilder;

/**
 * Use deprecated values only when the graph is not in main memory
 * @author vasistas
 */
public class MapMap implements Map<Long,Map<Long,Double>>, Iterable<Pair<Long,Long>>  {
    
    private CacheMap<Long,Map<Long,Double>> map;
    //private long size;
    
    public MapMap(String name) {
        map = CacheBuilder.createMultigraphCacheBuilder(name, (l)->Long.parseLong(l));
        //size = 0;
    }

    @Override @Deprecated
    public int size() {
        return (map.isEmpty() ? 0 : 1);
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override @Deprecated
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override @Deprecated
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Map<Long, Double> get(Object key) {
        return map.get(key);
    }
    
    public double get(Long row, Long col) {
        Map<Long,Double> m = map.get(row);
        if (m==null)
            return 0;
        else {
            Double val = m.get(col);
            return (val==null ? 0 : val);
        }
    }

    @Override
    public Map<Long, Double> put(Long key, Map<Long, Double> value) {
        return map.put(key, value);
    }

    public double put(Long row, Long col, Double val) {
        
        Double toret;
        if (get(row,col)==0) {
            Map<Long,Double> m = map.get(row);
            if (m==null) {
                m = new ConcurrentHashMap<>();//CacheBuilder.<Long,Double>createTmpCacheBuilder(string->Long.parseLong(string));
                m.put(col,val);
                map.put(row, m);
                return 0;
            } else {
                toret = m.put(col, val);
            }
        } 
        else
            toret = map.get(row).put(col, val);
        return (toret!=null ? toret : 0);
    }
    
    @Override
    public Map<Long, Double> remove(Object key) {
        Map<Long, Double> toret = map.remove(key);
        /*if (toret!=null) {
            size -= toret.size();
        }*/
        return toret;
    }
    
    public double remove(Long row, Long col) {
        Map<Long, Double> m = map.get(row);
        if (m==null)
            return 0;
        Double toret = map.get(row).remove(col);
        if (map.get(row).isEmpty()) //cleaning some memory
            map.remove(row);
        /*if (toret!=null)
            size--;*/
        return (toret==null ? 0 : toret);
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Map<Long, Double>> m) {
        for (Pair<? extends Long, ? extends Map<Long, Double>> x : new ConcreteMapIterator<>(m)) {
            long row = x.getFirst();
            //for (Long col : x.getSecond().keySet())
            x.getSecond().keySet().parallelStream().forEach((col) -> {
                put(row,col,x.getSecond().get(col));
            });
        }
    }
    
    public void putAll(MapMap m) {
        putAll((Map<? extends Long, ? extends Map<Long, Double>>)m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override @Deprecated
    public Set<Long> keySet() {
        return map.keySet();
    }
    
    @Override
    public Iterator<Pair<Long,Long>> iterator() {
        return new MultiKeyIteratorForCacheMap();
    }

    @Override @Deprecated
    public Collection<Map<Long, Double>> values() {
        return map.values();
    }
    
    public Collection<Double> valueValues() {
        List<Double> s = new LinkedList<>();
        values().parallelStream().forEach((x) -> {
            s.addAll(x.values());
        });
        return s;
    }

    @Override @Deprecated
    public Set<Entry<Long, Map<Long, Double>>> entrySet() {
        return map.entrySet();
    }
    
    public void save() {
        map.persist();
    }

    private class MultiKeyIteratorForCacheMap implements Iterator<Pair<Long, Long>> {
        
        private Iterator<Entry<Long, Map<Long, Double>>> it ;
        private Entry<Long, Map<Long, Double>> elem;
        private Iterator<Long> secondKeyIt;
        
        public MultiKeyIteratorForCacheMap() {
            it = map.getMapIterator();
            elem = null;
        }

        @Override
        public boolean hasNext() {
            //The second time, doesn't get into the EClipse code
            return it.hasNext();
        }

        @Override
        public Pair<Long, Long> next() {
            if (secondKeyIt==null)
                elem=null;
            else if (!secondKeyIt.hasNext())
                elem=null;
            //At the second time, it returns null since both elements are not satisfied o.O
            
                if (!hasNext())
                     if (elem==null)
                    return null;
            if (elem==null) {
                elem = it.next();
                secondKeyIt = elem.getValue().keySet().iterator();
            }
            return new Pair<>(elem.getKey(),secondKeyIt.next());
        }
    }
    
    
}
