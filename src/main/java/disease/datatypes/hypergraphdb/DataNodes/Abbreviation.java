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
package disease.datatypes.hypergraphdb.DataNodes;


import java.util.Objects;
import org.tweetsmining.model.graph.database.Entity;

/**
 *
 * @author Giacomo Bergami
 */
public class Abbreviation extends Entity {
    private String abbreviation;

    
    
    public String getAbbreviation() {
        return this.abbreviation;
    }
    /*public void setAbbreviation(String a) {
        this.abbreviation = a;
    }*/
    
    private String expansion;
    public String getExpansion() {
        return this.expansion;
    }
    /*public void setExpansion(String e) {
        this.expansion = e;
    }*/
    
    private String expandedExpansion;
    public String getExpandedExpansion() {
        return this.expandedExpansion;
    }
    /*public void setExpandedExpansion(String e) {
        this.expandedExpansion = e;
    }*/
    
    public Abbreviation(long pos, Object[] array) {
        super(pos,array);
        this.abbreviation = (String)array[0];
        this.expansion = (String)array[1];
        this.expandedExpansion = (String)array[2];
    }
    
    public static Object[] createArrayArgs(String abbreviation, String expansion, String expandedExpansion) {
        return new Object[]{abbreviation,expansion,expandedExpansion};
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.abbreviation);
        hash = 97 * hash + Objects.hashCode(this.expansion);
        hash = 97 * hash + Objects.hashCode(this.expandedExpansion);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Abbreviation other = (Abbreviation) obj;
        if (!Objects.equals(this.abbreviation, other.abbreviation)) {
            return false;
        }
        if (!Objects.equals(this.expansion, other.expansion)) {
            return false;
        }
        if (!Objects.equals(this.expandedExpansion, other.expandedExpansion)) {
            return false;
        }
        return true;
    }
    
}
