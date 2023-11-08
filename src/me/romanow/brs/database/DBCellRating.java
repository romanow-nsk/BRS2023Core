package me.romanow.brs.database;

import java.sql.SQLException;
import java.util.Vector;
import me.romanow.brs.model.OnlyDate;

public class DBCellRating extends DBItem{
    //--------------------------------------------------------------------------
    public DBCellRating(){ }
    public DBCellRating(int rid, int sid, int wk, int wk2){ 
        super(); 
        idRating=rid;
        idCell=sid;
        week=wk;
        week2=wk2;
        }
    private int idCell=0;
    private int idRating=0;
    private int week=0;
    private int week2=0;
    private int cDate=new OnlyDate().dateToInt();
    public int getWeek(){ return week; }
    public void setWeek(int id0){ week=id0; }
    public int getWeek2(){ return week2; }
    public void setWeek2(int id0){ week2=id0; }
    public int getIdCell(){ return idCell; }
    public void setIdCell(int id0){ idCell=id0; }
    public int getIdRating(){ return idRating; }
    public void setIdRating(int id0){ idRating=id0; }
    public int getCDate(){ return cDate; }
    public void setCDate(int id0){ cDate=id0; }
}
