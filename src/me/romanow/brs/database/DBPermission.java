package me.romanow.brs.database;

import java.io.DataOutputStream;
import java.sql.SQLException;
import java.util.Vector;

public class DBPermission extends DBNamedItem{
    public DBItem emptyCopy(){ return new DBPermission(); }
    public DBPermission(){ idTutor=0; idRating=0;   }
    public DBPermission(String nm, int tid, int rid){  super(nm); idTutor=tid; idRating=rid;  }
    private int idTutor=0;
    private int idRating=0;
    public int getIdRating(){ return idRating; }
    public int getIdTutor(){ return idTutor; }
    public void setIdRating(int id){ idRating=id; }
    public void setIdTutor(int id){ idTutor=id; }
}
