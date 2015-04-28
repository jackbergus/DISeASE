/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package disease.Dataset.interfaces;

import disease.Dataset.SetDictionary;
import disease.similarities.Similarity;
import disease.utils.DictionaryType;
import disease.utils.Storage;
import disease.datatypes.ConcreteMapIterator;
import disease.datatypes.MapIterator;
import disease.utils.datatypes.Pair;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author vasistas
 */
public class FileDictionary implements Dictionary<String,String> {

    private Map<String,String> real_dictionary;
    private String file;
    
    /**
     * Load a FileDictionary from a serialized file
     * @param path 
     */
    public FileDictionary(String path) {
        real_dictionary = Storage.<Map<String,String>>unserialize(path);
        file = path;
    }
    
    /**
     * Create a FileDictionary that will be saved later on
     */
    public FileDictionary() {
        this.real_dictionary = new HashMap<>();
    }
    
    public void save() {
        Storage.<Map<String,String>>serialize(real_dictionary, file);
    }
    
    public void save(String path) {
        file = path;
        save();
    }
    
    public void load() {
        real_dictionary = Storage.<Map<String,String>>unserialize(file);
    }
    
    public void put(String key, String value) {
        real_dictionary.put(key, value);
    }
    
    public Set<String> getMainTerms() {
        return this.real_dictionary.keySet();
    }
    
    @Override
    public String getValue(String key) {
        return this.real_dictionary.get(key);
    }

    @Override
    public String getKeyByExactValueMatch(String value) {
        for (Pair<String,String> p : new ConcreteMapIterator<>(this.real_dictionary)) {
            if (p.getSecond().equals(value))
                return p.getFirst();
        }
        return null;
    }

    @Override
    public Map<String, Double> getKeyBySimilarityMatching(String value, Similarity s, double precision) {
        Map<String,Double> toret = new TreeMap<>();
        for (Pair<String,String> p : new ConcreteMapIterator<>(this.real_dictionary)) {
            double d = s.sim(p.getSecond(), value);
            if (d>=precision)
                toret.put(file, d);
        }
        return toret;
    }

    @Override
    public void merge(MapIterator<String, String> d) {
        for (Pair<String,String> p : d) {
            this.real_dictionary.put(p.getFirst(), p.getSecond());
        }
    }

    @Override
    public Iterator<Pair<String,String>> iterator() {
        return new ConcreteMapIterator(this.real_dictionary).iterator();
    }

    /**
     * Uses the dictionary as a list of terms
     * @param dt
     * @return 
     */
    public WordList asWordList(DictionaryType dt) {
        return new SetDictionary(real_dictionary.keySet(),dt);
    }
    
}
