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

import disease.utils.AbbreviationExpansion;
import disease.Dataset.interfaces.WordList;
import disease.utils.Correction;
import disease.utils.DictionaryType;
import disease.utils.Pipeline;
import disease.utils.PipelineChunk;
import disease.similarities.MultiWordSimilarity;
import disease.similarities.Similarity;
import java.util.ListIterator;

/**
 *
 * @author vasistas
 */
public class Annotator {
    
    //Stemmed and tokenized word
    private Pipeline p;
    private MultiWordSimilarity mws = MultiWordSimilarity.getInstance();
    public Annotator(String pipeline) {
        // The pipeline already performs the tokenization an stemming phase
        p = new Pipeline(pipeline);
    }

    /**
     * Annotate the exact matchings (1. Annoation of 100% sure - this is also phase 3 after the similarity . The data you now have is "cleaned") 
     * @param dictionary
     * @param data_source 
     */
    private void annotate(WordList dictionary, DictionaryType data_source) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public double approximateExpansion(WordList dictionary, Similarity sim) {
        ListIterator<PipelineChunk> it = p.listIterator();
        double correction_score = 0;
        double count = 0;
        while (it.hasNext()) {
            PipelineChunk current = it.next();
            String value = current.getMeaning();
            Correction<String> c = mws.correctWord(dictionary, value, sim);
            correction_score += c.getScore();
            //System.out.println(value+" as "+c.getCorrectedWord()+" with "+c.getScore());
            it.set(current.setAnnotation("approx", c.getCorrectedWord()));
            count++;
        }
        return (correction_score/count);
    }
    
    public void annotate(WordList dictionary) {
        annotate(dictionary,dictionary.getType());
    }

    /**
     * Last step (5): remove from the pipeline the not annotated elements, as not meaningful
     */
    public void removeAnnotated() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    /**
     * Annotate the data assuming that the record in the Pipeline contains misspells.
     * (Step n°2: you will recevie in two different times two different dictionaries, one for Wikipedia and the other for ICD9)
     * @param fin 
     * @param data_source 
     */
    public void approximate_annotation(WordList fin, String data_source) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Translate abbreviations, and hence expand the abbr. with the actual meaning (semantics)
     * (Step n°4: expanding the annotations)  
     * @param learnedCorrector
     * @return 
     */
    public double translate(AbbreviationExpansion learnedCorrector) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String returnCleanedDocument() {
        StringBuilder sb = new StringBuilder();
        for (PipelineChunk x : p) {
            sb.append(" ");
            sb.append(x.getMeaning());
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return returnCleanedDocument();
    }

    /**
     * Initializes the annotation with the given word similarity
     */
    public void identitySemantics() {
        ListIterator<PipelineChunk> it = p.listIterator();
        while (it.hasNext()) {
            PipelineChunk current = it.next();
            it.set(current.setAnnotation("none", current.getOriginalText()[0]));
        }
    }


}
