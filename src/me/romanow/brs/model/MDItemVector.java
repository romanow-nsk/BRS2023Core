/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.model;

import java.util.Vector;
import me.romanow.brs.database.DBItem;

/**
 *
 * @author user
 */
public class MDItemVector extends Vector<DBItem>{
    public MDItemVector(DBItem src[]){
        super();
        clear();
        for(int i=0;i<src.length;i++)
            add(src[i]);
        }
    public boolean add(DBItem vv){ return super.add(vv); }
    public DBItem get(int i){ return super.get(i); }
    public MDItemVector(){ super(); }
    public DBItem getById(int id){
    	for(int i=0;i<size();i++)
            if (get(i).getId()==id) return get(i);
    	return null;
        }
    public int getIdxById(int id){
    	for(int i=0;i<size();i++)
    		if (get(i).getId()==id) return i;
    	return -1;
        }  
   public DBItem []toArray(){
        DBItem xx[]=new DBItem[size()];
        for(int i=0;i<size();i++)
            xx[i]=super.get(i);
        return xx;
        }
    public DBItem[] compressMarked(){
        int sz=0;
        for(int i=0;i<size();i++) 
            if(get(i).mark) sz++;
        DBItem yy[]=new DBItem[sz];
        sz=0;
        for(int i=0;i<size();i++) 
            if(get(i).mark) yy[sz++]=get(i);
        return yy;
        }
}
