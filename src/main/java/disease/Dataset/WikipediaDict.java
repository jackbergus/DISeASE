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
import disease.utils.wikipedia.WikipediaSingleton;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author vasistas
 */
public class WikipediaDict implements WordList {

    private static WikipediaDict self = null;
    private Set<String> collection;
    
    private WikipediaDict(Set<String> elems) {
        collection = new TreeSet<>();
        collection.addAll(elems);
    }
    
    public static WikipediaDict getInstance() {
        if (self==null) {
            self = new WikipediaDict(WikipediaSingleton.getInstance().getWikiPageTitles());
        }
        return self;
    }
    
    @Override
    public int size() {
        return collection.size();
    }

    @Override
    public boolean containsExactly(String elem) {
        return collection.contains(elem);
    }

    @Override
    public Iterator<String> iterator() {
        return collection.iterator();
    }

    @Override
    public void merge(WordList prev) {
        collection.addAll(prev.asCollection());
    }

    @Override
    public Collection<String> asCollection() {
        return this.collection;
    }

    @Override
    public DictionaryType getType() {
        return DictionaryType.WIKIPEDIA_PAGE_TITLE;
    }
    
}
