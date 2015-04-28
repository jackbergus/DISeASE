/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tweetsmining.model.graph.database;

import java.util.Objects;

/**
 * Maps a triple (Entity,Relation,Entity) to a type
 * @author gyankos
 */
public class ERTriple implements Comparable<ERTriple> {
    
    private Entity src;
    private Relation link;
    private Entity dst;
    private String hashkey;
    
    public boolean hasNull() {
        return (this.src == null || this.link== null|| this.dst==null);
    }
    public ERTriple(Entity source, Relation r, Entity dest) {
        this.src = source;
        this.link = r;
        this.dst = dest;
        String s  = (this.src==null? " " : this.src.toString());
        String l = (this.link==null? " " : this.link.toString());
        String d = (this.dst==null? " " : this.dst.toString());
        hashkey =  s+" "+l+" "+d;
    }
    
    public Entity getSource() { return src; }
    public Relation getRelation() { return link; }
    public Entity getDestination() { return dst; }
    
    
    @Override
    /**
     * this = <a,b>
     * o = <c,d>
     *
     * if (compare(a,c)==0) then compare(b,d) else compare(a,c)
     *
     */
    public int compareTo(ERTriple o) {
        if (o==null) return 1;
        int comp_first = Integer.compare(src.hashCode(), o.src.hashCode());
        if (comp_first == 0) {
            comp_first =  this.getRelation().getName().compareTo(o.getRelation().getName());
            if (comp_first == 0) {
                return Integer.compare(dst.hashCode(),o.dst.hashCode());
            }
        }
        return comp_first;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.src);
        hash = 89 * hash + Objects.hashCode(this.link);
        hash = 89 * hash + Objects.hashCode(this.dst);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ERTriple other = (ERTriple) obj;
        if (!Objects.equals(this.src, other.src)) {
            return false;
        }
        if (!Objects.equals(this.link, other.link)) {
            return false;
        }
        if (!Objects.equals(this.dst, other.dst)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return hashkey;
    }

    
}
