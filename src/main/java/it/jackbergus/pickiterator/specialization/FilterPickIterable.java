/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.jackbergus.pickiterator.specialization;

import it.jackbergus.pickiterator.IPickIterable;
import it.jackbergus.pickiterator.IPickIterator;
import it.jackbergus.pickiterator.PickIterable;
import it.jackbergus.pickiterator.PickIterator;
import it.jackbergus.pickiterator.PickIteratorConcatenation;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;

/**
 *
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public class FilterPickIterable<V> implements IPickIterable<V>{

    private Predicate<? super V> prop;
    private IPickIterable<V> it;
    
    public FilterPickIterable(IPickIterable<V> it,Predicate<? super V> prop) {
        this.it = it;
        this.prop = prop;
    }
    
    @Override
    public IPickIterator<V> iterator() {
        return new FilterPickIterator<>(it.iterator(),prop);
    }

    @Override
    public IPickIterator<V> iteratorWith(IPickIterable<V> next) {
        return new PickIteratorConcatenation<>(iterator(),next.iterator());
    }
    
}
