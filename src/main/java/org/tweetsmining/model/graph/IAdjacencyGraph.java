/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tweetsmining.model.graph;

import org.tweetsmining.model.matrices.IMatrix;

/**
 *
 * @author gyankos
 */
public interface IAdjacencyGraph extends IGraph, IMatrix {
 
    public boolean has(long i, long j);
    public void addEdge(long left, long right);
    public void addEdge(long left, long right, double value);
    
}
