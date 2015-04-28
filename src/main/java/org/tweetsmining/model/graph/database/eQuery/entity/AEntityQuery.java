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
package org.tweetsmining.model.graph.database.eQuery.entity;

import org.tweetsmining.model.graph.database.Entity;
import org.tweetsmining.model.graph.database.IGraphComponent;
import org.tweetsmining.model.graph.database.IQuery;
import org.tweetsmining.model.graph.database.logical.ERInterfaceLayer;

/**
 *
 * @author vasistas
 */
public abstract class AEntityQuery implements IQuery {
    
    
    public boolean prop(IGraphComponent igc) {
        if (igc==null)
            return false;
        return prop((Entity)igc);
    }
    
    protected abstract boolean prop(Entity e);
    
}
