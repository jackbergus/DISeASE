/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.jackbergus.pickstream;

import it.jackbergus.pickiterator.IPickIterable;
import it.jackbergus.pickiterator.PickIterable;
import it.jackbergus.pickiterator.PickIterableConcatenation;
import it.jackbergus.pickiterator.specialization.FilterPickIterable;
import it.jackbergus.pickiterator.specialization.MapPickIterable;
import it.jackbergus.pickiterator.specialization.NormalizeIterator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;


/**
 *
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public class PickStream<K> implements Iterable<K>  {

    private IPickIterable<K> it;
    public PickStream(IPickIterable<K> it) {
        this.it = it;
    }
    public PickStream(Iterable<K> it) {
        this.it = new PickIterable<>(it);
    }
    
    public PickStream<K> filter(Predicate<? super K> predicate) {
        return new PickStream<>(new FilterPickIterable<>(it,predicate));
    }
    
    public <R> PickStream<R> map(Function<? super K, ? extends R> mapper) {
        return new PickStream<>(new MapPickIterable<>(it,mapper));
    }
    
    public PickStream<K> contatenate(IPickIterable<K> nextIterator) {
        return new PickStream<>(new PickIterableConcatenation<>(it,nextIterator));
    }
    
    private <H> Iterable<H> iterableFromIterator(Iterator<H> it) {
        return () -> it ;
    }
    
    

    
    public <R, A> R collect(Collector<? super K, A, R> collector) {
        A container = collector.supplier().get();
        for (Optional<K> t : iterableFromIterator(it.iterator()))
            if (t.isPresent())
            collector.accumulator().accept(container, t.get());
        return collector.finisher().apply(container);
    }

    
    public Optional<K> min(Comparator<? super K> comparator) {
        if (!it.iterator().hasNext())
            return Optional.empty();
        else {
            Optional<K> minimum = Optional.empty();
            for (Optional<K> x : iterableFromIterator(it.iterator())) {
                if (x.isPresent())
                    if (!minimum.isPresent())
                        minimum = x;
                    else {
                        if (comparator.compare(x.get(), minimum.get()) < 0)
                            minimum = x;
                    }
            }
            return minimum;
        }
    }

    
    public Optional<K> max(Comparator<? super K> comparator) {
        if (!it.iterator().hasNext())
            return Optional.empty();
        else {
            Optional<K> minimum = Optional.empty();
            for (Optional<K> x : iterableFromIterator(it.iterator())) {
                if (x.isPresent())
                    if (!minimum.isPresent())
                        minimum = x;
                    else {
                        if (comparator.compare(x.get(), minimum.get()) > 0)
                            minimum = x;
                    }
            }
            return minimum;
        }
    }
    
    public Optional<K> findFirst() {
        return it.iterator().pick();
    }
    
    public Optional<K> findAny() {
        return findFirst();
    }
    
    public IPickIterable<K> get() {
        return it;
    }

    @Override
    public Iterator<K> iterator() {
        return new NormalizeIterator<>(it.iterator());
    }
    
}
