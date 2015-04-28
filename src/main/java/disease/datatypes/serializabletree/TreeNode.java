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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TreeNode<K,V> implements Serializable {

    private K identifier;
    private V icd;
    private List<K> children;
    private K father;

    // Constructor
    public TreeNode(K identifier, V icd, K parent) {
        this.identifier = identifier;
        this.icd = icd;
        this.children = new LinkedList<>();
        this.father = parent;
    }

    // Properties
    public K getKey() {
        return identifier;
    }
    
    public boolean isRoot() {
        try {
            if (father==null)
                return false;
        } catch (Throwable t) {
            return false;
        }
        return father.equals(identifier);
    }
    
    public boolean hasFather() {
        return (!isRoot());
    }
    
    public K getFather() {
        return father;
    }
    
    public V getValue() {
        return icd;
    }

    public List<K> getChildren() {
        return children;
    }

    // Public interface
    public void addChild(K identifier) {
        children.add(identifier);
    }
    
    @Override
    public String toString() {
        return this.identifier+"-"+this.icd;
    }
    
}