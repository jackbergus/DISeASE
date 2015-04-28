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
package org.tweetsmining.model.matrices;

import disease.utils.IteratorMapping;
import disease.utils.datatypes.Pair;
import it.jackbergus.pickiterator.IPickIterable;
import it.jackbergus.pickiterator.PickIterable;
import it.jackbergus.pickstream.PickStream;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.tweetsmining.model.graph.database.Relation;
import org.tweetsmining.model.graph.database.logical.ERInterfaceLayer;
import org.tweetsmining.model.matrices.IMatrix;

/**
 *
 * @author vasistas
 */
public class GraphViewMatrix implements IMatrix {

    private ERInterfaceLayer il;
    private String layername;
    
    public GraphViewMatrix(ERInterfaceLayer i, String layer) {
        this.il = i;
        this.layername = layer;
    }
    
    @Override
    public long nCols() {
        return Collections.max(il.getIds());
    }

    @Override
    public long nRows() {
        return Collections.max(il.getIds());
    }

    @Override
    public double get(long i, long j) {
        return il.getEdge(i, layername, j).getWeight();
    }

    @Override
    public double get(Pair<Long, Long> x) {
        return get(x.getFirst(),x.getSecond());
    }

    @Override
    public void incr(Pair<Long, Long> x, double val) {
        incr(x.getFirst(),x.getSecond(),val);
    }

    @Override
    public void rem(long i, long j) {
        il.removeEdge(i, layername, j);
    }

    @Override
    public void incr(long i, long j, double val) {
        double toadd = get(i,j);
        set(i, j, toadd+val);
    }

    @Override
    public void set(long i, long j, double val) {
        il.addEdge(i, layername, j,val);
    }

    @Override
    public void set(Pair<Long, Long> x, double val) {
        set(x.getFirst(),x.getSecond(),val);
    }

    @Override
    public IPickIterable<Pair<Long, Long>> getValueRange() {
        PickStream<Relation> k = new PickStream<>(il);
        return k.map((r)->{return new Pair<Long,Long>(r.getSource().getIndex(),r.getDestination().getIndex());})
                .get();
        
        //return IteratorMapping.<Relation>iteratorToStream(il.getRelations()).map((r)->{return new Pair<Long,Long>(r.getSource().getIndex(),r.getDestination().getIndex());}).collect(Collectors.toSet());
    }

    //In order to remove a row, it is sufficient to remove an entity
    @Override
    public void removeRow(long i) {
        System.err.println("Warning: removing row on graph matrix");
        removeEnt(i);
    }

    @Override
    public void removeCol(long j) {
        System.err.println("Warning: removing column on graph matrix");
        removeEnt(j);
    }

    @Override
    public void removeEnt(long elem) {
        il.remove(il.getEntity(elem));
    }

    @Override
    public void sum(IMatrix right) {
        PickStream<Pair<Long,Long>> k = new PickStream(right.getValueRange());
        k.forEach((x)->{
            double v = get(x); 
            set(x,v+right.get(x));
        });
    }

    @Override
    public void diff(IMatrix right) {
        PickStream<Pair<Long,Long>> k = new PickStream(right.getValueRange());
        k.forEach((x)->{
            double v = get(x); 
            set(x,v-right.get(x));
        });
    }

    @Override
    public Set<Long> getOut(long o) {
        return il.getOutSet(o).stream().map((x)->{return x.getIndex();}).collect(Collectors.toSet());
    }

    @Override
    public Set<Long> getIn(long o) {
        return il.getInSet(o).stream().map((x)->{return x.getIndex();}).collect(Collectors.toSet());
    }

    @Override
    public long getMaxKey() {
        return Collections.max(il.getIds());
    }

    @Override
    public long getMinKey() {
        return Collections.min(il.getIds());
    }

    @Override
    public void clear() {
        il.clear();
    }

    @Override
    public boolean has(long i, long j) {
        Relation r = il.hasRelation(i, layername, j);
        if (r==null)
            return false;
        return r.getWeight()>0;
    }

    @Override
    public void save() {
        il.save();
    }
    
}
