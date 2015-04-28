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
package disease.Dataset.DataIntegration;

import disease.Dataset.DataIntegration.SmallOntologyCode;
import disease.Algorithms.FloydWarshall;
//import disease.Algorithms.RandomWalkOutlierDetectionWithEquivalenceClass;
import disease.Dataset.Real.ICD9CMDictionary;
import disease.Dataset.Real.ICD9CMTable;
import disease.datatypes.ConcreteMapIterator;
import disease.datatypes.serializabletree.Tree;
import disease.datatypes.serializabletree.TreeNode;
import disease.ontologies.ICD9CMCode;
import disease.utils.datatypes.Pair;
import it.jackbergus.pickstream.PickStream;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.ArrayList;
import org.tweetsmining.model.graph.MultiLayerGraph;
import org.tweetsmining.model.graph.database.Entity;
import org.tweetsmining.model.graph.database.Relation;
import org.tweetsmining.model.graph.database.eQuery.relation.RDFQuery;

/**
 * This Ontology uses both ICD9CMDictionary and ICD9CMTable
 * @author vasistas
 */
public class SmallOntology {
    
    private MultiLayerGraph db;
    private double[][] M;
    //Non persistere
    
    private final static String RELATION = "Related";
    private static SmallOntology self;
    private SmallOntology() {
        this.code_to_long = new TreeMap<>();
        db = new MultiLayerGraph("data"+File.separator+"smallOntology.g",0);
        if (!db.getIds().isEmpty()) {
            System.out.println("Initializing code-to-id conversion");
            for (Entity x : db.getEntities()) {
                String fullCode = ((SmallOntologyCode)x).get().getCode();
                code_to_long.put(fullCode,x.getIndex());
            }
        }
    }
    
    public void saveAll() {
        db.save();
    }
    
    
    public static SmallOntology getInstance() {
        if (self==null) {
            self = new SmallOntology(); //not initialized instance
        }
        return self;
    }
    
    private double maxDouble = 0;
    
    private void addEdge(long src, long dst) {
        Relation r = db.getEdge(src, RELATION, dst);
        double w = (r==null ? 0 : r.getWeight());
        if (w==0) {
            w+=1;
            db.addEdge(src, RELATION, dst);
        } else {
            w+=1.0;
            db.addEdge(src, RELATION, dst, w);
        }
        if (maxDouble < w)
            maxDouble = w;
    }
    
    /**
     * Convert the strength of the edge as the less-distant edge
     */
    private void doFWNormalization() {
        System.out.println("Updating the weights");
       
        for (Relation r : db)  {
            double tostore = (maxDouble+1.0-r.getWeight())/maxDouble;
            db.addEdge(r.getSource().getIndex(), r.getName(), r.getDestination().getIndex(), tostore);
        }
       
       
       FloydWarshall fw = FloydWarshall.getInstance();
       System.out.println("Running the algorithm...");
       long min  = db.getRelationMatrix(RELATION).getMinKey();
       long max = db.getRelationMatrix(RELATION).getMaxKey()+1;
       double[][] M = fw.executealgorithm(db.getRelationMatrix(RELATION));
        
       
       //updateGraph(fw.run(db.getRelationMatrix(RELATION)),mi,iM);
       System.out.println("Getting the max");
       maxDouble = Double.NEGATIVE_INFINITY;
        for (int i=(int)min; i<(int)max; i++) {
            System.out.println("scanning "+i+" out of s"+ max);
            for (int j=(int)min; j<(int)max; j++) {
                double r=M[i][j];
                if (maxDouble<r&&r!=Double.NEGATIVE_INFINITY)
                    maxDouble = r;
            }
        }
        
        db.clear();
        System.out.println("Replacing values");
        for (int i=(int)min; i<(int)max; i++) {
            System.out.println("scanning "+i+" out of s"+ max);
            for (int j=(int)min; j<(int)max; j++) {
                double r=M[i][j];
                if (maxDouble<r&&r!=Double.NEGATIVE_INFINITY)
                    db.addEdge(i,RELATION,j,(maxDouble+1.0-r)/maxDouble);
                        //M[i][j] = (maxDouble+1.0-r)/maxDouble;
            }
        }
        
        System.out.println("Replacing with the current matrix");
        //db.release_and_update(RELATION,M,(int)max);
        System.out.println("Clearing... and then last store");
        db.save();
        
        //db.update(RELATION, fw.getResult(), max);
       
        
    }
    
    Map<String,Long> code_to_long;
    
    public static void main(String s[]){
        SmallOntology.getInstance().do_init();
    }
    
    /*
    This procedure will be called iff. the database is empty
    */
    public void do_init() {
        {
            //int count = 0;
        ICD9CMTable table = ICD9CMTable.init();
        Tree<String,ICD9CMCode> t = ICD9CMDictionary.getTree();
         //fast search
        System.out.println("Adding Table part");
        //int max = 19000; //to be sure - a priori known value ~= 13787
        System.out.println(table.size());
        
        for (Pair<ICD9CMCode, String> p : table) {
            //count++;
            //System.out.println(count+"c");
            ICD9CMCode c = p.getFirst();
            Long l = code_to_long.get(c.toString());
            if (l==null) {
                Entity e = db.createNewEntity(SmallOntologyCode.class, c);
                
                l = e.getIndex();
                code_to_long.put(c.toString(), l);
            }
            if (!c.hasOnlyThreeDigits()) {
                ICD9CMCode father = c.getThreeDigitsFather();
                Long lf = code_to_long.get(father.toString());
                if (lf==null) {
                    Entity e = db.createNewEntity(SmallOntologyCode.class, c);
                    lf = e.getIndex();
                    code_to_long.put(father.toString(), lf);
                }
                //Candidates have a fine-graned specification. We want to
                //generalize it to the father, so we get only the upgoing class
                //addEdge(l, lf);
                addEdge(lf,l);
            } else
                //Self loop on the father node: we want to make if the center of the cluster
                addEdge(l, l); 
        }
        System.out.println("Adding Tree part");
        //System.out.println(code_to_long.size());
        //System.exit(0);

        
        
        Iterator<TreeNode<String, ICD9CMCode>> it = t.iterator("condition");
        //int count=0;
        
        while (it.hasNext()) {
            //count++;
            TreeNode<String,ICD9CMCode> n = it.next();
            if (n.isRoot()) //skipping the root node
                continue;
            ICD9CMCode val = n.getValue();
            Long id = code_to_long.get(val.toString());
            if (id==null) {
                if (val.isEmpty()) continue; //not adding non-codes
                System.err.println(val+" is not here");
                //System.err.println((count)+" ");
                continue;
            }
            //In this case we follow the specification chain for a more precise result
            if (t.getParent(n).hasFather()) {
                long i = id;
                //getting only non-empty father nodes
                TreeNode<String, ICD9CMCode> fn = n;
                TreeNode<String, ICD9CMCode> prev = n;
                do {
                    prev = fn;
                    fn= t.getParent(fn);
                } while (fn!=null && fn.getValue().isEmpty()&&fn.hasFather()&&(!fn.equals(prev)));
                if (fn==null||fn.isRoot()||fn.equals(prev)) continue; //element has no father with code
                Long father_id = code_to_long.get(fn.getValue().toString());
                if (father_id==null) {
                    System.err.println(fn.getValue()+" father is not here");
                    continue;
                }
                long f = father_id;
                addEdge(f,i);
            }
            //Stating that this is a final specification class
            if (n.getChildren().isEmpty()) {
                try {
                    long toset = id;
                    addEdge(toset,toset);
                } catch (Throwable e) {
                    System.err.println("Suspected error:");
                    e.printStackTrace();
                }
            }
        }
        ICD9CMTable.release();
        }
        
        //The Floyd.Warshall algorithms considers the weights as distances and
        //not as edge strengths. We have to invert the value of the nodes
        
        System.out.println("Calculating minimum distances");
        ///this.doFWNormalization();
        
        System.out.println("Updating the weights");
        
        for (Relation r : db)  {
            double tostore = (maxDouble+1.0-r.getWeight())/maxDouble;
            db.addEdge(r.getSource().getIndex(), r.getName(), r.getDestination().getIndex(), tostore);
        }
       
       
       FloydWarshall fw = FloydWarshall.getInstance();
       System.out.println("Running the algorithm...");
       long min  = db.getRelationMatrix(RELATION).getMinKey();
       long max = db.getRelationMatrix(RELATION).getMaxKey()+1;
       M = fw.executealgorithm(db.getRelationMatrix(RELATION));
        
       
       //updateGraph(fw.run(db.getRelationMatrix(RELATION)),mi,iM);
       System.out.println("Getting the max");
       maxDouble = Double.NEGATIVE_INFINITY;
        for (int i=(int)min; i<(int)max; i++) {
            if (i%1000==0)
            System.out.println("scanning "+i+" out of s"+ max);
            for (int j=(int)min; j<(int)max; j++) {
                double r=M[i][j];
                if (maxDouble<r&&r!=Double.POSITIVE_INFINITY)
                    maxDouble = r;
            }
        }
        
        //db.clear();
        System.out.println("Replacing values");
        for (int i=(int)min; i<(int)max; i++) {
            if (i%1000==0)
            System.out.println("scanning "+i+" out of s"+ max);
            for (int j=(int)min; j<(int)max; j++) {
                double r=M[i][j];
                if (r!=Double.POSITIVE_INFINITY)
                    //M[i][j] = (maxDouble+1.0-r)/maxDouble;
                    db.addEdge(i,RELATION,j,(maxDouble+1.0-r)/maxDouble);
                        //M[i][j] = (maxDouble+1.0-r)/maxDouble;
            }
        }
        
        //db.release_and_update(RELATION, M, (int)max+1);
        System.out.println("Replacing with the current matrix");
        System.out.println("Clearing... and then last store");
        db.save();
        
        System.out.println("Stored.");
    }
    
    public double getWeight(String icd9_src, String icd9_dst) {
        Long idx_src = this.code_to_long.get(icd9_src);
        if (idx_src == null)
            return 0;
        Long idx_dst = this.code_to_long.get(icd9_dst);
        if (idx_dst == null)
            return 0;
        return db.getEdge(idx_src, RELATION, idx_dst).getWeight();
    }
    
    private RDFQuery rdfAll() {
        return new RDFQuery(db.entityAll(),db.relationAll(),db.entityAll());
    }
    
    /**
     * Creates a graph view over the selected vertices
     * @param all
     * @return 
     *//*
    public MultiLayerGraph getICDCodeView(Collection<String> all) {
        if (all.isEmpty()) {
            System.err.print("Error: no candidates");
            System.exit(0);
        } else
            System.out.println("Candidates: " + all.size());
        AEntityQuery[] args = all.parallelStream().map((x)->
        { return db.entityAnd(db.entityType(SmallOntologyCode.class.getName()),
                db.entityCompare(0, new BinaryOp() {
                    @Override
                    public boolean op(Object l, Object r) {
                        //System.out.println("inop");
                        if (l instanceof SmallOntologyCode) {
                            //System.err.println(r + "==?" + ((SmallOntologyCode)l).get().getCode());
                            return ((SmallOntologyCode)l).get().getCode().equals(r);
                        } else if (l instanceof String) {
                            //System.err.println(r + "==?" + l);
                            return ((String)l).equals(r);
                        } else if (l instanceof disease.ontologies.ICD9CMCode) {
                            if (l.toString().substring(0, 3).equals(r.toString().substring(0, 3)))
                                System.out.println(l.toString() + " is part of "+r);
                           return ((disease.ontologies.ICD9CMCode)l).getCode().equals(r);
                        } else if (l == null) {
                            System.err.println("ERROR: l is null against comparison r: "+ r);
                            return false;
                        } else
                            throw new RuntimeException("l is neither a String or a SmallOntology: "+l.getClass().getName());
                    }
                }, x));} ).toArray(size -> new AEntityQuery[size]);
        AEntityQuery vertex_query = db.entityOr(args);
        return db.solveSimpleRDFQuery(rdfAll(),vertex_query);
    }*/
    
    public void trivialClustering(Collection<Long> candidates) {
        Set<Long> c = new HashSet<>();
        c.addAll(candidates);//do not modify the original data
       
        int current = 0;
        Map<Integer,Set<Long>> idToCluster = new HashMap<>();
        Map<Long,Integer> elem_to_id = new HashMap<>();
       
        for (Iterator<Long> iterator = c.iterator(); iterator.hasNext();) {
            current++;
            int currentPosed = current;
            Long candidate = iterator.next();
            //A new candidate which is not contained in the collection
            if (!elem_to_id.containsKey(candidate)) {
                Set<Long> k = db.getInSet(candidate)
                        .parallelStream()
                        .map((x)->x.getIndex())
                        .filter((x)->candidates.contains(x))
                        .collect(Collectors.toSet());
                Set<Long> h = db.getOutSet(candidate)
                        .parallelStream()
                        .map((x)->x.getIndex())
                        .filter((x)->candidates.contains(x))
                        .collect(Collectors.toSet());
                
                for (Long neighbour : k) {
                    //If one of his neighbours already belongs to a class
                    if (elem_to_id.containsKey(neighbour)) {
                        currentPosed = elem_to_id.get(neighbour);
                        idToCluster.get(currentPosed).addAll(k);
                    } 
                }
            }
        }
       
    }
    
    /**
     * 
     * @param G                 Database
     * @param classes           Association betweeen the element and the father ID
     * @param candWithScore     Candidate's scores
     * @return                  Classification Result
     */
    public Map<Long,Double> runDesperate(MultiLayerGraph G, Collection<Long> classes, Map<Long,Double> candWithScore) {
        Set<Long> cand = candWithScore.keySet();
        Map<Long,Set<Long>> c = new HashMap<>();
        for (Long x : classes) {
            boolean res = false;
            for (Pair<Long,Set<Long>> p : new ConcreteMapIterator<>(c)) {
                if (p.getSecond().contains(x)) {
                    res = true; //skip condition
                    break;
                }
            }
            if (res)
                continue;
            c.put(x, new HashSet<>());
            c.get(x).add(x); //adding self
            Set<Long> K,y,oldK;
            K = new HashSet<>();
            K.addAll(G.getOutSet(x).stream().filter((e)->e!=null).map((entity) -> 
                entity.getIndex()
            ).collect(Collectors.toSet()));
            
            do {
                oldK = K;
                oldK.retainAll(cand);
                y = new HashSet<>();
                y.addAll(oldK);
                c.get(x).addAll(oldK);
                K = new HashSet<>();
                for (Long yy : y) {
                    K.addAll(G.getOutSet(yy).stream().filter((e)->e!=null).map((z)->z.getIndex()).collect(Collectors.toSet()));
                }
                K.retainAll(cand);
            } while (y.containsAll(K) && (!K.containsAll(oldK)));
        }
        
        Map<Long,Double> scoreMap = new HashMap<>();
        for (Pair<Long,Set<Long>> p : new ConcreteMapIterator<>(c)) {
            long candi = -1;
            double weight = -1;
            for (Long cc : p.getSecond()) {
                if (candWithScore.get(cc)>weight) {
                    weight = candWithScore.get(cc);
                    candi = cc;
                }
            }
            if (candi!=-1 && weight!=-1)
            scoreMap.put(candi,weight);
        }
        return scoreMap;
    }
    
    public Map<String, Double> getRandomWalkBestScores(Collection<String> all,Map<String,Double> score) {
        /*MultiLayerGraph mg = getICDCodeView(all);
        mg.save();*/
        Map<String,Double> toret = new HashMap<>();
        //Obtaining the association between String candidate and Long element
        {Map<String,Long> id_to_code = new HashMap<>();
        //Obtaining the association between the id and the class id
        Map<Long,String> id_to_classcode = new HashMap<>();
        //The acutal equivalence class: mapping an id to the super Id
        Map<Long,Long> id_to_codeid = new HashMap<>();
        Set<Long> classes = new HashSet<>();
        
        Map<Long,Double> idToScore = new HashMap<>();
        
        for (Entity x : db.getEntities()) {
            String fullCode = ((SmallOntologyCode)x).get().getCode();
            String supCode = fullCode.substring(0,3);
            if (all.contains(supCode)) {
                id_to_classcode.put(x.getIndex(), fullCode);
                idToScore.put(x.getIndex(),score.get(supCode));
                
                //fullCode is a supCode -> add the association
                if (fullCode.length()==3) {
                    id_to_code.put(supCode,x.getIndex());
                    classes.add(x.getIndex());
                }
            }
            
            
        }
        for (Pair<Long, String> p : new ConcreteMapIterator<>(id_to_classcode)) {
            id_to_codeid.put(p.getFirst(),id_to_code.get(p.getSecond()));
        }
        
        /* Defining a view matrix */
        //MatrixMatrix gm = new MatrixMatrix(id_to_codeid.keySet().size()+1);
        //MatrixMatrixMarkovClustering mcl = new MatrixMatrixMarkovClustering(db, id_to_codeid, gm, 3, 5);
        /*RandomWsalkOutlierDetectionWithEquivalenceClass.getInstance().run(db, id_to_codeid, 0.4,0.3)*/
        /*new ConcreteMapIterator<>(runDesperate(db,id_to_codeid)).stream().map((p) -> {
            toret.put(id_to_classcode.get(p.getFirst()), p.getSecond());
            return p;
            }).forEach((p) -> {
                System.out.println(id_to_classcode.get(p.getFirst())+" -- "+p.getSecond());
            });
         }*/
        for (Pair<Long, Double> x: new ConcreteMapIterator<>(this.runDesperate(db,classes,idToScore))) {
            //System.out.println(id_to_classcode.get(x.getFirst()) + " " +x.getSecond());
            toret.put(id_to_classcode.get(x.getFirst()), x.getSecond());
        }
    }
        return toret;
    }

    
}
