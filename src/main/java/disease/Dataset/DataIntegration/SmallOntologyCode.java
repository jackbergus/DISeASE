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
package disease.Dataset.DataIntegration;

import disease.ontologies.ICD9CMCode;
import org.tweetsmining.model.graph.database.Entity;

/**
 *
 * @author vasistas
 */
public class SmallOntologyCode extends Entity implements Comparable<SmallOntologyCode>  {

    static final long serialVersionUID = 5998487607835663592L;
    
    private ICD9CMCode code;
    public ICD9CMCode get() {
        return this.code;
    }
    
    public SmallOntologyCode(long pos, Object[] initialization_args) {
        super(pos, initialization_args);
        code = (ICD9CMCode)initialization_args[0];
    }

    @Override
    public int compareTo(SmallOntologyCode o) {
        return code.compareTo(o.get());
    }
    
}
