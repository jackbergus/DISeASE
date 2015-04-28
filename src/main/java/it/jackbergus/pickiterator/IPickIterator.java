/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.jackbergus.pickiterator;

import java.util.Iterator;
import java.util.Optional;

/**
 *
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public interface IPickIterator<K> extends Iterator<Optional<K>>, IPickable<Optional<K>> {
    
    //public Iterator<K> normalize();
    
}
