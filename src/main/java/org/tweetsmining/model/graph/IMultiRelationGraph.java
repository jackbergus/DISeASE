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
package org.tweetsmining.model.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.tweetsmining.model.graph.database.Entity;
import org.tweetsmining.model.graph.database.Relation;
import org.tweetsmining.model.matrices.GuavaMatrix;
import org.tweetsmining.model.matrices.IMatrix;

/**
 *
 * @author vasistas
 */
public interface IMultiRelationGraph extends Iterable<Relation> {
    
    public String getName();
    
        
    /**
     * 
     * @param name
     * @return True if the element is present, false otherwise
     */
    public boolean remove(Entity name);
    public Entity createNewEntity(Class<? extends Entity> c, Object... args);
    public Set<Long> getIds();
    public Collection<Entity> getEntities();
    public Entity getEntity(long e);
    //public Entity getVertex(long name);
    //void removeEntity(Entity name);
    
    public Relation hasRelation(long src, String name, long dst);
    public Iterator<Relation> getRelations();
    
    
    public void addEdge(long src, String name, long dst, double value);
    public void addEdge(long src, String name, long dst);
    public Relation getEdge(long src, String name, long dst);
    public void removeEdge(Relation r);
    public void removeEdge(long src, String name, long dst);
    public void clear();
    
    public Set<Entity> getOutSet(long entity);
    public Set<Entity> getInSet(long entity);
    
    public IMatrix getRelationMatrix(String rel);
    
}
