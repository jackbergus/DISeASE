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
package disease.Dataset;

import disease.Dataset.interfaces.WordList;
import disease.utils.DictionaryType;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author vasistas
 */
public class SetDictionary implements WordList {

    private Set<String> dictionary;
    private DictionaryType dt;
    
    public SetDictionary(Set<String> dict, DictionaryType type) {
        this.dictionary = dict;
        this.dt = type;
    }
    
    public SetDictionary() {
        this.dictionary = new TreeSet<>();
        this.dt = DictionaryType.WHOLE_WORDS;
    }
    
    @Override
    public int size() {
        return this.dictionary.size();
    }

    @Override
    public boolean containsExactly(String elem) {
        return this.dictionary.contains(elem);
    }

    @Override
    public Iterator<String> iterator() {
        return this.dictionary.iterator();
    }

    @Override
    public void merge(WordList prev) {
        Set<String> tmp = new TreeSet<>();
        tmp.addAll(dictionary); // the Dictionary could be an external view.
        for (String s : prev) {
            tmp.add(s);
        }
        this.dictionary = tmp;
    }

    @Override
    public Collection<String> asCollection() {
        return this.dictionary;
    }

    @Override
    public DictionaryType getType() {
        return this.dt;
    }
    
}
