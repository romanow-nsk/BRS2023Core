package me.romanow.brs.database;

import java.sql.SQLException;
import java.util.Vector;

public class DBRating extends DBNamedItem{
    public DBItem emptyCopy(){ return new DBRating(); }
    public DBRating(int id0){ super(id0,"");   }
    public DBRating(){ idCourse=0; idGroups=0;   }
    public DBRating(String nm, int tid, int rid, boolean sec)
        { super(nm); idCourse=tid; idGroups=rid;  second=sec; }
    private int idCourse=0;
    private int idGroups=0;
    private int idParams=0;
    private boolean second=false;
    public String []getValues(){ 
        String ss[]=new String[2];
        ss[0]=""+idCourse; 
        ss[1]=""+idGroups; 
        return ss; 
        }
    public int getIdGroups(){ return idGroups; }
    public int getIdCourse(){ return idCourse; }
    public int getIdParams(){ return idParams; }
    public void setIdGroups(int id){ idGroups=id; }
    public void setIdCourse(int id){ idCourse=id; }
    public void setIdParams(int id){ idParams=id; }
    public boolean getSecond(){ return second; }
    public void setSecond(boolean bl) { second=bl; }
}
