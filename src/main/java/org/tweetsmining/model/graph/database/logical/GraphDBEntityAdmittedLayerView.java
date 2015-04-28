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
import disease.utils.IteratorMapping;
import it.jackbergus.pickstream.PickStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.tweetsmining.model.graph.PropertyGraph;
import org.tweetsmining.model.graph.database.Entity;
import org.tweetsmining.model.graph.database.Relation;
import org.tweetsmining.model.graph.database.cache.CacheBuilder;
import org.tweetsmining.model.graph.database.counter.PoolID;
import org.tweetsmining.model.graph.database.eQuery.entity.AEntityQuery;
import org.tweetsmining.model.graph.database.eQuery.relation.RDFQuery;
import org.tweetsmining.model.graph.database.phisical.GraphDBEntityPhisicalLayer;
import org.tweetsmining.model.matrices.IMatrix;

/**
 *
 * @author vasistas
 */
public class GraphDBEntityAdmittedLayerView implements ERInterfaceLayer {
    
    private ERInterfaceLayer master;
    private CacheMap<Long,Entity> admitted = CacheBuilder.createTmpCacheBuilder(string->Long.parseLong(string));
            
    private CacheMap<String,PropertyGraph> expansion = CacheBuilder.createTmpCacheBuilder((String t)->t);
    private CacheMap<String,PropertyGraph> removal = CacheBuilder.createTmpCacheBuilder((String t)->t);
    
    public GraphDBEntityAdmittedLayerView(ERInterfaceLayer master) {
        this.master = master;
        
    }
    
    public GraphDBEntityAdmittedLayerView(ERInterfaceLayer master, AEntityQuery q) {
        master.getEntities().parallelStream()
                            .filter((x)->(q.prop(x)))
                            .forEach((x)->{
                                this.admitted.put(x.getIndex(), x);
                            });
        this.master = master;
    }

    @Override
    public Set<Long> getIds() {
        return this.master.getIds().parallelStream().filter((x)->(admitted.containsKey(x))).collect(Collectors.toSet());
    }

    @Override
    public Set<Entity> getEntities() {
        return this.master.getEntities().parallelStream().filter((x)->(admitted.containsKey(x.getIndex()))).collect(Collectors.toSet());
    }

    /*@Override
    public Entity getEntity(long e) {
        if (admitted.contains(e))
            return this.master.getEntity(e);
        else
            return null;
    }

    @Override
    public int getEntitySize(Entity e) {
        if (admitted.contains(e.getIndex()))
            return this.master.getEntitySize(e);
        else
            return -1;
    }

    @Override
    public <K> K getEntityArg(Entity e, int pos) {
        if (admitted.contains(e.getIndex()))
            return this.master.getEntityArg(e, pos);
        else
            return null;
    }

    @Override
    public int getEntitySize(long e) {
        if (admitted.contains(e))
            return this.master.getEntitySize(e);
        else
            return -1;
    }*/

    @Override
    public boolean remove(Entity name) {
        return (admitted.remove(name.getIndex())!=null);
    }
/*
    @Override
    public ERInterfaceLayer filterExcluded(Collection<Long> d) {
       return new GraphDBEntityExcludedLayerView(this,d);
    }

    @Override
    public ERInterfaceLayer filterPermitted(Collection<Long> d) {
        return new GraphDBEntityAdmittedLayerView(this,d);
    }
*/
    @Override
    public Entity getEntity(long e) {
        if (!admitted.containsKey(e))
            return null;
        return master.getEntity(e);
    }

    @Override
    public String getName() {
        return "View";
    }
/*
    @Override
    public Entity getVertex(long name) {
        return this.getEntity(name);
    }

    @Override
    public void removeVertex(Entity name) {
        this.admitted.remove(name.getIndex());
    }
*/
    @Override
    public void addEdge(long left, String rel, long right, double value) {
        if ((!this.admitted.containsKey(left))) {
            return;
        }
        if ((!this.admitted.containsKey(right))) {
            return;
        }
        
        if (!expansion.containsKey(rel)) {
            expansion.put(rel, PropertyGraph.create(rel));
        }
        if (removal.containsKey(rel)) {
            if (removal.get(rel).has(left,right))
                removal.get(rel).rem(left,right);
        }
        expansion.get(rel).addEdge(left,right,value);
    }

    @Override
    public void addEdge(long left, String r, long right) {
        if ((!this.admitted.containsKey(left))) {
            return;
        }
        if ((!this.admitted.containsKey(right))) {
            return;
        }
        
        if (removal.containsKey(r)) {
            if (removal.get(r).has(left, right))
                removal.get(r).rem(left, right);
        }
        //If the edge already exists, do not replicate
        if (master.getEdge(left, r, right).getWeight()>0)
            return;
        if (!expansion.containsKey(r)) {
            expansion.put(r, PropertyGraph.create(r));
        }
        expansion.get(r).addEdge(left,right);
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
        Relation zero = new Relation(getEntity(left),r,getEntity(right),0);
        if (this.removal.containsKey(r)) {
            if (this.removal.get(r).has(left, right))
                return zero;
        }
        //expansion has the updated value
        if (this.admitted.containsKey(left)&&this.admitted.containsKey(right)) {
            Relation toret = master.getEdge(left, r, right);
            if (!expansion.containsKey(r))
                return toret;
            else
                return new Relation(getEntity(left),r,getEntity(right),expansion.get(r).get(left, right));
        } else return zero;
    }

    @Override
    public void clear() {
        this.admitted.clear();
    }
    
    
    @Override
    public Relation hasRelation(long src, String name, long dst) {
        Relation zero = new Relation(getEntity(src),name,getEntity(dst),0);
        if ((!this.admitted.containsKey(src))||(!this.admitted.containsKey(dst)))
            return zero;
        PropertyGraph pg = this.removal.get(name);
        if (pg==null||(!pg.has(src, dst))) {
            pg = expansion.get(name);
            if (pg==null)
                return this.master.hasRelation(src,name,dst);
            else return new Relation(getEntity(src),name,getEntity(dst),pg.get(src, dst));
        } else 
            return zero;
    }
    
    @Override
    public Iterator<Relation> getRelations() {
        Collection<Relation> toret = new TreeSet<>();
        
        PickStream<Relation> k = new PickStream<>(master);
        toret.addAll(k.filter((Relation x) -> {
            if ((!this.admitted.containsKey(x.getSource().getIndex()))||(!this.admitted.containsKey(x.getDestination().getIndex())))
                return false;
            else if (!this.removal.containsKey(x.getName()))
                return true;
            else return !this.removal.get(x.getName()).has(x.getSource().getIndex(), x.getDestination().getIndex());
        }).collect(Collectors.toSet()));
        /*for (Pair<String, PropertyGraph> p : new ConcreteMapIterator<>(expansion)) {
            PropertyGraph tpg = p.getSecond();
            toret.addAll(tpg.getValueRange().stream().map((x) -> {
                long si = x.getFirst();
                long di = x.getSecond();
                //Entity s = getEntity((Long)x.getKey(0));
                //Entity d = getEntity((Long)x.getKey(1));
                return new Relation(tpg.getEntity(si),p.getFirst(),gdi,tpg.get(si, di));
                //return true;
            }).collect(Collectors.toSet()));
        }
        */
        toret.addAll(StreamSupport
                .stream(expansion.entrySet().spliterator(), false)
                .map((entry)->{
                    String relname = entry.getKey();
                    PropertyGraph pg = entry.getValue();
                    return new PickStream<>(pg.getValueRange())
                            .filter((x)->(this.admitted.containsKey(x.getFirst())&&this.admitted.containsKey(x.getSecond())))
                            .map((x) -> {
                                long si = x.getFirst();
                                long di = x.getSecond();
                                return new Relation(getEntity(si),relname,getEntity(di),pg.get(si, di));})
                            .collect(Collectors.toSet());
                }).flatMap(Collection::stream).collect(Collectors.toSet()));
        
        
        return toret.iterator();
    }
/*
    @Override
    public Set<Relation> simpleselect(AEntityQuery src, ARelationQuery rel, AEntityQuery dst) {
        Set<Relation> toret = master.getRelations();
        toret.stream().filter((Relation x) -> {
            if ((!this.admitted.contains(x.getSource()))||(!this.admitted.contains(x.getDestination())))
                return false;
            else if (!this.removal.containsKey(x.getName()))
                return true;
            else return !this.removal.get(x.getName()).has(x.getSource(), x.getDestination());
        });
        toret = new TreeSet<>();
        for (Pair<String, PropertyGraph> p : new ConcreteMapIterator<>(expansion)) {
            PropertyGraph tpg = p.getSecond();
            toret.addAll(tpg.getValueRange().stream().filter((MultiKey x) -> {
                long si = (Long)x.getKey(0);
                long di = (Long)x.getKey(1);
                Entity s = getEntity(si);
                Entity d = getEntity(di);
                Relation r = new Relation(si,p.getFirst(),di);
                return (src.prop(s)&&rel.prop(r)&&dst.prop(d)); //All the three conditions have to be met
            }).map((x) -> {
                long si = (Long)x.getKey(0);
                long di = (Long)x.getKey(1);
                //Entity s = getEntity((Long)x.getKey(0));
                //Entity d = getEntity((Long)x.getKey(1));
                return new Relation(si,p.getFirst(),di);
                //return true;
            }).collect(Collectors.toSet()));
        }
        return toret;
    }
   */ 
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
        master.save();
        admitted.persist();
        expansion.persist();
        removal.persist();
    }

    @Override
    public void update(Entity e) {
        this.admitted.put(e.getIndex(), e);
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
            
            this.admitted.put(e.getIndex(), e);
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
//
//    @Override
//    public void updateRelation(String layer, double[][] m, int N) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

    @Override
    public Iterator<Relation> iterator() {
        return this.getRelations();
    }
    
}
