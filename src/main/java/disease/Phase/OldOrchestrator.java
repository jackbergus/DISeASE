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

import disease.Dataset.Real.AbbreviationDictionary;
import disease.Dataset.Real.ICD9CMTable;
// import disease.Dataset.Real.MedicalRecordDataset;
import disease.ontologies.ICD9CMCode;

import java.io.File;

/**
 *
 * @author vasistas
 */
public class OldOrchestrator {
    
    private static OldOrchestrator self = null;
    private OldOrchestrator() {
        
    }
    
    /** 
     * 
     * @return 
     */
    public static OldOrchestrator getInstance() {
        if (self==null) 
            self = new OldOrchestrator();
        return self;
    }
    
    /**
     * This method starts 
     * @param query The medical record input as a Question from the GUI
     * @return      The result with top-3 elements
     */
    public static GUIResult run_the_algorithm(String query) {
        GUIResult start = new GUIResult(query);
        
        //this is just some bullshit to test the GUI
        //todo: replace by real stuff
        //start.appendResult(0.66634545, new ICD9CMCode("666.6"));
        //start.appendResult(0.3, new ICD9CMCode("602.8"));
        //start.appendResult(0.13454, new ICD9CMCode("702.4"));
        
        
        //Something to do it: load the stored models        
        
        return start;
    }
    
    private static String dataset_path = "disease" + File.separator +"data" + File.separator + "diagnoses_classified" + File.separator;
    
    public static void run_the_training() {
        System.out.println("Set-up the whole system.\n Phase 1: Building the Dataset Views");
        System.out.println("a. ICD-9-CM pdf parsing...");
        ICD9CMTable.init();
        System.out.println("b. Medical Abbreviation Dictionary...");
        AbbreviationDictionary.init();
        System.out.println("c. Medical Term Dictionary...");
        //MedicalDictionary.init();
        //Some more dataset??
        
        System.out.println("Reading the whole CSV dataset...");
        //MedicalRecordDataset mrd = new MedicalRecordDataset().feedWholeDataset(dataset_path, true);
        //mrd.filter_by_data_quality();
        //TODO: divide into training set and test set
        
        //DataCleaning dc = new DataCleaning();
        //dc.train(mrd.getWholeDataset());
    }
    
    public static void load_the_model() {
        System.out.println("Set-up the whole system.\n Phase 1: Building the Dataset Views");
        System.out.println("a. ICD-9-CM pdf unserialization");
        //ICD9CMTable.load();
        System.out.println("b. Medical Abbreviation Dictionary unserialization");
        //AbbreviationDictionary.load();
        System.out.println("c. Medical Term Dictionary unserialization");
        //MedicalDictionary.load();
        //Some more dataset??
        
        System.out.println("Reading the whole CSV dataset...");
        //MedicalRecordDataset mrd = new MedicalRecordDataset().feedWholeDataset(dataset_path, true);
        //mrd.filter_by_data_quality();
        //TODO: divide into training set and test set
    }

    public static void main(String params[]) {
        //Orchestrator.getInstance().run_the_algorithm("q");
        //new Orchestrator().run_the_training();
    }
    
}
