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
package org.tweetsmining.model.graph.database.logical;

import org.tweetsmining.model.matrices.GraphViewMatrix;
import com.blogspot.mydailyjava.guava.cache.jackbergus.CacheMap;
import disease.datatypes.ConcreteMapIterator;
import disease.utils.datatypes.Pair;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.tweetsmining.model.graph.database.Entity;
import org.tweetsmining.model.graph.database.counter.PoolID;
import org.tweetsmining.model.graph.database.Relation;
import org.tweetsmining.model.graph.database.cache.CacheBuilder;
import org.tweetsmining.model.graph.database.eQuery.entity.AEntityQuery;
import org.tweetsmining.model.graph.database.eQuery.relation.RDFQuery;
import org.tweetsmining.model.graph.database.logical.ERInterfaceLayer;
import org.tweetsmining.model.graph.database.logical.GraphDBQueryLayerView;
import org.tweetsmining.model.graph.database.phisical.GraphDBEntityPhisicalLayer;
import org.tweetsmining.model.matrices.IMatrix;

/**
 *
 * @author vasistas
 */
public class GraphDBEntityCollectionLayer  implements ERInterfaceLayer{

    private CacheMap<Long,Entity> entities;
    private CacheMap<String,Relation> relations;
    private ERInterfaceLayer eil;
    public GraphDBEntityCollectionLayer(ERInterfaceLayer eil) {
        entities = CacheBuilder.createTmpCacheBuilder(string->Long.parseLong(string));
        relations = CacheBuilder.createTmpCacheBuilder((String s)->s);
        this.eil = eil;
    }
    
    public void addEntity(Entity e) {
        entities.put(e.getIndex(), e);
    }
    
    @Override
    public Set<Long> getIds() {
        return entities.keySet();
    }

    @Override
    public Collection<Entity> getEntities() {
        return entities.values();
    }

    @Override
    public Entity getEntity(long e) {
        return entities.get(e);
    }


    @Override
    public boolean remove(Entity name) {
        boolean toret = (this.entities.remove(name.getIndex())!=null);
        //remove all the edges where "name" appears
        Set<String> torem = new TreeSet<>();
        for (Pair<String, Relation> x : new ConcreteMapIterator<>(this.relations)) {
            if (name.getIndex()==x.getSecond().getSource().getIndex()||name.getIndex()==x.getSecond().getDestination().getIndex())
                torem.add(x.getFirst());
        }
        torem.parallelStream().forEach((x)->{this.relations.remove(x);});
        return toret;
    }


    @Override
    public Relation hasRelation(long src, String name, long dst) {
        Relation empt = new Relation(getEntity(src),name,getEntity(dst),0);
        Relation toret = this.relations.get(empt.toString());
        return (toret==null ? empt : toret);
    }

    @Override
    public Iterator<Relation> getRelations() {
        return this.relations.values().iterator();
    }

    @Override
    public ERInterfaceLayer getPhisicalMaster() {
        return this;
    }

    @Override 
    public Entity createNewEntity(Class<? extends Entity> c, Object... args) {
        try {
            PoolID pi = PoolID.challengeId();
            long tmp = pi.get();
            //tmp++;
            //The only required argument for a class that extends Entity is a Long,
            //which stores the node ID
            Constructor<?> builder = c.getConstructor(Long.TYPE,Object[].class); 
            //The constructor automatically calls the superclass, that initializes the 
            //hashcode definition
            Entity e = (Entity)builder.newInstance(tmp,args);
            //Builds the hash code and "truly" initializes the object
            
            entities.put(tmp, e);
            pi.retain();
            //last = tmp;
            return e;
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(GraphDBEntityPhisicalLayer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public void save() {
        this.entities.persist();
    }

    @Override
    public void update(Entity e) {
        this.entities.put(e.getIndex(),e);
    }

    @Override
    public String getName() {
        return "Tmp";
    }

/*    @Override
    public Entity getVertex(long name) {
        return this.getEntity(name);
    }
*/
    /*@Override
    public void removeEntity(Entity name) {
        this.remove(name);
    }*/

    @Override
    public void clear() {
        this.entities.clear();
    }

    @Override
    public void addEdge(long src,String name, long dst, double value) {
        if (value==0) {
            removeEdge(src,name,dst);
        } else {
            Relation empt = new Relation(getEntity(src),name,getEntity(dst),value);
            this.relations.put(empt.toString(),empt);
        }
    }

    @Override
    public void addEdge(long src,String name, long dst) {
        addEdge(src,name,dst,1);
    }

    @Override
    public void removeEdge(Relation r) {
        this.relations.remove(r.toString(), r);
    }

    @Override
    public Relation getEdge(long src,String name, long dst) {
        Relation empt = new Relation(getEntity(src),name,getEntity(dst),0);
        Relation toret = this.relations.get(empt.toString());
        return (toret==null ? empt : toret);
    }

    @Override
    public void removeEdge(long src, String name, long dst) {
        removeEdge(new Relation(getEntity(src),name,getEntity(dst),0));
    }

    @Override
    public ERInterfaceLayer createView(RDFQuery relations, AEntityQuery entities) {
        return new GraphDBQueryLayerView(this,relations,entities);
    }

    @Override
    public Set<Entity> getOutSet(long entity) {
        Set<Entity> toret = new HashSet<>();
        toret.addAll(this.relations.values().parallelStream().filter((x)->(x.getSource().getIndex()==entity)).map((x)->{return x.getDestination();})
.collect(Collectors.toSet()));
        toret.addAll(this.eil.getOutSet(entity));
        return toret;
    }

    @Override
    public Set<Entity> getInSet(long entity) {
        Set<Entity> toret = new HashSet<>();
        toret.addAll(this.relations.values().parallelStream().filter((x)->(x.getDestination().getIndex()==entity)).map((x)->{return x.getSource();})
.collect(Collectors.toSet()));
        toret.addAll(this.eil.getInSet(entity));
        return toret;
    }


    @Override
    public IMatrix getRelationMatrix(String rel) {
        return new GraphViewMatrix(this,rel);
    }
//
//    @Override
//    public void updateRelation(String layer, double[][] m, int N) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//    

    @Override
    public Iterator<Relation> iterator() {
        return this.getRelations();
    }
    
}
