/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.database;

import java.sql.SQLException;
import java.util.Vector;
import me.romanow.brs.model.OnlyDate;

public class DBEvent extends DBNamedItem{
    public DBEvent(){ }
    public DBEvent(int id){ super(id,""); }
    public DBEvent(String nm, int dt, int id){ super(nm); evtDate=dt;  idRating=id; }
    public DBEvent(String nm, int id){ super(nm); idRating=id; }
    private int idRating=0;
    private int evtDate=new OnlyDate().dateToInt();    
    public int getIdRating(){ return idRating; }
    public void setIdRating(int id0){ idRating=id0; }
    public int getEvtDate(){ return evtDate; }
    public void setEvtDate(int id0){ evtDate=id0; }
    private boolean removed=false;
    public boolean getRemoved(){ return removed; }
    public void setRemoved(boolean bb){ removed=bb; }
}
