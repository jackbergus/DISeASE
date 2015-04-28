/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.jackbergus.pickiterator;

import java.util.Iterator;
import java.util.Optional;

/**
 * Implements an iterator where the first element could be accessed without
 * always scanning the list
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public class PickIterator<K> implements IPickIterator<K> {

    private Iterator<K> it;
    private Optional<K> local;
    
    public PickIterator(Iterator<K> it) {
        this.it = it;
        if (it==null)
            local = Optional.empty();
        else
            local = Optional.of(it.next());
    }
    
    public PickIterator() {
        this(null);
    }
    
    @Override
    public boolean hasNext() {
        if (it==null)
            return false;
        else
            return local.isPresent();
    }

    @Override
    public Optional<K> next() {
        Optional<K> toret = local;
        if (!hasNext()||!it.hasNext())
            local = Optional.empty();
        else {
            local = Optional.of(it.next());
        }
        return toret;
    }
    
    /**
     * Returns the next element without extracting it from the iterator
     * @return 
     */
    @Override
    public Optional<K> pick() {
        return local;
    }

    
}
