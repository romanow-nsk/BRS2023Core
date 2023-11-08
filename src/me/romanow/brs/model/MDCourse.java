/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import me.romanow.brs.database.DBCourse;

/**
 *
 * @author user
 */
public class MDCourse extends DBCourse{
    //public MDCell cells[]=null;
    public MDCellVector cells=new MDCellVector();
    public void save(DataOutputStream os) throws Throwable{
        saveDBValues(os);
        os.writeInt(cells.size());
        for(int i=0;i<cells.size();i++)
            cells.get(i).save(os);
        }
    public void load(DataInputStream is) throws Throwable{
        loadDBValues(is);
        int sz=is.readInt();
        cells.clear();
        for(int i=0;i<sz;i++){
            MDCell vv=new MDCell();
            vv.load(is);
            cells.add(vv);
            }
        } 
}
