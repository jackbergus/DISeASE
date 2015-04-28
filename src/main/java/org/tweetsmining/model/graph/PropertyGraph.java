/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tweetsmining.model.graph;

import disease.utils.datatypes.Pair;
import it.jackbergus.pickiterator.IPickIterable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.tweetsmining.model.graph.database.Entity;
import org.tweetsmining.model.graph.database.Relation;
import org.tweetsmining.model.matrices.GuavaMatrix;
import org.tweetsmining.model.matrices.IMatrix;
import org.tweetsmining.model.matrices.SimpleMatrix;

/**
 * This class provides over a view of given relations. See IMatrix and IGraph
 * for comments
 *
 * @author gyankos
 */
public class PropertyGraph implements IAdjacencyGraph, Serializable {

    
    static final long serialVersionUID = 2095508565023235623L;
    private String relationName;
    private transient IMatrix core;
    
    public PropertyGraph(String rel,IMatrix toset) {
        this.relationName = rel;
        this.core = toset;
    }
    
    @Override
    public String toString() {
        return relationName;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        // custom serialization
        out.writeUTF(relationName);
        core.save();
    }

    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        relationName = in.readUTF();
        core = new GuavaMatrix(relationName);
    }
    
    public static PropertyGraph create(String rel) {
        return new PropertyGraph(rel,new GuavaMatrix(rel));
    }
    
    /**
     * Create new Layered Graphs
     * @param rel       Name of the relation
     * @param N         If it is zero, returns a mutable graph, otherwise an N fixed graph
     * @return          The relation
     */
    public static PropertyGraph create(String rel, int N) {
        if (N==0)
            return new PropertyGraph(rel,new GuavaMatrix(rel));
        else
            return new PropertyGraph(rel,new SimpleMatrix(N));
    }
    
    
    @Override
    public boolean has(long i, long j) {
        return core.has(i, j);
    }
    
    @Override
    public String getName() {
        return relationName;
    }
    @Override
    public void addEdge(Entity left, Entity right, double value) {
        core.set(left.getIndex(),right.getIndex(),value);
    }

    @Override
    public void addEdge(long left, long right, double value) {
        core.set(left,right,value);
    }
    
    @Override
    public void addEdge(long left, long right) {
        core.set(left,right,1);
    }

    
    @Override
    public void addEdge(Entity left, Entity right) {
        addEdge(left,right,1);
    }

    @Override
    public void removeEdge(Entity left, Entity right) {
        core.rem(left.getIndex(),right.getIndex());
    }

    @Override
    public double getEdge(Entity left, Entity right) {
        return core.get(left.getIndex(),right.getIndex());
    }

    @Override
    public void clear() {
        core.clear();
    }

    @Override
    public long nCols() {
        return core.nCols();
    }

    @Override
    public long nRows() {
        return core.nRows();
    }

    @Override
    public double get(long i, long j) {
        return core.get(i, j);
    }

    @Override
    public double get(Pair<Long,Long> x) {
        return core.get(x);
    }

    @Override
    public void incr(Pair<Long,Long> x, double val) {
        core.incr(x, val);
    }

    @Override
    public void rem(long i, long j) {
        core.rem(i, j);
    }

    @Override
    public void incr(long i, long j, double val) {
        core.incr(i, j,val);
    }

    @Override
    public void set(long i, long j, double val) {
        core.set(i, j, val);
    }

    @Override
    public void set(Pair<Long,Long> x, double val) {
        core.set(x, val);
    }


    @Override
    public void removeRow(long i) {
        core.removeRow(i);
    }

    @Override
    public void removeCol(long j) {
        core.removeCol(j);
    }

    @Override
    public void removeEnt(long elem) {
        core.removeEnt(elem);
    }

    @Override
    public void sum(IMatrix right) {
        core.sum(right);
    }

    @Override
    public void diff(IMatrix right) {
        core.diff(right);
    }

    @Override
    public Set<Long> getOut(long o) {
        return core.getOut(o);
    }

    @Override
    public Set<Long> getIn(long o) {
        return core.getIn(o);
    }

    public IMatrix getCore() {
        return this.core;
    }

    @Override
    public long getMaxKey() {
        return this.core.getMaxKey();
    }

    @Override
    public long getMinKey() {
        return this.core.getMinKey();
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("save command for PropertyGraph: Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IPickIterable<Pair<Long,Long>> getValueRange() {
        return core.getValueRange();
    }
    
    
}
