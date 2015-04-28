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
package disease.utils;

/**
 * This class defines the annotation of a given set of words
 * @author Giacomo Bergami
 */
public class PipelineChunk {
    
    private String annotationType;
    private String[] values;
    private String semantics;
    private double weight;
    
    public PipelineChunk(String annotationType, String[] values, String assoc, double weight) {
        this.annotationType = annotationType;
        this.values = values;
        this.semantics = assoc;
        this.weight = weight;
    }
    
    public PipelineChunk(String value) {
        this(null,new String[]{value},null,1);
    }
    
    public PipelineChunk(String annotationType, String value, String assoc) {
        this(null,new String[]{value},null,1);
    }
    
    public boolean hasAnnotation() {
        return (this.annotationType!=null && this.semantics!=null);
    }
    
    public void setAnnotation(String type, String sem, double weight) {
        this.annotationType = type;
        this.semantics = sem;
        this.weight = weight;
    }
    
    public PipelineChunk setAnnotation(String type, String sem) {
        this.setAnnotation(type, sem, 1);
        return this;
    }
    
    
    public String getMeaning() {
        return semantics;
    }
    
    public String getType() {
        return annotationType;
    }
    
    public String[] getOriginalText() {
        return values;
    }
    
}
