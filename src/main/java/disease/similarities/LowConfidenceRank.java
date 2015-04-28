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
package disease.similarities;

import java.util.ArrayList;

/**
 *
 * @author vasistas
 */
public class LowConfidenceRank extends Similarity {

    private static LowConfidenceRank self;
    LowConfidenceRank() { }
    public static LowConfidenceRank getInstance() {
        if (self==null)
            self = new LowConfidenceRank();
        return self;
    }
        
    //Credits to http://www.catalysoft.com/articles/StrikeAMatch.html
    private String[] compareString_letterPairs(String str) {
        int numPairs = str.length()-1;
        String []pairs = new String[numPairs];
        for (int i=0; i<numPairs; i++)
            pairs[i] = str.substring(i,i+2);
        return pairs;
    }
    
    //Credits to http://www.catalysoft.com/articles/StrikeAMatch.html
    private ArrayList<String> compareString_wordLetterPairs(String str) {
        ArrayList<String> allPairs = new ArrayList<>();
        String[] words = str.split("\\s");
        for (String w : words) {
            String[] pairsInWord = compareString_letterPairs(w);
            for(int p=0; p<pairsInWord.length; p++) 
                allPairs.add(pairsInWord[p]);
        }
        return allPairs;
    }
    
    /**
     * Scores the 
     * @param str1
     * @param str2
     * @return 
     * @author http://www.catalysoft.com/articles/StrikeAMatch.html
     */
    @Override
    public double sim(String str1, String str2) {
        if (str1.length()==0 && str2.length()==0)
            return 1;
        else if ((str1.length()==0 || str2.length()==0))
            return 0;
        ArrayList<String> pairs1 = compareString_wordLetterPairs(str1.toLowerCase());
        ArrayList<String> pairs2 = compareString_wordLetterPairs(str2.toLowerCase());
        int intersection = 0;
        int union = pairs1.size() + pairs2.size();
        for (String pair1 : pairs1) {
            String toremove = null;
            for (String pair2 : pairs2) {
                if (pair1.equals(pair2)) {
                    intersection++;
                    toremove = pair2;
                    break;
                }
            }
            if (toremove!=null)
                pairs2.remove(toremove);
        }
        return (2.0*intersection)/union;
    }
    
}
