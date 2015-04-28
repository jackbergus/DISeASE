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
package manhoutbook;

import disease.utils.datatypes.Pair;

/**
 *
 * @author vasistas
 */
public class LogitResult extends Pair<Double,Double> {

    public LogitResult(Double ok, Double ko) {
        super(ok, ko);
    }
    
    public double getProbOkScore() {
        return super.getFirst();
    }
    
    public double getProbKoScore() {
        return super.getSecond();
    }
    
    public int toInt() {
        return (super.getFirst()>super.getSecond() ? 1 : 0);
    }
    
    
}
