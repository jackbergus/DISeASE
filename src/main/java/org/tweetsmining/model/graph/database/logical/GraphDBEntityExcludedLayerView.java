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
import disease.utils.IteratorMapping;
import disease.utils.datatypes.Pair;
import it.jackbergus.pickstream.PickStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.tweetsmining.model.graph.PropertyGraph;
import org.tweetsmining.model.graph.database.Entity;
import org.tweetsmining.model.graph.database.counter.PoolID;
import org.tweetsmining.model.graph.database.Relation;
import org.tweetsmining.model.graph.database.cache.CacheBuilder;
import org.tweetsmining.model.graph.database.eQuery.entity.AEntityQuery;
import org.tweetsmining.model.graph.database.eQuery.relation.RDFQuery;
import org.tweetsmining.model.graph.database.phisical.GraphDBEntityPhisicalLayer;
import org.tweetsmining.model.matrices.IMatrix;

/**
 *
 * @author vasistas
 */
public class GraphDBEntityExcludedLayerView  implements ERInterfaceLayer  {
    
    

    private ERInterfaceLayer master;
    private Set<Long> refused;
    
    private CacheMap<Long,Entity> included = CacheBuilder.createTmpCacheBuilder(string->Long.parseLong(string));
    private CacheMap<String,PropertyGraph> expansion = CacheBuilder.createTmpCacheBuilder(s->s);
    private CacheMap<String,PropertyGraph> removal = CacheBuilder.createTmpCacheBuilder(s->s);
    
    public GraphDBEntityExcludedLayerView(ERInterfaceLayer master, Set<Long> refused) {
        this.master = master;
        this.refused = refused;
    }
    
    @Override
    public Set<Long> getIds() {
        return this.master.getIds().parallelStream().filter((x)->(!refused.contains(x))).collect(Collectors.toSet());
    }

    @Override
    public Set<Entity> getEntities() {
        return this.master.getEntities().parallelStream().filter((x)->(!refused.contains(x.getIndex()))).collect(Collectors.toSet());
    }

    @Override
    public Entity getEntity(long e) {
        if (!refused.contains(e)) {
            Entity toret = this.master.getEntity(e);
            return (toret==null ? this.included.get(e) : toret);
        } else
            return null;
    }

    /*@Override
    public int getEntitySize(Entity e) {
        if (!refused.contains(e.getIndex()))
            return this.master.getEntitySize(e);
        else
            return -1;
    }

    @Override
    public <K> K getEntityArg(Entity e, int pos) {
        if (!refused.contains(e.getIndex()))
            return this.master.getEntityArg(e, pos);
        else
            return null;
    }

    @Override
    public int getEntitySize(long e) {
        if (!refused.contains(e))
            return this.master.getEntitySize(e);
        else
            return -1;
    }*/


   /* @Override
    public ERInterfaceLayer filterExcluded(Collection<Long> d) {
        return new GraphDBEntityExcludedLayerView(this,d);
    }

    @Override
    public ERInterfaceLayer filterPermitted(Collection<Long> d) {
        return new GraphDBEntityAdmittedLayerView(this,d);
    }*/
/*
    @Override
    public boolean hasEntity(long left) {
        return ((!this.refused.contains(left))&&(master.getEntity(left)!=null));
    }
*/
    @Override
    public String getName() {
        return "View";
    }

    /*@Override
    public Entity getEntity(long name) {
        if (refused.contains(name))
            return null;
        else
            return master.getEntity(name);
    }*/


    @Override
    public void addEdge(long left, String r, long right, double value) {
        if (value==0)
            removeEdge(left,r,right);
        else {
            this.refused.remove(left);
            this.refused.remove(right);
            if (!expansion.containsKey(r)) {
                expansion.put(r, PropertyGraph.create(r));
            }
            if (removal.containsKey(r)) {
                if (removal.get(r).has(left, right))
                    removal.get(r).rem(left, right);
            }
            expansion.get(r).addEdge(left,right,value);
        }
    }

    @Override
    public void addEdge(long left, String r, long right) {
        addEdge(left,r,right,1);
    }

    @Override
    public void removeEdge(long left, String r, long right) {
        if (!removal.containsKey(r)) {
            removal.put(r, PropertyGraph.create(r));
        }
        removal.get(r).set(left, right,1);
    }

    @Override
    public Relation getEdge(long left, String r, long right) {
        
        if (refused.contains(left)||refused.contains(right))
            return new Relation(getEntity(left),r,getEntity(right),0);
        if (removal.containsKey(r)) {
            if (removal.get(r).has(left, right))
                removal.get(r).rem(left, right);
        }
        if (this.expansion.containsKey(r)) {
            Relation toret = master.getEdge(left, r, right);
            if (!expansion.containsKey(r))
                return toret;
            else
                return new Relation(getEntity(left),r,getEntity(right),expansion.get(r).getEdge(getEntity(left), getEntity(right)));
        }
        return master.getEdge(left, r, right);
    }

    @Override
    public void clear() {
        this.refused.addAll(master.getIds());
        this.included.clear();
        this.expansion.cleanUp();
        this.removal.clear();
    }

    @Override
    public Relation hasRelation(long src, String name, long dst) {
        Entity left = getEntity(src);
        Entity right = getEntity(dst);
        Relation emp = new Relation(left,name,right,0);
        if (this.refused.contains(src)||this.refused.contains(dst))
            return emp;
        PropertyGraph pg = this.removal.get(name);
        if (pg==null||(!pg.has(src, dst))) {
            pg = expansion.get(name);
            if (pg==null)
                return this.master.hasRelation(src,name,dst);
            else return new Relation(left,name,right,pg.get(src, dst));
        } else 
            return emp;
    }

    @Override
    public Iterator<Relation> getRelations() {
        Set<Relation> toret;
        PickStream<Relation> k = new PickStream<>(master);
        toret = (k.
               filter((Relation x) -> {
            if (this.refused.contains(x.getSource().getIndex())||this.refused.contains(x.getDestination().getIndex()))
                return false;
            else if (!this.removal.containsKey(x.getName()))
                return true;
            else return !this.removal.get(x.getName()).has(x.getSource().getIndex(), x.getDestination().getIndex());
        }).collect(Collectors.toSet()));
        
        
        toret.addAll(StreamSupport
                .stream(expansion.entrySet().spliterator(), false)
                .map((entry)->{
                    String relname = entry.getKey();
                    PropertyGraph pg = entry.getValue();
                    return new PickStream<>(pg.getValueRange())
                            .filter((x)->{
                                if (this.refused.contains(x.getFirst())||this.refused.contains(x.getSecond()))
                                    return false;
                                else if (!this.removal.containsKey(relname))
                                    return true;
                                else return !this.removal.get(relname).has(x.getFirst(), x.getSecond());
                            })
                            .map((x) -> {
                                long si = x.getFirst();
                                long di = x.getSecond();
                                return new Relation(getEntity(si),relname,getEntity(di),pg.get(si, di));})
                            .collect(Collectors.toSet());
                }).flatMap(Collection::stream).collect(Collectors.toSet()));
        
        /*for (Pair<String, PropertyGraph> p : new ConcreteMapIterator<>(expansion)) {
            PropertyGraph tpg = p.getSecond();
            toret.addAll(tpg.getValueRange().stream().map((x) -> {
                long si = x.getFirst();
                long di = x.getSecond();
                //Entity s = getEntity((Long)x.getKey(0));
                //Entity d = getEntity((Long)x.getKey(1));
                return new Relation(getEntity(si),p.getFirst(),getEntity(di),tpg.get(si, di));
                //return true;
            }).collect(Collectors.toSet()));
        }*/
        return toret.iterator();
    }
    
    @Override
    public ERInterfaceLayer getPhisicalMaster() {
        return this.master.getPhisicalMaster();
    }

    @Override
    public ERInterfaceLayer createView(RDFQuery relations, AEntityQuery entities) {
        return new GraphDBQueryLayerView(this,relations,entities);
    }

    @Override
    public void save() {
        expansion.persist();
        removal.persist();
        master.save();
        included.persist();
    }

    @Override
    public void update(Entity e) {
        this.included.put(e.getIndex(), e);
    }

    @Override
    public boolean remove(Entity name) {
        if (refused.add(name.getIndex()))
            return true;
        else
            return (included.remove(name.getIndex())!=null);
    }

    @Override
    public Entity createNewEntity(Class<? extends Entity> c, Object... args) {
        try {
            PoolID pi = PoolID.challengeId();
            long tmp = pi.get();
            //tmp++;
            //The only required argument for a class that extends Entity is a Long,
            //which stores the node ID
            System.out.println(c.getConstructors()[0].getParameterTypes()[0].toString());
            Constructor<?> builder = c.getConstructor(Long.TYPE, Object[].class); 
            //The constructor automatically calls the superclass, that initializes the 
            //hashcode definition
            Entity e = (Entity)builder.newInstance(tmp,args);
            
            this.included.put(e.getIndex(), e);
            pi.retain();
            //last = tmp;
            return e;
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(GraphDBEntityPhisicalLayer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public void removeEdge(Relation r) {
        removeEdge(r.getSource().getIndex(),r.getName(),r.getDestination().getIndex());
    }

    @Override
    public Set<Entity> getOutSet(long entity) {
        Set<Entity> toret;
        {
            Set<Long> to_not_add = removal.values().parallelStream().map((x)->{return x.getOut(entity);}).flatMap(Collection::stream).collect(Collectors.toSet());
            toret = master.getOutSet(entity).stream()
                .filter((x)->(!to_not_add.contains(x.getIndex()))).collect(Collectors.toSet());
        }
        toret.addAll(this.expansion.values().parallelStream()
                .map((x)->{return (x.getOut(entity));})
                .flatMap(Collection::stream)
                .map((x)->{return getEntity(x);})
                .collect(Collectors.toSet())
        );
        return toret;
    }

    @Override
    public Set<Entity> getInSet(long entity) {
        Set<Entity> toret;
        {
            Set<Long> to_not_add = removal.values().parallelStream().map((x)->{return x.getIn(entity);}).flatMap(Collection::stream).collect(Collectors.toSet());
            toret = master.getInSet(entity).stream()
                .filter((x)->(!to_not_add.contains(x.getIndex()))).collect(Collectors.toSet());
        
        }
        toret.addAll(this.expansion.values().parallelStream()
                .map((x)->{return (x.getIn(entity));})
                .flatMap(Collection::stream)
                .map((x)->{return getEntity(x);})
                .collect(Collectors.toSet())
        );
        return toret;
    }
    
    @Override
    public IMatrix getRelationMatrix(String rel) {
        return new GraphViewMatrix(this,rel);
    }

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
