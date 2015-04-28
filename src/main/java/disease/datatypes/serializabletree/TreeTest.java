/*
 * Copyright (C) 2015 Giacomo Bergami
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

import disease.utils.Storage;
import java.util.Iterator;

/**
 *
 * @author Giacomo Bergami
 */
public class TreeTest {
    
    public static void main(String args[]) {
        {
            Tree<String,Double> td = new Tree();
            td.addNode("test", "",123.4);
            td.addNode("test/reckon", "test", 123.45);
            td.addNode("test/ize", "test", 123.45);
            td.addNode("test/reckon/ono", "test/reckon", 123.00);
            td.addNode("test/ize/bene", "test/ize", 123.4);
            td.setTraversalStrategy(TraversalStrategy.BREADTH_FIRST);
            Iterator<TreeNode<String,Double>> i = td.iterator("test");
            while  (i.hasNext()) {
                TreeNode<String,Double> n = i.next();
                System.out.println(n);
            }
            td.serialize("tree.ser");
        }
        Tree<String,Double> tk = new Tree<>("tree.ser");
        tk.setTraversalStrategy(TraversalStrategy.BREADTH_FIRST);
        Iterator<TreeNode<String,Double>> i = tk.iterator("test");
        while  (i.hasNext()) {
            TreeNode<String,Double> n = i.next();
            System.out.println(n);
        }
    }
    
}
