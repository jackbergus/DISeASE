/*
 * Copyright (C) 2015 Giacomo Bergami 
 *                    Alexander Pollok
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
import disease.Dataset.OnlineMedicalDictionary;
import disease.Dataset.Real.AbbreviationDictionary;
import disease.Dataset.Real.ICD9CMDictionary;
import disease.Dataset.Real.ICD9CMTable;
import disease.Dataset.Real.MedicalRecordDataset;
import disease.Phase.Annotator;
import disease.Phase.CandidateGeneration;
import disease.Phase.DataCleaning;
import disease.Phase.Expansion;
import disease.Phase.GUIResult;
import disease.configurations.DefaultConfs;
import disease.ontologies.ICD9CMCode;
import disease.similarities.LowConfidenceRank;
import disease.utils.wikipedia.WikipediaSingleton;
import java.io.File;
import java.util.Collection;
import manhoutbook.Observation;
import org.apache.mahout.classifier.OnlineLearner;

/**
 *
 * @author Giacomo Bergami
 *         
 */
public class Orchestrator {
    
    private static Orchestrator self = null;
    private Orchestrator() {
        //While creating the orchestrator, the overall configurations are loaded
        DefaultConfs.setupConfigurations();
    }
    
    /** 
     * 
     * @return 
     */
    public static Orchestrator getInstance() {
        if (self==null) 
            self = new Orchestrator();
        return self;
    }
    
    private static TestPhase tp = new TestPhase();
    
    /**
     * This method starts 
     * @param query The medical record input as a Question from the GUI
     * @return      The result with top-3 elements
     */
    public static GUIResult run_the_algorithm(String query) {
        GUIResult start  = tp.score_record(query);
        
        //Something to do it: load the stored models  
        return start;
    }
    
    private static String dataset_path = "data" + File.separator + "diagnoses_classified" + File.separator;
    
    public static void create_serialized_dataset() {
        System.out.println("Set-up the whole system.\n Phase 1: Building the Dataset Views");
        System.out.println("a. Medical Term Dictionary..."); //permits the data expansion
        OnlineMedicalDictionary.stemmedDictionary();
        OnlineMedicalDictionary.perfectDictionary();
        OnlineMedicalDictionary.perfectToStemmedDictionary();
        
        System.out.println("b. Medical Abbreviation Dictionary...");
        AbbreviationDictionary.init();
        
        System.out.println("c. ICD-9-CM pdf parsing...");
        ICD9CMTable.init();
        ICD9CMDictionary.getInstance().init();
        ICD9CMDictionary.makeSimilarities();
        ICD9CMDictionary.getTree();
        
        System.out.println("d. Wikipedia Representation...");
        WikipediaSingleton.init();
        
        System.out.println("In order to serialize even BabelNet, please run BabelNetPhase as a distinct phase");
    }
    
    public static void run_the_training() {
        
        
        //Some more dataset??
        
        System.out.println("Reading the whole CSV dataset...");
        MedicalRecordDataset mrd = new MedicalRecordDataset().feedWholeDataset(dataset_path, true);
        mrd.filter_by_data_quality();
        //TODO: divide into training set and test set
        
        DataCleaning dc = new DataCleaning();
        dc.train(mrd.getWholeDataset());
    }
    
    private static TrainingPhase tt;
    public static void load_the_model(boolean is_logit_creation, boolean interrupted) {
        
        System.out.println("Set-up the whole system.\n Phase 1: Building the Dataset Views");
        System.out.println("a. Medical Term Dictionary..."); //permits the data expansion
        OnlineMedicalDictionary.stemmedDictionary();
        OnlineMedicalDictionary.perfectDictionary();
        OnlineMedicalDictionary.perfectToStemmedDictionary();
        
        System.out.println("b. Medical Abbreviation Dictionary...");
        AbbreviationDictionary.init();
        
        
        //System.out.println("In order to serialize even BabelNet, please run BabelNetPhase as a distinct phase");
        
        //This step really takes lotta of time
        System.out.println("e. Creating the ontology...");
        if (is_logit_creation && (!interrupted)) 
            SmallOntology.getInstance().do_init();
        else
            SmallOntology.getInstance();
        //System.err.println("Breaking here: trying not to avoid the re-run of the model");
        //System.exit(1);
        
        System.out.println("c. ICD-9-CM pdf parsing...");
        ICD9CMTable.init();
        ICD9CMDictionary.getInstance().init();
        ICD9CMDictionary.makeSimilarities();
        ICD9CMDictionary.getTree();
        
        System.out.println("d. Wikipedia Representation...");
        WikipediaSingleton.init();
        
        //System.out.println("Now supposing that BabelNet has been loaded: loading the CSVs");
        

        
        //loading the data as the train test.
        //The most time consuming operation
        
        /*
           In this case is_logit_creation equals to true. When you call this
           method without the "train" argument from command line, the method
           is called with a "false" parameter, and hence tt will load the
           model which has been stored in the previous training phase.
        */
        tt = new TrainingPhase((!is_logit_creation) && (!interrupted));
        
        if (is_logit_creation||interrupted) {
            /*
                This class digestes the whole dataset of the classified elements.
                In this case this is the training set.
            */
            MedicalRecordDataset training = new MedicalRecordDataset();
        
            /*
               This method fetches the whole dataset. In your case the test set
               is located in: "data"+File.separator+"Classified"
            */
            training.feedWholeDataset("data"+File.separator+"Classified", true);
            /*  
                getWholeDataset -> fetches all the csvs from the Training set,
                and parses it into a MedicalRecord class
                train_documents-> see the method for further explainations
            */
            tt.train_documents(training.getWholeDataset());
            System.out.println("Stored Logit - Done");
            System.exit(0);
        }
    }

    public static void main(String params[]) {
        
        
        
        //Initializes a new version of the dataset
        //Orchestrator.create_serialized_dataset();
        //System.exit(0);
        /*
        //Stats the dataset training part
        Orchestrator.run_the_training();
        */
        
        //Orchestrator.load_the_model(true);
        //String test = "lesione parafaringea";
        /*
        
        MultiWordSimilarity hoc = MultiWordSimilarity.getInstance();
        for (Pair<String, Correction> x: new ConcreteMapIterator<>(hoc.singleWordCorrection("sinusite cronica", test))) {
            System.out.println("String: " + x.getFirst() + " Correction with:"+x.getSecond().getCorrectedWord()+" and score "+x.getSecond().getScore());
        }
        System.out.println("===");
        System.out.println(hoc.sim("sinusite cronica", test));
        System.exit(0);*/
        
        //Annotator a;
        /*
            Worse scoring technique 
        ================================
                Annotator a = new Annotator((Expansion.expand_cleaned_text(test)));
        a.do_bogus_data_cleaning(); //false semantic annotation
        Classification.getInstance().candidateGeneration(a);
        
        System.out.println("=====");
                */
        
        /*a = new Annotator(((test)));
        a.identitySemantics(); //false semantic annotation
        CandidateGeneration.getInstance().candidateGenerationWithExactMatchInformation(a);
        
        System.out.println("=====");
        System.out.println(Expansion.expand_cleaned_text(test));
        a = new Annotator((Expansion.expand_cleaned_text(test)));
        a.identitySemantics(); //false semantic annotation - keeping the same text
        CandidateGeneration.getInstance().candidateGenerationWithExactMatchInformation(a);
        System.out.println("======2");
        CandidateGeneration.getInstance().candidateGenerationWithApproximatedExpansion(a,LowConfidenceRank.getInstance());
        */
        
        
    }
    
}
