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
package disease.ontologies;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 *
 * @author vasistas
 */
public class ICD9CMCode  implements Comparable<ICD9CMCode>, Serializable  {
    
    //private transient ICD9Tree t = null; //not serializing a reference
    private String code = "";
    
    public ICD9CMCode(String all) {
        try {
            if (Pattern.compile("\\d\\d\\d(\\d)+").matcher(all).matches()) {
                code = all.substring(0, 3)+"."+all.substring(3);
            } else if (Pattern.compile("\\d\\d\\d").matcher(all).matches())
                this.code = all;
            else if (Pattern.compile("\\d\\d\\d.(\\d)+").matcher(all).matches()) {
                this.code = all;
            } else 
                this.code = "";
        } catch (Throwable ex) {
            ex.printStackTrace();
            System.err.println("Error with code: ["+ all+"]");
            throw ex;
        }
        //System.out.println("------"+this.real_value);
    }
    
    public ICD9CMCode() {
        this("");
    }
    
    public void setCode(String all) {
        if (Pattern.compile("\\d\\d\\d(\\d)+").matcher(all).matches()) {
            code = all.substring(0, 3)+"."+all.substring(3);
        } else if (Pattern.compile("\\d\\d\\d").matcher(all).matches())
            this.code = all;
        else if (Pattern.compile("\\d\\d\\d.(\\d)+").matcher(all).matches()) {
            this.code = all;
        } else 
            this.code = "";
    }
    public String getCode() {
        return this.code;
    }

    public boolean hasOnlyThreeDigits() {
        return (this.code.length()==3);
    }
    
    public boolean isEmpty() {
        return (this.code.length()==0);
    }

    public ICD9CMCode getThreeDigitsFather() {
        if (hasOnlyThreeDigits())
            return this;
        else{
            if (isEmpty())
                return new ICD9CMCode();
            else
                return new ICD9CMCode(code.substring(0, 3));
        }
    }
    
    @Override
    public String toString() {
        return code;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ICD9CMCode))
            return false;
        ICD9CMCode extern = (ICD9CMCode)o;
        return toString().equals(extern.toString());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.code);
        return hash;
    }

    @Override
    public int compareTo(ICD9CMCode o) {
        return toString().compareTo(o.toString());
    }
    
}
