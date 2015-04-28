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
package disease.Testing;

import com.blogspot.mydailyjava.guava.cache.jackbergus.CacheMap;
import com.blogspot.mydailyjava.guava.cache.overflow.FileSystemCacheBuilder;
import com.google.common.cache.Cache;
import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tweetsmining.model.graph.database.cache.CacheBuilder;

/**
 *
 * @author vasistas
 */
public class CacheTest {
    
    public static void main(String args[]) {
        
            CacheMap<String,String> c;
            c = CacheBuilder.createMultigraphCacheBuilder("test.map",(String t)->t);
            String j = c.get("ciao");
            System.out.println(j);
            c.put("ciao","bella");
            c.put("mia","elementa");
            c.put("foca","py");
            for (String x : c.keySet()) {
                System.out.println(x+"-"+c.get(x));
            }
            c.persist();
            
            // c.cleanUp();
            //c.invalidateAll();//store
            //m = c.asMap();
            //System.out.println(j);
    }
    
}
