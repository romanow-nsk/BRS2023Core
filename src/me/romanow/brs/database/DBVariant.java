/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.database;

import me.romanow.brs.model.OnlyDate;

/**
 *
 * @author user
 */
public class DBVariant extends DBStudentCellItem{
    public DBVariant(){ super(); }
    public DBVariant(int idr, int ids, int idc, String dt){
        super(idr,ids,idc);
        variant=dt;
        }
    private String variant="";
    private int cDate=new OnlyDate().dateToInt();
    public String getVariant(){ return variant; }
    public void setVariant(String ss){ variant=ss; }
    public int getCDate(){ return cDate; }
    public void setCDate(int id0){ cDate=id0; }
}
