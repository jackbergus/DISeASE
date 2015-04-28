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
package disease.Algorithms;

import disease.utils.AMapUtil;
import disease.utils.datatypes.Pair;
import it.jackbergus.pickstream.PickStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.tweetsmining.model.graph.IMultiRelationGraph;
import org.tweetsmining.model.graph.database.Entity;
import org.tweetsmining.model.matrices.GuavaMatrix;
import org.tweetsmining.model.matrices.IMatrix;
import org.tweetsmining.model.matrices.SimpleMatrix;
import org.tweetsmining.model.matrices.SimpleMatrixOp;

/**
 *
 * @author Giacomo Bergami
 */
public class RandomWalkOutlierDetectionWithEquivalenceClass {
    
    private static RandomWalkOutlierDetectionWithEquivalenceClass self = null;
    private RandomWalkOutlierDetectionWithEquivalenceClass() {
        
    }
    public static RandomWalkOutlierDetectionWithEquivalenceClass getInstance() {
        if (self==null)
            self = new RandomWalkOutlierDetectionWithEquivalenceClass();
        return self;
    }
    
    private Pair<Long,Long> convertCoordinatesToMatrix(Long x, Long y, Map<Long,Integer> fromMap) {
        long fst = fromMap.get(x).longValue();
        long snd = fromMap.get(y).longValue();
        //System.out.println("<"+x+","+y+"> --> <"+fst+","+snd+">");
        return new Pair<>(fst,snd);
    }
    
    private Pair<Long,Long> convertCoordinatesToGraph(Pair<Long,Long> to,long[] toMap) {
        long fst = toMap[to.getFirst().intValue()];
        long snd = toMap[to.getSecond().intValue()];
        return new Pair<>(fst,snd);
    }
    
    /**
     * Runs the outlier detection
     * @param G                 Original Graph
     * @param idToClass         Converts the id to the belonging class
     * @param t                 Thereshold value
     * @param e                 Error tolerance
     * @return                  Ranked Outliers - The lower nodes are to be considered outliers
     */
    public Map<Long,Double> run(IMultiRelationGraph G, Map<Long,Long> idToClass, double t, double e) {
        double d = 0.1;
        double c_old[], c[]; //connectivity vectors
        double delta = 0.0;
        double n;
        Long elems[];
        //Map<Long,Integer> id_to_pos = new TreeMap<>();
        int N;
        
        long posToInt[] = new long[idToClass.size()];
        Map<Long,Integer> idToPos2 = new HashMap<>();
        
        Map<Long,Double> r = new HashMap<>();
        IMatrix St;
        {
            Set<Long> ids = idToClass.keySet();
            //Defining the mapping-fast matrix
            SimpleMatrix M = new SimpleMatrix(ids.size()+1);
            //Deifining the mapping from graph db to fast matrix
            { 
                int incrpos = 0;
                for (Long id : ids) {
                    posToInt[incrpos] = id;
                    idToPos2.put(id, incrpos);
                    incrpos++;
                }
            }
            
            for (Long x : ids) {
                for (Entity y : G.getOutSet(x)) {
                    if (y==null)
                        continue;
                    long z = y.getIndex();
                    if (ids.contains(z)) {
                        
                        M.set(convertCoordinatesToMatrix(x,z,idToPos2),G.getEdge(x, "Related", z).getWeight());
                    }
                }
                for (Entity y : G.getInSet(x)) {
                    if (y==null)
                        continue;
                    long z = y.getIndex();
                    if (ids.contains(z)) {
                        M.set(convertCoordinatesToMatrix(x,z,idToPos2),G.getEdge(z, "Related", x).getWeight());
                    }
                }
            }
            
            
            
            //Obtaining the entities over which perform the computation
            
            ///fast access
            elems = ids.toArray(new Long[ids.size()]);
            N = elems.length;
            //Initializing other data structures
            c_old = new double[N];
            c = new double[N];
            n = N;
            for (int i=0; i<N; i++) {
                c_old[i] = (1.0)/(n);
                //id_to_pos.put(elems[i],i);
            }

            //Defining M as the SN matrix
            for (long src=0; src<((long)N); src++) {
                for (long dst=0; dst<((long)N); dst++) {
                    Set<Long> neigh = new TreeSet<>();
                    neigh.addAll(M.getOut(src));
                    neigh.retainAll(M.getOut(dst));
                    M.set(src, dst, neigh.size());
                }
            }
            St = SimpleMatrixOp.transpose(OldMarkovClustering.mclNorm(M));
        } 
        
        
        while (delta<e) {
            if (delta==0)
                System.err.println("Error: delta is zero.. o.O");
            else
                System.out.println("Not zero: "+delta);
            for (int i=0; i<N; i++) { //for each row in St
                Long rowid = elems[i];
                c[i] = 0;
                for (Long colid : St.getOut(rowid)) {
                    //int pos = id_to_pos.get(rowid);
                    c[i] += St.get(rowid,colid) * c_old[rowid.intValue()];
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
            Long idClass = idToClass.get(posToInt[elems[i].intValue()]);
            if (r.containsKey(idClass))
                c[i] = Math.max(c[i],r.get(idClass));
            r.put(idClass,c[i]);
        }
        
        return AMapUtil.sortByValue(r);
    }
    
}
