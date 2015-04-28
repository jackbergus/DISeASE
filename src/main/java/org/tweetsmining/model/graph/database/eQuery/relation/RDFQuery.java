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

import org.tweetsmining.model.graph.database.Relation;
import org.tweetsmining.model.graph.database.eQuery.entity.AEntityQuery;
import org.tweetsmining.model.graph.database.eQuery.entity.EntityAll;
import org.tweetsmining.model.graph.database.logical.ERInterfaceLayer;

/**
 *
 * @author vasistas
 */
public class RDFQuery {
    
    private AEntityQuery srcq;
    private ARelationQuery relq;
    private AEntityQuery dstq;
    private RDFQuery[] array;
    
    public RDFQuery(AEntityQuery source, ARelationQuery relation, AEntityQuery destination) {
        this.srcq = (source==null ? EntityAll.getInstance() : source);
        this.relq = (relation==null ? RelationAll.getInstance() : relation);
        this.dstq = (destination==null ? EntityAll.getInstance() : source);
        array = new RDFQuery[]{this};
    }
    
    /**
     * 
     * @param master    Query Layer
     * @param unite     The multiple queries are to be interpreted as a disjunction
     */
    public RDFQuery(RDFQuery... unite) {
        this.array = unite;
        if (unite.length==1) {
            srcq = unite[0].srcq;
            relq = unite[0].relq;
            dstq = unite[0].dstq;
        }
    }
    
    public boolean prop(Relation r) {
        if (array.length == 1) {
            return (srcq.prop(r.getSource())
                    && relq.prop(r) && dstq.prop(r.getDestination()));
        } else for (RDFQuery q : array) {
            if (q.prop(r))
                return true;
        }
        return false;
    }
    
}
