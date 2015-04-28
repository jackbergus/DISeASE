/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package disease.Dataset;

import disease.Dataset.Real.WebPageDictionary;
import disease.Dataset.interfaces.FileDictionary;
import disease.Phase.cleaner.CleanItalian;
import disease.Phase.cleaner.Cleanser;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author vasistas
 */
public class OnlineMedicalDictionary {
    
    private final static String ONLINE_STEMMED_TO_STEMMED_DICTIONARY = "data"+File.separator+"stemmed_title_to_stemmed_content.ser";
    private final static String ONLINE_WHOLE_TO_WHOLE_DICTIONARY = "data"+File.separator+"title_to_content.ser";
    private final static String ONLINE_WHOLE_TO_STEMMED = "data"+File.separator+"title_to_stemmed_title.ser";
    
    private static void addit(FileDictionary fd, String url, Cleanser ci) {
        try {
            Document document = Jsoup.connect(url).get();
            Elements collection = document.body().select("li");
            for (Element li : collection) {
                String main_term = li.select("b").get(0).text();
                
                String derived_term;
                try {
                    derived_term = li.select("i").get(0).text();
                } catch (Throwable t) {
                    derived_term = main_term;
                }
                //System.out.println(main_term + " - " + derived_term);
                if (ci!=null) {
                    main_term = ci.cleanedString(main_term);
                    derived_term = ci.cleanedString(derived_term);
                }
                fd.put(main_term, derived_term);
            }
        } catch (IOException ex) {
            Logger.getLogger(WebPageDictionary.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void addit2(FileDictionary fd, String url, Cleanser ci) {
        try {
            Document document = Jsoup.connect(url).get();
            Elements collection = document.body().select("li");
            for (Element li : collection) {
                String main_term = li.select("b").get(0).text();
                
                String derived_term;
                derived_term = ci.cleanedString(main_term);
                
                fd.put(main_term, derived_term);
            }
        } catch (IOException ex) {
            Logger.getLogger(WebPageDictionary.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static String getURL(String lang, String alphabet) {
        return "http://users.ugent.be/~rvdstich/eugloss/"+lang+"/lijst"+alphabet+".html";
    }
    
    private static void createDictionary(FileDictionary fd, Cleanser ci) {
        String alphabet[] = {"b","c","d","e","f","g","h","i","l","m","n","o","p","q","r","s","t","u","v","x","z"};
        addit(fd,getURL("IT","a"),ci);
        for (String letter : alphabet) {
            addit(fd,getURL("IT",letter),ci);
        }
    }
    
    private static void perfectToStemmed(FileDictionary fd, Cleanser ci) {
        String alphabet[] = {"b","c","d","e","f","g","h","i","l","m","n","o","p","q","r","s","t","u","v","x","z"};
        addit(fd,getURL("IT","a"),ci);
        for (String letter : alphabet) {
            addit2(fd,getURL("IT",letter),ci);
        }
    }
    
    public static FileDictionary stemmedDictionary() {
        if (new File(ONLINE_STEMMED_TO_STEMMED_DICTIONARY).exists()) {
            return new FileDictionary(ONLINE_STEMMED_TO_STEMMED_DICTIONARY);
        } else {
            Cleanser ci = CleanItalian.getInstance();
            FileDictionary fd = new FileDictionary();
            createDictionary(fd,ci);
            fd.save(ONLINE_STEMMED_TO_STEMMED_DICTIONARY);
            return fd;
        }
    }
    
    public static FileDictionary perfectDictionary() {
        if (new File(ONLINE_WHOLE_TO_WHOLE_DICTIONARY).exists()) {
            return new FileDictionary(ONLINE_WHOLE_TO_WHOLE_DICTIONARY);
        } else {
            FileDictionary fd = new FileDictionary();
            createDictionary(fd,null);
            fd.save(ONLINE_WHOLE_TO_WHOLE_DICTIONARY);
            return fd;
        }
    }
    
    public static FileDictionary perfectToStemmedDictionary() {
        if (new File(ONLINE_WHOLE_TO_STEMMED).exists()) {
            return new FileDictionary(ONLINE_WHOLE_TO_STEMMED);
        } else {
            FileDictionary fd = new FileDictionary();
            Cleanser ci = CleanItalian.getInstance();
            perfectToStemmed(fd,ci);
            fd.save(ONLINE_WHOLE_TO_STEMMED);
            return fd;
        }
    }
    
    
}
