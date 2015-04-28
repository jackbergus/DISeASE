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
package disease.Dataset.Real.EventParsing;

import disease.datatypes.ConcreteMapIterator;
import disease.utils.datatypes.Pair;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 *
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */

    public class EventMatcher<Event>  implements Iterable<Pair<Event,Collection<String>>>, Iterator<Pair<Event,Collection<String>>> {
        PeekIterator<String> iterator;
        Map<Predicate<String>,Event> map;
        LinkedList<Predicate<String>> to_discard_strings;
        //StateMachine<Event> sm;
        
        public EventMatcher(Iterator<String> corpus) {
            this.iterator = new PeekIterator<>(corpus);
            map = new LinkedHashMap<>(); //Insertion orderer => prioritized
            //sm = new StateMachine<>();
            to_discard_strings = new LinkedList<>();
        }
        
        public Event addInitializationPattern(String regex, Event e) {
            return map.put(s -> Pattern.compile(regex).matcher(s).matches(),e);
        }
        
        public Event addInitializationPattern(Predicate<String> p, Event e) {
            return map.put(p,e);
        }
        
        public Event getMatchingEvent(String val) throws EventException {
            for (Pair<Predicate<String>,Event> p : new ConcreteMapIterator<>(map)) {
                if (p.getFirst().test(val)) 
                    return p.getSecond();
            }
            throw new EventException();
        }
        
        /**
         * Adds a predicate for removing non-relevant strings
         * @param p When the predicate returns true, the string won't be included inside the collection
         */
        public void addToDiscardPredicate(Predicate<String> p) {
            this.to_discard_strings.add(p);
        }
        
        public boolean doDiscard(String eval) {
            if (to_discard_strings.stream().anyMatch((p) -> (p.test(eval)))) {
                return true;
            }
            return false;
        }
        
        Predicate<String> consider = val -> !(val.endsWith("-")||val.endsWith(" al")||val.endsWith(" a"));
        
        public Pair<Event,Collection<String>> getNextEvent() {

            LinkedList<String> coll = new LinkedList<>();
            boolean starting_event = true;
            Event current = null;
            boolean consider_next_event = true;
            while (iterator.hasNext()) {
                String value = iterator.peek(); //obtains the first element
                if (value.contains("663.8"))
                    System.out.println("beak here");
                if (!consider_next_event) {
                    iterator.next();
                    coll.add(value);
                    consider_next_event = consider.test(value);
                    continue;
                }
                if (doDiscard(value)) {
                    iterator.next();
                    continue;
                }
                consider_next_event = consider.test(value);
                try {
                    Event tmp = getMatchingEvent(value);
                    
                    // If this is the first event, I store the first value and hence I provide my first association
                    if (starting_event) {
                        current = tmp;
                        starting_event = false; 
                        iterator.next();
                        coll.add(value);
                    } else 
                    //the event has ended
                        return new Pair<>(current,coll);
                } catch (EventException e) {
                   //There is no suitable event: the current event is still happening
                   coll.add(value);
                   iterator.next();
                }
            }
            //the whole iterator has been processed: return the final data
            return new Pair<>(current,coll);
        }

    @Override
    public Iterator<Pair<Event, Collection<String>>> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Pair<Event, Collection<String>> next() {
        return getNextEvent();
    }
        
}