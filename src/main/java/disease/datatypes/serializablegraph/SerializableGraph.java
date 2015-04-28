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
package disease.datatypes.serializablegraph;

import disease.utils.Storage;
import disease.datatypes.ConcreteMapIterator;
import disease.utils.datatypes.Pair;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.tweetsmining.model.graph.database.Entity;

/**
 *
 * @author vasistas
 */
public class SerializableGraph implements Iterable<Pair<? extends Entity,Pair<String,? extends Entity>>>{
    private Map<Entity,Map<String,Set<Entity>>> m;
    
    public void serialize(String path) {
        Storage.<Map<Entity,Map<String,Set<Entity>>>>serialize(m, path);
    }
    
    public SerializableGraph(String file) {
        m = Storage.<Map<Entity,Map<String,Set<Entity>>>>unserialize(file);
    }
    
    public SerializableGraph() {
        m = new HashMap<>();
    }
    
    public void put(Entity src, String relation,Entity dst) {
        Map<String, Set<Entity>> fst = m.get(src);
        if (fst==null) //map has src
            m.put(src, new HashMap<>());
        fst = m.get(src);
        
        Set<Entity> snd = fst.get(relation);
        if (snd==null) { //map has not the relation
            m.get(src).put(relation, new HashSet<>());
        }
        m.get(src).get(relation).add(dst);
    }

    public ConcreteMapIterator<Entity, Map<String, Set<Entity>>> iterate_adj() {
        return new ConcreteMapIterator<>(m);
    }
    
    
    /*
    public static void main(String args[]) {
        { 
            SerializableGraph s = new SerializableGraph();
            Sense s1 = new Sense("titolo1","senso2");
            Sense s2 = new Sense("titolo2","senso3");
            Term t1 = new Term("Termine1");
            s.put(s1, "Element", s1);
            s.put(s1, "Element", s2);
            s.put(s2, "Termine",t1);
            s.put(s1, "Termine",t1);
            s.put(s1, "Termine",t1);
            s.serialize("graph.ser");
        }
        SerializableGraph z = new SerializableGraph("graph.ser");
        for (Pair<? extends Entity, Pair<String, ? extends Entity>> x:z) {
            System.out.println(x.getFirst()+" "+x.getSecond().getFirst()+" "+x.getSecond().getSecond());
        }
    }
    */
    
    @Override
    public Iterator<Pair<? extends Entity, Pair<String, ? extends Entity>>> iterator() {
        return new EdgeIterator(this);
    }
    
}
