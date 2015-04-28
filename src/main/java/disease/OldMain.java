/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package disease;

import disease.Dataset.OnlineMedicalDictionary;
import disease.Dataset.Real.AbbreviationDictionary;
import disease.Dataset.Real.ICD9CMDictionary;
import disease.Dataset.Real.ICD9CMTable;
import disease.utils.wikipedia.WikipediaSingleton;

/**
 *
 * @author vasistas
 */
@Deprecated
public class OldMain {
    
    public static void main(String args[]) {
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
    
}
