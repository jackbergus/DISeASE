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

import disease.Dataset.DataIntegration.SmallOntology;
import disease.Dataset.MedicalDictionary;
import disease.Dataset.OnlineMedicalDictionary;
import disease.Dataset.QuickAssoc;
import disease.Dataset.Real.ICD9CMTable;
import disease.Dataset.WikipediaIT;
import disease.Dataset.interfaces.WordList;
import disease.datatypes.serializabletree.Tree;
import disease.similarities.LowConfidenceRank;
import disease.similarities.MultiWordSimilarity;
import disease.similarities.Similarity;
import disease.utils.DictionaryType;
import disease.utils.ToObservation;
import disease.utils.wikipedia.WikipediaSingleton;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import manhoutbook.LogisticRegression;
import manhoutbook.Observation;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;

/**
 *
 * @author vasistas
 */
public class CandidateGeneration {
    
    private static CandidateGeneration self = null;
    private SmallOntology dist = SmallOntology.getInstance();
    
    private WordList it_medical_dict;
    private Tree<String,Double>   t;
    private QuickAssoc qa;
    private WikipediaIT wiki;
    //private SenseNet   sn;
    private OnlineLogisticRegression lr;
    private MultiWordSimilarity sim = MultiWordSimilarity.getInstance();
    
    private CandidateGeneration() {
        this.it_medical_dict = OnlineMedicalDictionary.stemmedDictionary().asWordList(DictionaryType.WHOLE_WORDS);//TODO: aggiungere parole mediche
        this.qa = QuickAssoc.getInstance();
        
        //this.t = ICD9Tree.getInstance();
        //this.sn = SenseNet.getInstance();
        this.lr = null;
    }
    
    public static CandidateGeneration getInstance() {
        if (self==null) {
            self = new CandidateGeneration();
        }
        return self;
    }
    
    public void dataMatching(Annotator er) {
        
    }
    
    private ICD9CMTable it = ICD9CMTable.init();
    private WikipediaSingleton ws = WikipediaSingleton.getInstance();
    
    
    /**
     * Generating the candidates for a Medical Record's Annotator. This provides
     * the highest rankings when there is a perfect match between references,
     * otherwise poor results are provided.
     * @param expected  Expected ICD9CM code
     * @param er 
     * @return  
     */
    public Map<Double, List<String>> candidateGenerationWithModel(Annotator er) {
        Map<String,Double> map = new HashMap<>();
        {
            //Phase 1
            it.candidateGenerationForICD9CMTaxonomy(er, sim, 0.4).stream().forEach((c) -> {
                map.put(c.getCorrectedWord().substring(0, 3), c.getScore());
            });
            it.candidateGenerationFromCodeSpecifications(er, sim, 0.4).stream().forEach((c) -> {
                String code =c.getCorrectedWord().substring(0, 3);
                Double score = map.get(code);
                if (score != null)
                    score = Math.max(score, c.getScore());
                else 
                    score = c.getScore();
                map.put(code,score);
                
            });
            it.candidateGenerationFromCodeSpecificationsWithTitle(er, sim, 0.4).stream().forEach((c) -> {
                String code =c.getCorrectedWord().substring(0, 3);
                Double score = map.get(code);
                if (score != null)
                    score = Math.max(score, c.getScore());
                else 
                    score = c.getScore();
                map.put(code,score);
            });
            
            ws.candidateGenerationForWikiTitle(er, sim, 0.4).stream().forEach((c) -> {
                c.getCorrectedWord().stream().map((xx) -> {
                    return xx.substring(0,3);
                }).forEach((xx) -> {
                    Double score = map.get(xx);
                    if (score != null)
                        score = Math.max(score, c.getScore());
                    else 
                        score = c.getScore();
                    map.put(xx,score);
                    double candidate_generation_score  = c.getScore();
                //String candidate = c.getCorrectedWord();
                });
                
            });
            
            ws.candidateGenerationForWikiContent(er, sim, 0.4).stream().filter((t1) -> {
                if (t1.getCorrectedWord().isEmpty()) {
                    return false;
                } else
                    return true;
            }).forEach((c) -> {
                c.getCorrectedWord().stream().map((xx) -> {
                    return xx.getThreeDigitsFather().toString();
                }).forEach((xx) -> {
                    Double score = map.get(xx);
                    if (score != null)
                        score = Math.max(score, c.getScore());
                    else 
                        score = c.getScore();
                    map.put(xx,score);
                });
               
                });
            
        }
        /*
        double imprecision = er.approximateExpansion(it_medical_dict,LowConfidenceRank.getInstance());//osim oonly
        //List<Observation> los = new LinkedList<>();
        //String father = expected.substring(0,3);
        {
            it.candidateGenerationForICD9CMExpandedTaxonomy(er, sim, 0.4).stream().forEach((c) -> {
                map.put(c.getCorrectedWord().substring(0, 3), c.getScore());
            });
            it.candidateGenerationFromCodeExpandedSpecifications(er, sim, 0.4).stream().forEach((c) -> {
                String code =c.getCorrectedWord().substring(0, 3);
                Double score = map.get(code);
                if (score != null)
                    score = Math.max(score, c.getScore());
                else 
                    score = c.getScore();
                map.put(code,score);
            });
            it.candidateGenerationFromCodeSpecificationsWithTitleBothExpanded(er, sim, 0.4).stream().forEach((c) -> {
                String code =c.getCorrectedWord().substring(0, 3);
                Double score = map.get(code);
                if (score != null)
                    score = Math.max(score, c.getScore());
                else 
                    score = c.getScore();
                map.put(code,score);
            });
            
            ws.candidateGenerationForExpandedWikiTitle(er, sim, 0.4).stream().forEach((c) -> {
                 c.getCorrectedWord().stream().forEach((xx) -> {
                     xx = xx.substring(0,3);
                    Double score = map.get(xx);
                    if (score != null)
                        score = Math.max(score, c.getScore());
                    else 
                        score = c.getScore();
                    map.put(xx,score);
                });
                
            });
            
            ws.candidateGenerationForExpandedWikiContent(er, sim, 0.4).stream().forEach((c) -> {
                 c.getCorrectedWord().stream().forEach((xx) -> {
                     xx = xx.getThreeDigitsFather();
                    Double score = map.get(xx.toString());
                    if (score != null)
                        score = Math.max(score, c.getScore());
                    else 
                        score = c.getScore();
                    map.put(xx.toString(),score);
                });
            });

            
        }*/
        //Sorting
        Map<Double,List<String>> sorting = new TreeMap<>(Collections.reverseOrder());
        for (String x:map.keySet()) {
            Double val = map.get(x);
            if (!sorting.containsKey(val))
                sorting.put(val, new LinkedList<>());
            sorting.get(val).add(x);
        }
        return sorting;
    }
    
    /**
     * Generating the candidates for a Medical Record's Annotator. This provides
     * the highest rankings when there is a perfect match between references,
     * otherwise poor results are provided.
     * @param expected  Expected ICD9CM code
     * @param er 
     * @return  
     */
    public List<Observation> candidateGenerationWithExactMatchInformation(String expected, Annotator er, LogisticRegression lr) {
        
        List<Observation> los = new LinkedList<>();
        String father = expected.substring(0,3);
        {
            //Phase 1
            it.candidateGenerationForICD9CMTaxonomy(er, sim, 0.4).stream().forEach((c) -> {
                double candidate_generation_score  = c.getScore();
                String candidate = c.getCorrectedWord();
                int phase = 1;
                double sem = dist.getWeight(candidate, expected);
                int equals = candidate.substring(0, 3).equals(father) ? 1 : 0;
                los.add(new ToObservation(candidate_generation_score,phase,sem,equals).createObservation());
                //map.put(c.getCorrectedWord().substring(0, 3), c.getScore());
            });
            it.candidateGenerationFromCodeSpecifications(er, sim, 0.4).stream().forEach((c) -> {
                /*String code =c.getCorrectedWord().substring(0, 3);
                Double score = map.get(code);
                if (score != null)
                    score = Math.max(score, c.getScore());
                else 
                    score = c.getScore();
                map.put(code,score);*/
                double candidate_generation_score  = c.getScore();
                String candidate = c.getCorrectedWord();
                int phase = 2;
                double sem = dist.getWeight(candidate, expected);
                int equals = candidate.substring(0, 3).equals(father) ? 1 : 0;
                los.add(new ToObservation(candidate_generation_score,phase,sem,equals).createObservation());
                
            });
            it.candidateGenerationFromCodeSpecificationsWithTitle(er, sim, 0.4).stream().forEach((c) -> {
                /*String code =c.getCorrectedWord().substring(0, 3);
                Double score = map.get(code);
                if (score != null)
                    score = Math.max(score, c.getScore());
                else 
                    score = c.getScore();
                map.put(code,score);*/
                double candidate_generation_score  = c.getScore();
                String candidate = c.getCorrectedWord();
                int phase = 3;
                double sem = dist.getWeight(candidate, expected);
                int equals = candidate.substring(0, 3).equals(father) ? 1 : 0;
                los.add(new ToObservation(candidate_generation_score,phase,sem,equals).createObservation());
                
            });
            
            ws.candidateGenerationForWikiTitle(er, sim, 0.4).stream().forEach((c) -> {
                c.getCorrectedWord().stream().forEach((candidate) -> {
                    /*Double score = map.get(xx);
                    if (score != null)
                        score = Math.max(score, c.getScore());
                    else 
                        score = c.getScore();
                    map.put(xx,score);*/
                    double candidate_generation_score  = c.getScore();
                //String candidate = c.getCorrectedWord();
                int phase = 4;
                double sem = dist.getWeight(candidate, expected);
                int equals = candidate.substring(0, 3).equals(father) ? 1 : 0;
                los.add(new ToObservation(candidate_generation_score,phase,sem,equals).createObservation());
                
                });
                
            });
            
            ws.candidateGenerationForWikiContent(er, sim, 0.4).stream().filter((t1) -> {
                if (t1.getCorrectedWord().isEmpty()) {
                    return false;
                } else
                    return true;
            }).forEach((c) -> {
                /*c.getCorrectedWord().stream().map((xx) -> {
                    return xx.getThreeDigitsFather().toString();
                }).forEach((xx) -> {
                    Double score = map.get(xx);
                    if (score != null)
                        score = Math.max(score, c.getScore());
                    else 
                        score = c.getScore();
                    map.put(xx,score);
                });*/
                c.getCorrectedWord().stream().forEach((candidate) -> {
                    /*Double score = map.get(xx);
                    if (score != null)
                        score = Math.max(score, c.getScore());
                    else 
                        score = c.getScore();
                    map.put(xx,score);*/
                    double candidate_generation_score  = c.getScore();
                //String candidate = c.getCorrectedWord();
                int phase = 5;
                double sem = dist.getWeight(candidate.toString(), expected);
                int equals = candidate.toString().substring(0, 3).equals(father) ? 1 : 0;
                los.add(new ToObservation(candidate_generation_score,phase,sem,equals).createObservation());
                
                });
            });
            
        }
        //Sorting
        /*Map<Double,List<String>> sorting = new TreeMap<>(Collections.reverseOrder());
        for (String x:map.keySet()) {
            Double val = map.get(x);
            if (!sorting.containsKey(val))
                sorting.put(val, new LinkedList<>());
            sorting.get(val).add(x);
        }
        
        for (Double s : sorting.keySet()) {
            System.out.println(s+" - " +sorting.get(s).toString());
        }*/
        return los;
    }
    
    public static void main(String []args) {
        
    }
    
    
    public List<Observation> candidateGenerationWithApproximatedExpansion(String expected, Annotator er,Similarity osim) {
        double imprecision = er.approximateExpansion(it_medical_dict,osim);//osim oonly
        List<Observation> los = new LinkedList<>();
        String father = expected.substring(0,3);
        // Map<String,Double> map = new TreeMap<>();
        {
            it.candidateGenerationForICD9CMExpandedTaxonomy(er, sim, 0.4).stream().forEach((c) -> {
                double candidate_generation_score  = c.getScore();
                String candidate = c.getCorrectedWord();
                int phase = 6;
                double sem = dist.getWeight(candidate, expected);
                int equals = candidate.substring(0, 3).equals(father) ? 1 : 0;
                los.add(new ToObservation(candidate_generation_score,phase,sem,equals).createObservation());
               
            });
            it.candidateGenerationFromCodeExpandedSpecifications(er, sim, 0.4).stream().forEach((c) -> {
                double candidate_generation_score  = c.getScore();
                String candidate = c.getCorrectedWord();
                int phase = 7;
                double sem = dist.getWeight(candidate, expected);
                int equals = candidate.substring(0, 3).equals(father) ? 1 : 0;
                los.add(new ToObservation(candidate_generation_score,phase,sem,equals).createObservation());
            });
            it.candidateGenerationFromCodeSpecificationsWithTitleBothExpanded(er, sim, 0.4).stream().forEach((c) -> {
                double candidate_generation_score  = c.getScore();
                String candidate = c.getCorrectedWord();
                int phase = 8;
                double sem = dist.getWeight(candidate, expected);
                int equals = candidate.substring(0, 3).equals(father) ? 1 : 0;
                los.add(new ToObservation(candidate_generation_score,phase,sem,equals).createObservation());
                
            });
            
            ws.candidateGenerationForExpandedWikiTitle(er, sim, 0.4).stream().forEach((c) -> {
                 c.getCorrectedWord().stream().forEach((candidate) -> {
                    /*Double score = map.get(xx);
                    if (score != null)
                        score = Math.max(score, c.getScore());
                    else 
                        score = c.getScore();
                    map.put(xx,score);*/
                    double candidate_generation_score  = c.getScore();
                //String candidate = c.getCorrectedWord();
                int phase = 9;
                double sem = dist.getWeight(candidate, expected);
                int equals = candidate.substring(0, 3).equals(father) ? 1 : 0;
                los.add(new ToObservation(candidate_generation_score,phase,sem,equals).createObservation());
                
                });
                
            });
            
            ws.candidateGenerationForExpandedWikiContent(er, sim, 0.4).stream().forEach((c) -> {
                 c.getCorrectedWord().stream().forEach((candidate) -> {
                    /*Double score = map.get(xx);
                    if (score != null)
                        score = Math.max(score, c.getScore());
                    else 
                        score = c.getScore();
                    map.put(xx,score);*/
                    double candidate_generation_score  = c.getScore();
                //String candidate = c.getCorrectedWord();
                int phase = 10;
                double sem = dist.getWeight(candidate.toString(), expected);
                int equals = candidate.toString().substring(0, 3).equals(father) ? 1 : 0;
                los.add(new ToObservation(candidate_generation_score,phase,sem,equals).createObservation());
                
                });
            });

            
        }
        //Sorting
        /*Map<Double,List<String>> sorting = new TreeMap<>(Collections.reverseOrder());
        for (String x:map.keySet()) {
            Double val = map.get(x);
            if (!sorting.containsKey(val))
                sorting.put(val, new LinkedList<>());
            sorting.get(val).add(x);
        }
        
        for (Double s : sorting.keySet()) {
            System.out.println(s+" - " +sorting.get(s).toString());
        }*/
        return los;
    }
    
}
