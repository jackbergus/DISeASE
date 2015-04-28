/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tweetsmining.model.matrices;

import com.blogspot.mydailyjava.guava.cache.jackbergus.CacheMap;
import disease.Algorithms.fwelement.DirectedEdge;
import disease.utils.datatypes.Pair;
import it.jackbergus.pickiterator.PickIterable;
import it.jackbergus.pickiterator.PickIterator;
import it.jackbergus.pickstream.PickStream;
import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.tweetsmining.model.graph.database.cache.CacheBuilder;


/**
 *
 * @author gyankos
 */
public class GuavaMatrix implements IMatrix, Serializable {

    private transient MapMap p;
    /*private long row_min = 0;
    private long row_max = 0;
    private long col_min = 0;
    private long col_max = 0;*/
    private transient CacheMap<Long,Integer> colSet;
    
    public GuavaMatrix() {
        this(CacheBuilder.getnerateTmpFileName());
    }
    
    public GuavaMatrix(String relation) {
        p = new MapMap(relation);
        colSet = CacheBuilder.createMultigraphCacheBuilder(relation+File.separator+"colSet", (l)->Long.parseLong(l));
    }

    /*public GuavaMatrix(IMatrix cpy) {
        this();
        try {
            for (Pair<Long,Long> x : cpy.getValueRange()) {
                set(x, cpy.get(x));
            }
        } catch (Throwable ex) {
            Logger.getLogger(GuavaMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
    
    @Override
    public void clear() {
        p.clear();
    }

    /**
     * Returns the value of cell (i,j)
     *
     * @param i riga
     * @param j colonna
     * @return valore
     */
    @Override
    public double get(long xi, long xj)  {
        Long i = xi;
        Long j = xj;
        /*if (i < row_min || i > row_max || j < col_min || j > col_max) {
            return 0;
        }
        if (!p.contains(i, j)) {
            return 0;
        }*/
        return p.get(i, j);
    }
    
    @Override
    public boolean has(long xi, long xj)  {
        Long i = xi;
        Long j = xj;
        return (p.get(i, j)!=0);
    }

    /**
     * Returns the cell's value
     *
     * @param x Coordinate della cella
     * @return
     */
    @Override
    public double get(Pair<Long,Long> x) {
        return get(x.getFirst(),x.getSecond());
    }

    /**
     * Increments x's cell of val
     *
     * @param x Coordinate della cella
     * @param val Valore
     */
    @Override
    public void incr(Pair<Long,Long> x, double val)  {
        if (val!=0)
            set(x, get(x) + val);
    }

    /**
     * Clears cell (i,j)
     *
     * @param i Roe
     * @param j Column 
     */
    @Override
    public void rem(long xi, long xj)  {
        Long i = xi;
        Long j = xj;
        p.remove(i, j);
        if (colSet.get(i)==1)
            colSet.remove(i);
        else
            colSet.put(i,colSet.get(i)-1);
    }

    /**
     * Increments of val the cell (i,j)
     *
     * @param i Row
     * @param j Column
     * @param val Value
     */
    @Override
    public void incr(long xi, long xj, double val)  {
        Long i = xi;
        Long j = xj;
        if (val!=0)
            set(i, j, get(i, j) + val);
    }

    @Override
    public void set(long xi, long xj, double val)  {
        Long i = xi;
        Long j = xj;
        if (i < 0 || j < 0) {
            return;
        }
        if (val == 0) {
            rem(i, j);
        } else {
            /*if (i < row_min) {
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
            }*/
            p.put(i, j, val);
            if (colSet.get(j)==null)
                colSet.put(j,1);
            else
                colSet.put(j,colSet.get(j)+1);
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
    public PickIterable<Pair<Long,Long>> getValueRange()  {
        return new PickIterable<>(p);
    }

    /**
     * Removes the whole i-th row
     *
     * @param i
     */
    @Override
    public void removeRow(long i) {
        Map<Long, Double> k = null;
        if (i >= 0 && i <= 0) {
            k = p.remove(i);
        }
        /*if (k==null)
            return;
        if (i == row_min) {
            row_min++;
        }
        if (i == row_max) {
            row_max--;
        }*/
    }

    /**
     * Rimuove l'longera colonna j
     *
     * @param j
     */
    @Override
    public void removeCol(long pp) {
        Long j = pp;
        Set<Long> emptyRows = new TreeSet<>();
        Set<Long> tmpKeys = p.keySet();
        tmpKeys.stream().forEach((x) -> {
            p.remove(x, j);
            if (p.get(x) == null || p.get(x).isEmpty())
                emptyRows.add(x);
        });
        emptyRows.parallelStream().forEach((x) -> {
            p.remove(x);
        });
        colSet.remove(j);
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
        PickStream<Pair<Long,Long>> k = new PickStream<>(right.getValueRange());
        try {
            k.forEach((x)->{this.incr(x, right.get(x));});
        } catch (Throwable ex) {
            Logger.getLogger(GuavaMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void diff(IMatrix right) {
        PickStream<Pair<Long,Long>> k = new PickStream<>(right.getValueRange());
        try {
            k.forEach((x)->{this.incr(x, -right.get(x));});
        } catch (Throwable ex) {
            Logger.getLogger(GuavaMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public long nCols() {
        return this.colSet.size();
    }
    
    public Set<Long> getCols() {
        return this.colSet.keySet();
    }

    @Override
    public long nRows() {
        return  p.keySet().size();
    }
    
    public Set<Long> getRows() {
        return p.keySet();
    }
    
    @Override
    public long getMaxKey() {
        return Math.max((long)Collections.max(p.keySet()),(long)Collections.max(this.colSet.keySet()));
    }
    
    @Override
    public long getMinKey() {
        return Math.min((long)Collections.min(p.keySet()),(long)Collections.min(this.colSet.keySet()));
    }

    @Override
    public Set<Long> getOut(long o) {
        Long k = o;
        if (p.get(k)==null)
            return new HashSet<>();
        return p.get(k).keySet();
    }
    
    public List<DirectedEdge> getOutDS(long pp) {
        Long o = pp;
        return getOut(o).stream().map((os)->{
            return new DirectedEdge(o.intValue(),os.intValue(),get(o,os));
        }).collect(Collectors.toList());
    }

    @Override
    public Set<Long> getIn(long z) {
        Long j = z;
        return p.keySet().parallelStream().filter((row)->((p.get(row)==null) ? false : p.get(row).containsKey(j))).collect(Collectors.toSet());
    }

    @Override
    public void save() {
        colSet.persist();
        p.save();
    }

}
