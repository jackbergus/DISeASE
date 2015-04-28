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
package disease.utils;

import disease.ontologies.ICD9CMCode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author vasistas
 */
public class MedicalRecord {
    
    private String emergency_room_record;
    private Set<ICD9CMCode> codes;
    private Map<ICD9CMCode,Double> learned;
    
    public MedicalRecord(String description, Set<ICD9CMCode> code) {
        this.emergency_room_record = description;
        this.codes = code;
        this.learned = new HashMap<>();
    }
    
    public MedicalRecord(String description) {
        this(description,new HashSet<>());
    }
    
    public boolean hasCode() {
        return (!codes.isEmpty());
    }
    
    public void setLearnedCode(ICD9CMCode code, double weight) {
        this.learned.put(code, weight);
    }
    
    public Set<ICD9CMCode> getCodes() {
        return this.codes;
    }
    
    public String getCleanedRecord() {
        return this.emergency_room_record;
    }
    
    public Set<Map.Entry<ICD9CMCode, Double>> getLearnedCodes() {
        return this.learned.entrySet();
    }
    
}
