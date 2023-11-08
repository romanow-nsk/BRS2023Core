
package me.romanow.brs.model;

import me.romanow.brs.database.DBNote;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.romanow.brs.ciu.CIUConnection;
import me.romanow.brs.ciu.CIUError;
import me.romanow.brs.ciu.CIUStudent;
import me.romanow.brs.ciu.CIUTeacher;

import me.romanow.brs.database.*;
import me.romanow.brs.interfaces.*;

/**
 *
 * @author user
 */
public class MDBaseUserSQL extends MDBaseUser{
    public final static int blockSize=32000;
    private DBConnect conn=null;
    public DBConnect getConnect(){ return conn; }
    public MDBaseUserSQL(DBTutor tutor, DBProfile profile, DBConnect conn){ 
        super(tutor,profile);
        this.conn=conn; 
        }
    public int createFileDataId() throws Throwable{
        return insert(new DBFileData());
        }

    public void removeCurrent(Class table)throws Throwable{
        if (!testStudentCell()) return;
        conn.deleteLinked(table, rating, studentItem, cellItem);
        }
    public DBItem []getCurrentAll(Class table)throws Throwable{
        if (!testStudentCell()) return null;
        return conn.getList(table, rating, studentItem, cellItem);
        }
    public DBItem getCurrent(Class table)throws Throwable{
        if (!testStudentCell()) return null;
        DBItem x[]=conn.getList(table, rating, studentItem, cellItem);
        return (x.length==0) ? null : x[x.length-1];
        }
    //--------------------------------------------------------------------------
    @Override
    public void getRatingList(int id, int mode) throws Throwable{
        switch (mode){
            case 0: ratingList=conn.getList(DBRating.class); break;
            //----- Список рейтингов только для разрешенных -----------------
            case 1: MDItemVector xx=new MDItemVector(conn.getList(DBRating.class)); 
                    DBTutor item2=new DBTutor();
                    item2.setId(id);
                    for(int i=0;i<xx.size();i++) xx.get(i).mark=false;
                    DBItem yy[]=conn.getList(DBPermission.class, item2);
                    for (int i=0;i<yy.length;i++){
                        int k=xx.getIdxById(((DBPermission)yy[i]).getIdRating());
                        if (k!=-1) xx.get(k).mark=true;
                        }
                    ratingList=xx.compressMarked();
                    break;
           //--------- Список рейтингов для группы ----------------------------
            case 2: DBGroups item=new DBGroups();
                    item.setId(id);
                    ratingList=conn.getList(DBRating.class,item); 
                    break;
            }
        }
    @Override
    public String  testUser(DBTutor item, boolean ciuLogin) throws Throwable{
        DBTutor item2=(DBTutor)conn.getById(DBTutor.class, item.getId());
        if (item2==null) return "Нет логина "+item.getName();
        if (!ciuLogin){
            if (item2.getPass().length()==0)
                return "Нет локального пароля";
            if (!item2.getPass().equals(item.getPass()))
                return "Неправильный пароль";
            else 
                return "";
            }
        String out="";
        try {
            Object oo=(new CIUConnection()).get("/isu/ido_auth",item.getName(),item.getPass(),null,entry);
            if (oo instanceof CIUError) return "Недопустимый логин/пароль ЦИТ";
            CIUTeacher st=(CIUTeacher)oo;
            if (item2.getCiuName().length()==0){
                item2.setCiuName(st.f+" "+st.n+" "+st.o);
                conn.update(item2);
                }
            } catch(Throwable ee){
                out="Ошибка сервера ЦИТ: "+ee.getMessage(); 
                }
        return out;
        }
    @Override
    public void testEdit() throws Throwable{
        editEnabled=false;
        if (tutor==null || rating==null) return;
        DBItem xx[]=conn.getList(DBPermission.class, rating, tutor);
        editEnabled=(xx.length==1);
        }
    //=====================================================================
    public void loadShortRating(int id) throws Throwable{
        rating=null;
        rating=(MDRating)conn.getById(MDRating.class,id);
        rating.course=(MDCourse)conn.getById(MDCourse.class,rating.getIdCourse());
        rating.groups=(MDGroups)conn.getById(MDGroups.class,rating.getIdGroups());
        rating.params=(DBParams)conn.getById(DBParams.class,rating.getIdParams());
        }
    @Override
    public void loadRating(int id) throws Throwable{
        loadShortRating(id);
        loadStudentList();
        loadCellList();
        loadEventList();
        loadPermissionList();
        rating.notes=null;
        }
    @Override    
    public void deleteEvent() throws Throwable{
        if (rating==null || eventItem==null) return;
        conn.deleteLinked(DBPropusk.class, eventItem);
        conn.delete(eventItem.getClass(),eventItem.getId());
        loadEventList();
        }
    @Override
    public int insertEvent(String name,int cdate) throws Throwable{
        if (rating==null) return 0;
        DBEvent ev=new DBEvent(name,rating.getId());
        if (cdate!=0) ev.setEvtDate(cdate);
        conn.insert(ev);
        loadEventList();
        return ev.getId();
        }
    @Override
    public void connect() throws Throwable {
        conn.connect(entry);
        }
    @Override
    public void reconnect() throws Throwable {
        conn.reconnect();
        }
    @Override
    public void close() throws Throwable {
        conn.close();
        }
    @Override
    public boolean isConnected() throws Throwable {
        boolean bb =  conn.isConnected();
        return bb;
        }
    //--------------------------------------------------------------------------
    @Override 
    public double calcStudentRating() throws Throwable{
        if (rating==null || studentItem==null)  return 0;
        return calcStudentRating(conn.getList(DBNote.class, rating, studentItem));
        }
    @Override
    public MDEvent getEvent(boolean full) throws Throwable {
        return getEvent(full,(MDEvent)conn.getById(MDEvent.class, eventItem.getId()),
                conn.getList(DBPropusk.class, rating, eventItem));
        }
    public MDEvent getEvent(boolean full,MDEvent item, DBItem propusk[]) throws Throwable {
        item.propusk=rating.groups.students;
    	for(int i=0;i<item.propusk.size();i++){
    		item.propusk.get(i).off=false;
    		item.propusk.get(i).renew=false;
    		}
  	for(int i=propusk.length-1;i>=0;i--){
  		DBPropusk note=(DBPropusk)propusk[i];
  		int ii=item.propusk.getIdxById(note.getIdStudent());
  		if (ii==-1) continue;
                MDStudent vv=item.propusk.get(ii);
                if (vv.renew) continue;
                vv.off=!note.getRemoved();
                vv.renew=true;
  		}
        for(int i=0;i<item.propusk.size();i++)
            item.propusk.get(i).renew=false;
        if (!full){
            int cnt=0;
            for(int i=0;i<item.propusk.size();i++)
                if (item.propusk.get(i).off) cnt++;
            MDStudent out[]=new MDStudent[cnt];
            cnt=0;
            for(int i=0;i<item.propusk.size();i++)
                if (item.propusk.get(i).off)
                    out[cnt++]=item.propusk.get(i);
            item.propusk=new MDStudentVector(out);
            }
  	return item;
        }
    @Override
    public void changeEvent(int studId, boolean prop) throws Throwable {
        DBPropusk  pr=new DBPropusk(studId,rating.getId(),eventItem.getId());
        pr.setRemoved(prop);
        conn.insert(pr);     
        }    
    @Override
    public void changeNote(MDNote note) throws Throwable {
        if (!testStudentCell()) return;
        conn.insert(new DBNote(note));
    }

    @Override
    public void changeVariant(String value) throws Throwable {
        removeCurrent(DBVariant.class);
        DBVariant note=new DBVariant(rating.getId(),studentItem.getId(),cellItem.getId(),value);
        conn.insert(note);
    }
    //-------------- Удаление старых версий ------------------------------------
    private void deleteOldFiles(boolean doc) throws Throwable{
        DBItem xx[]=getCurrentAll(doc ? DBDocFile.class : DBArchFile.class);
        for(int i=0;i<xx.length;i++){
            DBFile file=(DBFile)xx[i];
            if (!file.inArchive()) continue;
            deleteFile(file.getId(),file.getFileId(),doc);
            }
        removeCurrent(doc ? DBDocFile.class : DBArchFile.class);
        }
    @Override
    public void changeFile(String value,int id, boolean full, boolean doc) throws Throwable {
        if (!testStudentCell()) return;
        if (full) deleteOldFiles(doc);
        if (doc)
            conn.insert(new DBDocFile(rating.getId(),studentItem.getId(),cellItem.getId(),id,value));
        else
            conn.insert(new DBArchFile(rating.getId(),studentItem.getId(),cellItem.getId(),id,value));
        }
    @Override
    public void changeBrigade(int val, boolean val2) throws Throwable {
        if (rating==null || studentItem==null) return;
        conn.deleteLinked(DBStudRating.class, rating, studentItem);
        DBStudRating sr=new DBStudRating();
        sr.setIdRating(rating.getId());
        sr.setIdStudent(studentItem.getId());
        sr.setBrigade(val);
        sr.setSecond(val2);
        conn.insert(sr);
        setStudentItem(studentItem.getId());
        }

    @Override
    public void changeWeek(int val, int val2) throws Throwable {
        if (rating==null || cellItem==null) return;
        conn.deleteLinked(DBCellRating.class, rating, cellItem);
        DBCellRating sr=new DBCellRating();
        sr.setIdRating(rating.getId());
        sr.setIdCell(cellItem.getId());
        sr.setWeek(val);
        sr.setWeek2(val2);
        conn.insert(sr);
        setCellItem(cellItem.getId());       
        }
    //============== Разные вычисления по месту БД =============================
    private DBStudRating getStudRatingById(DBItem studRatingList[],int id){
    	if (studRatingList==null) return null;
    	for (int i=studRatingList.length-1; i>=0; i--){
    		DBStudRating xx=(DBStudRating)studRatingList[i];
    		if (xx.getIdStudent()==id) return xx;
    		}
    	return null;
    	}
    private DBCellRating getCellRatingById(DBItem cellRatingList[],int id){
    	if (cellRatingList==null) return null;
    	for (int i=cellRatingList.length-1; i>=0; i--){
    		DBCellRating xx=(DBCellRating)cellRatingList[i];
    		if (xx.getIdCell()==id) return xx;
    		}
    	return null;
    	}
    //------------------ Сумма баллов по курсу ---------------------------------
    public int calcCourseBalls() throws Throwable{
        if (rating==null) return 0;
        int sum=0;
        for (int i=0;i<rating.course.cells.size();i++)
           sum+=((DBCell)rating.course.cells.get(i)).getBall();
        return sum;
        }
    //--------------------------------------------------------------------------
    @Override
    public double calcPropusk()throws Throwable{
        return calcPropusk(conn.getList(DBPropusk.class, rating, studentItem),studentItem);
    	}

    public void setStudentItem(int id)throws Throwable{
        studentItem=(MDStudent)conn.getById(MDStudent.class, id);
        if (studentItem==null) throw new BRSException(BRSException.serv,"Не найден студент по id="+id);
        DBItem xx[]=conn.getList(DBStudRating.class, rating,studentItem);
        studentItem.brigade=0;
        studentItem.second=false;
        studentItem.cDate=0;
        if (xx.length!=0){
            DBStudRating vv=(DBStudRating)xx[xx.length-1];
            studentItem.brigade=vv.getBrigade();
            studentItem.second=vv.getSecond();
            studentItem.cDate=vv.getCDate();
            }
        }
    public void setCellItem(int id)throws Throwable{
        cellItem=(MDCell)conn.getById(MDCell.class, id);
        if (cellItem==null) throw new BRSException(BRSException.serv,"Не найдена уч.единица по id="+id);
        DBItem xx[]=conn.getList(DBCellRating.class, rating,cellItem);
        cellItem.week=0;
        cellItem.week2=0;
        cellItem.cDate=0;
        if (xx.length!=0){
            DBCellRating vv=(DBCellRating)xx[xx.length-1];
            cellItem.week=vv.getWeek();
            cellItem.week2=vv.getWeek2();
            cellItem.cDate=vv.getCDate();
            }
        }
    public void setEventItem(int id)throws Throwable{
        eventItem=(MDEvent)conn.getById(MDEvent.class, id);
        if (eventItem==null) throw new BRSException(BRSException.serv,"Не найдено событие по id="+id);
        }
    @Override
    public void loadStudentList() throws Throwable{
        DBItem xx[]=conn.getList(MDStudent.class, rating.groups);
        DBItem zz[]=conn.getList(DBStudRating.class, rating);
        rating.groups.students.clear();
        for(int i=0;i<xx.length;i++){
            MDStudent vv=(MDStudent)xx[i];
            DBStudRating gg=getStudRatingById(zz,xx[i].getId());
            if (gg!=null){
                vv.brigade=gg.getBrigade();
                vv.second=gg.getSecond();
                vv.cDate=gg.getCDate();
                }
            rating.groups.students.add(vv);
            }
        }
    @Override
    public void loadCellList() throws Throwable{
        DBItem xx[]=conn.getList(MDCell.class, rating.course);
        DBItem zz[]=conn.getList(DBCellRating.class, rating);
        rating.course.cells.clear();
        for(int i=0;i<xx.length;i++){
            MDCell vv=(MDCell)xx[i];
            DBCellRating cr=getCellRatingById(zz,vv.getId());
            if (cr!=null){
                vv.week=cr.getWeek();
                vv.week2=cr.getWeek2();
                vv.cDate=cr.getCDate();
                }
            rating.course.cells.add(vv);
            }        
        sortCellList();
        }
    
    @Override
    public void loadEventList() throws Throwable{
        DBItem xx[]=conn.getList(MDEvent.class, rating);
        rating.events.clear();
        for(int i=0;i<xx.length;i++){
            rating.events.add((DBEvent)xx[i]);
            }        
        }
    @Override
    public void loadPermissionList() throws Throwable {
        if (this.tutor==null)
            return;                             // Все - для админа
        DBItem xx[] = conn.getList(DBPermission.class, rating);
        permission = new DBTutor[xx.length];
        for(int i=0;i<xx.length;i++){           // Массив Id-ов
            int id = ((DBPermission)xx[i]).getIdTutor();
            DBTutor tt = (DBTutor)conn.getById(DBTutor.class, id);
            permission[i] = tt;
            }
        }
    
    //----------------------------- Администрирование --------------------------
    public int insert(DBItem item) throws Throwable {
        conn.insert(item);
        return item.getId();
        }
    @Override
    public void loadStudentRating(int id,int sid) throws Throwable{
        loadShortRating(id);
        loadStudentList();
        loadCellList();
        loadEventList();
        studentItem=(MDStudent)rating.groups.students.getById(sid);
        if (studentItem==null) return;
        //----------------------------------------------------------------------
        DBItem xx[]=conn.getList(DBNote.class, rating,studentItem);
        rating.notes.clear();        
        for(int i=0;i<xx.length;i++){
            rating.notes.add(xx[i]);
            }
        xx=conn.getList(DBVariant.class, rating,studentItem);
        rating.vars.clear();        
        for(int i=0;i<xx.length;i++){
            rating.vars.add(xx[i]);
            }
        xx=conn.getList(DBArchFile.class, rating,studentItem);
        rating.archs.clear();        
        for(int i=0;i<xx.length;i++){
            rating.archs.add(xx[i]);
            }
        xx=conn.getList(DBDocFile.class, rating,studentItem);
        rating.docs.clear();        
        for(int i=0;i<xx.length;i++){
            rating.docs.add(xx[i]);
            }
        xx=conn.getList(DBPropusk.class, rating,studentItem);
        rating.propusk.clear();
        for(int i=0;i<xx.length;i++){
            rating.propusk.add(xx[i]);
            }
        }
    @Override
    public void loadCellRating(int id,int cid) throws Throwable{
        loadShortRating(id);
        loadStudentList();
        loadCellList();
        loadEventList();
        cellItem=(MDCell)rating.course.cells.getById(cid);
        if (cellItem==null) return;
        //----------------------------------------------------------------------
        DBItem xx[]=conn.getList(DBNote.class, rating,cellItem);
        rating.notes.clear();        
        for(int i=0;i<xx.length;i++){
            rating.notes.add(xx[i]);
            }
        xx=conn.getList(DBVariant.class, rating,cellItem);
        rating.vars.clear();        
        for(int i=0;i<xx.length;i++){
            rating.vars.add(xx[i]);
            }
        xx=conn.getList(DBArchFile.class, rating,cellItem);
        rating.archs.clear();        
        for(int i=0;i<xx.length;i++){
            rating.archs.add(xx[i]);
            }
        xx=conn.getList(DBDocFile.class, rating,cellItem);
        rating.docs.clear();        
        for(int i=0;i<xx.length;i++){
            rating.docs.add(xx[i]);
            }
        xx=conn.getList(DBPropusk.class, rating);
        rating.propusk.clear();
        for(int i=0;i<xx.length;i++){
            rating.propusk.add(xx[i]);
            }
        }
    @Override
    public void loadFullRating(int id) throws Throwable{
        loadShortRating(id);
        loadStudentList();
        loadCellList();
        loadEventList();
        loadPermissionList();
        //----------------------------------------------------------------------
        DBItem xx[]=conn.getList(DBNote.class, rating);
        rating.notes.clear();        
        for(int i=0;i<xx.length;i++){
            rating.notes.add(xx[i]);
            }
        xx=conn.getList(DBVariant.class, rating);
        rating.vars.clear();        
        for(int i=0;i<xx.length;i++){
            rating.vars.add(xx[i]);
            }
        xx=conn.getList(DBArchFile.class, rating);
        rating.archs.clear();        
        for(int i=0;i<xx.length;i++){
            rating.archs.add(xx[i]);
            }
        xx=conn.getList(DBDocFile.class, rating);
        rating.docs.clear();        
        for(int i=0;i<xx.length;i++){
            rating.docs.add(xx[i]);
            }
        xx=conn.getList(DBPropusk.class, rating);
        rating.propusk.clear();
        for(int i=0;i<xx.length;i++){
            rating.propusk.add(xx[i]);
            }
        }
    //=============== ЗДЕСЬ СБОРКА МОДЕЛЬНОГО ОБЪЕКТА
    @Override
    public MDNote getNote() throws Throwable{
        if (!testStudentCell()) return null;
        DBNote note=(DBNote)getCurrent(DBNote.class);
        DBVariant var=(DBVariant)getCurrent(DBVariant.class);
        DBDocFile doc=(DBDocFile)getCurrent(DBDocFile.class);
        DBArchFile arch=(DBArchFile)getCurrent(DBArchFile.class);
        MDNote note2=null;
        if (note==null && var==null && doc==null && arch==null)
            return null;
        if(note!=null) note2=new MDNote(note);
        else{
            note2=new MDNote(studentItem.getId(),rating.getId(),cellItem.getId(),0,0);
            note2.setRemoved(true);
            }
        if (var!=null) note2.setVariant(var.getVariant());
        if (doc!=null){
            note2.setIdDoc(doc.getFileId());
            note2.setDocFile(doc.getFileName());
            note2.setDocDate(doc.getCDate());
            }
        if (arch!=null){
            note2.setIdArch(arch.getFileId());
            note2.setArchFile(arch.getFileName());
            note2.setArchDate(arch.getCDate());
            }
        return note2;
        }
    @Override
    public DBItem []getNoteHistory() throws Throwable{
        return getCurrentAll(DBNote.class);
    	}
    @Override
    public int writeFile(File ff,boolean doc, int cdate)throws Throwable {
        return writeFile(ff, cdate);
        }
    private int writeFile(File ff,int cdate)throws Throwable {
        FileInputStream in=null;
        int lnt=(int)ff.length();
        byte data[]=new byte[blockSize];
        DBFileData cmd=new DBFileData();
        conn.insert(cmd);
        int idFile=cmd.getId();
        try {
            String ss=ff.getPath();
            in=new FileInputStream(ss);
            while(lnt!=0){
            	int sz=(lnt>blockSize ? blockSize : lnt);
            	lnt-=sz;
            	in.read(data,0,sz);
            	DBFileData block=new DBFileData();
            	block.setIdFileData(idFile);
            	block.setData(new String(Base64Coder.encode(data,0,sz)));
            	conn.insert(block);
            	}
        	in.close();
            } catch(Throwable ee){ 
                if (in!=null) in.close();
                throw new BRSException(ee);
                }
        return idFile;
        }
    @Override
    public void readFile(File ff,  int id, boolean doc, int cdate)throws Throwable {
        readFile(ff,id,cdate);
        }
    private void readFile(File ff,  int id, int cdate)throws Throwable {
        FileOutputStream out=null;
        try {
            DBFileData vv=new DBFileData();
            vv.setId(id);
            int ids[]=conn.getListId(DBFileData.class,vv);
            //DBItem data[]=conn.getList(DBFileData.class,vv);
            out=new FileOutputStream(ff.getPath());
            for(int i=0;i<ids.length;i++){
                DBFileData it=(DBFileData)conn.getById(DBFileData.class, ids[i]);
            	byte bb[]=Base64Coder.decode(it.getData());
            	for(int j=0;j<bb.length;j++)
            		out.write(bb[j]);
            	}
            out.close();
            } catch(Throwable ee){ 
                if (out!=null) out.close();
                throw new BRSException(ee);
                }
        }
    
    @Override
    public void deleteFile(int id, int fid, boolean doc) throws Throwable {
        DBFileData vv=new DBFileData();
        vv.setId(fid);
        conn.deleteLinked(DBFileData.class,vv);
        conn.delete(doc ? DBDocFile.class : DBArchFile.class, id);
        }
    
    @Override
    public void delete(Class table, int id) throws Throwable {
        conn.delete(table,id);
        }

    @Override
    public DBItem[] getTutorList() throws Throwable {
        DBItem xx[]=conn.getList(DBTutor.class);        
        return xx;
        //DBItem out[]=new DBItem[xx.length];
        //for(int i=0;i<xx.length;i++){
        //    DBNamedItem zz=(DBNamedItem)xx[i];
        //    out[i]=new DBNamedItem(zz.getId(),zz.getName());
        //    }
        //return out;
        }

    @Override
    public DBItem[] getGroupList() throws Throwable {
        return conn.getList(DBGroups.class);        
        }

    @Override
    public DBItem[] getStudentList(int groupId) throws Throwable {
        DBGroups gr=new DBGroups();
        gr.setId(groupId);
        DBItem xx[]=conn.getList(DBStudent.class,gr); 
        return xx;
        //DBItem out[]=new DBItem[xx.length];
        //for(int i=0;i<xx.length;i++){
        //    DBNamedItem zz=(DBNamedItem)xx[i];
        //    out[i]=new DBNamedItem(zz.getId(),zz.getName());
        //    }
        //return out;        
        }   
    @Override
    public String  testStudent(DBStudent item, boolean ciuLogin) throws Throwable{
        DBStudent item2=(DBStudent)conn.getById(DBStudent.class, item.getId());
        if (item2==null) return "Нет логина "+item.getName();
        if (!ciuLogin){
            if (item2.getPass().length()==0)
                return "Нет локального пароля";
            if (!item2.getPass().equals(item.getPass()))
                return "Неправильный пароль";
            else 
                return "";
            }
        String out="";
        try {
            if (item.getCiuLogin().length()==0) return "Отсутствует логин ЦИТ";
            Object oo=(new CIUConnection()).get("/isu/ido_auth",item.getCiuLogin(),item.getPass(),null,entry);
            if (oo instanceof CIUError) return "Недопустимый логин/пароль ЦИТ";
            CIUStudent st=(CIUStudent)oo;
            String ss=st.f+" "+st.n+" "+st.o;
            ss=ss.toUpperCase().trim();
            String s2=item.getName().toUpperCase().trim();
            if (!ss.equals(s2)) return "Чужой логин: "+ss;
            if (item2.getCiuLogin().length()==0){
                item2.setCiuLogin(item.getCiuLogin());
                conn.update(item2);
                }
            } catch(Throwable ee){ out="Ошибка сервера ЦИТ"; }
        return out;
        }

    @Override
    public boolean testBase(String name) throws Throwable {
        return true;
        }
    @Override
    public DBIdentification getDBIdentification() throws Throwable {
        return (DBIdentification)conn.getFirst(DBIdentification.class);
        }
    @Override
    public void flush() throws Throwable {
        }
    private int getFileCount(Class clazz) throws Throwable{
        int count=0;
        DBItem xx[] = conn.getList(clazz, rating);
        for (int i=0;i<xx.length;i++){
            DBFile file=(DBFile)xx[i];
            if (file.inArchive())
                count++;
            }
        return count;
        }
    @Override
    public int getFileCount() throws Throwable {
        if (rating==null)
            return 0;
        return 
            getFileCount(DBDocFile.class) +
            getFileCount(DBArchFile.class);
        }

    }