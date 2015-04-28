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

import manhoutbook.LogisticRegression;
import manhoutbook.Observation;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;

/**
 *
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public class ToObservation {
    
    private double candidate_generation_score;
    private double phase_part;
    private double ontology_distance;
    private int expected;
    
    private double[] array;
    
    public ToObservation(double candidate_generation_score,
        double phase_part,
        double ontology_distance,
        int have_common_father) {
        array = new double[3];
        array[0] = candidate_generation_score;
        array[1] = phase_part;
        array[2] = ontology_distance;
        expected = have_common_father;
    }
            
    
    public Observation createObservation() {
        DenseVector v = new DenseVector(array);
        return new Observation(v,expected);
    }
    
}
