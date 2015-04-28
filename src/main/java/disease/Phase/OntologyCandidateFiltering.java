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
package disease.Phase;

import disease.Algorithms.FloydWarshall;
import disease.Algorithms.MarkovClustering;
import disease.Algorithms.RandomWalkOutlierDetection;
import disease.datatypes.ConcreteMapIterator;
import disease.utils.datatypes.Pair;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.tweetsmining.model.graph.MultiLayerGraph;
import org.tweetsmining.model.matrices.GuavaMatrix;
import org.tweetsmining.model.matrices.IMatrix;

/**
 *
 * @author vasistas
 */
@Deprecated
public class OntologyCandidateFiltering {
    
    /**
     * Given an ontology, performs the FW algorithm over the graph, and creates a
     * full connected graph. To be done in the training phase
     * @param ontology          Original ontology
     * @param relation_layer    Relations Layer
     * @return 
     */
    public static FloydWarshall OntologyTraining(MultiLayerGraph ontology,String relation_layer) {
        FloydWarshall multisource_leastpath = FloydWarshall.getInstance();
        multisource_leastpath.executealgorithm(ontology.getRelationMatrix(relation_layer));
        return multisource_leastpath;
    }
    
    
    /**
     * 
     * @param FWontology                  Trained ontology
     * @param relation_layer              IDs of the element to chose in FW;
     * @param outlier_alg_thereshold
     * @param outlier_alg_tolerance
     * @param outlier_k_thereshold
     * @param mcl_inflation
     * @param mcl_iterations
     * @return 
     */
    public static Collection<LinkedList<Long>> FWOntologyFiltering
        (GuavaMatrix FWontology, 
            Collection<Long> candidate_ids,
            String relation_layer, 
            double outlier_alg_thereshold, 
            double outlier_alg_tolerance, 
            double outlier_k_thereshold,
            int mcl_inflation,
            int mcl_iterations) {
        
            
        /*candidate_ids.forEach((x)->{FWontology.removeEnt(x);});
        RandomWalkOutlierDetection od = RandomWalkOutlierDetection.getInstance();
        Map<Long, Double> l = od.run(FWontology, outlier_alg_thereshold, outlier_alg_tolerance);
        Set<Long> toremove = new TreeSet<>();
        for (Pair<Long,Double> p : new ConcreteMapIterator<>(l)) {
            if (p.getSecond()<outlier_k_thereshold) {
                toremove.add(p.getFirst());
            }
        }
        toremove.forEach((x)->{FWontology.removeEnt(x);});
        return MarkovClustering.collectMCLFeatures(MarkovClustering.mcl(FWontology, mcl_inflation, mcl_iterations));
    */
            return null;
                }
        
    
    
}
