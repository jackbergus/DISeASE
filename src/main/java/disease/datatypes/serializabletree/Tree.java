/*
 * Copyright (C) created 2007-2014 by Brett Alistair Kromkamp <brett@polishedcode.com>
 * modified 2015 by Alexander Pollok <alexander.pollok@gmail.com>
 *                  Giacomo Bergami <giacomo@openmailbox.org>
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

package disease.datatypes.serializabletree;

import disease.utils.Storage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Tree<K,V> implements Serializable {

    private final static int ROOT = 0;

    public HashMap<K, TreeNode<K,V>> nodes;
    private int size;
    private transient TraversalStrategy traversalStrategy;

    // Constructors
    public Tree() {
        this(TraversalStrategy.DEPTH_FIRST);
        this.size = 0;
    }

    public Tree(TraversalStrategy traversalStrategy) {
        this.nodes = new HashMap<>();
        this.traversalStrategy = traversalStrategy;
    }
    
    public Tree(String path) {
        Tree<K,V> tmp = Storage.<Tree<K,V>>unserialize(path);
        this.nodes = tmp.nodes;
        this.size = tmp.size;
        this.traversalStrategy = TraversalStrategy.DEPTH_FIRST;
        System.out.println("Node #:"+size+" -- "+nodes.size());
    }
    
    public void serialize(String path) {
        Storage.<Tree<K,V>>serialize(this,path);
    }

    public TreeNode<K,V> getNode(K key) {
        return this.nodes.get(key);
    }
    
    // Properties
    public HashMap<K, TreeNode<K,V>> getNodes() {
        return nodes;
    }

    public TraversalStrategy getTraversalStrategy() {
        return traversalStrategy;
    }

    public void setTraversalStrategy(TraversalStrategy traversalStrategy) {
        this.traversalStrategy = traversalStrategy;
    }

    // Public interface
    /*public TreeNode<K,V> addNode(K identifier, V icd) {
    	size++;
        return this.addNode(identifier, null, icd);
    }*/

    public TreeNode<K,V> addNode(K identifier, K parent, V icd) { 
        if (this.nodes.containsKey(identifier)) { 
            return this.nodes.get(identifier); 
        } else { 
            size++; 
            TreeNode<K,V> node = new TreeNode<>(identifier, icd, parent); 
            nodes.put(identifier, node);
            
            if (parent != null) 
                { nodes.get(parent).addChild(identifier); }

            return node; 
        } 
    }
    
    public TreeNode<K,V> getParent(TreeNode<K,V> node) {
        if (!node.hasFather())
            return null;
        else return nodes.get(node.getFather());
    }
    
    public int getSize(){
    	return size;
    }

    public void display(K identifier) {
        this.display(identifier, ROOT);
    }

    public void display(K identifier, int depth) {
        List<K> children = nodes.get(identifier).getChildren();

        if (depth == ROOT) {
            System.out.println(nodes.get(identifier).getKey());
        } else {
            String tabs = String.format("%0" + depth + "d", 0).replace("0", "    "); // 4 spaces
            System.out.println(tabs + nodes.get(identifier).getKey());
        }
        depth++;
        for (K child : children) {
            // Recursive call
            this.display(child, depth);
        }
    }

    public Iterator<TreeNode<K,V>> iterator(K identifier) {
        return this.iterator(identifier, traversalStrategy);
    }

    public Iterator<TreeNode<K,V>> iterator(K identifier, TraversalStrategy traversalStrategy) {
        return traversalStrategy == TraversalStrategy.BREADTH_FIRST ?
                new BreadthFirstTreeIterator(nodes, identifier) :
                new DepthFirstTreeIterator(nodes, identifier);
    }
}