/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tweetsmining.model.graph.database;

import java.util.Arrays;
import java.util.Objects;
import org.tweetsmining.model.graph.database.logical.ERInterfaceLayer;
import org.tweetsmining.model.graph.database.phisical.GraphDBEntityPhisicalLayer;

/**
 * Commodity class for the Jena Resources (subject/object)
 * @author gyankos
 */
public abstract class Entity implements IGraphComponent {
    
    private ERInterfaceLayer theJoker;
    private static final long serialVersionUID = 2079110959550063321L;
    private String entityType;
    private long ID;
    private final Object[] initialization_args;
    
    
    /**
     * Crea un'entità all'interno del modello
     * @param entityType   The base element entity type 
     * @param pos          number of the entity
     * @param initialization_args
     */
    public Entity(long pos, Object[] initialization_args) {
        this.entityType = getClass().getName(); //retrieve automaticall the name of the subclass
        this.ID = pos; //adds automatically the 
        this.initialization_args = initialization_args;
    }
    
    
    public final void update() {
        theJoker.update(this);
    }
    
    public String getClassName() {
        return entityType;
    }
    
    public int size() {
        return this.initialization_args.length;
    }
    
    public long getIndex() {
        return ID;
    }
    
    public Object getArg(int pos) {
        if (initialization_args.length<=pos || pos<0)
            return null;
        return initialization_args[pos];
    }
    
    public Object[] getArgs() {
        return initialization_args;
    }
    
    public void setArg(int pos, Object o) {
        if (initialization_args.length<=pos || pos<=0)
            return;
        this.initialization_args[pos] = o;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.entityType);
        hash = 97 * hash + (int) (this.ID ^ (this.ID >>> 32));
        hash = 97 * hash + Arrays.deepHashCode(this.initialization_args);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Entity other = (Entity) obj;
        if (!Objects.equals(this.entityType, other.entityType)) {
            return false;
        }
        if (this.ID != other.ID) {
            return false;
        }
        if (!Arrays.deepEquals(this.initialization_args, other.initialization_args)) {
            return false;
        }
        return true;
    }

    public void remove() {
        theJoker.remove(this);
    }

    
}
