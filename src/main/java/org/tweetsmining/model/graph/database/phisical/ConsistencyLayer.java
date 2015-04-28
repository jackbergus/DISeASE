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
package org.tweetsmining.model.graph.database.phisical;

import com.blogspot.mydailyjava.guava.cache.jackbergus.CacheMap;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.tweetsmining.model.graph.database.Entity;
import org.tweetsmining.model.graph.database.Relation;
import org.tweetsmining.model.graph.database.cache.CacheBuilder;
import org.tweetsmining.model.graph.database.eQuery.entity.AEntityQuery;
import org.tweetsmining.model.graph.database.eQuery.relation.RDFQuery;
import org.tweetsmining.model.graph.database.logical.ERInterfaceLayer;
import org.tweetsmining.model.matrices.GraphViewMatrix;
import org.tweetsmining.model.matrices.GuavaMatrix;
import org.tweetsmining.model.matrices.IMatrix;

/**
 *
 * @author vasistas
 */
public class ConsistencyLayer implements  ERInterfaceLayer{
    
    //maps a member hashcode into its consistencyMap entity
    private CacheMap<Integer,Set<Entity>> consistencyMap;
    private final static String CONSISTENCY_MAP = "consistency_map.ser";
    
    private ERInterfaceLayer master;
    
    private String path;
    
    public ConsistencyLayer(String path, boolean temporary, ERInterfaceLayer master) {

        if (!path.endsWith(File.separator))
            this.path = path + File.separator;
        else
            this.path = path;
        
        consistencyMap = CacheBuilder.createMultigraphCacheBuilder(this.path+CONSISTENCY_MAP,string->Integer.parseInt(string));
        this.master = master;
    }
    
    public ConsistencyLayer(String path, boolean temporary, int N) {

        if (!path.endsWith(File.separator))
            this.path = path + File.separator;
        else
            this.path = path;
        
        consistencyMap = CacheBuilder.createMultigraphCacheBuilder(this.path+CONSISTENCY_MAP,string->Integer.parseInt(string));
        this.master = new GraphDBEntityPhisicalLayer(path,temporary,N);
    }

    @Override
    public ERInterfaceLayer createView(RDFQuery relations, AEntityQuery entities) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ERInterfaceLayer getPhisicalMaster() {
        return this.master;
    }

    @Override
    public void save() {
        this.master.save();
        this.consistencyMap.persist();
    }

    @Override
    public void update(Entity e) {
        
    }

    @Override
    public String getName() {
        return path;
    }

    @Override
    public boolean remove(Entity name) {
        return this.master.remove(name);
    }

    @Override
    public Entity createNewEntity(Class<? extends Entity> c, Object... args) {
        Entity tmp = this.master.createNewEntity(c, args), toret = null;
        int hash = tmp.hashCode();
        Set<Entity> s = this.consistencyMap.get(hash);
        if (s==null) {
            Set<Entity> l = new TreeSet<>();
            l.add(tmp);
            this.consistencyMap.put(hash, l);
            return tmp;
        } else {
            Set<Entity> tmp2 = s.stream().filter((x)->(x.equals(tmp))).collect(Collectors.toSet());
            if (tmp2.size()>1) {
                System.err.println("Equality for entity has not been well implemented: "+c.getName());
            }
            Iterator<Entity> it = tmp2.iterator();
            this.master.remove(tmp);//remove double
            return it.next();
        }
    }
    

    @Override
    public Set<Long> getIds() {
        return this.master.getIds();
    }

    @Override
    public Collection<Entity> getEntities() {
        return this.master.getEntities();
    }

    @Override
    public Entity getEntity(long e) {
        return master.getEntity(e);
    }

    @Override
    public Relation hasRelation(long src, String name, long dst) {
        return master.hasRelation(src, name, dst);
    }

    @Override
    public Iterator<Relation> getRelations() {
        return master.getRelations();
    }

    @Override
    public void addEdge(long src, String name, long dst, double value) {
        master.addEdge(src, name, dst, value);
    }

    @Override
    public void addEdge(long src, String name, long dst) {
        master.addEdge(src, name, dst);
    }

    @Override
    public Relation getEdge(long src, String name, long dst) {
        return master.getEdge(src, name, dst);
    }

    @Override
    public void removeEdge(Relation r) {
        master.removeEdge(r);
    }

    @Override
    public void removeEdge(long src, String name, long dst) {
        master.removeEdge(src, name, dst);
    }

    @Override
    public void clear() {
        this.master.clear();
        consistencyMap.clear();
    }

    @Override
    public Set<Entity> getOutSet(long entity) {
        return this.master.getOutSet(entity);
    }

    @Override
    public Set<Entity> getInSet(long entity) {
        return this.master.getInSet(entity);
    }

    @Override
    public IMatrix getRelationMatrix(String rel) {
        return new GraphViewMatrix(this,rel);
    }

//    @Override
//    public void updateRelation(String layer, double[][] m, int N) {
//        this.master.updateRelation(layer, m, N);
//    }

    @Override
    public Iterator<Relation> iterator() {
        return this.getRelations();
    }
    
}
