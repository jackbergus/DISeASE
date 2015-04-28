package org.tweetsmining.model.graph.database.logical;


import org.tweetsmining.model.graph.IMultiRelationGraph;
import org.tweetsmining.model.graph.database.Entity;
import org.tweetsmining.model.graph.database.eQuery.entity.AEntityQuery;
import org.tweetsmining.model.graph.database.eQuery.relation.RDFQuery;
import org.tweetsmining.model.matrices.IMatrix;

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

/**
 *
 * @author vasistas
 */
public interface ERInterfaceLayer extends IMultiRelationGraph {
    
    public ERInterfaceLayer createView(RDFQuery relations,AEntityQuery entities);
    
    ERInterfaceLayer getPhisicalMaster();
    public void save();
    public void update(Entity e);
    //public void updateRelation(String layer, double[][] m, int N);
    
}

