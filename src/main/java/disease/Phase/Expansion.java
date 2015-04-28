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

import disease.Dataset.MedicalDictionary;
import disease.Dataset.OnlineMedicalDictionary;
import disease.Dataset.Real.AbbreviationDictionary;
import disease.Dataset.interfaces.FileDictionary;
import disease.Phase.cleaner.CleanItalian;

/**
 *
 * @author vasistas
 */
public class Expansion {
    
    private static FileDictionary di = OnlineMedicalDictionary.stemmedDictionary();
    private static AbbreviationDictionary ad = AbbreviationDictionary.getInstance();
    
    public static String expand_cleaned_text(String toexpand) {
      
        
        String expanded[];
        {
            String toexpand_splitted[] = toexpand.split(" ");
            String tmp[] =  new String[toexpand_splitted.length];

            for (int i=0; i<toexpand_splitted.length; i++) {
                String x = toexpand_splitted[i];
                String expansion = ad.getValue(x);
                if (expansion!=null)
                    tmp[i] = expansion;
                else
                    tmp[i] = x;
            }
            StringBuilder builder = new StringBuilder();
            for(String s : tmp) {
                builder.append(s).append(" ");
            }
            expanded = CleanItalian.getInstance().cleanedString(builder.toString()).split(" ");
        }
        
        //Dictionary expansion
        StringBuilder sw = new StringBuilder();
        for (int i=0; i<expanded.length; i++) {
            String toappend = di.getValue(expanded[i]);
            if (toappend==null)
                sw.append(expanded[i]);
            else
                sw.append(toappend);
            if (i<expanded.length-1) 
                sw.append(" ");
        }
        
        return sw.toString();
    }
    
}
