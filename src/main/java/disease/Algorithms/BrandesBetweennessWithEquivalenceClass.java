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
import disease.utils.IntList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.tweetsmining.model.graph.IMultiRelationGraph;
import org.tweetsmining.model.graph.database.Entity;

/**
 * Strictly related with the SmallOntologyCode
 * @author vasistas
 */
public class BrandesBetweennessWithEquivalenceClass {
    
    private static BrandesBetweennessWithEquivalenceClass self = null;
    private BrandesBetweennessWithEquivalenceClass() {
        
    }
    public static BrandesBetweennessWithEquivalenceClass getInstance() {
        if (self==null)
            self = new BrandesBetweennessWithEquivalenceClass();
        return self;
    }
    
    /**
     * 
     * @param G   Graph containing the whole knowledge
     * @param id_to_supid__equivalenceclass  Defines the equivalence class. The keys of the map are the candidates
     * @return  Scoring the supId/classes
     */
    public Map<Long,Double> run( IMultiRelationGraph G, Map<Long,Long> id_to_supid__equivalenceclass) {
        Queue<Integer> Q = new LinkedList<>();
        Stack<Integer> S = new Stack<>();
        
        Set<Long> ids = id_to_supid__equivalenceclass.keySet();
        if (ids.size()==0)
        {
            System.err.println("Error: empty graph size");
            System.exit(1);
        }
        Long elems[]  = ids.toArray(new Long[ids.size()]);
        Map<Long,Integer> id_to_pos = new TreeMap<>();
        //Map<Long,Double> class_to_size = new TreeMap<>();
        
        int N = elems.length;
        for (int i=0; i<N; i++)
            id_to_pos.put(elems[i], i);
        
        double dist[] = new double[N];
        IntList Pred[] = new IntList[N];
        double sig[] = new double[N];
        double delta[] = new double[N];
        
        double betweenness[] = new double[N];
        for (int i=0; i<N; i++)
            betweenness[i] = 0;
        
        for (int s=0; s<N; s++) {
            //initialization
            if (s%100==0)
                System.out.println(s + "s of "+ N);
            
            for (int j=0; j<N; j++) {
                Pred[j] = new IntList();
                dist[j] = Double.POSITIVE_INFINITY;
                sig[j] = 0;
            }
            dist[s]= 0;
            sig[s] = 1;
            Q.add(s);
            
            //System.out.println("FirstCycle");
            while (!Q.isEmpty()) {
                int v = Q.remove();
                S.push(v);
                //System.out.println("iteringA "+v);
                Set<Entity> k = G.getOutSet(elems[s]);
                //The graph is not symmetric.
                k.addAll(G.getInSet(elems[s]));
                Set<Long> lk = k.stream()
                        .filter((x)->x!=null)
                        .map((x)->{return x.getIndex();})
                        .filter((x)->ids.contains(x))
                        .collect(Collectors.toSet());
                if (lk.isEmpty())
                    System.err.println("WARNING: " + s + " has no in/out set");
                //Selecting only the graph formed by the enlarged candidates
                for (Long out : lk) {
                    //System.out.println("Out og <GetOutSet noew " + out);
                    if (!id_to_pos.containsKey(out))
                        continue;
                    int w = id_to_pos.get(out);
                    if (Double.compare(dist[w], Double.POSITIVE_INFINITY)==0) {
                        dist[w] = dist[v]+1;
                        Q.add(w);
                    }
                    if (Double.compare(dist[w], dist[v]+1)==0) {
                        sig[w] = sig[w]+sig[v];
                        Pred[w].add(v);
                    }
                }
            }
            //System.out.println("MedCycle");
            //accumulation
            for (int k=0; k<N; k++) 
                delta[k]=0;
            while (!S.empty()) {
                
                int w = S.pop();
                //System.out.println("iteringB "+w);
                Pred[w].forEach((v)->{
                    delta[v]=delta[v]+(sig[v])/(sig[w])*(1+delta[w]);
                });
                if (w!=s)
                    betweenness[w] += delta[w];
            }
           // System.out.println("MedCycleOk");
        }
        //System.out.println("FinalCycle");
        //Saving the result per class as average
        Map<Long,Double> result_summed_by_class = new HashMap<>();
        for (int i=0; i<N; i++) {
            double tostore = 0; //value to update
            Long id = elems[i]; //getting the id from the array position
            Long clazz = id_to_supid__equivalenceclass.get(id); //getting the class
            
            if (result_summed_by_class.containsKey(clazz)) {
                tostore = result_summed_by_class.get(clazz);
            } 
            tostore = Math.max(tostore,betweenness[i]);
            
            result_summed_by_class.put(clazz,tostore);
        }
        
        return AMapUtil.sortByValue(result_summed_by_class);
    }
    
    
}
