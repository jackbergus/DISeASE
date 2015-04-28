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
package disease.datatypes.serializabletree;

import com.blogspot.mydailyjava.guava.cache.jackbergus.StackCache;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author vasistas
 */
public abstract class DFSVisit<K,V> {
    
    private Tree<K,V> t;
    private Stack<TreeNode<K,V>> visit_order = new Stack<>();
    private StackCache<K> get_ancestors;
    
    private int lvl = 0;
    public DFSVisit(Tree<K,V> t,Function<String,K> conv) {
        this.t = t;
        get_ancestors = new StackCache<>(conv);
    }
    
    protected abstract void visit(TreeNode<K,V> n, Stack<K> get_ancestors);
    
    public void run(K id) {
        TreeNode<K,V> root = t.getNode(id);
        visit_order.add(root);
        while (!visit_order.empty()) {
            TreeNode<K,V> current = visit_order.pop();
            get_ancestors.add(current.getKey());
            visit(current,get_ancestors);
            get_ancestors.pop();
            if (!current.getChildren().isEmpty()) {
                Set<TreeNode<K,V>> s = current.getChildren().stream().map((x)->{return t.getNode(x);}).collect(Collectors.toSet());
                //if (s.size()>10)
                //    System.out.println(s.size());
                visit_order.addAll(s);
            } 
        }
    }
    
}
