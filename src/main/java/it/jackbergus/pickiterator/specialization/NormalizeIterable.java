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

import it.jackbergus.pickiterator.IPickIterable;
import java.util.Iterator;

/**
 *
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public class NormalizeIterable<K> implements Iterable<K> {
        private final IPickIterable<K> self;

        public NormalizeIterable(IPickIterable<K> self) {
            this.self = self;
        }

        @Override
        public Iterator<K> iterator() {
            return new NormalizeIterator<>(self.iterator());
        }
    }