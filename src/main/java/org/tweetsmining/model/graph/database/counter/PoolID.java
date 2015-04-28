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
package org.tweetsmining.model.graph.database.counter;

import com.blogspot.mydailyjava.guava.cache.jackbergus.CacheMap;
import java.util.concurrent.Semaphore;
import org.tweetsmining.model.graph.database.cache.CacheBuilder;

/**
 * The class generates unique IDs for different database indices
 * @author vasistas
 */
public class PoolID {

    
    
    private static Counter c = Counter.getCounter();
    private static Semaphore sharedSem = new Semaphore(1);
    
    private Semaphore sem;
    private boolean dogame;

    private PoolID(Semaphore sharedSem) {
        this.sem = sharedSem;
        this.dogame = true;
    }
    public static PoolID challengeId() {
        try {
            sharedSem.acquire();
            return new PoolID(sharedSem);
        } catch (InterruptedException ex) {
            return null;
        }
    }
    /**
     * Returns the challenged value (if the challenge is running)
     * @return 
     */
    public long get() {
        if (this.dogame)
            return c.get();
        else
            throw new RuntimeException("Game has been already played.");
    }
    /**
     * Notifies that the Id has already been assigned. Increases the value
     * @return 
     */
    public boolean retain() {
        if (!this.dogame)
            return false;
        this.dogame = false;
        c.incr();
        sharedSem.release();
        return true;
    }
    /**
     * Resigns the challenge
     */
    public void discard() {
        if (!this.dogame)
           return;
        dogame = false;
        sharedSem.release();
    }
    public static void reset() {
        c.reset();
    }
    
}
