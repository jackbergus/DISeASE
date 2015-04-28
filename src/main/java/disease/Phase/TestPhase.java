package disease.Phase;

import disease.Dataset.DataIntegration.SmallOntology;
import disease.Dataset.Real.ICD9CMTable;
import disease.Dataset.Real.MedicalRecordDataset;
import disease.datatypes.ConcreteMapIterator;
import disease.ontologies.ICD9CMCode;
import disease.similarities.MultiWordSimilarity;
import disease.utils.MedicalRecord;
import disease.utils.ToObservation;
import disease.utils.TrainingResult;
import disease.utils.datatypes.Pair;
import disease.utils.wikipedia.WikipediaSingleton;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import manhoutbook.Observation;

/*
 * Copyright (C) 2015 Giacomo Bergami <giacomo@openmailbox.org>
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
/**
 *
 * @author Alexander Pollok
 */
public class TestPhase {

    private ICD9CMTable it = ICD9CMTable.init();
    private SmallOntology onta = SmallOntology.getInstance();
    private TrainingPhase tt = new TrainingPhase(false);
    
    // @author Alexander Pollok
    public void validate_the_model() {

                    //for each MedicalRecord in the TestSet dataset (1)
        //    candidates are generated (2)
        //    a subset of best candidates is build (3)
        //    for each of those best candidates a score is generated and the candidate with the highest score is stored (4)
        //    the winner candidate is compared to the codes from the sample solution (5)
        //    a output string is built (6)
            //initialisations
        MedicalRecordDataset validation = new MedicalRecordDataset();
        validation.feedWholeDataset("data" + File.separator + "TestSet", true);
        Set<MedicalRecord> rs = validation.getWholeDataset();

        //output is the string which is printed at the end, corresponds to the input for the modelica script
        PrintWriter outputWriter;
        try {
            outputWriter = new PrintWriter("data.txt");
            outputWriter.write("data := [");
            Iterator<MedicalRecord> itt = rs.iterator();
            int len = rs.size();
            int count = 1;
            while (itt.hasNext()) {
                System.out.println(count + " of "+len);
                MedicalRecord mr = itt.next();
                boolean hasn = !itt.hasNext();
                Iterator<TrainingResult> ittt = scoreSingleRecord(mr).iterator();
                while (ittt.hasNext()) {
                    TrainingResult s = ittt.next();
                    outputWriter.write("[" + Double.toString(s.getConfidence()) + "," + Integer.toString(s.getCorrectness()) + "]");
                    if ((ittt.hasNext()) || (itt.hasNext())) {
                        outputWriter.write(",");
                    }
                }
                count++;
                outputWriter.flush();
            }
            outputWriter.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TestPhase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    /**
     * Given a medical record, he tries to score it
     *
     * @param mr
     * @return
     * @author Alexander Pollok and Giacomo Bergami
     */
    public Collection<TrainingResult> scoreSingleRecord(MedicalRecord mr) {

        String record = mr.getCleanedRecord();
        //If the Medical Record has no ICD entries, it means that it
        //is a query record, hence we don't have a proper correctness value
        GUIResult result = score_record(record);
        ArrayList<TrainingResult> toret = new ArrayList<>();
        int correct = (mr.getCodes().isEmpty() || result.getRankedResults().isEmpty() ? 1 : 0);

        // (5)
        for (Pair<Double, Set<String>> x : new ConcreteMapIterator<>(result.getRankedResults())) {
            double confidence = x.getFirst();
            for (String y : x.getSecond()) {
                correct = 0;
                for (ICD9CMCode code : mr.getCodes()) {
                    if (y.startsWith(code.getCode().substring(0, 3))) {
                        correct = 1; //the assignment is correct if we get at least one of them all
                        break; //Setting it only once
                        
                    }
                }
                toret.add(new TrainingResult(confidence,correct,y));
            }
        }

        // (6)
        return toret;
    }

    
    /**
     * 
     * @author Alexander Pollok and Giacomo Bergami
     * 
     * @param record
     * @return 
     */
    public GUIResult score_record(String record) {
        
        Annotator er = new Annotator(record);
        er.identitySemantics();

        MultiWordSimilarity sim = MultiWordSimilarity.getInstance();
        WikipediaSingleton ws = WikipediaSingleton.getInstance();

        //the candidate generation results are stored redundantly
        Map<String, Double> mapscore = new HashMap<>(); //code and score
        Map<String, Integer> mapphase = new HashMap<>(); //code and phase

        // (2)
        {
            //phase 1
            it.candidateGenerationForICD9CMTaxonomy(er, sim, 0.4).stream().forEach((c) -> {
                mapscore.put(c.getCorrectedWord().substring(0, 3), c.getScore());
                mapphase.put(c.getCorrectedWord().substring(0, 3), 1);
            });
            //phase 2
            it.candidateGenerationFromCodeSpecifications(er, sim, 0.4).stream().forEach((c) -> {
                String code = c.getCorrectedWord().substring(0, 3);
                Double score = mapscore.get(code);
                if (score != null) {
                    score = Math.max(score, c.getScore());
                } else {
                    score = c.getScore();
                }
                mapscore.put(code, score);
                mapphase.put(code, 2);
            });
            //phase 3
            it.candidateGenerationFromCodeSpecificationsWithTitle(er, sim, 0.4).stream().forEach((c) -> {
                String code = c.getCorrectedWord().substring(0, 3);
                Double score = mapscore.get(code);
                if (score != null) {
                    score = Math.max(score, c.getScore());
                } else {
                    score = c.getScore();
                }
                mapscore.put(code, score);
                mapphase.put(code, 3);
            });
            //phase 4
            ws.candidateGenerationForWikiTitle(er, sim, 0.4).stream().forEach((c) -> {
                c.getCorrectedWord().stream().map((xx) -> {
                    return xx.substring(0, 3);
                }).forEach((xx) -> {
                    Double score = mapscore.get(xx);
                    if (score != null) {
                        score = Math.max(score, c.getScore());
                    } else {
                        score = c.getScore();
                    }
                    mapscore.put(xx, score);
                    mapphase.put(xx, 4);
                });
            });
            //phase 6
            it.candidateGenerationForICD9CMExpandedTaxonomy(er, sim, 0.4).stream().forEach((c) -> {
                mapscore.put(c.getCorrectedWord().substring(0, 3), c.getScore());
                mapphase.put(c.getCorrectedWord().substring(0, 3), 6);
            });
        }

        Map<Double, List<String>> sorting = new TreeMap<>(Collections.reverseOrder());
        for (String x : mapscore.keySet()) {
            Double val = mapscore.get(x);
            if (!sorting.containsKey(val)) {
                sorting.put(val, new LinkedList<>());
            }
            sorting.get(val).add(x);
        }

        // (3)
        List<String> all = new ArrayList<>();
        sorting.values().forEach(all::addAll);
        Map<String, Double> k = onta.getRandomWalkBestScores(all, mapscore);
        Set<String> best = k.keySet();

        
        // (4)
        GUIResult toret = new GUIResult(record);
        {
            TreeMap<String,Double> results = new TreeMap(Collections.reverseOrder());
            double maximum = -1;
            for (Pair<String, Double> p : new ConcreteMapIterator<>(onta.getRandomWalkBestScores(all,mapscore))) {
                //REMOVING NOT IN-CLASS IDs
                if (p.getFirst()!=null && p.getFirst().length()!=0){
                    String tooutput = p.getFirst();
                    results.put(tooutput,p.getSecond());
                    maximum = Double.max(p.getSecond(),maximum);
                }
            }
            Iterator<String> itt = results.keySet().iterator();
            while (itt.hasNext()) {
                String v = itt.next();
                double tostore = results.get(v)/maximum;
                tostore = (tostore>=0.85 ? 1 : tostore/0.85);
                toret.appendResult(tostore, v);
                //System.out.println(it.getValue(new ICD9CMCode(v)) + " :: " + tostore);
                    
            }
        }
        
        for (String candidate : all) {
            double confidence = 0.0;
            String icd = "";
            String father ="";
            for (String compare : best) {
                int commonfather = (compare.startsWith(candidate.substring(0, 3)) ? 1 : 0);
                Observation ob = new ToObservation(mapscore.get(candidate),
                                                    mapphase.get(candidate),
                                                    onta.getWeight(candidate, compare),
                                                    commonfather).createObservation();
                double score = onta.getWeight(candidate, compare);//tt.score_record(ob);
                if (score > confidence) {
                    confidence = score;
                    icd = candidate;
                }
            }
            if (confidence>0) {
                confidence = (confidence>=0.85 ? 1 : confidence/0.85);
                toret.appendResult(confidence, icd);
                //System.out.println(it.getValue(new ICD9CMCode(icd)) + " ::v2 " + confidence);
            }
        }
        
        return toret;
        
    }
    

}
