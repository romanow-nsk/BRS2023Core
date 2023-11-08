/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.model;

import me.romanow.brs.database.DBItem;
import me.romanow.brs.database.DBStudentCellItem;

/**
 *
 * @author user
 */
public class MDStudentCellVector extends MDItemVector{
    public DBStudentCellItem get(int i){ return (DBStudentCellItem)super.get(i); }
    public MDStudentCellVector(DBStudentCellItem src[]){ super(src); }
    public MDStudentCellVector(){ super(); }
    public DBItem getForStudentCell(DBStudentCellItem item ){
        for(int i=size()-1; i>=0;i--){
            DBStudentCellItem xx=(DBStudentCellItem)get(i);
            if (xx.getIdRating()==item.getIdRating() && xx.getIdStudent()==item.getIdStudent() && xx.getIdCell()==item.getIdCell())
                return xx;
            }
        return null;
        }
}
