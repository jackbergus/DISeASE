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
import it.jackbergus.pickiterator.IPickIterable;
import it.jackbergus.pickiterator.PickIterable;
import it.jackbergus.pickstream.PickStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public class SimpleMatrix implements IMatrix {

    private double[][] core;
    private int M;
    
    public SimpleMatrix(int max) {
        //core = matr;
        M = max;
        core = new double[max+1][max+1];
    }
    
    public SimpleMatrix(int max, double[][] M) {
        //core = matr;
        this.M = max;
        core = M;
    }
    
    @Override
    public void clear() {
        for (int i=0; i<M; i++)
            for (int j=0; j<M; j++)
                core[i][j] = 0;
    }
    
    public boolean has(long i, long j) {
        return (core[(int)i][(int)j]!=0);
    }
    
    @Override
    public long nCols() {
        return M;
    }

    @Override
    public long nRows() {
        return M;
    }

    @Override
    public double get(long i, long j) {
        return core[(int)i][(int)j];
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
        core[(int)i][(int)j] = 0;
    }

    @Override
    public void incr(long i, long j, double val) {
        core[(int)i][(int)j] += val;
    }
    

    @Override
    public void set(long i, long j, double val) {
        core[(int)i][(int)j] = val;
    }

    @Override
    public void set(Pair<Long, Long> x, double val) {
        core[x.getFirst().intValue()][x.getSecond().intValue()] = val;
    }

    @Override
    public IPickIterable<Pair<Long, Long>> getValueRange() {
        Set<Pair<Long,Long>> toret = new HashSet<>();
        for (int i=0; i<=M; i++)
            for (int j=0; j<=M; j++)
                if (core[i][j]>0)
                    toret.add((new Pair<>((long)i,(long)j)));
        return new PickIterable<>(toret);
    }

    @Override
    public void removeRow(long i) {
        for (int j=0; j<=M; j++)
            core[(int)i][j] = 0;
    }

    @Override
    public void removeCol(long j) {
        for (int i=0; i<=M; i++)
            core[i][(int)j] = 0;
    }

    @Override
    public void removeEnt(long elem) {
        removeCol(elem);
        removeRow(elem);
    }

    @Override
    public void sum(IMatrix right) {
        if (((int)right.getMaxKey())>M)
            throw new IndexOutOfBoundsException("The incoming right matrix is bigger than the immutable one");
        PickStream<Pair<Long,Long>> k = new PickStream<>(right.getValueRange());
        k.forEach((p)->{this.incr(p, right.get(p));});
    }

    @Override
    public void diff(IMatrix right) {
        if (((int)right.getMaxKey())>M)
            throw new IndexOutOfBoundsException("The incoming right matrix is bigger than the immutable one");
        
        PickStream<Pair<Long,Long>> k = new PickStream<>(right.getValueRange());
        k.forEach((p)->{this.incr(p, -right.get(p));});
    }

    @Override
    public Set<Long> getOut(long o) {
        Set<Long> toret = new HashSet<>();
        int i = (int)o;
        for (int j=0; j<=M; j++) {
            if (core[i][j]!=0)
                toret.add((long)i);
        }
        return toret;
    }

    @Override
    public Set<Long> getIn(long o) {
        Set<Long> toret = new HashSet<>();
        int j = (int)o;
        for (int i=0; i<=M; i++) {
            if (core[i][j]!=0)
                toret.add((long)i);
        }
        return toret;
    }

    public double[][] getCore() {
        return this.core;
    }

    @Override
    public long getMaxKey() {
        return M;
    }

    @Override
    public long getMinKey() {
        return 0;
    }

    @Override
    public void save() {
        //No persistency
    }
    
}
