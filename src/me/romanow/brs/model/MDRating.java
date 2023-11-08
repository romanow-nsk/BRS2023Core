/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Vector;
import me.romanow.brs.database.*;
import me.romanow.brs.interfaces.BRSException;
import me.romanow.brs.xml.XMLString;

public class MDRating extends DBRating{
    public MDGroups groups=null;
    public MDCourse course=null;
    public DBParams params=null;
    public MDItemVector events=new MDItemVector();
    public MDItemVector propusk=new MDItemVector();
    public MDStudentCellVector notes=new MDStudentCellVector();
    public MDStudentCellVector vars=new MDStudentCellVector();
    public MDStudentCellVector docs=new MDStudentCellVector();
    public MDStudentCellVector archs=new MDStudentCellVector();
    public MDRating(int id){ super(id); }
    public MDRating(){ super(0); }
    public void save(DataOutputStream os) throws Throwable{
        saveDBValues(os);
        groups.save(os);
        course.save(os);
        params.saveDBValues(os);
        os.writeInt(notes.size());
        Vector<DBField> ff=new DBNote().getFields();
        for(int i=0;i<notes.size();i++){
            notes.get(i).setFields(ff);
            notes.get(i).saveDBValues(os);
            }
        ff=new DBEvent().getFields();
        os.writeInt(events.size());
        for(int i=0;i<events.size();i++){
            events.get(i).setFields(ff);
            events.get(i).saveDBValues(os);
            }
        ff=new DBPropusk().getFields();
        os.writeInt(propusk.size());
        for(int i=0;i<propusk.size();i++){
            propusk.get(i).setFields(ff);
            propusk.get(i).saveDBValues(os);
            }
        ff=new DBVariant().getFields();
        os.writeInt(vars.size());
        for(int i=0;i<vars.size();i++){
            vars.get(i).setFields(ff);
            vars.get(i).saveDBValues(os);
            }
        ff=new DBDocFile().getFields();
        os.writeInt(docs.size());
        for(int i=0;i<docs.size();i++){
            docs.get(i).setFields(ff);
            docs.get(i).saveDBValues(os);
            }
        ff=new DBArchFile().getFields();
        os.writeInt(archs.size());
        for(int i=0;i<archs.size();i++){
            archs.get(i).setFields(ff);
            archs.get(i).saveDBValues(os);
            }
        }
    
    public void load(DataInputStream is) throws Throwable{
        loadDBValues(is);
        groups=new MDGroups();
        groups.load(is);
        course=new MDCourse();
        course.load(is);
        params=new DBParams();
        params.loadDBValues(is);
        notes.clear();
        Vector<DBField> ff=new DBNote().getFields();
        int sz=is.readInt();
        for(int i=0;i<sz;i++){
            DBNote nt=new DBNote();
            nt.setFields(ff);
            nt.loadDBValues(is);
            notes.add(nt);
            }
        ff=new DBEvent().getFields();
        events.clear();
        sz=is.readInt();
        for(int i=0;i<sz;i++){
            DBEvent nt=new DBEvent();
            nt.setFields(ff);
            nt.loadDBValues(is);
            events.add(nt);
            }
        ff=new DBPropusk().getFields();
        propusk.clear();
        sz=is.readInt();
        for(int i=0;i<sz;i++){
            DBPropusk nt=new DBPropusk();
            nt.setFields(ff);
            nt.loadDBValues(is);
            propusk.add(nt);
            }
        ff=new DBVariant().getFields();
        vars.clear();
        sz=is.readInt();
        for(int i=0;i<sz;i++){
            DBVariant nt=new DBVariant();
            nt.setFields(ff);
            nt.loadDBValues(is);
            vars.add(nt);
            }
        ff=new DBDocFile().getFields();
        docs.clear();
        sz=is.readInt();
        for(int i=0;i<sz;i++){
            DBDocFile nt=new DBDocFile();
            nt.setFields(ff);
            nt.loadDBValues(is);
            docs.add(nt);
            }
        ff=new DBArchFile().getFields();
        archs.clear();
        sz=is.readInt();
        for(int i=0;i<sz;i++){
            DBArchFile nt=new DBArchFile();
            nt.setFields(ff);
            nt.loadDBValues(is);
            archs.add(nt);
            }
        } 
    //--------------------------------------------------------------------------
    int getMaxId(Vector<DBItem> src){
        if (src.size()==0) return 1;
        return src.get(src.size()-1).getId();
        }
    public void changeNote(MDNote note) throws Throwable {
        note.setId(getMaxId(notes)+1);
        note.changeDate();
        notes.add(new DBNote(note));
        }
    public void changeVariant(String value, int studentId, int cellId) throws Throwable {
        DBVariant note=new DBVariant(getId(),studentId,cellId,value);
        note.setId(getMaxId(vars)+1);
        vars.add(note);    	
        }
    public void changeEvent(int studId, int eventId, boolean prop) throws Throwable {
        DBPropusk  pr=new DBPropusk(studId,getId(),eventId);
        pr.setRemoved(prop);
        pr.setId(getMaxId(propusk)+1);
        propusk.add(pr);
        }
    public int insertEvent(String ss, int cdate) throws Throwable {
        DBEvent ev=new DBEvent(ss,getId());
        ev.setId(getMaxId(events)+1);
        events.add(ev);
        return ev.getId();
        }
    }
