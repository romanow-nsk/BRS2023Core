package me.romanow.brs.database;

import me.romanow.brs.model.OnlyDate;

public class DBStudentCellItem extends DBItem{
    public DBStudentCellItem(){}
    public DBStudentCellItem(int idr, int ids, int idc){
    	idStudent=ids;
    	idRating=idr;
    	idCell=idc;
        }
    private int idStudent=0;
    private int idCell=0; 
    private int idRating=0; 
    public int getIdStudent(){ return idStudent; }
    public void setIdStudent(int id0){ idStudent=id0; }
    public int getIdCell(){ return idCell; }
    public void setIdCell(int id0){ idCell=id0; }
    public int getIdRating(){ return idRating; }
    public void setIdRating(int id0){ idRating=id0; }
}
