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

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.tweetsmining.model.graph.database.Relation;

/**
 *
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public class IteratorMapping<K> {
    
    public <T> Iterator<T> mapIterator(Function<K,T> fun, Iterator<K> it) {
        return new Iterator<T> (){
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }
            @Override
            public T next() {
                K tmp = it.next();
                return fun.apply(tmp);
            }
        };
    }
    
    /*public static <T> Iterable<T> iteratorToIterable(Iterator<T> sourceIterator) {
        return () -> sourceIterator;
    }*/
    
    /*public static <T> Stream<T> iteratorToStream(Iterator<T> it) {
        return StreamSupport.stream(IteratorMapping.<T>iteratorToIterable(it).spliterator(), false);
    }*/
    
}
