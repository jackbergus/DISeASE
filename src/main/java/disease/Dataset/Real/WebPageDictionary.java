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
package disease.Dataset.Real;

import disease.Phase.cleaner.CleanItalian;
import disease.utils.Storage;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author vasistas
 */
public class WebPageDictionary {
    
    private Map<String,String> dictionary;
    private CleanItalian ci = CleanItalian.getInstance();
    
    public WebPageDictionary(String url,boolean stemmed) {
        this.dictionary = new TreeMap<>(Collections.reverseOrder());
        try {
            Document document = Jsoup.connect(url).get();
            Elements collection = document.body().select("li");
            for (Element li : collection) {
                String main_term = li.select("b").get(0).text();
                
                String derived_term;
                try {
                    derived_term = li.select("i").get(0).text();
                } catch (Throwable t) {
                    derived_term = main_term;
                }
                //System.out.println(main_term + " - " + derived_term);
                if (stemmed) {
                    main_term = ci.cleanedString(main_term);
                    derived_term = ci.cleanedString(derived_term);
                }
                this.dictionary.put(main_term, derived_term);
            }
        } catch (IOException ex) {
            Logger.getLogger(WebPageDictionary.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public WebPageDictionary() {
        
    }
    
    public Set<String> getMainTerms() {
        return this.dictionary.keySet();
    }
    
    public String getExpandedSense(String key) {
        if (this.dictionary.containsKey(key))
            return this.dictionary.get(key);
        else
            return null;
    }
    
    public void merge(WebPageDictionary second) {
        for (String s : second.getMainTerms()) {
            dictionary.put(s, second.getExpandedSense(s));
        }
    }
    
    public void save_serialized(String path) {
        Storage.<Map<String,String>>serialize(dictionary, path);
    }
    
    public void load_serialized(String path) {
        this.dictionary = Storage.<Map<String,String>>unserialize(path);
    }
    
}
