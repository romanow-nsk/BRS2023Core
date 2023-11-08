package me.romanow.brs.database;

import java.sql.SQLException;
import java.util.Vector;
import me.romanow.brs.model.OnlyDate;

public class DBStudRating extends DBItem{
    //--------------------------------------------------------------------------
    public DBStudRating(){ }
    public DBStudRating(int rid, int sid, int brig, boolean sc){ 
        super(); 
        idRating=rid;
        idStudent=sid;
        brigade=brig;
        second=sc;
        }
    private int idStudent=0;
    private int idRating=0;
    private boolean second=false;
    private int brigade=0;
    private int cDate=new OnlyDate().dateToInt();
    public int getBrigade(){ return brigade; }
    public void setBrigade(int id0){ brigade=id0; }
    public int getIdStudent(){ return idStudent; }
    public void setIdStudent(int id0){ idStudent=id0; }
    public int getIdRating(){ return idRating; }
    public void setIdRating(int id0){ idRating=id0; }
    public boolean getSecond(){ return second; }
    public void setSecond(boolean bl) { second=bl; }
    public int getCDate(){ return cDate; }
    public void setCDate(int id0){ cDate=id0; }
}
