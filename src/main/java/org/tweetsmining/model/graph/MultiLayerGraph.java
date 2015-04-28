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
package org.tweetsmining.model.graph;

import disease.ontologies.ICD9CMCode;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.tweetsmining.model.graph.database.Entity;
import org.tweetsmining.model.graph.database.Relation;
import org.tweetsmining.model.graph.database.eQuery.BinaryOp;
import org.tweetsmining.model.graph.database.eQuery.entity.AEntityQuery;
import org.tweetsmining.model.graph.database.eQuery.entity.EntityHasType;
import org.tweetsmining.model.graph.database.eQuery.entity.EntityHasValue;
import org.tweetsmining.model.graph.database.eQuery.entity.EntityQueryAnd;
import org.tweetsmining.model.graph.database.eQuery.entity.EntityQueryOr;
import org.tweetsmining.model.graph.database.eQuery.entity.EntityAll;
import org.tweetsmining.model.graph.database.eQuery.relation.ARelationQuery;
import org.tweetsmining.model.graph.database.eQuery.relation.RDFQuery;
import org.tweetsmining.model.graph.database.eQuery.relation.RelationAll;
import org.tweetsmining.model.graph.database.eQuery.relation.RelationHasType;
import org.tweetsmining.model.graph.database.eQuery.relation.RelationHasValue;
import org.tweetsmining.model.graph.database.eQuery.relation.RelationQueryAnd;
import org.tweetsmining.model.graph.database.eQuery.relation.RelationQueryOr;
import org.tweetsmining.model.graph.database.logical.ERInterfaceLayer;
import org.tweetsmining.model.graph.database.logical.GraphDBEntityAdmittedLayerView;
import org.tweetsmining.model.graph.database.logical.GraphDBEntityExcludedLayerView;
import org.tweetsmining.model.graph.database.logical.GraphDBQueryLayerView;
import org.tweetsmining.model.graph.database.phisical.GraphDBEntityPhisicalLayer;
import org.tweetsmining.model.matrices.GuavaMatrix;
import org.tweetsmining.model.matrices.IMatrix;

/**
 *
 * @author Giacomo Bergami
 */
public class MultiLayerGraph implements IMultiRelationGraph {
    
    private ERInterfaceLayer eelst;
    private String name;
    
    /**
     * Uses a precedently defined view
     * @param view  View Used
     * @param name  Database Name
     */
    public MultiLayerGraph(String name, ERInterfaceLayer view) {
        this.eelst = view;
        this.name = name;
    }
    
    public IMatrix filterEntities(Collection<String> c) {
        
        
        //Selecting all the entities that have the type SmallOntologyCode and which
        //ICD9CMCode's string representation is contained in c
        AEntityQuery e = this.entityAnd(this.entityType("SmallOntologyCode"),this.entityOr(c.parallelStream().map((str)->{
            return this.entityCompare(0, new BinaryOp() {
                
                @Override
                public boolean op(Object l, Object r) {
                    if (l instanceof ICD9CMCode) {
                        return ((ICD9CMCode)l).getCode().equals(r);
                    } else return false;
                }
            }, str);
        }).toArray(size->new AEntityQuery[size])));
        
        GraphDBEntityAdmittedLayerView newView = new GraphDBEntityAdmittedLayerView(this.eelst,e);
        
        return new MultiLayerGraph("view",newView).getRelationMatrix("Related");
    }
    
    public MultiLayerGraph(String path, int N) {
        this.eelst = new GraphDBEntityPhisicalLayer(path,N);
        /*if (path.contains(File.separator)) {
            String arr[] = path.split(File.separator);
            this.name = arr[arr.length-1];
        } else*/
        this.name = path;
    }
    public MultiLayerGraph(int N) {
        this.eelst = new GraphDBEntityPhisicalLayer(N);
        this.name = "temp";
    }
    
    /**
     * Selects all the entities
     * @return 
     */
    public AEntityQuery entityAll() {
        return EntityAll.getInstance();
    }
    
    /**
     *  Logic and between the entity queries
     * @param queries   Subqieries to evaluate
     * @return          Compiled or query
     */
    public AEntityQuery entityOr(AEntityQuery... queries){
        return new EntityQueryOr(queries);
    }
    /**
     *  Logic or between the entity queries
     * @param queries   Subqieries to evaluate
     * @return          Compiled or query
     */
    public AEntityQuery entityAnd(AEntityQuery... queries){
        return new EntityQueryAnd(queries);
    }
    /**
     * Gets the i-th value from the Entity and compares it with a given value
     * @param entityArgPos  i-th argument of the element
     * @param op            Comparison operator
     * @param right         comparison value
     * @return              Compiled query
     */
    public AEntityQuery entityCompare(int entityArgPos, BinaryOp op, Object right) {
        return new EntityHasValue(entityArgPos,op,right);
    }
    /**
     * Checks if the entity name has one of the desired types
     * @param args  String representation of the possible types
     * @return 
     */
    public AEntityQuery entityType(String... args) {
        return new EntityHasType(args);
    }
    
    
    
    /**
     * Selects all the entities
     * @return 
     */
    public ARelationQuery relationAll() {
        return RelationAll.getInstance();
    }
    
    /**
     *  Logic and between the entity queries
     * @param queries   Subqieries to evaluate
     * @return          Compiled or query
     */
    public ARelationQuery relationOr(ARelationQuery... queries){
        return new RelationQueryOr(queries);
    }
    /**
     *  Logic or between the entity queries
     * @param queries   Subqieries to evaluate
     * @return          Compiled or query
     */
    public ARelationQuery relationAnd(ARelationQuery... queries){
        return new RelationQueryAnd(queries);
    }
    /**
     * Gets the i-th value from the Entity and compares it with a given value
     * @param op            Comparison operator
     * @param right         comparison value
     * @return              Compiled query
     */
    public ARelationQuery relationCompare(BinaryOp op, Object right) {
        return new RelationHasValue(op,right);
    }
    /**
     * Checks if the entity name has one of the desired types
     * @param args  String representation of the possible types
     * @return 
     */
    public ARelationQuery relationType(String... args) {
        return new RelationHasType(args);
    }
    
    
    public Set<Long> solveVertexQuery(AEntityQuery q) {
        return eelst.getEntities().parallelStream()
                        .filter((e)-> (q.prop(e)))
                        .map((e)->{return e.getIndex();})
                        .collect(Collectors.toSet());
    }

    /**
     * Returns the RDFQuery as a view over a graph.
     * @param q     Property among the queries
     * @param l     Property among the vertices 
     * @return 
     */
    public MultiLayerGraph solveSimpleRDFQuery(RDFQuery q,AEntityQuery l) {
        //Actually, a selection creates a view... Adding a bogus layer
        GraphDBQueryLayerView view = new GraphDBQueryLayerView(eelst,q,l);
        return new MultiLayerGraph(name+" - View",view);
    }
    
    public MultiLayerGraph graphModifyInView() {
        return new MultiLayerGraph(name+" - View",new GraphDBEntityExcludedLayerView(eelst,new TreeSet<>()));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean remove(Entity name) {
        return eelst.remove(name);
    }

    @Override
    public Entity createNewEntity(Class<? extends Entity> c, Object... args) {
        return eelst.createNewEntity(c, args);
    }

    @Override
    public Set<Long> getIds() {
        return eelst.getIds();
    }

    @Override
    public Collection<Entity> getEntities() {
        return eelst.getEntities();
    }

    @Override
    public Entity getEntity(long e) {
        return eelst.getEntity(e);
    }

    @Override
    public Relation hasRelation(long src, String name, long dst) {
        return eelst.hasRelation(src, name, dst);
    }

    @Override
    public Iterator<Relation> getRelations() {
        return eelst.getRelations();
    }

    @Override
    public void addEdge(long src, String name, long dst, double value) {
        eelst.addEdge(src, name, dst,value);
    }

    @Override
    public void addEdge(long src, String name, long dst) {
         eelst.addEdge(src, name, dst);
    }

    @Override
    public Relation getEdge(long src, String name, long dst) {
        return eelst.getEdge(src, name, dst);
    }

    @Override
    public void removeEdge(Relation r) {
        eelst.removeEdge(r);
    }

    @Override
    public void removeEdge(long src, String name, long dst) {
        eelst.removeEdge(src, name, dst);
    }

    @Override
    public void clear() {
        eelst.clear();
    }

    @Override
    public Set<Entity> getOutSet(long entity) {
        return eelst.getOutSet(entity);
    }

    @Override
    public Set<Entity> getInSet(long entity) {
        return eelst.getInSet(entity);
    }

    @Override
    public IMatrix getRelationMatrix(String rel) {
        return eelst.getRelationMatrix(rel);
    }

    public void save() {
        this.eelst.save();
    }
//    
//    public void update(String relation, double[][] M, int size) {
//        this.eelst.updateRelation(name, M, size);
//    }
//    
//    
//    public void release_and_update(String relation, double[][] M, int size) {
//        this.clear();
//        this.eelst.updateRelation(name, M, size);
//    }

    @Override
    public Iterator<Relation> iterator() {
        return getRelations();
    }
    
}
