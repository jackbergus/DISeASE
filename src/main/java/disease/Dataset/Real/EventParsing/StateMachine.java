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

/**
 * Stores the previous and the current event inside the machine
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
  public class StateMachine<T> {
      private T previous, current;
      
      public StateMachine() {
          previous = null;
          current = null;
      }
      public T setCurrent(T val) {
          previous = current;
          current = val;
          return previous;
      }
      
      public T getPrevious() {
          return previous;
      }
      
      public T getCurrent() {
          return current;
      }
      
      public boolean isAtStart() {
          return (previous==null && current==null);
      }
      
      public boolean isFirstEvent() {
          return (previous==null && current!=null);
      }
        
    };