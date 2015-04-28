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
package disease.Testing;

import org.tweetsmining.model.graph.MultiLayerGraph;
import org.tweetsmining.model.graph.testclasses.T1;
import org.tweetsmining.model.graph.database.Entity;
import org.tweetsmining.model.graph.database.IQuery;
import org.tweetsmining.model.graph.database.counter.PoolID;
import org.tweetsmining.model.graph.database.eQuery.BinaryOp;
import org.tweetsmining.model.graph.database.eQuery.entity.AEntityQuery;
import org.tweetsmining.model.graph.database.eQuery.relation.RDFQuery;

/**
 *
 * @author vasistas
 */
public class GraphDBTesting {
    
    
    
    public static void main(String args[]) {
        
        MultiLayerGraph testdb = new MultiLayerGraph("test.graph",0);
        
        PoolID counter = PoolID.challengeId();
        if (counter.get()!=0){
            System.err.println("Counter is not on zero");
            System.exit(0);
        }
        counter.discard();//releases the POOLID
        
        Entity v1 = testdb.createNewEntity(T1.class, new Object[]{"Ciao","Mondo",(Integer)22});
        
        System.out.println("1. Checking Predicate Validity");
        AEntityQuery q1 = testdb.entityAll();
        if (!q1.prop(v1)) {
            System.err.println("Alert: all condition not satisfied");
        } else 
            System.out.println("\t a. Top(v1)");
        q1 = testdb.entityCompare(1, BinaryOp.equals(), "Mondo");
        if (!q1.prop(v1)) {
            System.err.println("Alert: all condition not satisfied");
        } else 
            System.out.println("\t b. Q1(v1)");
        IQuery q2 = testdb.entityCompare(1, BinaryOp.equals(), "Ciao");
        if (q2.prop(v1)) {
            System.err.println("Alert: all condition not satisfied");
        }  else 
            System.out.println("\t c. !Q2(v1)");
        
        System.out.println("2. Checking Graph View Validity");
        if (testdb.getEntity(v1.getIndex()).getArg(2)!=(Integer)22)
            System.err.println("Alert: all condition not satisfied");
        else 
            System.out.println("\t a. Extracting correct element");
        
        
        MultiLayerGraph view1 = testdb.solveSimpleRDFQuery(new RDFQuery(testdb.entityAll(),testdb.relationAll(),testdb.entityAll()), q1);
        if (view1.getEntity(v1.getIndex()).getArg(2)!=(Integer)22)
            System.err.println("Alert: all condition not satisfied");
        else 
            System.out.println("\t b. Extracting correct element v1 from q1-view");
        Entity v3 = testdb.createNewEntity(T1.class, new Object[]{"Viva il","Mondo",(Integer)23});
        if (view1.getEntity(v3.getIndex()).getArg(2)!=(Integer)23)
            System.err.println("Alert: all condition not satisfied");
        else 
            System.out.println("\t c. Extracting correct element v3 from q1-view");
        
        Entity v2 = testdb.createNewEntity(T1.class, new Object[]{"Viva il","Calcio",(Integer)23});
        if (view1.getEntity(v2.getIndex())!=null)
            System.err.println("Alert: all condition not satisfied");
        else 
            System.out.println("\t d. Not extracting element v2 from q1-view");
        testdb.addEdge(v1.getIndex(), "pino", v2.getIndex());
        testdb.addEdge(v1.getIndex(), "daniele", v3.getIndex());
        if (view1.hasRelation(v1.getIndex(), "pino", v2.getIndex())==null||
                view1.hasRelation(v1.getIndex(), "pino", v2.getIndex()).getWeight()!=0)
            System.err.println("Alert: all condition not satisfied");
        else 
            System.out.println("\t e. Not extracting edge (v1,v2) from q1-view");
        
        view1.addEdge(v2.getIndex(), "blues", v3.getIndex(), 5);
        if (testdb.hasRelation(v2.getIndex(), "blues", v3.getIndex())==null||
                testdb.hasRelation(v2.getIndex(), "blues", v3.getIndex()).getWeight()!=0)
            System.err.println("Alert: all condition not satisfied");
        else 
            System.out.println("\t f. view edge not appearing in the main element");
        if (view1.hasRelation(v2.getIndex(), "blues", v3.getIndex())!=null &&
                view1.hasRelation(v2.getIndex(), "blues", v3.getIndex()).getWeight()==0)
            System.err.println("Alert: all condition not satisfied");
        else 
            System.out.println("\t g. view edge appearing in the view");
        
        view1.clear();
        testdb.clear();
        //PoolID.reset();
        
        System.out.println("All test have been passed");
        
    }
    
}
