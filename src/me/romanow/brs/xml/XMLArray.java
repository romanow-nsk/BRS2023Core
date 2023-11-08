/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.xml;

import me.romanow.brs.database.DBItem;

/**
 *
 * @author user
 */
public class XMLArray {
    public DBItem list[]=new DBItem[0];
    public XMLArray(DBItem vv[]){ list=vv; }
    public XMLArray(){ list=new DBItem[0]; }
}
