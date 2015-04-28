/*
 * Copyright (C) 2015 Giacomo Bergami
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

/**
 *
 * @author Giacomo Bergami
 */


import Jama.Matrix;
import disease.utils.datatypes.Pair;
import it.jackbergus.pickstream.PickStream;
import java.util.Optional;

/**
 *
 * @author Giacomo Bergami
 */
public abstract class SimpleMatrixOp {
    
        public static int getRow(Pair<Long,Long> x) {
        if (x==null)
            return -1;
        else
            return x.getFirst().intValue();
    }
    
    public static int getCol(Pair<Long,Long> x) {
        if (x==null)
            return -1;
        else
            return x.getSecond().intValue();
    }
       
    
    
   /**
    * Matrix sum
    * @param left
    * @param right
    * @return
    */
   public static GMatrix sum(IMatrix left, IMatrix right) {
       GMatrix g = new GMatrix();
       PickStream<Pair<Long,Long>> ls = new PickStream<>(left.getValueRange());
       ls.forEach((x)->{g.set(x, left.get(x));});
       
       ls = new PickStream<>(right.getValueRange());
       ls.forEach((x)->{g.incr(x, right.get(x));});
       return g;
   }
   
   /**
    * MAtrix difference
    * @param left
    * @param right
    * @return
    */
   public static GMatrix diff(IMatrix left, IMatrix right) {
       GMatrix g = new GMatrix();
       PickStream<Pair<Long,Long>> ls = new PickStream<>(left.getValueRange());
       ls.forEach((x)->{g.set(x, left.get(x));});
       ls = new PickStream<>(right.getValueRange());
       ls.forEach((x)->{g.incr(x, -right.get(x));});
       return g;
   }
   
   /**
    * Matrix product
    * @param left
    * @param right
    * @return 
    */
   public static GMatrix prod(IMatrix left, IMatrix right) {
       GMatrix g = new GMatrix();
       
       for (Pair<Long, Long> l : new PickStream<>(left.getValueRange())) {
           Long li = l.getSecond();
           for (Pair<Long, Long> r : new PickStream<>(left.getValueRange()).filter((x)->li.equals(x.getFirst()))) {
               //Long ri = r.getFirst();
               //if (li.equals(ri)) {
                   //double lv = (Double)left.get(l);
                   //double rv = (Double)right.get(r);
                   //System.out.println(lv+" "+rv+" "+lv*rv);
                   g.incr(l.getFirst(),r.getSecond(),left.get(l)*right.get(r));
               //}
           };
        };
       return g;
   }
   
   
   /**
    * Matrix divide
    * @param left
    * @param r
    * @return
    */
   public static GMatrix div(IMatrix left, double r) {
       GMatrix g = new GMatrix();
       PickStream<Pair<Long,Long>> k = new PickStream<>(left.getValueRange());
            k.forEach((l)->{
               Long li = l.getFirst();
               Long ri = l.getSecond();
               g.incr(li,ri,(left.get(l)/r));
       });
       return g;
   }
   
     /**
    * Matrix divide
    * @param left
    * @param right
    * @return
    */
   public static GMatrix div(IMatrix left, IMatrix right) {
       GMatrix g = new GMatrix();
       for (Pair<Long, Long> l : new PickStream<>(left.getValueRange())) {
           for (Pair<Long, Long> r : new PickStream<>(right.getValueRange())) {
               Long li = l.getFirst();
               Long ri = l.getSecond();
               if (li.equals(ri)) {
                   double lv = (Double)left.get(l);
                   double rv = (Double)right.get(r);
                   //System.out.println(lv+" "+rv+" "+lv*rv);
                   g.incr(li,ri,lv/rv);
               }
           };
       };
       return g;
   }
   

   /**
    * Matrix transpose
    * @param m
    * @return
    */
   public static GMatrix transpose(IMatrix m)  {
       GMatrix g = new GMatrix();
       PickStream<Pair<Long,Long>> x = new PickStream<>(m.getValueRange());
            x.forEach((k)->{
            Long li = k.getFirst();
           Long ri = k.getSecond();
           g.set(ri, li, m.get(k));
       });
       return g;
   }
   
   public static GMatrix toGMatrix(Matrix m) {
        GMatrix toret = new GMatrix();
        for (int i=0; i<m.getRowDimension(); i++)
            for (int j=0; j<m.getColumnDimension(); j++)
                toret.set(i, j, m.get(i, j));
        return toret;
    }

    public static Matrix toMatrix(IMatrix m) {
        int size = (int)m.getMaxKey();
        Matrix toret = new Matrix(size,size);
        try {
            PickStream<Pair<Long,Long>> k = new PickStream<>(m.getValueRange());
            k.forEach((x)->{
                toret.set(SimpleMatrixOp.getRow(x), SimpleMatrixOp.getCol(x), m.get(x));
            });
            return toret;
        } catch (Throwable ex) {
            return toret;
        }
    }
    
    public static double[] toColumn(IMatrix m) {
        if (m.nCols()==1) {
            double toret [] = new double[(int)m.nRows()];
            PickStream<Pair<Long,Long>> k = new PickStream<>(m.getValueRange());
            k.forEach((x)->{
                toret[x.getFirst().intValue()] = m.get(x);
            });
            return toret;
        } else
            return new double[0];
    }
   
   public static GMatrix stationaryDistribution(IMatrix m) {
       Matrix tmp = toMatrix(m);
       int N = tmp.getColumnDimension();
       Matrix B = tmp.minus(Matrix.identity(N, N));
       for (int j = 0; j < N; j++)
           B.set(0, j, 1.0);
       Matrix b = new Matrix(N, 1);
       b.set(0, 0, 1.0);
       return toGMatrix(B.solve(b));
   }
   
   public static GMatrix diagonal(double... d) {
       GMatrix tmp = new GMatrix();
       for (int i=0; i<d.length; i++)
           tmp.set(i,i, d[i]);
       return tmp;
   }
   
   public static GMatrix diagonal(double val, int size) {
       GMatrix tmp = new GMatrix();
       for (int i=0; i<size; i++)
           tmp.set(i,i, val);
       return tmp;
   }
   
   public static double[] rowSums(IMatrix m) {
       long size = Math.max(m.nCols(), m.nRows());
       double toret[] = new double[(int)size];
       for (int i=0; i<size; i++)
           toret[i]=0;
       for (int i=0; i<size; i++)
           for (int j=0; j<size; j++)
               toret[i] += m.get(i,j);
       return toret;
   }
   
   public static GMatrix regularizedLaplacianMatrix(IMatrix m) {
       GMatrix tmp = SimpleMatrixOp.prod(m, SimpleMatrixOp.transpose(m));
       long size = tmp.nCols();
       GMatrix i = diagonal(1,size);
       GMatrix d = diagonal(rowSums(tmp));
       GMatrix laplacian = new GMatrix();
       for (int ii = 0; ii<size; ii++)
           for (int ji = 0; ji<size; ji++)
               laplacian.set(ii, ji, (d.get(ii, ji)-tmp.get(ii, ji))/ Math.sqrt(d.get(ii, ii)*d.get(ji, ji)));
       return laplacian;
   }
    
}

