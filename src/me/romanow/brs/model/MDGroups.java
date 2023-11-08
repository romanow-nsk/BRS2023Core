/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import me.romanow.brs.database.DBGroups;
import me.romanow.brs.database.DBStudent;

/**
 *
 * @author user
 */
public class MDGroups extends DBGroups{
    //public MDStudent students[]=null;
    public MDStudentVector students=new MDStudentVector();
    public void save(DataOutputStream os) throws Throwable{
        saveDBValues(os);
        os.writeInt(students.size());
        for(int i=0;i<students.size();i++)
            students.get(i).save(os);
        }
    public void load(DataInputStream is) throws Throwable{
        loadDBValues(is);
        int sz=is.readInt();
        students.clear();
        for(int i=0;i<sz;i++){
            MDStudent vv=new MDStudent();
            vv.load(is);
            students.add(vv);
            }
        } 

}
