/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.database;

import java.lang.reflect.Array;

/**
 *
 * @author user
 */
//---------- Для XML - хранит массив ссылок ПК (целевого класса) --------------------
public class DBArray<T extends DBItem> {
    public T array[];
    public DBArray(DBItem src[]){ 
        array=(T[])Array.newInstance(array.getClass().getComponentType(), src.length); 
        for(int i=0;i<array.length;i++)
            array[i]=(T)src[i];
        }
    public DBItem[] getItems(){
        DBItem ss[]=new DBItem[array.length];
        for(int i=0;i<array.length;i++)
            ss[i]=array[i];
        return ss;
        }
}
