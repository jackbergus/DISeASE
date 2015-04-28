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
package disease.utils.datatypes;

import java.util.Objects;

/**
 *
 * @author vasistas
 */
public class Pair<T,K>  {
    private T fst;
    private K snd;
    
    public Pair(T first, K second) {
        this.fst = first;
        this.snd = second;
    }
    
    public T getFirst() {
        return this.fst;
    }
    
    public K getSecond() {
        return this.snd;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair))
            return false;
        Pair<T,K> p = (Pair<T,K>)o;
        return (p.fst.equals(fst) && p.snd.equals(snd));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.fst);
        hash = 59 * hash + Objects.hashCode(this.snd);
        return hash;
    }
    
}
