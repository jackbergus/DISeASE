/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.jackbergus.pickiterator.specialization;

import it.jackbergus.pickiterator.IPickIterable;
import it.jackbergus.pickiterator.IPickIterator;
import it.jackbergus.pickiterator.PickIteratorConcatenation;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public class MapPickIterable<K,V> implements IPickIterable<V>{

    private Function<? super K, ? extends V> map;
    private IPickIterable<K> it;
    
    public MapPickIterable(IPickIterable<K> it,Function<? super K, ? extends V> prop) {
        this.it = it;
        this.map = prop;
    }
    
    @Override
    public IPickIterator<V> iterator() {
        return new MapPickIterator<>(it.iterator(),map);
    }
    
    @Override
    public IPickIterator<V> iteratorWith(IPickIterable<V> next) {
        return new PickIteratorConcatenation<>(iterator(),next.iterator());
    }
}
