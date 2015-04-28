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

import disease.Phase.Annotator;
import disease.utils.Correction;
import disease.datatypes.MapIterator;
import disease.utils.datatypes.Pair;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author vasistas
 */
public class ScoreToRank /*extends Similarity ?? */ {
    
    private Similarity sim;
    public ScoreToRank(Similarity casted) {
        this.sim = casted;
    }
    
    public double rankWithVectorQuery(String document, String query[]) {
        double score = 0;
        
        //Scoring the word presence
        String tmp = document.toLowerCase();
        for (String t : query)
            score += this.sim.sim(tmp, t);
        score = score / query.length;
        
        return score;
    }
    
    /**
     * Returns the string similarity score using an argument-passed string similarity function
     * @param document
     * @param query
     * @param scoringf
     * @return 
     */
    public static double similarityRank(String document, String query[], Similarity scoringf) {
        double score = 0;
        
        //Scoring the word presence
        String tmp = document.toLowerCase();
        for (String t : query)
            score += scoringf.sim(tmp, t);
        score = score / query.length;
        
        return score;
    }
    
   
    /**
     * Returns the string similarity score using the equality as a strict binary 
     * similarity function.
     * @param document
     * @param query
     * @return 
     */
    public static double plainRank(String document, String query[]) {
        return similarityRank(document,query,Equality.getInstance());
    }

    /**
     * Applies the candidate generation to the key of a map
     * @param <T>           Key over which perform the evaluation
     * @param <K>           Value to report back
     * @param document      Document that serves as a query over the corpus
     * @param entry         Entry for the iteration
     * @param simobject     MultiWord similarity function
     * @param th            Candidate Selection Thereshold
     * @return 
     */
    public static <T,K> List<Correction<K>> candidateGeneration(Annotator document, MapIterator<T,K> entry, MultiWordSimilarity simobject, double th) {
        String cleaned = document.returnCleanedDocument();
        List<Correction<K>> mp = new LinkedList<>();
        for (Pair<T, K> x:entry) {
            T key = x.getFirst();
            K value = x.getSecond();
            double score =simobject.sim(cleaned, key.toString());
            if (score>=th) {
                mp.add(new Correction<>(value,score));
            }
        }
        return mp;
    }
    
    /**
     * Applies the candidate generation to the value of a map
     * @param <T>
     * @param <K>
     * @param document          
     * @param entry             Dataset entry over wich perform the score
     * @param simobject         Function through wich perform the term similarity
     * @param th                Thereshold
     * @param record_as_query   If it is true, then the record is used as a query and the entry as a document. Otherwise, viceversa
     * @return 
     */
    public static <T,K> List<Correction<T>> inverseCandidateGeneration(Annotator document, MapIterator<T,K> entry, MultiWordSimilarity simobject, double th, boolean record_as_query) {
        String cleaned = document.returnCleanedDocument();
        List<Correction<T>> mp = new LinkedList<>();
        for (Pair<T, K> x:entry) {
            T key = x.getFirst();
            K value = x.getSecond();
            double score;
            if (record_as_query)
                score =simobject.sim(cleaned, value.toString());
            else
                score = simobject.sim(value.toString(),cleaned);
            if (score>=th) {
                mp.add(new Correction<>(key,score));
            }
        }
        return mp;
    }


    
}
