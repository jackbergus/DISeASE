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
package disease.Phase;

import com.google.common.collect.Lists;
import disease.Dataset.QuickAssoc;
import disease.Dataset.WikiPageView;
import disease.Dataset.WikipediaDict;
import disease.Dataset.WikipediaIT;
import disease.Dataset.interfaces.WordList;
import disease.ontologies.ICD9CMCode;
import disease.similarities.LowConfidenceRank;
import disease.similarities.MultiWordSimilarity;
import disease.utils.MedicalRecord;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import manhoutbook.LogisticRegression;
import manhoutbook.Observation;
import org.apache.mahout.classifier.OnlineLearner;
import org.apache.mahout.classifier.sgd.CrossFoldLearner;

/**
 *
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public class TrainingPhase {
    
    //TODO
    private WordList it_medical_dict;
    private QuickAssoc qa  = QuickAssoc.getInstance();
    
    private MultiWordSimilarity sim = MultiWordSimilarity.getInstance();
    private LowConfidenceRank lcr = LowConfidenceRank.getInstance();
    private CandidateGeneration cg = CandidateGeneration.getInstance();
    
    private List<Observation> store_in_table(MedicalRecord er) {
        Annotator a = new Annotator(er.getCleanedRecord());
        a.annotate(this.it_medical_dict); 
        a.annotate(WikipediaDict.getInstance());
        double error_in_cleaning = a.translate(qa.getLearnedCorrector());
        Map<WikiPageView,ICD9CMCode> map = null;
        
        /*{
            Set<WikiPageView> p = new HashSet<>();
            for (String term : t.findDiseasesByAnnotations(a)) {
                p.add(wiki.getApproxPageFromName(term));
            }
            p.addAll(wiki.getApproxPagesFromAnnotations(a));
            p.addAll(wiki.getPagesFollowingLinks(p));
            map = t.filterPageSetWithICD9CMCode(p);
        }*/
        
        final List<Observation> data = Lists.newArrayList();
        
        map.forEach((WikiPageView t1, ICD9CMCode icdcode) -> {
            double count = 0;
            double score = 0;
            // String Similarity too ??
            StringBuilder sb = new StringBuilder();
            for (String paragraph : t1.getSupportParagraph(a)) {
                score += WikipediaIT.CosScoreWithAnnotatedAsQuery(paragraph,a);
                count++;
                sb.append(paragraph);
            }
            score = score / count;          // string cosine score
            String paras = sb.toString();
            //double senseNetWeight = sn.checkSenseMatching(paras,a); // checking the sense of the paragraph's network
            /* XXX
                    for (ICD9CMCode originalid : er.getCodes()) {
                data.add(new Observation(score,senseNetWeight,t1.calculateTypeCoherence(icdcode, originalid),(originalid.equals(icdcode) ? 1 : 0)));
            }
                    */
        });
        
        return data;
        
    }
    
    
    
    /**
     * Behaves as a training or a test class dependingly on the boolean variable
     * @param isTrain 
     */
    public TrainingPhase(boolean isTrain) {
        if (isTrain)
            lr = LogisticRegression.getInstance();
        else 
            lr = new LogisticRegression(GLOBAL_MODEL_FILE);
    }
    
    private LogisticRegression lr = null;
    private final static String GLOBAL_MODEL_FILE = "data" + File.separator + "Aglobal_model.mod";
    
    /**
     * Trains the data and stores the learned Logit model
     * @param all_docs
     * @return 
     */
    public OnlineLearner train_documents(Collection<MedicalRecord> all_docs) {
        /*
           @Alexander:
            This is the training part of the method. When the training is done,
            all the doucments are evaluated with the evaluate method. In this 
            case you have an a-priori knowledge of the label to be classified
        */
        //Collection<Observation> perform_metrics  = evaluate(all_docs);
        // train: trains the main model and stores it in the data folder
        OnlineLearner ol =  (CrossFoldLearner)lr.train(all_docs, GLOBAL_MODEL_FILE);
        // stores the model (quite useless in the training phase, but that's it)
        lr.setLearner((CrossFoldLearner)ol);
        return ol;
    }
    
    /**
     * 
     * @param ob  Test set observation - obtained as in "evaluate" method in the current class
     * @return    Probability of the relevance of the observation
     */
    public double score_record(Observation ob) {
        return lr.testModel(ob).getProbOkScore();
    }
    
    
    /*
    public GUIResult score_record(String record) {
        //The schema of the relation is the following:
        // 1: score of the candidate generation phase
        // 2: index of the candidate generation phase
        // 3: ontology distance between generated code and expected one
        // Expected value: shallow (father) equivalence
        GUIResult gr = new GUIResult("");
        
        MedicalRecord mr = new MedicalRecord(record,new HashSet<>());
        
            //for (ICD9CMCode code : mr.getCodes()) {
                //record and code are the to-check couple
        
                 Annotator a = new Annotator(((record)));
                a.identitySemantics();
                int count = 0;
                for (Pair<Double, List<String>> p : new ConcreteMapIterator<>(cg.candidateGenerationWithModel(a))) {
                    if (count ==2)
                    break;
                    else count++;
                    gr.appendResult(p.getFirst(), p.getSecond().toString());
                }
            //}
        
       return gr;
    }
    */

    /*private Collection<Observation> evaluate(Collection<MedicalRecord> all_docs) {
        //The schema of the relation is the following:
        // 1: score of the candidate generation phase
        // 2: index of the candidate generation phase
        // 3: ontology distance between generated code and expected one
        // Expected value: shallow (father) equivalence
        List<Observation> toret = new LinkedList<>();
        
        //
           @Alexander:
            this is Java8. With this method, I'm trying to parallelize the 
            execution of the foreach method
        
        all_docs.forEach((mr) -> {
            String record = mr.getCleanedRecord();
            for (ICD9CMCode code : mr.getCodes()) {
                //record and code are the to-check couple.
                Annotator a = new Annotator(((record)));
                a.identitySemantics();
                
                //
                   @Alexander:
                   please note that these methods use the a-priori knowledge
                   of the classification label. In this case is 
                
                   code.toString()
                
                   This is the classification string from the domain experts
                   (whatever it really means). Pleaso note that:
                     * the score at point 3 uses the a-priori knowledge to 
                       define the score between the elements (distance 
                       between the expected value and the mined value through
                       candidate generation). In this case we don't have such
                       prior knowledge, and so we must decide a way to decide which
                       is the most probable input to choose as a distance in this point
                       Please note that in this case the label of the test set
                       data must not be used as an a-priori knowledge, but as an
                       a-posteriori one.
                       We could choose here the best technique to set the 3rd parameter of the observation
                
                   All these consideration could be solved in a brainstorming session
                
                toret.addAll(cg.candidateGenerationWithExactMatchInformation(code.toString(), a, lr));
                toret.addAll(cg.candidateGenerationWithApproximatedExpansion(code.toString(), a, lcr));
            }
        });
        return toret;
    }*/

    
}
