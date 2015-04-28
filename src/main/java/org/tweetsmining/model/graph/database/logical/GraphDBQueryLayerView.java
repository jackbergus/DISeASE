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
import disease.utils.IteratorMapping;
import it.jackbergus.pickiterator.PickIterable;
import it.jackbergus.pickiterator.PickIterator;
import it.jackbergus.pickiterator.specialization.NormalizeIterator;
import it.jackbergus.pickstream.PickStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.tweetsmining.model.graph.database.Entity;
import org.tweetsmining.model.graph.database.Relation;
import org.tweetsmining.model.graph.database.eQuery.entity.AEntityQuery;
import org.tweetsmining.model.graph.database.eQuery.relation.RDFQuery;
import org.tweetsmining.model.graph.database.phisical.GraphDBEntityPhisicalLayer;
import org.tweetsmining.model.matrices.IMatrix;

/**
 *
 * @author vasistas
 */
public class GraphDBQueryLayerView implements ERInterfaceLayer {

    private RDFQuery qrel;
    private AEntityQuery qent;
    private ERInterfaceLayer master;
    private GraphDBEntityPhisicalLayer extension;
    private GraphDBEntityCollectionLayer master_removed;
    //private CacheMap<Long,Entity> updatedEntity;
    
    public GraphDBQueryLayerView(ERInterfaceLayer master,RDFQuery relations,AEntityQuery entities) {
        this.qrel = relations;
        this.qent = entities;
        this.master = master;
        this.extension = new GraphDBEntityPhisicalLayer(0); //tmp and mutable
        this.master_removed = new GraphDBEntityCollectionLayer(this.master); //tmp
        //this.updatedEntity = CacheBuilder.createTmpCacheBuilder();
    }
    
    
    private boolean checkRelation(Relation r){
        if (r==null)
            return false;
        //Case of a missing element, not matched by the query
        if (r.getSource() == null || r.getDestination() == null)
            return false;
        return (qrel.prop(r) 
                    &&  checkEntityInQ(r.getSource()) 
                    && checkEntityInQ(r.getDestination()));
    }
    
    private boolean checkEntityInQ(Entity e) {
        if (e==null)
            return false;
        return qent.prop(e);
    }
    
    
    /**
     * Returns the master view elements with the eventual updates
     * @param e
     * @return 
     */
    private Entity getUpdatedEntity(long e) {
        Entity toret = extension.getEntity(e);
        if (toret!=null)
            return toret;
        if (master_removed.getEntity(e)!=null)
            return null;
        toret = master.getEntity(e);
        if ((toret==null)||(!checkEntityInQ(toret)))
            return null;
        return toret;
    }
    
    /**
     * Returns the master view elements with the eventual updates
     * @param r
     * @return 
     */
    private Relation getUpdatedRelation(Relation r) {
        long si = 0, di = 0;
        try {
            si = r.getSource().getIndex();
            di = r.getDestination().getIndex();
        } catch (Throwable t) {
            return null;
        }
        if (getEntity(si)==null||getEntity(di)==null)
            return new Relation(r.getSource(),r.getName(),r.getDestination(),0);
        Relation toret = this.extension.hasRelation(si, r.getName(), di);
        if (toret!=null)
            return toret;
        toret = master_removed.hasRelation(si, r.getName(), di);
        if (toret!=null)
            return null;
        toret = master.hasRelation(si, r.getName(), di);
        if ((toret==null)||(!qrel.prop(toret)))
            return null;
        
        return toret;
    }

    @Override
    public Set<Long> getIds() {
        return getEntities().stream()
                .map((x)->{return x.getIndex();})
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Entity> getEntities() {
        Set<Entity> toret = new TreeSet<>();
        toret.addAll(extension.getEntities());
        //toret.addAll(updatedEntity.values());
        //Set<Long> ue = updatedEntity.keySet();
        Collection<Entity> mrIds = master_removed.getEntities();
        Set<Long> ue = extension.getIds();
        //
        toret.addAll(master.getEntities().stream()
                .filter((Entity x) -> (checkEntityInQ(x))&&(!mrIds.contains(x)&&!ue.contains(x.getIndex())))
                .collect(Collectors.toSet()));
       
        return toret;
    }

    @Override
    public Entity getEntity(long e) {
        Entity toret = extension.getEntity(e);
        return (toret!=null ? toret : getUpdatedEntity(e));
    }


    @Override
    public boolean remove(Entity name) {
        if (!extension.remove(name)) {
            if (master_removed.getEntity(name.getIndex())!=null)
                return false;
            else {
                master_removed.addEntity(name);
                return true;
            }
        } 
        return true;
    }
    
    @Override
    public Iterator<Relation> getRelations() {
        PickStream<Relation> k = new PickStream<>(this.master);
        k = k.contatenate(new PickIterable<>(this.extension));
        k =  k.filter((x) -> (checkRelation(x))&&(master_removed.hasRelation(x.getSource().getIndex(), x.getName(), x.getDestination().getIndex())).getWeight()==0);
        NormalizeIterator<Relation> normalizeIterator = new NormalizeIterator<>(k.get().iterator());
       //Calling the iterator of the PickStream
        return normalizeIterator;
    }

    @Override
    public ERInterfaceLayer getPhisicalMaster() {
        return this.master.getPhisicalMaster();
    }

    @Override
    public Entity createNewEntity(Class<? extends Entity> c, Object... args) {
        return extension.createNewEntity(c, args);
    }

    @Override
    public void save() {
        extension.save();
        //updatedEntity.persist();
        master_removed.save();
    }

    @Override
    public void update(Entity e) {
        if (this.master_removed.getEntity(e.getIndex())!=null)
            this.master_removed.remove(e);
        this.extension.update(e);
    }

    @Override
    public String getName() {
        return this.master.getName()+" - QueryView";
    }

/*    @Override
    public Entity getVertex(long name) {
        return getEntity(name);
    }
*/
/*    @Override
    public void removeEntity(Entity name) {
        remove(name);
    }
*/
    /**
     * It clears the view
     */
    @Override
    public void clear() {
        extension.clear();
        master_removed.clear();
    }

    @Override
    public void removeEdge(Relation r) {
        removeEdge(r.getSource().getIndex(),r.toString(),r.getDestination().getIndex());
    }

    @Override
    public Relation hasRelation(long src, String name, long dst) {
        Relation blank = new Relation(getEntity(src),name,getEntity(dst),0);
        Relation toret = getUpdatedRelation(blank);
        if (toret!=null)
            return toret;
        toret = extension.hasRelation(src, name, dst);
        return (toret==null ? blank : toret);
    }

    @Override
    public void addEdge(long src, String name, long dst, double value) {
        if (value==0)
            removeEdge(src,name,dst);
        else {
            extension.addEdge(src, name, dst, value);
        }
    }

    @Override
    public void addEdge(long src, String name, long dst) {
        addEdge(src,name,dst,1);
    }

    @Override
    public void removeEdge(long src, String name, long dst) {
        Relation torem = extension.hasRelation(src, name, dst);
        if (torem!=null) {
            extension.removeEdge(torem);
        } else {
            if (master_removed.hasRelation(src, name, dst)!=null) {
                master.removeEdge(src, name, dst);
            }
        }
    }

    @Override
    public Relation getEdge(long src, String name, long dst) {
        return hasRelation(src,name,dst);
    }

    @Override
    public ERInterfaceLayer createView(RDFQuery relations, AEntityQuery entities) {
        return new GraphDBQueryLayerView(this,relations,entities);
    }
    
    @Override
    public Set<Entity> getOutSet(long entity) {
        /*
        return IteratorMapping.<Relation>iteratorToStream(getRelations())
                .filter((x)->x.getSource()!=null&&x.getSource().getIndex()==entity&&x.getDestination()!=null)
                .map((x)->x.getDestination())
                .collect(Collectors.toSet());*/
        Set<Entity> toret = new HashSet<>();
        for (Relation x : this) {
            System.out.println("What");
            if (x.getSource()!=null&&x.getSource().getIndex()==entity&&x.getDestination()!=null) {
                toret.add(x.getDestination());
            }
        }
        return toret;
        
    }
    

    @Override
    public Set<Entity> getInSet(long entity) {
        PickStream<Relation> k = new PickStream<>(this);
        return k
                .filter((x)->x.getDestination()!=null&&x.getDestination().getIndex()==entity&&x.getSource()!=null)
                .map((x)->x.getDestination())
                .collect(Collectors.toSet());
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
