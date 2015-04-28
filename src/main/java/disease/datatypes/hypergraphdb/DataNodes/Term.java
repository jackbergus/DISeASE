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
package disease.datatypes.hypergraphdb.DataNodes;

import java.util.Objects;
import org.tweetsmining.model.graph.database.Entity;

/**
 *
 * @author vasistas
 */
public class Term extends Entity {
    
    private String term = "";
    public void setTerm(String t) { 
        this.term = t;
    }
    /*public String getTerm() {
        return this.term;
    }*/
    
   private String expandedTerm = "";
    public void setExpandedTerm(String s) {
        this.expandedTerm = s;
    }
    /*public String getExpandedTerm() {
        return this.expandedTerm;
    }*/
    
    @Deprecated
    public Term(String term) {
        super(-1, (Object[])null);
        this.term = term;
    }
    
    public Term(long pos, Object[] initialization_args) {
        super(pos, initialization_args);
        this.term = (String)initialization_args[0];
        this.expandedTerm = (String)initialization_args[1];
    }
    
    public static Object[] createArrayArgs(String term,String expandedTerm) {
        return new Object[]{term,expandedTerm};
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Term))
            return false;
        Term t = (Term)o;
        return (term.equals(t.term) && expandedTerm.equals(t.expandedTerm));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.term);
        hash = 23 * hash + Objects.hashCode(this.expandedTerm);
        return hash;
    }

    
    @Override
    public String toString() {
        return term;
    }
    
}
