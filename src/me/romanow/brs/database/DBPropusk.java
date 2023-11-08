/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.database;

import java.sql.SQLException;
import java.util.Vector;
import me.romanow.brs.model.OnlyDate;

public class DBPropusk extends DBItem{
    public DBPropusk(){}
    public DBPropusk(int ids, int idr, int ide){
    	idStudent=ids;
    	idRating=idr;
    	idEvent=ide;
    	removed=false;
    }
    private int idStudent=0;
    private int idEvent=0; 
    private int idRating=0; 
    private boolean removed=false;
    public int getIdStudent(){ return idStudent; }
    public void setIdStudent(int id0){ idStudent=id0; }
    public int getIdEvent(){ return idEvent; }
    public void setIdEvent(int id0){ idEvent=id0; }
    public int getIdRating(){ return idRating; }
    public void setIdRating(int id0){ idRating=id0; }
    public boolean getRemoved(){ return removed; }
    public void setRemoved(boolean bb){ removed=bb; }
    private int cDate=new OnlyDate().dateToInt();
    public int getCDate(){ return cDate; }
    public void setCDate(int id0){ cDate=id0; }
}
