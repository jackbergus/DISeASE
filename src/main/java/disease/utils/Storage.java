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
package disease.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 *
 * @author Giacomo Bergami
 */
public class Storage {
    
    
    /** Serializes the data structure into a .ser file
     * 
     * @param <T> Type of the data structure
     * @param elem  the data structure      
     * @param filename where to store the file
     */
    public static <T> void serialize(T elem, String filename) {
        try
           {
                  FileOutputStream fos = new FileOutputStream(filename);
                  ObjectOutputStream oos = new ObjectOutputStream(fos);
                  oos.writeObject(elem);
                  oos.close();
                  fos.close();
                  System.out.println(filename + " is serialized");
           }catch(IOException ioe)
            {
                  ioe.printStackTrace();
                  System.exit(1);
            }
    }
    
    /**
     * Given a file in a .ser format, returns the unserialized object
     * @param <T>       Expected type of the object
     * @param filename  Serialized file 
     * @return          Unserialized object
     */
    public static <T> T unserialize(String filename) {
        try
      {
         FileInputStream fis = new FileInputStream(filename);
         ObjectInputStream ois = new ObjectInputStream(fis);
         T object = (T) ois.readObject();
         ois.close();
         fis.close();
         return object;
      }catch(IOException ioe)
      {
         ioe.printStackTrace();
         return null;
      }catch(ClassNotFoundException c)
      {
         System.out.println("Class not found");
         c.printStackTrace();
         return null;
      }
    }
    
}
    

