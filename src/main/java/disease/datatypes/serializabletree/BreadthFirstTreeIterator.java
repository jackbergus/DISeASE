/*
 * Copyright (C) 2007-2014 by Brett Alistair Kromkamp <brett@polishedcode.com>.
 *                    2015 by Giacomo Bergami 
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

import java.util.*;

/*
 * See URL: http://en.wikipedia.org/wiki/Breadth-first_search
 */

public class BreadthFirstTreeIterator<K,V> implements Iterator<TreeNode<K,V>> {

    private static final int ROOT = 0;

    private Queue<TreeNode<K,V>> queue;
    private HashMap<K, TreeNode<K,V>> tree_ref;

    public BreadthFirstTreeIterator(HashMap<K, TreeNode<K,V>> tree, K identifier) {
        queue = new LinkedList<>();
        tree_ref = tree;

        if (tree.containsKey(identifier)) {
            queue.add(tree.get(identifier));
        }
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public TreeNode<K,V> next() {
        TreeNode<K,V> toret = queue.poll();
        toret.getChildren().stream().forEach((x) -> { 
            queue.add(tree_ref.get(x));
        });
        return toret;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}