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
package disease.Dataset.interfaces;

import disease.similarities.Similarity;
import disease.datatypes.MapIterator;
import disease.utils.datatypes.Pair;
import java.util.Map;

/**
 *
 * @author vasistas
 */
public interface Dictionary<K,V> extends Iterable<Pair<K,V>> {
    
    public V getValue(K key);
    public K getKeyByExactValueMatch(V value);
    public Map<K,Double> getKeyBySimilarityMatching(V value, Similarity s, double precision);
    public void merge(MapIterator<K,V> d);
    
    
}
