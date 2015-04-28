/*
 * Copyright (C) 2007-2014 by Brett Alistair Kromkamp <brett@polishedcode.com>.
 *               2015      by Giacomo Bergami
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

package disease.datatypes.serializabletree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

/*
 * See URL: http://en.wikipedia.org/wiki/Depth-first_search
 */

public class DepthFirstTreeIterator<K,V> implements Iterator<TreeNode<K,V>> {
    private Stack<TreeNode<K,V>> stack;
    HashMap<K, TreeNode<K,V>> tree_ref;

    public DepthFirstTreeIterator(HashMap<K, TreeNode<K,V>> tree, K identifier) {
        stack = new Stack<>();
        tree_ref = tree;
        if (tree.containsKey(identifier)) {
            stack.push(tree.get(identifier));
        }
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public TreeNode<K,V> next() {
        TreeNode<K,V> toret = stack.pop();
        for (K x:toret.getChildren()) {
            stack.push(tree_ref.get(x));
        }
        return toret;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}