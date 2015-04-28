/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.jackbergus.pickiterator;

import java.util.Optional;

/**
 *
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public interface IPickIterable<K> {
    
    public IPickIterator<K> iterator();
    /**
     * Concatenates the current iterator with the next one
     * @param next
     * @return 
     */
    public IPickIterator<K> iteratorWith(IPickIterable<K> next);
    
}
