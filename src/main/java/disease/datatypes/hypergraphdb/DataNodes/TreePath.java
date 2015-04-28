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
public class TreePath extends Entity {
    private String firstTerm;

    
    public String getFirstTerm() {
        return this.firstTerm;
    }
    /*public void setFirstTerm(String s) {
        this.firstTerm = s;
    }*/
    
    private String wholePath;
    public String getWholePath() {
        return this.wholePath;
    }
    /*public void setWholePath(String s){
        this.wholePath = s;
    }*/
    //codice
    
    public static Object[] createTermPath(String firstTerm, String wholePath) {
        return new Object[]{firstTerm,wholePath};
    }
    
    
    public TreePath(long pos, Object[] initialization_args) {
        super(pos, initialization_args);
        this.firstTerm = (String) initialization_args[0];
        this.wholePath = (String) initialization_args[1];
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.firstTerm);
        hash = 29 * hash + Objects.hashCode(this.wholePath);
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
        final TreePath other = (TreePath) obj;
        if (!Objects.equals(this.firstTerm, other.firstTerm)) {
            return false;
        }
        if (!Objects.equals(this.wholePath, other.wholePath)) {
            return false;
        }
        return true;
    }
    
}
