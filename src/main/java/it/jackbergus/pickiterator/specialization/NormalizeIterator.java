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
package it.jackbergus.pickiterator.specialization;

import it.jackbergus.pickiterator.IPickIterator;
import java.util.Iterator;

/**
 *
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public class NormalizeIterator<K> implements Iterator<K> {
        private final IPickIterator<K> self;

        public NormalizeIterator(IPickIterator<K> self) {
            this.self = self;
        }

        @Override
        public boolean hasNext() {
            if (self==null)
                return false;
            while (self.hasNext() && (!self.pick().isPresent()))
                self.next();
            return self.hasNext() && self.pick().isPresent();
        }

        @Override
        public K next() {
            if (hasNext()) {
                K toret = self.pick().get();
                self.next();
                return toret;
            } else 
                return null;
        }
    }
