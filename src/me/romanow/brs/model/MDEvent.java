/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import me.romanow.brs.database.DBEvent;
import me.romanow.brs.database.DBStudent;
import me.romanow.brs.xml.XMLBoolean;


public class MDEvent extends DBEvent{
    public MDStudentVector propusk=null;
    public MDEvent(){}
    public MDEvent(DBEvent src){
        super();
        setId(src.getId());
        setName(src.getName());
        setIdRating(src.getIdRating());
        setRemoved(src.getRemoved());
        setEvtDate(src.getEvtDate());
        }
    public void save(DataOutputStream os) throws Throwable{
        saveDBValues(os);
        os.writeInt(propusk.size());
        for(int i=0;i<propusk.size();i++)
            propusk.get(i).saveDBValues(os);
        }
    public void load(DataInputStream is) throws Throwable{
        loadDBValues(is);
        int sz=is.readInt();
        propusk.clear();
        for(int i=0;i<sz;i++){
            MDStudent vv=new MDStudent();
            vv.loadDBValues(is);
            propusk.add(vv);
            }
        } 
}
