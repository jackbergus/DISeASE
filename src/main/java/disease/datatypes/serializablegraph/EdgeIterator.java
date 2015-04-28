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

import disease.datatypes.ConcreteMapIterator;
import disease.datatypes.MapIterator;
import disease.utils.datatypes.Pair;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.tweetsmining.model.graph.database.Entity;

/**
 *
 * @author vasistas
 */
public class EdgeIterator implements Iterator<Pair<? extends Entity,Pair<String,? extends Entity>>>{

    private SerializableGraph sg;
    private final MapIterator<Entity, Map<String, Set<Entity>>> start_iterator;
    private MapIterator<String,Set<Entity>> node_adj_iterator;
    private Entity current_src;
    private String current_rel;
    private Entity current_dst;
    private Iterator<Entity> dest_iterator;
    public EdgeIterator(SerializableGraph sg) {
        this.sg = sg;
        this.start_iterator = sg.iterate_adj();
        Pair<Entity, Map<String, Set<Entity>>> k = this.start_iterator.next();
        this.current_src = k.getFirst();
        this.node_adj_iterator = new ConcreteMapIterator<>(k.getSecond());
        Pair<String, Set<Entity>> edge_forming = this.node_adj_iterator.next();
        this.current_rel = edge_forming.getFirst();
        this.dest_iterator = edge_forming.getSecond().iterator();
    }
    
    @Override
    public boolean hasNext() {
        return (this.start_iterator.hasNext() || this.node_adj_iterator.hasNext() || this.dest_iterator.hasNext());
    }

    @Override
    public Pair<? extends Entity, Pair<String, ? extends Entity>> next() {
        
        if (this.dest_iterator.hasNext()) {
            return new Pair<>(this.current_src,new Pair<>(this.current_rel,this.dest_iterator.next()));
        } else if (this.node_adj_iterator.hasNext()) {
            Pair<String, Set<Entity>> edge_forming = this.node_adj_iterator.next();
            this.current_rel = edge_forming.getFirst();
            this.dest_iterator = edge_forming.getSecond().iterator();
            return new Pair<>(this.current_src,new Pair<>(this.current_rel,this.dest_iterator.next()));
        } else if (this.start_iterator.hasNext()) {
            Pair<Entity, Map<String, Set<Entity>>> k = this.start_iterator.next();
            this.current_src = k.getFirst();
            this.node_adj_iterator = new ConcreteMapIterator<>(k.getSecond());
            Pair<String, Set<Entity>> edge_forming = this.node_adj_iterator.next();
            this.current_rel = edge_forming.getFirst();
            this.dest_iterator = edge_forming.getSecond().iterator();
            return new Pair<>(this.current_src,new Pair<>(this.current_rel,this.dest_iterator.next()));
        } else
            return null;
    }
    
    
    
}
