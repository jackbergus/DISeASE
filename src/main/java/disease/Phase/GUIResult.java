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
package disease.Phase;

import disease.ontologies.ICD9CMCode;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author vasistas
 */
public class GUIResult {
   
    //Note: provide to the final gui only 3 results, the top-3 ones
    private TreeMap<Double,Set<String>> results;
    private String query;
    
    public GUIResult(String query) {
        this.query = query;
        this.results = new TreeMap(Collections.reverseOrder());
    }
    
    public void appendResult(Double score, String code) {
        if (!this.results.containsKey(score)) {
            this.results.put(score, new HashSet<>());
        }
        this.results.get(score).add(code);
    }
    
    public Map<Double,Set<String>> getRankedResults() {
        return this.results;
    }
    
    public String getOriginalQuery() {
        return this.query;
    }
    
    /*
    private static void main(String[] params) {
        //GUIResult p = new GUIResult(params[0]);
        for (Entry<Double,String> entry : p.getRankedResults().entrySet()) {
            
        }
    }
    */
    
}
