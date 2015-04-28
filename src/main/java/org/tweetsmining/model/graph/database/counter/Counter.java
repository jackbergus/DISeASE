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
package org.tweetsmining.model.graph.database.counter;

import com.blogspot.mydailyjava.guava.cache.jackbergus.CacheMap;
import org.tweetsmining.model.graph.database.cache.CacheBuilder;

/**
 *
 * @author Giacomo Bergami
 */
    
    public class Counter {
        private CacheMap<String, Long> max = CacheBuilder.createPersistentCounter();
        private static final String ID = "COUNTER";
        private static Counter self = null;
        private Counter() {
            long val = 0;
            if (max.size()==0)
                max.put(ID, val);
        }
        public static Counter getCounter() {
            if (self==null)
                self= new Counter();
            return self;
        }
        public long get() {
            return max.get(ID);
        }
        public void incr() {
            max.put(ID, max.get(ID)+1);
        }
        public void reset() {
            max.cleanUp();
            max.clear();
        }
    }