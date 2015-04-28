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
package disease.Algorithms;

import disease.utils.AMapUtil;
import disease.utils.datatypes.Pair;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.tweetsmining.model.graph.IMultiRelationGraph;
import org.tweetsmining.model.graph.database.Entity;
import org.tweetsmining.model.matrices.IMatrix;
import org.tweetsmining.model.matrices.SimpleMatrix;
import org.tweetsmining.model.matrices.SimpleMatrixOp;

/**
 *
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public abstract class OldMultiLayerGraphAlgorithm  {
    
    
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
    
    private long posToInt[];
    private Map<Long,Long> idToClass;
    protected IMatrix M;
    protected IMultiRelationGraph graph;
    
    /**
     * Initializes the algorithms
     * @param G                 Original Graph
     * @param idToClass         Converts the id to the belonging class
     */
    public OldMultiLayerGraphAlgorithm(IMultiRelationGraph G, Map<Long,Long> idToClass, IMatrix toInitialize) {
        
        this.idToClass = idToClass;
        this.M = toInitialize;
        this.graph = G;
        
        //Map<Long,Integer> id_to_pos = new TreeMap<>();
        int N;
        posToInt= new long[idToClass.size()];
        
        Map<Long,Integer> idToPos2 = new HashMap<>();
        
        {
            Set<Long> ids = idToClass.keySet();
            //Defining the mapping-fast matrix
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
            
            
        } 
        
    }
    
    protected abstract void execute(IMultiRelationGraph G, Map<Long,Long> idToClass, IMatrix toInitialize);
    protected abstract Map<Long, Double> getResult(Map<Long,Long> idToClass);
    
    public Map<Long, Double> start() {
        execute(this.graph,this.idToClass,this.M);
        return  AMapUtil.sortByValue(getResult(this.idToClass));
    }
    
    
}
