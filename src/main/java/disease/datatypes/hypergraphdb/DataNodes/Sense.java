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
public class Sense extends Entity {
    
    private String content = "";
    @Deprecated
    public void setContent(String s) {
        this.content = s;
    }
    public String getContent() {
        return content;
    }
    
    private String expandedContent = "";
    /*public void setExpandedContent(String s) {
        this.expandedContent = s;
    }*/
    public String getExpandedContent() {
        return expandedContent;
    }
    
    private String term = "";
    @Deprecated
    public void setTerm(String s) {
        this.term = s;
    }
    public String getTerm() {
        return this.term;
    }
    
    private String expandedTerm = "";
    @Deprecated
    public void setExpandedTerm(String s) {
        this.expandedTerm = s;
    }
    public String getExpandedTerm() {
        return this.expandedTerm;
    }
    
    public static Object[] createArrayArgs(String content,String ec, String term) {
        return new Object[]{content,ec,term};
    }
    
    public Sense(long pos, Object[] initialization_args) {
        super(pos, initialization_args);
        this.content = (String)initialization_args[0];
        this.expandedContent = (String)initialization_args[1];
        this.term = (String)initialization_args[2];
    }
    
    @Deprecated
    public Sense() {
        super(-1,(Object[])null);
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Sense))
            return false;
        Sense s = (Sense)o;
        return (content.equals(s.content) && term.equals(s.term) && expandedContent.equals(s.expandedContent) && expandedTerm.equals(s.expandedTerm));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.content);
        hash = 29 * hash + Objects.hashCode(this.expandedContent);
        hash = 29 * hash + Objects.hashCode(this.term);
        hash = 29 * hash + Objects.hashCode(this.expandedTerm);
        return hash;
    }

    
    @Override
    public String toString() {
        return this.term+" (as) "+this.content;
    }

}
