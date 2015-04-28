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
package disease.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author vasistas
 */
public class AbbreviationExpansion {
    
    //private Map<String,Correction> learnedMap;
    
    private AbbreviationExpansion() {
        //learnedMap = new HashMap<>();
    }
    
    private static AbbreviationExpansion self = null;
    public static AbbreviationExpansion getInstance() {
        if (self==null) 
            self = new AbbreviationExpansion();
        return self;
    }
    
    public void putCorrection(String wrong, String cleaned, double score) {
        //learnedMap.put(wrong,new Correction(cleaned,score));
    }
    
    public Set<Correction> get(String wrong) {
        //return learnedMap.get(wrong);
        return null;
    }

    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
