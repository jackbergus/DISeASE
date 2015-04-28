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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.collections4.map.MultiKeyMap;

/**
 *
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public final class GMatrix implements IMatrix {

    MultiKeyMap<Long,Double> p;
    long row_min = 0;
    long row_max = 0;
    long col_min = 0;
    long col_max = 0;

    public GMatrix() {
        p = MultiKeyMap.multiKeyMap(new LinkedMap());
    }

    public GMatrix(IMatrix cpy) {
        this();
        try {
            for (Pair<Long, Long> x : new PickStream<>(cpy.getValueRange())) {
                set(x, cpy.get(x));
            }
        } catch (Throwable ex) {
            Logger.getLogger(GMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns the value of cell (i,j)
     *
     * @param i riga
     * @param j colonna
     * @return valore
     */
    @Override
    public double get(long i, long j) {
        if (i < row_min || i > row_max || j < col_min || j > col_max) {
            return 0;
        }
        if (!p.containsKey(i, j)) {
            return 0;
        }
        return p.get(i, j);
    }

    /**
     * Returns the cell's value
     *
     * @param x Coordinate della cella
     * @return
     */
    @Override
    public double get(Pair<Long,Long> x) {
        return get(x.getFirst(), x.getSecond());
    }

    /**
     * Increments x's cell of val
     *
     * @param x Coordinate della cella
     * @param val Valore
     */
    @Override
    public void incr(Pair<Long,Long> x, double val)  {
        set(x, get(x) + val);
    }

    /**
     * Clears cell (i,j)
     *
     * @param i Roe
     * @param j Column 
     */
    @Override
    public void rem(long i, long j) {
        p.removeAll(i, j);
    }

    /**
     * Increments of val the cell (i,j)
     *
     * @param i Row
     * @param j Column
     * @param val Value
     */
    @Override
    public void incr(long i, long j, double val) {
        set(i, j, get(i, j) + val);
    }

    @Override
    public void set(long i, long j, double val)  {
        if (i < 0 || j < 0) {
            return;
        }
        if (val == 0) {
            rem(i, j);
        } else {
            if (i < row_min) {
                row_min = i;
            }
            if (i > row_max) {
                row_max = i;
            }
            if (j < col_min) {
                col_min = j;
            }
            if (j > col_max) {
                col_max = j;
            }
            p.put(i, j, val);
        }

    }

    @Override
    public void set(Pair<Long,Long> x, double val)  {
        set(x.getFirst(), x.getSecond(), val);
    }

    /**
     * Returns the non-zero matrix cells
     *
     * @return
     */
    @Override
    public IPickIterable<Pair<Long,Long>> getValueRange()  {
        return new PickIterable<>(p.keySet().parallelStream().map((y)->{
            return new Pair<Long,Long>(y.getKey(0), y.getKey(1));
        }).collect(Collectors.toSet()));
    }

    /**
     * Removes the whole i-th row
     *
     * @param i
     */
    @Override
    public void removeRow(long i) {
        if (i >= row_min && i <= row_max) {
            p.removeAll(i);
        }
        if (i == row_min) {
            row_min++;
        }
        if (i == row_max) {
            row_max--;
        }

    }

    /**
     * Rimuove l'intera colonna j
     *
     * @param j
     */
    @Override
    public void removeCol(long j) {
        if (j >= col_min && j <= col_max) {
            for (long i = row_min; i <= row_max; i++) {
                p.removeAll(i, j);
            }
        }
        if (j == col_min) {
            row_min++;
        }
        if (j == col_max) {
            row_max--;
        }
    }

    /**
     * Rimuove riga e colonna dello stesso numero
     *
     * @param elem
     */
    @Override
    public void removeEnt(long elem) {
        removeRow(elem);
        removeCol(elem);
    }

    @Override
    public void sum(IMatrix right) {
        try {
            PickStream<Pair<Long,Long>> val;
            val = new PickStream<>(this.getValueRange());
            val.contatenate(right.getValueRange());
            
            for (Pair<Long, Long> x:val) {
                this.incr(x, right.get(x));
            }
        } catch (Throwable ex) {
            Logger.getLogger(GMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void diff(IMatrix right) {
        try {
            PickStream<Pair<Long,Long>> val;
            val = new PickStream<>(this.getValueRange());
            val.contatenate(right.getValueRange());
            for (Pair<Long, Long> x:val) {
                this.incr(x, -right.get(x));
            }
        } catch (Throwable ex) {
            Logger.getLogger(GMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public long nCols() {
        return col_max+1;
    }

    @Override
    public long nRows() {
        return row_max+1;
    }

    @Override
    public void clear() {
        p.clear();
    }

    @Override
    public void save() {    }

    @Override
    public boolean has(long i, long j) {
        return (get(i,j)!=0);
    }

    @Override
    public Set<Long> getOut(long o) {
        //Not very efficient
        return p.keySet().parallelStream().filter((x)->Long.compare(x.getKey(0), o)==0).map((MultiKey<? extends Long> x) -> {
            return (Long)x.getKey(1);
        }).collect(Collectors.toSet());
    }
    

    @Override
    public Set<Long> getIn(long o) {
        //Not very efficient
        return p.keySet().parallelStream().filter((x)->Long.compare(x.getKey(1), o)==0).map((MultiKey<? extends Long> x) -> {
            return (Long)x.getKey(0);
        }).collect(Collectors.toSet());
    }

    @Override
    public long getMaxKey() {
        return Math.max(row_max,col_max);
    }

    @Override
    public long getMinKey() {
        return Math.max(row_min,col_min);
    }

    public int size() {
        return p.size();
    }

}
