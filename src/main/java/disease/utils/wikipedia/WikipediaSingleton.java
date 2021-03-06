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
package disease.utils.wikipedia;

import disease.Dataset.MedicalDictionary;
import disease.Dataset.OnlineMedicalDictionary;
import disease.Dataset.WikiPageView;
import disease.Dataset.interfaces.WordList;
import disease.Phase.cleaner.CleanItalian;
import disease.Phase.Annotator;
import disease.ontologies.ICD9CMCode;
import disease.utils.Correction;
import disease.datatypes.ConcreteMapIterator;
import disease.utils.Storage;
import disease.datatypes.MapIterator;
import disease.utils.datatypes.Pair;
import disease.similarities.*;
import disease.utils.DictionaryType;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Giacomo Bergami
 */
public class WikipediaSingleton {
    
    private CleanItalian ci = CleanItalian.getInstance();
    private static WikipediaSingleton self = null;
    private Similarity lcs = LowConfidenceRank.getInstance();

    
    private Map<String,String> code_to_wikipagetitle = new HashMap<>();
    private Map<String,String> stemmed_title_to_expandedtitle = new HashMap<>();
    private Map<String,List<String>> wikipagetitle_to_codes = new HashMap<>();
    private final WordList meddict =  OnlineMedicalDictionary.stemmedDictionary().asWordList(DictionaryType.ITALIAN_MEDICAL_DICTIONARY);
    
    public List<Correction<List<String>>> candidateGenerationForWikiTitle(Annotator document, MultiWordSimilarity mws, double thereshold) {
        return ScoreToRank.<String,List<String>>candidateGeneration(document, new ConcreteMapIterator<>(wikipagetitle_to_codes), mws, thereshold);
    }
    public List<Correction<List<String>>> candidateGenerationForExpandedWikiTitle(Annotator document, MultiWordSimilarity mws, double thereshold) {
        return ScoreToRank.<String,List<String>>candidateGeneration(document, new MapIterator<String,List<String>>() {

            private final Iterator<String> title_iterator = wikipagetitle_to_codes.keySet().iterator();
            
            @Override
            public boolean hasNext() {
                return title_iterator.hasNext();
            }

            @Override
            public Pair<String, List<String>> next() {
                String title = title_iterator.next();
                String expanded_title = stemmed_title_to_expandedtitle.get(title);
                return new Pair<>(expanded_title,wikipagetitle_to_codes.get(title));
            }

            @Override
            public Iterator<Pair<String, List<String>>> iterator() {
                return this;
            }
        }, mws, thereshold);
    }
    
    private Map<String,String> real_wikipagetitle_to_stemmed = new HashMap<>();
    private Map<String,String> wikipagetitle_to_content = new HashMap<>();
    private Map<String,String> wikipagetitle_to_expandedcontent = new HashMap<>();
    
    public List<Correction<List<ICD9CMCode>>> candidateGenerationForWikiContent(Annotator document, MultiWordSimilarity mws, double thereshold) {
        String cleaned = document.returnCleanedDocument();
        List<Correction<List<ICD9CMCode>>> mp = new LinkedList<>();
        
        getAllMedicalPages().stream().forEach((x) -> {
            double score =mws.sim(cleaned, x.getContent());
            if (score>=thereshold) {
                mp.add(new Correction<>(x.getCodeList(),score));
            }
        });
        return mp;
    }
    
    public List<Correction<List<ICD9CMCode>>> candidateGenerationForExpandedWikiContent(Annotator document, MultiWordSimilarity mws, double thereshold) {
        String cleaned = document.returnCleanedDocument();
        List<Correction<List<ICD9CMCode>>> mp = new LinkedList<>();
        
        getAllExpandedMedicalPages().stream().forEach((x) -> {
            double score =mws.sim(cleaned, x.getContent());
            if (score>=thereshold) {
                mp.add(new Correction<>(x.getCodeList(),score));
            }
        });
        return mp;
    }
    
    private Map<String,List<String>> wikipagetitle_to_wikipagetitle = new HashMap<>();
    
    private WikipediaSingleton() {
        
    }
    
    public Set<String> getWikiPageTitles() {
        return wikipagetitle_to_codes.keySet();
    }
    
    public boolean containsPage(String title) {
        return this.wikipagetitle_to_codes.containsKey(title);
    }
    
    public WikiPageView createPageView(String title) {
        if (containsPage(title)) {
            List<String> pages;
            if (wikipagetitle_to_wikipagetitle.containsKey(title))
                pages = wikipagetitle_to_wikipagetitle.get(title);
            else
                pages = new ArrayList<>();
            List<ICD9CMCode> codes;
            if (wikipagetitle_to_codes.containsKey(title))
                codes = this.wikipagetitle_to_codes.get(title).stream().map((String t) -> new ICD9CMCode(t)).collect(Collectors.<ICD9CMCode>toList());
            else
                codes = new ArrayList<>();
            return new WikiPageView(title, codes, wikipagetitle_to_content.get(title),pages);
        } else
            return null;
    }
    
    /**
     * Returns a Wikipedia Page where the text has been expanded. Note that
     * the contained links are the page names with no expansion.
     * @param title     Not-extended title (original title)
     * @return 
     */
    public WikiPageView createExpandedPageView(String title) {
        if (containsPage(title)) {
            List<String> pages;
            if (wikipagetitle_to_wikipagetitle.containsKey(title)) {
                pages = wikipagetitle_to_wikipagetitle.get(title);
            } else
                pages = new ArrayList<>();
            List<ICD9CMCode> codes;
            if (wikipagetitle_to_codes.containsKey(title))
                codes = this.wikipagetitle_to_codes.get(title).stream().map((String t) -> new ICD9CMCode(t)).collect(Collectors.<ICD9CMCode>toList());
            else
                codes = new ArrayList<>();
            return new WikiPageView(this.stemmed_title_to_expandedtitle.get(title), codes, wikipagetitle_to_expandedcontent.get(title),pages);
        } else
            return null;
    }
    
    public List<WikiPageView> getAllMedicalPages() {
        return wikipagetitle_to_codes.keySet().stream().map(this::createPageView).collect(Collectors.toList());
    }
    
    public List<WikiPageView> getAllExpandedMedicalPages() {
        return wikipagetitle_to_codes.keySet().stream().map(this::createExpandedPageView).collect(Collectors.toList());
    }
    
    public static WikipediaSingleton getInstance() {
        if (self==null)
            self = new WikipediaSingleton();
        return self;
    }
    
    private void do_store(){
        //if (!(new File(CTW).exists() && new File(STE).exists() && new File(WTC).exists()
        //        && new File(WTCT).exists() && new File(WTEC).exists() && new File(WTW).exists())) {
            Storage.<Map<String,String>>serialize(code_to_wikipagetitle, CTW);
            Storage.<Map<String,String>>serialize(stemmed_title_to_expandedtitle, STE);
            Storage.<Map<String,List<String>>>serialize(wikipagetitle_to_codes, WTC);
            Storage.<Map<String,String>>serialize(wikipagetitle_to_content, WTCT);
            Storage.<Map<String,String>>serialize(wikipagetitle_to_expandedcontent, WTEC);
            Storage.<Map<String,List<String>>>serialize(wikipagetitle_to_wikipagetitle, WTW);
            Storage.<Map<String,String>>serialize(real_wikipagetitle_to_stemmed, RTS);
        //}
    }
    
    
    public static void init() {
        if (!new File(CTW).exists())
            System.out.println("CTW: "+CTW);
        if (!new File(STE).exists())
            System.out.println("STE: "+STE);
        if (!new File(WTC).exists())
            System.out.println("WTC: "+WTC);
        if (!new File(WTCT).exists())
            System.out.println("WTCT: "+WTCT);
        if (!new File(WTEC).exists())
            System.out.println("WTEC: "+WTEC);
        if (!new File(WTW).exists())
            System.out.println("WTW: "+WTW);
        /*if (!new File(RTS).exists())
            System.out.println("RTS: "+RTS);*/
        if (!(new File(CTW).exists() && 
                new File(STE).exists() && 
                new File(WTC).exists() &&
                new File(WTCT).exists() && 
                new File(WTEC).exists() && 
                new File(WTW).exists() /*&&
                new File(RTS).exists()*/))
            new ParseDump().parseWiki();
        else
            getInstance().do_load();
    }
    
    private static final String CTW = "data"+File.separator+"code_to_wikipagetitle.ser";
    private static final String STE = "data"+File.separator+"title_to_expandedtitle.ser";
    private static final String WTC = "data"+File.separator+"wikipagetitle_to_codes.ser";
    private static final String WTCT = "data"+File.separator+"wikipagetitle_to_content.ser";
    private static final String WTEC = "data"+File.separator+"wikipagetitle_to_expandedcontent.ser";
    private static final String WTW = "data"+File.separator+"wikipagetitle_to_wikipagetitle.ser";
    private static final String RTS = "data"+File.separator+"wikipagetitlereal_to_wikipagetitlestemmed.ser";
    
    private void do_load() {
        code_to_wikipagetitle = Storage.<Map<String,String>>unserialize(CTW);
        stemmed_title_to_expandedtitle = Storage.<Map<String,String>>unserialize(STE);
        wikipagetitle_to_codes = Storage.<Map<String,List<String>>>unserialize(WTC);
        wikipagetitle_to_content = Storage.<Map<String,String>>unserialize(WTCT);
        wikipagetitle_to_expandedcontent = Storage.<Map<String,String>>unserialize(WTEC);
        wikipagetitle_to_wikipagetitle = Storage.<Map<String,List<String>>>unserialize(WTW);
        real_wikipagetitle_to_stemmed = Storage.<Map<String,String>>unserialize(RTS);
    }
    
    public static void store() {
        getInstance().do_store();
    }
    
    public static void load() {
        getInstance().do_load();
    }
    
    public void putCodeToWikiTitle(ICD9CMCode code, String title) { 
        String clean = ci.cleanedString(title);
        this.code_to_wikipagetitle.put(code.toString(), title);
        //stores even the expanded version
        Annotator a = new Annotator(clean);
        a.identitySemantics();
        a.approximateExpansion(meddict, this.lcs);
        this.stemmed_title_to_expandedtitle.put(clean, a.returnCleanedDocument());
        
    }
    
    public void putWikiToCode(String title, List<ICD9CMCode> codes) {
        LinkedList<String> lls = new LinkedList<>();
        codes.stream().forEach((x) -> {
            lls.add(x.toString());
        });
        this.wikipagetitle_to_codes.put(ci.cleanedString(title), lls);
    }
    
    public void putWikiToContent(String title, String content) {
        String cleaned = ci.cleanedString(title);
        this.real_wikipagetitle_to_stemmed.put(title, cleaned);
        content = ci.cleanedString(content);
        this.wikipagetitle_to_content.put(cleaned, content);
        Annotator a = new Annotator(content);
        a.identitySemantics();
        a.approximateExpansion(meddict, lcs);
        this.wikipagetitle_to_expandedcontent.put(cleaned, a.returnCleanedDocument());
    }
   
    
    public void putTitleToMulti(String title, List<String> dest) {
        this.wikipagetitle_to_wikipagetitle.put((title), dest);
    }
    
}
