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
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.tweetsmining.model.graph.IMultiRelationGraph;
import org.tweetsmining.model.graph.database.Entity;
import org.tweetsmining.model.graph.database.logical.ERInterfaceLayer;

/**
 *
 * @author vasistas
 */
public class BrandesBetweenness {
    
    private static BrandesBetweenness self = null;
    private BrandesBetweenness() {
        
    }
    public static BrandesBetweenness getInstance() {
        if (self==null)
            self = new BrandesBetweenness();
        return self;
    }
    
    public Map<Long,Double> run( IMultiRelationGraph G) {
        Queue<Integer> Q = new LinkedList<>();
        Stack<Integer> S = new Stack<>();
        Set<Long> ids = G.getIds();
        if (ids.size()==0)
        {
            System.err.println("Error: empty graph size");
            System.exit(1);
        }
        Long elems[]  = ids.toArray(new Long[ids.size()]);
        Map<Long,Integer> id_to_pos = new TreeMap<>();
        
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
            System.out.println(s + " of "+ N);
            
            for (int j=0; j<N; j++) {
                Pred[j] = new IntList();
                dist[j] = Double.POSITIVE_INFINITY;
                sig[j] = 0;
            }
            dist[s]= 0;
            sig[s] = 1;
            Q.add(s);
            
            System.out.println("FirstCycle");
            while (!Q.isEmpty()) {
                int v = Q.remove();
                S.push(v);
                System.out.println("iteringA "+v);
                Set<Entity> k = G.getOutSet(elems[s]);
                System.out.println("Out og <GetOutSet noew");
                for (Long out : k.stream().map((x)->{return x.getIndex();}).collect(Collectors.toSet())) {
                    int w = id_to_pos.get(out);
                    if (dist[w]==Double.POSITIVE_INFINITY) {
                        dist[w] = dist[v]+1;
                        Q.add(w);
                    }
                    if (dist[w]==dist[v]+1) {
                        sig[w] = sig[w]+sig[v];
                        Pred[w].add(v);
                    }
                }
            }
            
            //accumulation
            for (int k=0; k<N; k++) 
                delta[k]=0;
            while (!S.empty()) {
                
                int w = S.pop();
                System.out.println("iteringB "+w);
                Pred[w].forEach((v)->{
                    delta[v]=delta[v]+(sig[v])/(sig[w])*(1+delta[w]);
                });
                if (w!=s)
                    betweenness[w] += delta[w];
            }
        }
        
        Map<Long,Double> toret = new TreeMap<>();
        for (int i=0; i<N; i++)
            toret.put(elems[i],betweenness[i]);
        return AMapUtil.sortByValue(toret);
    }
    
    
}
