/*
 * Copyright (C) 2015 Giacomo Bergami <giacomo@openmailbox.org>
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
 *
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public class TrainingResult {
    
    private double confidence;
    private int correctness;
    private String code;
    
    /**
     * Returns the result of the scoring of a given code
     * @param conf  Confidence value
     * @param supp
     * @param c 
     */
    public TrainingResult(double conf, int supp, String c) {
        this.confidence = conf;
        this.correctness = supp;
        this.code = c;
    }


    /**
     * @return the confidence
     */
    public double getConfidence() {
        return confidence;
    }

    /**
     * @param confidence the confidence to set
     */
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }


    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the correctness
     */
    public int getCorrectness() {
        return correctness;
    }

    /**
     * @param correctness the correctness to set
     */
    public void setCorrectness(int correctness) {
        this.correctness = correctness;
    }
    
    
}
