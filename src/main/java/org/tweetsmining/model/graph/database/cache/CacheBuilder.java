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
package org.tweetsmining.model.graph.database.cache;

import com.blogspot.mydailyjava.guava.cache.jackbergus.CacheMap;
import com.blogspot.mydailyjava.guava.cache.overflow.FileSystemCacheBuilder;
import com.blogspot.mydailyjava.guava.cache.overflow.FileSystemPersistingCache;
import com.google.common.primitives.UnsignedLongs;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vasistas
 */
public class CacheBuilder {
    private final static int TMP_CACHE_SIZE = 10;
    private final static int DATABSE_CACHE_SIZE = 200;
    
    public static String getnerateTmpFileName() {
        return new StringBuilder()
                    .append("tmpCache")
                    .append(UnsignedLongs.toString(new Date().getTime()))
                    .append(UUID.randomUUID())
                    .toString();
        
    }
    
    public static <K,V> CacheMap<K,V> createTmpCacheBuilder(Function<String,K> conv) {
        try {
            
            //File f = File.createTempFile(getnerateTmpFileName(), "");
            File f= java.nio.file.Files.createTempDirectory(getnerateTmpFileName()).toFile();
            f.deleteOnExit();
            return new CacheMap<>((FileSystemPersistingCache<K, V>) FileSystemCacheBuilder.<K,V>newBuilder().persistenceDirectory(f).maximumSize(DATABSE_CACHE_SIZE).softValues().build(),conv);
        } catch (IOException ex) {
            Logger.getLogger(CacheBuilder.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static CacheMap<String,Long> createPersistentCounter() {
        File f= new File("db_id_counter");
        return new CacheMap<>((FileSystemPersistingCache<String, Long>) FileSystemCacheBuilder.<String, Long>newBuilder().persistenceDirectory(f).maximumSize(0).softValues().<String,Long>build(), (String t) -> t);
    }
    
    public static <K,V> CacheMap<K,V> createMultigraphCacheBuilder(String databaseName,Function<String,K> conv) {
        File f = new File(databaseName);
        int runtimeValue = (f.exists() ? 13787 : TMP_CACHE_SIZE);
        return new CacheMap<>((FileSystemPersistingCache<K, V>) FileSystemCacheBuilder.<K,V>newBuilder().persistenceDirectory(f).maximumSize(runtimeValue).softValues().build(),conv);
    }
    
}
