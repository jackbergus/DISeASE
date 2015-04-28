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
package org.tweetsmining.model.graph.database.eQuery.relation;

import org.tweetsmining.model.graph.database.eQuery.*;
import org.tweetsmining.model.graph.database.Relation;
import org.tweetsmining.model.graph.database.logical.ERInterfaceLayer;

/**
 *
 * @author vasistas
 */
public class RelationHasValue extends ARelationQuery{

    private BinaryOp p;
    private final Object val;
    public RelationHasValue(BinaryOp bo, Object value) {
        this.p = bo;
        this.val = value;
    }

    /**
     * 
     * @param e
     * @return 
     */
    @Override
    protected boolean prop(Relation e) {
        return p.op(e, val);
    }
    
}
