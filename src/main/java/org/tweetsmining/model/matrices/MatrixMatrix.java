/*
 * Copyright (C) 2015 Giacomo Bergami <giacomo@openmailbox.org>
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

import disease.utils.datatypes.Pair;
import it.jackbergus.pickstream.PickStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.SparseMatrix;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.function.VectorFunction;

/**
 *
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public class MatrixMatrix  {

    private Matrix f;
    private int M;
    
    public MatrixMatrix(int max) {
        //core = matr;
        M = max;
        f = new SparseMatrix(max+1,max+1);
    }
    
    public MatrixMatrix times(MatrixMatrix m) {
        return new MatrixMatrix(f.times(m.getCore()));
    }
    
    public int size() {
        return new Double(f.aggregateRows((vector) -> vector.getNumNonZeroElements()).zSum()).intValue();
    }
    
    public MatrixMatrix(Matrix f) {
        //core = matr;
        M = Math.max(f.numCols(),f.numRows());
        this.f = f;
    }
    
    public void clear() {
        f = new SparseMatrix(M+1,M+1);
    }
    
    public boolean has(long i, long j) {
        return (f.getQuick((int)i, (int)j)!=0);
    }
    
    public long nCols() {
        return f.numCols();
    }


    public long nRows() {
        return f.numCols();
    }

    public double get(long i, long j) {
        return f.getQuick((int)i, (int)j);
    }

    public double get(Pair<Long, Long> x) {
        return get(x.getFirst(),x.getSecond());
    }

    public void incr(Pair<Long, Long> x, double val) {
        incr(x.getFirst(),x.getSecond(),val);
    }

    public void rem(long i, long j) {
        f.setQuick((int)i, (int)j, 0);
    }

    public void incr(long i, long j, double val) {
        f.setQuick((int)i, (int)j, get(i,j)+val);
    }
    public void set(long i, long j, double val) {
        f.setQuick((int)i, (int)j, val);
    }

    public void set(Pair<Long, Long> x, double val) {
        set(x.getFirst(),x.getSecond(),val);
    }

    public void removeRow(long i) {
        f.set((int)i, new double[M+1]);
    }

    public void removeCol(long j) {
        f.assignColumn((int)j, new RandomAccessSparseVector(M+1));
    }

    public void removeEnt(long elem) {
        removeCol(elem);
        removeRow(elem);
    }

    public void sum(MatrixMatrix right) {
        f = f.plus(right.f);
    }

    public void diff(MatrixMatrix right) {
        f = f.minus(right.f);
    }

    public Set<Long> getOut(long o) {
        Set<Long> toret = new HashSet<>();
        int i = (int)o;
        for (int j=0; j<=M; j++) {
            if (get(i,j)!=0)
                toret.add((long)i);
        }
        return toret;
    }

    public Set<Long> getIn(long o) {
        Set<Long> toret = new HashSet<>();
        int j = (int)o;
        for (int i=0; i<=M; i++) {
            if (get(i,j)!=0)
                toret.add((long)i);
        }
        return toret;
    }

    public Matrix getCore() {
        return this.f;
    }


    public long getMaxKey() {
        return M;
    }

    public long getMinKey() {
        return 0;
    }

    public void save() {
        //No persistency
    }
    
    public int rowSize() {
        return new Double(f.aggregateRows((v)->(v.nonZeroes().iterator().hasNext()?1:0)).zSum()).intValue();
    }
    
    public int colSize() {
        return new Double(f.aggregateColumns((v)->(v.nonZeroes().iterator().hasNext()?1:0)).zSum()).intValue();
    }
    
    private class MatrixIterator implements Iterator<Pair<Long,Long>> {
        private final Iterator<Vector.Element> main;
        int currentRow;
        private Iterator<Vector.Element> it;

        public MatrixIterator() {
            main =f.aggregateRows((v)->(v.nonZeroes().iterator().hasNext()?1:0)).nonZeroes().iterator();
            if (main.hasNext()) {
                currentRow = main.next().index();
                it = f.viewRow(currentRow).nonZeroes().iterator();
            } else {
                currentRow =-1;
                it = null;
            }
        }
        
        @Override
        public boolean hasNext() {
            return (it!=null && (main.hasNext() || it.hasNext()));
        }

        @Override
        public Pair<Long, Long> next() {
            if (!hasNext())
                return null;
            else {
                if (it!=null && it.hasNext()) 
                    return new Pair<>((long)currentRow,(long)it.next().index());
                else {
                    if (main.hasNext()) {
                        currentRow = main.next().index();
                        it = f.viewRow(currentRow).nonZeroes().iterator();
                        return new Pair<>((long)currentRow,(long)it.next().index());
                    } else {
                        currentRow =-1;
                        it = null;
                    }
                }
            }
            System.err.println("Error: this event should never happen");
            return null;
        }
        
    }
    
    private class MatrixIterable implements Iterable<Pair<Long,Long>> {

        @Override
        public Iterator<Pair<Long, Long>> iterator() {
            return new MatrixIterator();
        }
        
    }
    
    public PickStream<Pair<Long,Long>> getValueRange() {
        return new PickStream<>(new MatrixIterable());
    }
    
}
