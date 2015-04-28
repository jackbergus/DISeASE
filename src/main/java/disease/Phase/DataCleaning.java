/*
 * Copyright (C) 2015 Giacomo Bergami
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
import disease.Dataset.QuickAssoc;
import disease.Dataset.WikipediaIT;
import disease.Dataset.interfaces.WordList;
import disease.utils.MedicalRecord;
import disease.Dataset.SetDictionary;
import disease.Dataset.WikiPageView;
import disease.Dataset.WikipediaDict;
import disease.ontologies.ICD9CMCode;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Giacomo Bergami
 */
public class DataCleaning {
    
    //private ICD9Tree icdt;
    private QuickAssoc qa;
    private WikipediaIT wiki;
    //private ICD9CMDict md;
    private WordList wikipedia_dict;
    //private Dictionary icd9cm_dict;
    
    public DataCleaning() {
        this.qa = QuickAssoc.getInstance();
        //this.icdt = ICD9Tree.getInstance();
        this.wikipedia_dict = WikipediaDict.getInstance();
    }
    
    
    public void train_single_record(String emergency_room_record, Set<ICD9CMCode> codes) {
        Set<WikiPageView> wps = new HashSet<>();
        
        /* TODO: urgently
        for (ICD9CMCode id : codes) {
            if (id.hasOnlyThreeDigits()) {
                id = id.getThreeDigitsFather();
            }
            WikiPage p = qa.idToPage(id);
            if (p == null) {
                p = wiki.getApproxPageFromName(icdt.getNameById(id));
                if (p==null)
                    continue; //skip to the next code...
                else
                    qa.setIdToPage(id,p);
            }
            wps.add(p);
        }
        */
        
        //training phase 1 - checking presence of exact words
        
        //Stemming and tokenizing the word
        Annotator a = new Annotator(emergency_room_record);
        //TODO: term expansion in a
        
        
        WordList fin = new SetDictionary();
        
        {
            //WordList i = new ICD9CMDict(codes);
            //a.annotate(i);
            //a.annotate(wikipedia_dict,"Wikipedia");
            a.removeAnnotated();
            //fin.merge(i);
        }
        
        
        //the words that remain now are the words that have no exact meaning
        //second phase
        fin.merge(this.wikipedia_dict);
        a.approximate_annotation(fin,"approxed");
        qa.learn_assoc_fromAnnotation(a); //stores the association result
        throw new RuntimeException("METHOD TO COMPLETE HEAVILY!!!");
    }
    
    public void train(Set<MedicalRecord> er) {
        for (MedicalRecord e : er) {
            this.train_single_record(e.getCleanedRecord(), e.getCodes());
        }
    }
    
    public String clean(String record) {
        Annotator a = new Annotator(record);
        a.translate(qa.getLearnedCorrector());
        return a.returnCleanedDocument(); //returns cleaned text, that is the semantic of the wrong word
    }
    
}
