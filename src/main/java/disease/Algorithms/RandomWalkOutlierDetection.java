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
package disease.Algorithms;

import disease.utils.AMapUtil;
import it.jackbergus.pickstream.PickStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.tweetsmining.model.matrices.GuavaMatrix;
import org.tweetsmining.model.matrices.IMatrix;
import org.tweetsmining.model.matrices.SimpleMatrixOp;

/**
 *
 * @author vasistas
 */
public class RandomWalkOutlierDetection {
    
    private static RandomWalkOutlierDetection self = null;
    private RandomWalkOutlierDetection() {
        
    }
    public static RandomWalkOutlierDetection getInstance() {
        if (self==null)
            self = new RandomWalkOutlierDetection();
        
        return self;
    }
    
    /**
     * Runs the outlier detection
     * @param m Original Graph Matrix
     * @param t Thereshold value
     * @param e Error tolerance
     * @return  Ranked Outliers - The lower nodes are to be considered outliers
     */
    public Map<Long,Double> run(IMatrix m, double t, double e) {
        double d = 0.1;
        double c_old[], c[]; //connectivity vectors
        double delta = 0.0;
        double n;
        Long elems[];
        Map<Long,Integer> id_to_pos = new TreeMap<>();
        int N;
        
        Map<Long,Double> r = new HashMap<>();
        IMatrix St;
        {
            GuavaMatrix M = new GuavaMatrix();
            new PickStream<>(m.getValueRange()).forEach((p)->{
                if (m.get(p)>=t)
                    M.set(p, 1);
            });
            //Obtaining the entities over which perform the computation
            Set<Long> ids = new TreeSet<>();
            ids.addAll(M.getCols());
            ids.addAll(M.getRows());
            ///fast access
            elems = ids.toArray(new Long[ids.size()]);
            N = elems.length;
            //Initializing other data structures
            c_old = new double[N];
            c = new double[N];
            n = N;
            for (int i=0; i<N; i++) {
                c_old[i] = (1.0)/(n);
                id_to_pos.put(elems[i],i);
            }

            //Defining M as the SN matrix
            for (Long src : elems) {
                for (Long dst : elems) {
                    Set<Long> neigh = new TreeSet<>();
                    neigh.addAll(M.getOut(src));
                    neigh.retainAll(M.getOut(dst));
                    M.set(src, dst, neigh.size());
                }
            }
            St = SimpleMatrixOp.transpose(OldMarkovClustering.mclNorm(M));
        } 
        
        while (delta<e) {
            for (int i=0; i<N; i++) { //for each row in St
                Long rowid = elems[i];
                c[i] = 0;
                for (Long colid : St.getOut(rowid)) {
                    int pos = id_to_pos.get(rowid);
                    c[i] += St.get(rowid,colid) * c_old[pos];
                }
                c[i] = (d/n) + (1-d)*c[i];
            }
            delta = 0;
            for (int i =0; i<c_old.length; i++)
                delta += Math.pow(c[i]-c_old[i],2);
            delta = Math.sqrt(delta);
            c_old = c;
        }
        
        for (int i=0; i<N; i++) {
            r.put(elems[i],c[i]);
        }
        
        return AMapUtil.sortByValue(r);
    }
    
}
