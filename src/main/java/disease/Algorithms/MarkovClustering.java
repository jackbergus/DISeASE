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

/**
 *
 * @author vasistas
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import disease.utils.datatypes.Pair;
import it.jackbergus.pickstream.PickStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.mahout.math.Matrix;
import org.tweetsmining.model.graph.IMultiRelationGraph;
import org.tweetsmining.model.matrices.MatrixMatrix;

/**
 *
 * @author Mattia Trombon
 */
public class MarkovClustering extends MultiLayerGraphAlgorithm {

    /**
     * Matrix inflation approximated as a matrix product
     * @param m     matrix  
     * @param inf   Inflation product
     * @return 
     */
    public static MatrixMatrix mclInflate (MatrixMatrix m, int inf){
        int i;
        Matrix tmp = m.getCore();
        Matrix mm = m.getCore();
        for(i=1;i<=inf;i++){
            tmp = tmp.times(mm);
        }
        return new MatrixMatrix(tmp);
    }

    /**
     * Matrix normalization
     * @param m
     * @return 
     */
    public static MatrixMatrix mclNorm (MatrixMatrix m) {
        int columSum = 0;
        Matrix mc = m.getCore();
        mc = mc.transpose();
        mc = mc.divide(columSum);
        return new MatrixMatrix(mc.transpose());
    }

    /**
     * Performs the matrix clustering
     * @param m         Matrix 
     * @param inf       Inflate parameter
     * @param iter      Iteration parameter
     * @return          The clustered matrix
     */
    public static MatrixMatrix mcl(MatrixMatrix m,int inf, int iter) {
        int i;
        MatrixMatrix oldm;
        MatrixMatrix mNorm;
        for(i=1;i<=iter;i++){
            oldm = m;
            
            mNorm = mclNorm(m);
            m = mNorm.times(mNorm);
            m = mclInflate(m, inf);
            m = mclNorm(m);
            
            //obtaining the common valus
            
            int oldm_size = oldm.size();
            
            long row = Math.max(m.colSize(), oldm.colSize());
            long col = Math.max(m.rowSize(), oldm.rowSize());
            final MatrixMatrix finalm = m;
            
            
            if (oldm.size()==m.size() && oldm.size()==(row*col))
            {
                int count = 0;
                
                for (Pair<Long, Long> x : new PickStream<>(oldm.getValueRange()).filter((x)->(finalm.get(x)!=0.0)).collect(Collectors.toList())) {
                    if (oldm.get(x.getFirst().intValue(),x.getSecond().intValue())==m.get(x.getFirst().intValue(),x.getSecond().intValue()))
                        count += 1;
                }
                //break through ~ stationary matrix reached
                if (count == row*col)
                    return m;
            }
            
        }
        return m;
    }


    /**
     * Given a matrix over which we have performed MCL, we want to return the set of clusters
     * @param m
     * @return 
     */
    public static Collection<LinkedList<Long>> collectMCLFeatures (MatrixMatrix m){
        //Maps each cluster on the i-th row into a list of elements 
        HashMap<Long,LinkedList<Long>> hm = new HashMap<>();
        
        //Obtaining the clusters only for the non-empty rows
        new PickStream<>(m.getValueRange()).forEach((ij) -> {
            Long right = ij.getSecond();
            Long left = ij.getFirst();
            //System.out.println(ij.getKey(0)+"~"+ij.getKey(1));
            if (!hm.containsKey(left))
                hm.put(left, new LinkedList<>());
            hm.get(left).add(right);
        });

        //Obtaining the clusters only
        return hm.values();
    }
    private final int inf;
    private final int iter;

    public MarkovClustering(IMultiRelationGraph G, Map<Long, Long> idToClass, MatrixMatrix toInitialize, int inf, int iter) {
        super(G, idToClass, toInitialize);
        this.inf = inf;
        this.iter = iter;
    }

    protected void execute(IMultiRelationGraph G, Map<Long, Long> idToClass, MatrixMatrix toInitialize) {
        super.M = mcl(toInitialize,inf, iter);
    }

    @Override
    protected Map<Long, Double> getResult(Map<Long, Long> idToClass) {
        Collection<LinkedList<Long>> clusters = collectMCLFeatures(super.M);
        Map<Long,Double> classCount = new HashMap<>();
        Map<Long,Double> classRank = new HashMap<>();
        
        for (LinkedList<Long> cluster : clusters) {
            
            //Ranking the cluster
            double countClusterEdges = 0;
            double aggr = 0;
            Set<Long> getClasses = new HashSet<>();
            
            //Evaluates the weight of the edges in the cluster
            for (Long src : cluster) {
                for (Long dst: cluster) {
                    if (Long.compare(dst, src)==0)
                        continue;
                    double val = super.graph.getEdge(src, "Relation", dst).getWeight();
                    val = Math.max(val, super.graph.getEdge(dst, "Relation", src).getWeight());
                    if (val>0) {
                        countClusterEdges+=1.0;
                        aggr+=val;
                    }
                }
                getClasses.add(idToClass.get(src));
            }
            aggr = aggr / ((double)getClasses.size());
            
            //Updating the class weights
            for (Long clazz : getClasses) {
                double count = 0;
                double value = 0;
                if (classCount.containsKey(clazz)) {
                    count = classCount.get(clazz);
                    value = classRank.get(clazz);
                }
                classCount.put(clazz, count+1);
                classRank.put(clazz, value+aggr);
            }
            
        }
        
        //Averaging the results
        for (Long key : classRank.keySet()) {
            double count = classCount.get(key);
            double range = classRank.get(key);
            classRank.put(key, range/count);
        }
        
        return classRank;
    }
}
