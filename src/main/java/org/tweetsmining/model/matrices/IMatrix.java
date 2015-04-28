/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tweetsmining.model.matrices;


import disease.utils.datatypes.Pair;
import it.jackbergus.pickiterator.IPickIterable;
import java.io.Serializable;
import java.util.Set;


/**
 *
 * @author gyankos
 */
public interface IMatrix extends Serializable {
    
   public long nCols();
   public long nRows();
    
   public void clear();
   public void save();
   
   public boolean has(long i, long j);
   
   public double get(long i, long j);
   /**
    * Returns the cell's value
    * @param x cell coordinates
    * @return 
    */
   public double get(Pair<Long,Long> x);
   
   /**
    * Adds val to the x cell value
    * @param x      Cell coordinates
    * @param val    Value
    * @ 
    */
   public void incr(Pair<Long,Long> x, double val) ;
   
   /**
    * Removes the cell (i,j)s
    * @param i  Row
    * @param j  Column
    */
   public void rem(long i, long j);
   
   /**
    * Add val to the cell (i,j)
    * @param i  row
    * @param j  Column
    * @param val    Value
    * @ 
    */
   public void incr(long i, long j, double val) ;
   
   
   public void set(long i, long j,double val) ;
   
   public void set(Pair<Long,Long> x,double val) ;
   
   /**
    * Removes cells that have non-empty values
    * @return
    * @ 
    */
   public IPickIterable<Pair<Long,Long>> getValueRange() ;
   
   /**
    * Removes the whole row i
    * @param i 
    */
   public void removeRow(long i);
   
   /**
    * Removes the whole column j
    * @param j 
    */
   public void removeCol(long j);
   
   /**
    * Removes both row and columns with the same number
    * @param elem 
    */
   public void removeEnt(long elem);
   
   public void sum(IMatrix right);
   public void diff(IMatrix right);
   
   public Set<Long> getOut(long o);
   public Set<Long> getIn(long o);
   
   public long getMaxKey();
   public long getMinKey();
    
}
