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
package org.tweetsmining.model.graph.database.phisical;

import com.blogspot.mydailyjava.guava.cache.jackbergus.CacheMap;
import com.google.common.collect.Iterators;
import org.tweetsmining.model.graph.database.logical.ERInterfaceLayer;
import disease.datatypes.ConcreteMapIterator;
import disease.utils.datatypes.Pair;
import it.jackbergus.pickstream.PickStream;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
import org.tweetsmining.model.graph.database.logical.GraphDBQueryLayerView;
import org.tweetsmining.model.matrices.IMatrix;

/**
 *
 * @author Giacomo Bergami
 */
public class GraphDBEntityPhisicalLayer implements ERInterfaceLayer {
    
    //private long last = 0;
    private CacheMap<String,PropertyGraph> pg;
    
    //////////////////////////////////////////////////////////////////////////
    private CacheMap<Long,String> node_id_to_node_type;
    private final static String NODE_ID_TO_NODE_TYPE = "node_id_to_node_type.ser";
    
    //////////////////////////////////////////////////////////////////////////
    private CacheMap<String,Map<Long,Entity>> node_type_to_node_id_to_object;
    private final static String NODE_TYPE_TO_NODE_ID_TO_OBJECT = "node_type_to_node_id_to_object.ser";
    
    private int choice;
    /**
     *
     * @param path          Path where to persist the graph database
     * @param temporary     If the persistency should be temporary (memory usage) or not
     * @param N             If zero, returns a mutable graph, otherwise a fixed-N size graph
     */
    public GraphDBEntityPhisicalLayer(String path, boolean temporary, int N) {

        choice = N;
        if (!path.endsWith(File.separator))
            this.path = path + File.separator;
        else
            this.path = path;
        
        System.out.println("Initializing graph "+ path + " which is " + (temporary? "temporary": "not temporary") + " and has N="+N);
        
        if (!temporary) {
        //File f = new File(path+NODE_ID_TO_NODE_TYPE);
        //if (!f.exists())
        //    node_id_to_node_type = new TreeMap<>();
        //else 
        //    node_id_to_node_type =  Storage.unserialize(path+NODE_ID_TO_NODE_TYPE);
         node_id_to_node_type = CacheBuilder.createMultigraphCacheBuilder(this.path+NODE_ID_TO_NODE_TYPE,s->Long.parseLong(s));
        //f = new File(path+NODE_TYPE_TO_NODE_ID_TO_OBJECT);
        //if (!f.exists())
        //    node_type_to_node_id_to_object = new TreeMap<>();
        // else 
        //    node_type_to_node_id_to_object =  Storage.unserialize(path+NODE_TYPE_TO_NODE_ID_TO_OBJECT);
            node_type_to_node_id_to_object = CacheBuilder.createMultigraphCacheBuilder(this.path+NODE_TYPE_TO_NODE_ID_TO_OBJECT,s->s);
        } else {
            node_id_to_node_type = CacheBuilder.createTmpCacheBuilder(s->Long.parseLong(s));
            node_type_to_node_id_to_object =  CacheBuilder.createTmpCacheBuilder(s->s);
        }
        pg = CacheBuilder.createMultigraphCacheBuilder(this.path+"pg.graph",s->s);
    }
    
    
    
    /**
     * Generates a persistent GraphDB at a given location
     * @param path 
     */
    public GraphDBEntityPhisicalLayer(String path, int N) {
        this(path,false,N);
    }
    
    /**
     * Generates a temporary GraphDB Database
     */
    public GraphDBEntityPhisicalLayer(int N) {
        this(CacheBuilder.getnerateTmpFileName(),false,N);
    }
    
     /**
     * Given a class that extends Entity, it returns the istantiated element 
     * inside the model
     * @param c         Class of the desired object
     * @param args      Initialization parameteres (Each object has a required full initialization)
     * @return 
     */
    @Override
    public Entity createNewEntity(Class<? extends Entity> c, Object... args) {
        try {
            PoolID pi = PoolID.challengeId();
            long tmp = pi.get();
            //tmp++;
            //The only required argument for a class that extends Entity is a Long,
            //which stores the node ID
            //System.out.println(c.getConstructors()[0].getParameterTypes()[2].getName()+" "+c.getConstructors()[0].getParameterTypes()[1].getName());
            Constructor<?> builder = c.getConstructor(Long.TYPE,Object[].class); 
            //The constructor automatically calls the superclass, that initializes the 
            //hashcode definition
            Entity e = (Entity)builder.newInstance(tmp,args);
            //Builds the hash code and "truly" initializes the object
            //e.init(args);
            
            node_id_to_node_type.put(tmp, e.getClassName());
            
            if (!node_type_to_node_id_to_object.containsKey(e.getClassName()))
                node_type_to_node_id_to_object.put(e.getClassName(),new TreeMap<>());
            if (!node_type_to_node_id_to_object.get(e.getClassName()).containsKey(tmp))
                node_type_to_node_id_to_object.get(e.getClassName()).put(tmp, e);
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
        this.pg.persist();
        this.node_id_to_node_type.persist();
        this.node_type_to_node_id_to_object.persist();
    }

    @Override
    public void update(Entity e) {
        if (e==null)
            return;
        if (!node_id_to_node_type.containsKey(e.getIndex()))
            throw new RuntimeException("Error: the current Entity has not been created by GraphDBE... (index not present)");
        if (!node_type_to_node_id_to_object.containsKey(e.getClassName()))
            throw new RuntimeException("Error: the current Entity has not been created by GraphDBE... (classname not present)");
        node_type_to_node_id_to_object.get(e.getClassName()).put(e.getIndex(), e);
    }
    
    //////////////////////////////////////////////////////////////////////////
    @Override
    public Set<Long> getIds() {
        return this.node_id_to_node_type.keySet();
    }
    
    @Override
    public Entity getEntity(long e) {
        if (!this.node_id_to_node_type.containsKey(e))
            return null;
        String type = this.node_id_to_node_type.get(e);
        if (!this.node_type_to_node_id_to_object.containsKey(type))
            return null;
        return this.node_type_to_node_id_to_object.get(type).get(e);
    }
    
    @Override
    public Set<Entity> getEntities() {
        return StreamSupport
                .stream(new ConcreteMapIterator<>(this.node_id_to_node_type).spliterator(),false)
                .map((x) -> {
                    return node_type_to_node_id_to_object.get(x.getSecond()).get(x.getFirst());
        }).collect(Collectors.toSet());
    }
    
    
    
    
    //////////////////////////////////////////////////////////////////////////
    private String path;
    
   
    
    @Override
    public boolean remove(Entity e) {
        if (e==null)
            return false;
        boolean val = (node_id_to_node_type.remove(e.getIndex())!=null);
        if (node_type_to_node_id_to_object.containsKey(e.getClassName()))
            node_type_to_node_id_to_object.get(e.getClassName()).remove(e.getIndex());
        //remove all the edges
        for (String key : pg.keySet()) {
            pg.get(key).removeEnt(e.getIndex());
        }
        return val;
    }



    @Override
    public String getName() {
        return "MultiLayerGraph";
    }

    /*@Override
    public Entity getVertex(long name) {
        return getEntity(name);
    }*/

   /* @Override
    public void removeEntity(Entity name) {
        remove(name);
    }*/

    @Override
    public void removeEdge(Relation r) {
        if (pg.containsKey(r.getName()))
            pg.get(r.getName()).rem(r.getSource().getIndex(),r.getDestination().getIndex());
    }

    @Override
    public void clear() {
        for (Pair<String, PropertyGraph> x:new ConcreteMapIterator<>(pg)) {
            x.getSecond().clear();
        }
        pg.clear();
    }

    @Override
    public Relation hasRelation(long src, String name, long dst) {
        if (!pg.containsKey(name))
            return new Relation(getEntity(src),name,getEntity(dst),0);
        return new Relation(getEntity(src),name,getEntity(dst),pg.get(name).get(src, dst));
    }

    @Override
    public Iterator<Relation> getRelations() {
        //Set<Relation> rels = new TreeSet<>();
        /*if (pg.entrySet().isEmpty())
            System.err.println("ERROR: EMPTY ELEMENT");
        */
        ConcreteMapIterator<String, PropertyGraph> l = new ConcreteMapIterator<>(pg);
        Iterator<Relation> toret = null;
        
        for (Pair<String, PropertyGraph> entry : l) {
            
            String relname = entry.getFirst();
            PropertyGraph pg = entry.getSecond();
            System.out.println("entry : "+relname);
            
            //IteratorMapping<Pair<Long, Long>> k = new IteratorMapping<>();
            System.out.println("Creating iterator now...");
            
            
            PickStream<Pair<Long,Long>> k = new PickStream<>(pg.getValueRange());
            Iterator<Relation> tmp = k.map((x)->{
                long si = x.getFirst();
                long di = x.getSecond();
                
                return new Relation(getEntity(si),relname,getEntity(di),pg.get(si, di));}
            ).iterator();
            if (toret==null)
                toret = tmp;
            else
                toret = Iterators.concat(toret, tmp);
        }
        return toret;
        
                /*
        
        for (Pair<String, PropertyGraph> p : new ConcreteMapIterator<>(pg)) {
            PropertyGraph tpg = p.getSecond();
            rels.addAll(tpg.getValueRange().stream().map((x) -> {
                long si = x.getFirst();
                long di = x.getSecond();
                return new Relation(getEntity(si),p.getFirst(),getEntity(di),tpg.get(si, di));
            }).collect(Collectors.toSet()));
        }
        return rels;*/
    }

    @Override
    public ERInterfaceLayer getPhisicalMaster() {
        return this;
    }

    @Override
    public void addEdge(long src, String name, long dst, double value) {
        if (value==0) {
            removeEdge(src,name,dst);
        } else {
            if (!pg.containsKey(name))
                pg.put(name, PropertyGraph.create(name,choice));
            pg.get(name).addEdge(src,dst,value);
        }
    }

    @Override
    public void addEdge(long src, String name, long dst) {
        if (!pg.containsKey(name))
            pg.put(name, PropertyGraph.create(name,choice));
        pg.get(name).addEdge(src,dst);
    }

    @Override
    public void removeEdge(long src, String name, long dst) {
        if (!pg.containsKey(name))
            pg.put(name, PropertyGraph.create(name,choice));
        pg.get(name).addEdge(src,dst);
    }

    @Override
    public Relation getEdge(long src, String name, long dst) {
        if (!pg.containsKey(name))
            return new Relation(getEntity(src),name,getEntity(dst),0);
        else 
            return new Relation(getEntity(src),name,getEntity(dst),pg.get(name).get(src,dst));
    }

    @Override
    public ERInterfaceLayer createView(RDFQuery relations, AEntityQuery entities) {
        return new GraphDBQueryLayerView(this,relations,entities);
    }

    @Override
    public Set<Entity> getOutSet(long entity) {
        Iterator<Map.Entry<String, PropertyGraph>> j = pg.getMapIterator();
        Set<Entity> toret = new HashSet<>();
        while (j.hasNext()) {
            PropertyGraph elem = j.next().getValue();
            Set<Long> ispect = elem.getOut(entity);
            toret.addAll(ispect.stream().map((x)->{return getEntity(x);})
                .collect(Collectors.toSet()));
        }
        return toret;
    }
    

    @Override
    public Set<Entity> getInSet(long entity) {
        return pg.values().stream()
                .map((x)->{return x.getIn(entity);})
                .flatMap(Collection::stream)
                .map((x)->{return getEntity(x);})
                .collect(Collectors.toSet());
    }

    @Override
    public IMatrix getRelationMatrix(String rel) {
        return this.pg.get(rel).getCore();
    }

    /*
    @Override
    public void updateRelation(String layer, double[][] m, int size) {
        pg.put(layer,new PropertyGraph(layer,new MatrixMatrix(size,m)));
    }
*/

    @Override
    public Iterator<Relation> iterator() {
        return this.getRelations();
    }

    
}
