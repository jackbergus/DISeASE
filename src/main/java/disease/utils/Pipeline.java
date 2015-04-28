/*
 * Copyright (C) 2015 vasistas
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
package disease.utils;

import disease.Phase.cleaner.CleanItalian;
import disease.utils.PipelineChunk;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 *
 * @author vasistas
 */
public class Pipeline implements List<PipelineChunk> {
    
    private List<PipelineChunk> pipeline;
    
    public Pipeline(String elem) {
        this.pipeline = CleanItalian.getInstance().cleanedStringList(elem).stream().map((String t) -> new PipelineChunk(t)).collect(Collectors.toList());
    }
    
    @Override
    public ListIterator<PipelineChunk> iterator() {
        return this.pipeline.listIterator();
    }

    @Override
    public int size() {
        return this.pipeline.size();
    }

    @Override
    public boolean isEmpty() {
        return this.pipeline.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.pipeline.contains(o);
    }

    @Override
    public Object[] toArray() {
        return this.pipeline.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.pipeline.toArray(a);
    }

    @Override
    public boolean add(PipelineChunk e) {
        return this.pipeline.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return this.pipeline.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.pipeline.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends PipelineChunk> c) {
        return this.pipeline.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends PipelineChunk> c) {
        return this.pipeline.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.pipeline.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.pipeline.retainAll(c);
    }

    @Override
    public void clear() {
        pipeline.clear();
    }

    @Override
    public PipelineChunk get(int index) {
        return pipeline.get(index);
    }

    @Override
    public PipelineChunk set(int index, PipelineChunk element) {
        return pipeline.set(index, element);
    }

    @Override
    public void add(int index, PipelineChunk element) {
        pipeline.add(index, element);
    }

    @Override
    public PipelineChunk remove(int index) {
        return pipeline.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int lastIndexOf(Object o) {
        return pipeline.lastIndexOf(o);
    }

    @Override
    public ListIterator<PipelineChunk> listIterator() {
        return pipeline.listIterator();
    }

    @Override
    public ListIterator<PipelineChunk> listIterator(int index) {
        return pipeline.listIterator(index);
    }

    @Override
    public List<PipelineChunk> subList(int fromIndex, int toIndex) {
        return pipeline.subList(fromIndex, toIndex);
    }
    
}
