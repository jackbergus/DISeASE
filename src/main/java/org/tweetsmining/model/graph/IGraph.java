/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tweetsmining.model.graph;

import org.tweetsmining.model.graph.database.Entity;


/**
 *
 * @author gyankos
 */
public interface IGraph {
    
    public String getName();
    public void addEdge(Entity left, Entity right, double value);
    public void addEdge(Entity left, Entity right);
    public void removeEdge(Entity left, Entity right);
    public double getEdge(Entity left, Entity right);
    public void clear();
    
}
