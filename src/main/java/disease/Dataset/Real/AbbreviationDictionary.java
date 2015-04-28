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
package disease.Dataset.Real;

import disease.Dataset.OnlineMedicalDictionary;
import disease.Dataset.SetDictionary;
import disease.Dataset.interfaces.Dictionary;
import disease.Dataset.interfaces.WordList;
import disease.Phase.cleaner.CleanItalian;
import disease.Phase.Annotator;
import disease.similarities.Levenshtein;
import disease.similarities.Similarity;
import disease.utils.DictionaryType;
import disease.utils.PDFToText;
import disease.utils.Storage;
import disease.datatypes.MapIterator;
import disease.utils.datatypes.Pair;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Giacomo Bergami
 */
public class AbbreviationDictionary implements Dictionary<String,String> {
    
    private static AbbreviationDictionary self = null;
    private WordList meddict = OnlineMedicalDictionary.stemmedDictionary().asWordList(DictionaryType.ITALIAN_MEDICAL_DICTIONARY);
    private Map<String,String> abbr_to_expansion;
    private Map<String,String> abbr_meta_expansion; //uses the medical Dictionary to store the expanded version
    private Similarity lev;
    private CleanItalian ci = CleanItalian.getInstance();
    
    private final static String ABBR_TO_EXPANSION = "data"+File.separator+"abbr_to_expansion.ser";
    private final static String ABBR_META_EXPANSION = "data"+File.separator+"abbr_meta_expansion.ser";
    
    public WordList asWordList() {
        return new SetDictionary(abbr_to_expansion.keySet(),DictionaryType.ITALIAN_ABBREVIATION_DICTIONARY);
    }
    
    public WordList asExpandedWordList() {
        return new SetDictionary(abbr_meta_expansion.keySet(),DictionaryType.ITALIAN_ABBREVIATION_DICTIONARY);
    }
    
    private String areas[] = {"Anestesiologia","Pediatria","Terapia","Anatomia",
            "Biochimica","Chimica","Neurologia","Radiologia","Farmacologia",
            "Cardiologia","Urologia","Farmacologia","Endocrinologia","Epatologia",
            "Medicina d’Urgenza","Pneumologia","Fisica","Immunologia","Otorinolaringoiatria",
            "Chirurgia","Infermieristico","Ostetricia","Fisiologia",
            "Laboratorio","Chirurgia vascolare","Ortopedia","Medicina alternativa",
            "Psichiatria","Dermatologia","Reumatologia", "Neonatologia", "Virologia",
            "Ematologia","Gastroenterologia","Odontoiatria","Patologia","Psicologia",
            "Microbiologia","Statistica","Oculistica","Genetica","Oncologia",
            "Nefrologia","Urologia","Ginecologia","Biologia molecolare","Medicina nucleare",
            "Chirurgia Plastica","Diabetologia","Trapianti d’organo","Sierologia",
            "Chiropratica", "Plastica", "fisica","Vascolare"};
    
    private AbbreviationDictionary() {
        this.abbr_to_expansion = new HashMap<>();
        this.lev = new Levenshtein();
    }
    
    public static AbbreviationDictionary getInstance() {
        if (self==null)
            self = new AbbreviationDictionary();
        return self;
    }
    
    public String expandAbbreviation(String stored_abbreviation) {
        return this.abbr_to_expansion.get(stored_abbreviation);
    }
    
    /**
     * Expand the abbreviation with the expanded defintion
     * @param stored_abbreviation
     * @return 
     */
    public String expandAbbreviationExpanded(String stored_abbreviation) {
        return this.abbr_meta_expansion.get(stored_abbreviation);
    }
    
    /*public Map<String,Double> queryAbbreviationBySimilarity(String imprecise_abbr, double precision) {
        Map<String,Double> toret = new HashMap<>();
        for (String abbr : this.abbr_to_expansion.keySet()) {
            
            double score = (lev.sim(abbr, imprecise_abbr));
            if (score>=precision) {
                toret.put(abbr,score);
                System.out.println(abbr + " -- " + score);
            }
        }
        return toret;
    }*/
    
    private void do_init() {
        getInstance();
        String pdf = PDFToText.processFile("data"+File.separator+"acromed.pdf", false, 0);
        for (String line : pdf.split("\n")) {
            if (line.startsWith("Acronim")|| line.startsWith("Aggiorna") || line.startsWith("Per la ricerca"))
                continue;
            else if (line.startsWith("Si ringraziano"))
                break;
            String abbr;
            String mean;
            if (line.contains(".")) {
                int index = line.lastIndexOf(".");
                abbr = line.substring(0,index);
                
                mean = line.substring(index+1);
            } else {
                int index = line.indexOf(" ");
                try {
                    abbr = line.substring(0, index);
                    mean = line.substring(index);
                } catch (Throwable t) {
                    System.err.println(line);
                    continue;
                }
            }
            for (String s : areas) {
                if (mean.contains(s)) {
                    mean = mean.replace(s, "");
                    break;
                }
            }
            for (String s : areas) {
                if (abbr.contains(s)) {
                    continue;
                }
            }
            //System.out.println(abbr + " --" + mean);
            mean = ci.cleanedString(mean);
            this.abbr_to_expansion.put(abbr, mean);
            Annotator a = new Annotator(mean);
            a.identitySemantics();
            a.approximateExpansion(meddict, lev);
            this.abbr_to_expansion.put(abbr, a.returnCleanedDocument());
        }
        Storage.<Map<String,String>>serialize(this.abbr_to_expansion,ABBR_TO_EXPANSION);
        Storage.<Map<String,String>>serialize(this.abbr_meta_expansion,ABBR_META_EXPANSION);
    }
    
    public static void init() {
        if (!(new File(ABBR_TO_EXPANSION).exists() && new File(ABBR_META_EXPANSION).exists()))
            getInstance().do_init();
        else
            getInstance().do_load();
    }
    
    private void do_load() {
        this.abbr_to_expansion = Storage.<Map<String,String>>unserialize(ABBR_TO_EXPANSION);
        this.abbr_meta_expansion = Storage.<Map<String,String>>unserialize(ABBR_META_EXPANSION);
    }

    @Override
    public String getValue(String key) {
        return abbr_to_expansion.get(key);
    }
    
    public String getExpandedValue(String key) {
        return abbr_meta_expansion.get(key);
    }

    @Override
    public String getKeyByExactValueMatch(String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, Double> getKeyBySimilarityMatching(String value, Similarity s, double precision) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void merge(MapIterator<String, String> d) {
        throw new UnsupportedOperationException(this.getClass().getName()+": Do not add elements in here.");
    }

    @Override
    public Iterator<Pair<String, String>> iterator() {
        throw new UnsupportedOperationException(this.getClass().getName()+": Do not iterate.");
    }
    
}
