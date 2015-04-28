/*
 * Copyright (C) 2015 Giacomo Bergami giacomo@openmailbox.org
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
package disease.Dataset;

/**
 *
 * @author Giacomo Bergami giacomo@openmailbox.org
 */
@Deprecated
public class MedicalDictionary /*implements Dictionary<String,String>*/ {
    
    /*
    private static MedicalDictionary self = null;
    private WebPageDictionary wpd;
    private static String path = "data"+File.separator+"italia_med_dictionary.ser";
    private static String getURL(String lang, String alphabet) {
        return "http://users.ugent.be/~rvdstich/eugloss/"+lang+"/lijst"+alphabet+".html";
    }
    
    public MedicalDictionary() {
    }
    
    public static MedicalDictionary getInstance() {
        if (self==null)
            self = new MedicalDictionary();
        return self;
    }
    
    private void do_init() {
        String alphabet[] = {"b","c","d","e","f","g","h","i","l","m","n","o","p","q","r","s","t","u","v","x","z"};
        wpd = new WebPageDictionary(getURL("IT","a"));
        for (String letter : alphabet) {
            wpd.merge(new WebPageDictionary(getURL("IT",letter)));
        }
        wpd.save_serialized(path);
    }
    
    public static void init() {
        getInstance().do_init();
    }
    
    private void do_load() {
        wpd = new WebPageDictionary();
        wpd.load_serialized(path);
    }
    
    public static void load() {
        getInstance().do_load();
    }

    /** 
     * Eventually expands the medical term to its specification or detailment
     * @param key
     * @return 
    // 
    @Override
    public String getValue(String key) {
        return wpd.getExpandedSense(key+" "); //normalizing stemmed words
    }

    /** 
     * Given a full value, the (useless) query retrieves the exact key matching
     * @param value
     * @return 
    //
    @Override
    public String getKeyByExactValueMatch(String value) {
        for (String x : wpd.getMainTerms()) {
            if (wpd.getExpandedSense(x).equals(value))
                return x;
        }
        return null;
    }

    /**
     * Uses the dictionary as a list of terms
     * @return 
    //
    public WordList asWordList() {
        return new SetDictionary(wpd.getMainTerms(),DictionaryType.ITALIAN_MEDICAL_DICTIONARY);
    }

    /**
     * 
     * @param query The long-description (or single word) to benchmark against the value
     *              or the key itself. In the first case a MultiWordSimilarity is used.
     * @param single_word_similarity Metric for single_word similarity
     * @return 
    //
    @Override
    public Map<String,Double> getKeyBySimilarityMatching(String query, Similarity single_word_similarity, double precision) {
        Map<String,Double> m = new TreeMap(Collections.reverseOrder());
        MultiWordSimilarity mws = new MultiWordSimilarity(single_word_similarity);
        wpd.getMainTerms().stream().forEach((key) -> {
            String value = wpd.getExpandedSense(key);
            double score = Math.max(mws.sim(query, value), mws.sim(query, key));
            if (score>precision) {
                m.put( key,score );
            }
        });
        return m;
    }
    
    
    */
    
}
