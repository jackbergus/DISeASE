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

import disease.Dataset.interfaces.WordList;
import disease.Phase.cleaner.CleanItalian;
import disease.Phase.cleaner.Cleanser;
import disease.utils.Correction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class provides a non-simmetric similarity, that could be used both
 * for text ranking and term corrections
 * @author vasistas
 */
public class MultiWordSimilarity extends Similarity {

    private Cleanser default_cleanser;
    private Similarity base_similarity;
    
    public MultiWordSimilarity(Cleanser language_based_cleanser, Similarity single_word_similarity) {
        this.default_cleanser = language_based_cleanser;
        this.base_similarity = single_word_similarity;
    }
    
    public MultiWordSimilarity(Similarity single_word_similarity) {
        this(CleanItalian.getInstance(),single_word_similarity);
    }
    
    /**
     * Calculates a MultiWordSimilarity by using Levenshtein for targeting
     * precisely long words and LowConfidenceRank for strengthen the low results
     */
    private MultiWordSimilarity() {
        this(new Similarity() {
                
                //private Similarity l = new MyMongeElkan();
                private Similarity lcr = new  LowConfidenceRank();
                
                @Override
                public double sim(String word1, String word2) {
                    return ((lcr.sim(word1, word2)));
                }
            });
    }
    
    private static MultiWordSimilarity self = null;
    public static MultiWordSimilarity getInstance() {
        if (self==null)
            self = new MultiWordSimilarity();
        return self;
    }
    
    /**
     * Given the data element and a query, returns how the data is similar to the query
     * 
     * score[i] = max_j{sim(q[i],d[1]),...,sim(q[i],d[n])}
     * 
     * @param query
     * @param data
     * @return 
     */
    @Override
    public double sim(String query, String data) {
        String query_vec[] = this.default_cleanser.cleanedStringAsArray(query);
        String data_vec[] = this.default_cleanser.cleanedStringAsArray(data);
        double score = 0;
        int discarded = 0;
        for (String q : query_vec) {
            double d = 0;
            for (String t : data_vec) {
                double s = this.base_similarity.sim(q, t);
                if (s>d)
                    d = s;
            }
            score += d;
        }
        return (score / (((double)(query_vec.length-discarded)) * 1.0));
    }
    
    /**
     * 
     * @param dictionary        Dictionary for word matching
     * @param data              Single word to correct by similarity
     * @param tsim               Single word similarity function
     * @return 
     */
    public Correction<String> correctWord(WordList dictionary, String data, Similarity tsim) {
        double score = 0;
        String correction = data;
        for (String q : dictionary) {
                if (q.length()==0)
                    continue;
                q = q.substring(0,q.length()-1);
                double s = tsim.sim(q, data);
                //if (q.startsWith("fari"))
                //    System.err.println(q);
                if (s>score && s>=0.45) {
                    score = s;
                    correction = q;
                }
        }
        return new Correction<>(correction,score);
    }
    
    public static void main(String []args) {
        String q = CleanItalian.getInstance().cleanedString("viva la pappa con il pomodoro");
        String s2 =  CleanItalian.getInstance().cleanedString("viva la pizza napoletana doc");
        String s3 =  CleanItalian.getInstance().cleanedString("viva la pappa pomodorata");
        String s4 =  CleanItalian.getInstance().cleanedString("viva la pappina con il");
        
        
        
            MultiWordSimilarity mws = new MultiWordSimilarity(new Similarity() {
                
                private Similarity l = new Levenshtein();
                private Similarity lcr = new  LowConfidenceRank();
                
                @Override
                public double sim(String word1, String word2) {
                    return ((l.sim(word1, word2)*0.8)+(lcr.sim(word1, word2)*0.2));
                }
            });
            System.out.println(mws.sim(q, s2));
            System.out.println(mws.sim(q, s3));
            System.out.println(mws.sim(q, s4));
            System.out.println("--------------");
        
        
    }
    
    
    private Map<String,Correction> singleWordCorrection(String query_vec[], String data_vec[]) {
        Map<String,Correction> toret = new HashMap<>();
        for (String t : data_vec) {
            if (!toret.containsKey(t)) {
                double d = 0;
                String corrected = "";
                for (String q : query_vec) {
                    double s = this.base_similarity.sim(t, q);
                    if (s>d) {
                        d = s;
                        corrected = q;
                    }
                }
                toret.put(t, new Correction(corrected,d));
            }
        }
        return toret;
    }
    
    /**
     * Given a query contained the words to benchmark over a data strings, it
     * returns a dictionary of the possible corrections. NOTE THAT this technique
     * should be used only for term-by-term correction, and not for multi-term
     * corrections; for this other purpose, use the sim function
     * 
     * @param query     The query the words that we would like to benchmark in the data
     * @param data      The data over which perform the correction
     * @return A map of stemmed terms (both query and correction) where the
     *         correction is scored
     */
    public Map<String,Correction> singleWordCorrection(String query, String data) {
        String query_vec[] = this.default_cleanser.cleanedStringAsArray(query);
        String data_vec[] = this.default_cleanser.cleanedStringAsArray(data);
        return this.singleWordCorrection(query_vec, data_vec);
    }
    
    
    public Map<String,Correction> multiWordCorrection(Collection<String> query, String data) {
        Set<String> elems = new HashSet<>();
        query.stream().forEach((quera) -> {
            elems.addAll(this.default_cleanser.cleanedStringList(quera));
        });
        return (this.singleWordCorrection(elems.toArray(new String[]{}), this.default_cleanser.cleanedStringAsArray(data)));
    }
     
    
}
