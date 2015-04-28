/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.jackbergus.pickiterator;

/**
 *
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public interface IPickable<K> {
    
    public K pick();
    
}
