/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.jackbergus.pickiterator.specialization;

import it.jackbergus.pickiterator.IPickIterator;
import it.jackbergus.pickiterator.PickIterator;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;

/**
 *
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public class FilterPickIterator<V> implements IPickIterator<V> {

    private Predicate<? super V> prop;
    private IPickIterator<V> original;
    public FilterPickIterator(IPickIterator<V> it, Predicate<? super V> prop) {
        this.original = it;
        this.prop = prop;
    }
    @Override
    public boolean hasNext() {
        if (original == null || prop == null)
            return false;
        while (original.hasNext() && (!prop.test(original.pick().get())))
            original.next(); //scan and pass to the next element
        if (!original.hasNext())
            return false;
        return prop.test(original.pick().get());
    }

    @Override
    public Optional<V> next() {
        if (original == null || prop == null)
            return Optional.empty();
        if (hasNext())
            return original.next();
        else
            return Optional.empty();
    }

    @Override
    public Optional<V> pick() {
        if (original == null || prop == null)
            return Optional.empty();
        if (hasNext())
            return original.pick();
        else
            return Optional.empty();
    }
    
    
    
}
