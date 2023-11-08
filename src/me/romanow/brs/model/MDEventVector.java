/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.model;

import me.romanow.brs.database.DBEvent;

/**
 *
 * @author user
 */
public class MDEventVector extends MDItemVector{
    public DBEvent get(int i){ return (DBEvent)super.get(i); }
    public MDEventVector(DBEvent src[]){ super(src); }
    public MDEventVector(){ super(); }
}
