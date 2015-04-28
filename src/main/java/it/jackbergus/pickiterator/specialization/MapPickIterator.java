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
import java.util.function.Function;

/**
 * Maps a value of an iterator to another one
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public class MapPickIterator<T,V> implements IPickIterator<V> {
    
    private IPickIterator<T> original;
    private Function<? super T, ? extends V> map;
    public MapPickIterator(IPickIterator<T> original,Function<? super T, ? extends V> map) {
        this.original = original;
        this.map = map;
    }

    @Override
    public boolean hasNext() {
        if (original == null || map == null)
            return false;
        return original.hasNext();
    }

    @Override
    public Optional<V> next() {
        if (original == null || map == null)
            return Optional.empty();
        Optional<T> tmp = original.next();
        if (tmp.isPresent()) {
            return Optional.of(map.apply(tmp.get()));
        } else
            return Optional.empty();
    }

    @Override
    public Optional<V> pick() {
        if (original == null || map == null)
            return Optional.empty();
        Optional<T> tmp = original.pick();
        if (tmp.isPresent()) {
            return Optional.of(map.apply(tmp.get()));
        } else
            return Optional.empty();
    }
    
    
    
}
