/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.jackbergus.pickiterator;

/**
 * Creates a PickIterator from an Iterable Object
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public class PickIterable<K> implements IPickIterable<K> {

    private Iterable<K> it;
    public PickIterable(Iterable<K> it) {
        this.it = it;
    }
    
    @Override
    public PickIterator<K> iterator() {
        return new PickIterator(it.iterator());
    }

    @Override
    public IPickIterator<K> iteratorWith(IPickIterable<K> next) {
        return new PickIteratorConcatenation<>(iterator(),next.iterator());
    }
    
}
