/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tweetsmining.model.graph.database;

import java.util.Objects;

/**
 * The general instance of a Jena Property is seen as a Matrix between the entities
 * @author gyankos
 */
public class Relation implements IGraphComponent {
    
    private Entity src;
    private String lay;
    private Entity dst;
    private double weight;
   
    public Relation(Entity left, String layer, Entity right, double weight) {
        this.lay = layer;
        this.src = left;
        this.dst = right;
        this.weight = weight;
    }
    
    public String getName() {
        return lay;
    }
    
    public Entity getSource() {
        return this.src;
    }
    
    public Entity getDestination() {
        return this.dst;
    }
    
    @Override
    public String toString() {
        return src+" "+lay+" "+dst;
    }
    
    public double getWeight() {
        return this.weight;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.lay);
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
        final Relation other = (Relation) obj;
        if (!Objects.equals(this.lay, other.lay)) {
            return false;
        }
        return true;
    }
    
    
}
