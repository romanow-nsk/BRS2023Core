/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.model;

import me.romanow.brs.database.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;

import me.romanow.brs.database.DBEntry;
import me.romanow.brs.database.DBEvent;
import me.romanow.brs.database.DBItem;
import me.romanow.brs.database.DBPropusk;
import me.romanow.brs.database.DBTutor;
import me.romanow.brs.interfaces.BRSException;
import static me.romanow.brs.model.MDBaseUserSQL.blockSize;

/**
 *
 * @author user
 */
public class MDBaseUserBinFile extends MDBaseUser{
    private static final String ratingListName="ratingList.dat";
    public int lastEventSize=0;
    public int lastNoteSize=0;
    public int lastPropuskSize=0;
    public int lastVariantSize=0;    
    private int changesCount=0;	
    private String keyString="";
    private String dirName=null;
    public MDItemVector fileRatingList=new MDItemVector();
    public boolean isLocalFile(){
        return true;
        }
    public String getKeyString() {
        return keyString;
        }
    public void setKeyString(String keyString) {
        this.keyString = keyString;
        }
    public void setLastSize(){
        lastNoteSize=rating.notes.size();
        lastEventSize=rating.events.size();
        lastPropuskSize=rating.propusk.size();
        lastVariantSize=rating.vars.size();
        }
    private void testDir() throws Throwable{
        File dir=new File(dirName);
        if (!dir.exists()) dir.mkdir();
        }
    public MDBaseUserBinFile(String dir){ 
        super(null,null);
        rating=null;
        dirName=dir;
        }
    @Override
    public void flush() throws Throwable {
        if (rating==null || !wasChanged) return;
        wasChanged=false;
        save();
        }
    public void load(MDRatingDescription dsc) throws Throwable {
        testDir();
        flush();
        entry=dsc.entry;
        DataInputStream is=new DataInputStream(new FileInputStream(dirName+dsc.rating.getName()+DBEntry.localExt));
        load(is);
        is.close();
        }
    public void save() throws Throwable {
        testDir();
        DataOutputStream is=new DataOutputStream(new FileOutputStream(dirName+getRatingFileName()+DBEntry.localExt));
        save(is);
        is.close();
        }
    public void save(DataOutputStream os) throws Throwable{
        entry.save(os);
        os.writeBoolean(tutor!=null);
        if (tutor!=null) 
            tutor.saveDBValues(os);
        if (permission==null){
            os.writeInt(0);
            }
        else{
            os.writeInt(permission.length);
            for(int i=0;i<permission.length;i++)
                permission[i].save(os);            
            }
        os.writeBoolean(editEnabled);
        os.writeInt(lastNoteSize);
        os.writeInt(lastEventSize);
        os.writeInt(lastPropuskSize);
        os.writeInt(lastVariantSize);
        os.writeInt(changesCount);
        os.writeUTF(keyString);
        rating.save(os);
        }
public void save(MDBaseUser vv) throws Throwable {
        testDir();
        DataOutputStream os=new DataOutputStream(new FileOutputStream(dirName+vv.getRatingFileName()+DBEntry.localExt));
        lastNoteSize=vv.rating.notes.size();
        lastEventSize=vv.rating.events.size();
        lastPropuskSize=vv.rating.propusk.size();
        lastVariantSize=vv.rating.vars.size();
        changesCount=0;
        vv.entry.save(os);
        os.writeBoolean(vv.tutor!=null);
        if (vv.tutor!=null) 
            vv.tutor.saveDBValues(os);     
        if (vv.permission==null){
            os.writeInt(0);
            }
        else{
            os.writeInt(vv.permission.length);
            for(int i=0;i<vv.permission.length;i++)
                vv.permission[i].save(os);            
            }
        os.writeBoolean(vv.editEnabled);
        os.writeInt(lastNoteSize);
        os.writeInt(lastEventSize);
        os.writeInt(lastPropuskSize);
        os.writeInt(lastVariantSize);
        os.writeInt(changesCount);
        os.writeUTF(keyString);
        vv.rating.save(os);
        os.close();
        }
    public void load(String fname) throws Throwable {
        DataInputStream is=new DataInputStream(new FileInputStream(dirName+fname+DBEntry.localExt));
        load(is);
        }
    public void load(MDBaseUser user, int id) throws Throwable {
        user.loadFullRating(id);
        entry=user.entry;
        tutor=user.tutor;
        rating=user.rating;
        permission = user.permission;
        editEnabled=user.editEnabled;
        setLastSize();
        }
    public void loadShort(DataInputStream is) throws Throwable{
        testDir();
        flush();
        loadBase(is);
        rating=new MDRating();
        rating.loadDBValues(is);    // Только объект БД !!!!!!!!!!!!!!!!!!!!!!!!!!
        }
    public void load(DataInputStream is) throws Throwable{
        testDir();
        flush();
        loadBase(is);
        rating=new MDRating();
        rating.load(is);
        }
    public void loadBase(DataInputStream is) throws Throwable{
        entry=new DBProfile();
        entry.load(is);
        tutor=null;
        if (is.readBoolean()){
            tutor=new DBTutor();
            tutor.loadDBValues(is);
            }
        int nn=is.readInt();
        if (nn == 0)
            permission=null;
        else{
            permission=new DBTutor[nn];
            for (int i=0;i<nn;i++){
                permission[i]=new DBTutor();
                permission[i].load(is);
                }
            }
        editEnabled=is.readBoolean();
        lastNoteSize=is.readInt();
        lastEventSize=is.readInt();
        lastPropuskSize=is.readInt();
        lastVariantSize=is.readInt();
        changesCount=is.readInt();
        keyString=is.readUTF();
        } 
    private DBItem []getNoteList(int mode){
    	Vector<DBNote> xx=new Vector();
    	for(int i=0;i<rating.notes.size();i++){
    		DBNote tt=(DBNote)rating.notes.get(i);
    		boolean b1=false;
    		if (mode==0) b1=tt.getIdStudent()==studentItem.getId() && tt.getIdCell()==cellItem.getId();
    		if (mode==1) b1=tt.getIdCell()==cellItem.getId();
    		if (mode==2) b1=tt.getIdStudent()==studentItem.getId();
    		if (b1) xx.add(tt);
    		}
    	DBItem out[]=new DBItem[xx.size()];
    	for(int i=0;i<xx.size();i++)
    		out[i]=xx.get(i);
    	return out;
    	}
    private DBItem []getPropuskList(int mode){
    	Vector<DBPropusk> xx=new Vector();
    	for(int i=0;i<rating.propusk.size();i++){
    		DBPropusk tt=(DBPropusk)rating.propusk.get(i);
    		boolean b1=false;
    		if (mode==0) b1=tt.getIdStudent()==studentItem.getId() && tt.getIdEvent()==eventItem.getId();
    		if (mode==1) b1=tt.getIdEvent()==eventItem.getId();
    		if (mode==2) b1=tt.getIdStudent()==studentItem.getId();
    		if (b1) xx.add(tt);
    		}
    	DBItem out[]=new DBItem[xx.size()];
    	for(int i=0;i<xx.size();i++)
    		out[i]=xx.get(i);
    	return out;
    	}
    //------------ Предполагается упорядоченные по id
    int getMaxId(Vector<DBItem> src){
        if (src.size()==0) return 1;
        return src.get(src.size()-1).getId();
        }
    public void synchDataUpDown(MDBaseUser GG) throws Throwable{
        for(int i=lastNoteSize;i<rating.notes.size();i++){
        	DBNote note=(DBNote)rating.notes.get(i);
        	GG.studentItem=(MDStudent)GG.rating.groups.students.getById(note.getIdStudent());
        	GG.cellItem=(MDCell)GG.rating.course.cells.getById(note.getIdCell());
                GG.changeNote(new MDNote(note));
        	}
        for(int i=lastVariantSize;i<rating.vars.size();i++){
        	DBVariant var=(DBVariant)rating.vars.get(i);
        	GG.studentItem=(MDStudent)GG.rating.groups.students.getById(var.getIdStudent());
        	GG.cellItem=(MDCell)GG.rating.course.cells.getById(var.getIdCell());
                GG.changeVariant(var.getVariant());
        	}
        //--- Переустановить id-ы для новых событий и записать их в пропуски
        for(int i=lastEventSize;i<rating.events.size();i++){
            DBEvent dd=(DBEvent)rating.events.get(i);
            int oldId = dd.getId();
            int newId = GG.insertEvent(dd.getName(),dd.getEvtDate());
            for(int j=lastPropuskSize;j<rating.propusk.size();j++){
                DBPropusk xx = (DBPropusk)rating.propusk.get(j);
                xx.setIdEvent(newId);
                }
            dd.setId(newId);
            }
        for(int i=lastPropuskSize;i<rating.propusk.size();i++){
            DBPropusk vv=(DBPropusk)rating.propusk.get(i);
            GG.eventItem=(DBEvent)rating.events.getById(vv.getIdEvent());
            GG.changeEvent(vv.getIdStudent(), vv.getRemoved());
            }
        for(int i=0;i<rating.course.cells.size();i++)
            if (rating.course.cells.get(i).cellRatingChanged){
                GG.cellItem=rating.course.cells.get(i);
                GG.changeWeek(GG.cellItem.week, GG.cellItem.week2);
                rating.course.cells.get(i).cellRatingChanged=false;
                }
        for(int i=0;i<rating.groups.students.size();i++)
            if (rating.groups.students.get(i).studRatingChanged){
                GG.studentItem=rating.groups.students.get(i);
                GG.changeBrigade(GG.studentItem.brigade, GG.studentItem.second);
                rating.groups.students.get(i).studRatingChanged=false;
                }
        setLastSize();
        save();
        }
    public String testChangesString(){
        String ss="";
        int xx[]=testChanges();
        int sum=xx[0]+xx[1]+xx[2]+xx[3]+xx[4]+xx[5];
        if (sum==0) return ss;
        ss="Изменения в рейтинге:\n";
        ss+="оценки-"+xx[0]+", ";
        ss+="пропуски-"+xx[1]+", ";
        ss+="события-"+xx[2]+",\n";
        ss+="сроки-"+xx[3]+", ";
        ss+="варианты-"+xx[5]+", ";
        ss+="бригады-"+xx[4];
        return ss;
        }
    public int []testChanges(){
        int xx[]=new int[6];
        xx[0]=rating.notes.size()-lastNoteSize;
        xx[5]=rating.vars.size()-lastVariantSize;
        xx[1]=rating.propusk.size()-lastPropuskSize;
        xx[2]=rating.events.size()-lastEventSize;
        xx[3]=0;
        for(int i=0;i<rating.course.cells.size();i++)
            if (rating.course.cells.get(i).cellRatingChanged)
                xx[3]++;
        xx[4]=0;
        for(int i=0;i<rating.groups.students.size();i++)
            if (rating.groups.students.get(i).studRatingChanged)
                xx[4]++;
        return xx;
        }
    public int testAllChanges(){
    	int x[]=testChanges();
    	return x[0]+x[1]+x[2]+x[3]+x[4]+x[5];
    	}
    //==========================================================================
    @Override
    public void connect() throws Throwable {
    	}
    @Override
    public void reconnect() throws Throwable {
    	}
    @Override
    public void close() throws Throwable {
        flush();
        rating=null;
    	}
    @Override
    public boolean isConnected() throws Throwable {
        return true;
    	}
    int getRatingIndex(int id) throws Throwable{
        for (int i=0;i<ratingList.length;i++)
            if(ratingList[i].getId()==id)
                return i;
        throw new BRSException(BRSException.bug,"Ошибка id-рейтинга в лок. файле");
        }
    @Override             
    public void loadRating(int id) throws Throwable {  
        load(((MDBaseUserBinFile)ratingList[getRatingIndex(id)]).getRatingFileName());
        }
    @Override
    public void loadFullRating(int id) throws Throwable {
        load(((MDBaseUserBinFile)ratingList[getRatingIndex(id)]).getRatingFileName());
    	}
    @Override
    public void loadStudentRating(int id, int sid) throws Throwable {
        // Уже загружен -  установлен
    }

    @Override
    public void loadCellRating(int id, int cid) throws Throwable {
        // Уже загружен -  установлен
    }

    @Override
    public void testEdit() throws Throwable {
        // Уже загружен - установлен
    	}

    @Override
    public MDNote getNote() throws Throwable {
        if (!testStudentCell()) return null;
        DBNote note=(DBNote)getForStudentCell(rating.notes);
        DBVariant var=(DBVariant)getForStudentCell(rating.vars);
        DBDocFile doc=(DBDocFile)getForStudentCell(rating.docs);
        DBArchFile arch=(DBArchFile)getForStudentCell(rating.archs);
        MDNote note2=null;
        if (note==null && var==null && doc==null && arch==null)
            return null;
        if(note!=null) note2=new MDNote(note);
        else{
            note=new MDNote(studentItem.getId(),rating.getId(),cellItem.getId(),0,0);
            note.setRemoved(true);
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
            note2.setArchDate(doc.getCDate());
            }
        return note2;      
    	}

    @Override
    public DBItem[] getNoteHistory() throws Throwable {
        if (!testStudentCell()) return new DBItem[0];
        return getNoteList(0);
    	}
    //-------------------------------------------------------------------------------------
    private boolean testUser(DBTutor xx[]){
        if (tutor == null) return true;
        for(int i=0;i<xx.length;i++)
            if (xx[i].getName().equals(tutor.getName()))
                return true;
        return false;
        }
    public void loadFileRatingList(boolean allTutors) throws Throwable {
        File ff=new File(dirName);
        fileRatingList.clear();
        if (!ff.isDirectory()) {
            ratingList=new DBItem[0]; 
            return;
            }
        File dd[]=ff.listFiles();
        int k=0;
        for(int i=0;i<dd.length;i++){
            String ss=dd[i].getName();
            if (!ss.endsWith(DBEntry.localExt)) continue;
            MDBaseUserBinFile xx = new MDBaseUserBinFile(dirName);
            try {
                DataInputStream dis = new DataInputStream(new FileInputStream(dirName+ss));
                xx.loadShort(dis);
                dis.close();
                if (!entry.getIP().equals(xx.entry.getIP()))
                    continue;
                if (!entry.getDbName().equals(xx.entry.getDbName()))
                    continue;
                if (!allTutors && !testUser(xx.permission))
                    continue;
                xx.setName(xx.rating.getName());
                xx.setId(xx.rating.getId());     // id = id рейтинга !!!!!!!!!!!
                fileRatingList.add(xx);
                } catch (Throwable ee){}
            }
        }
    public DBItem []getFileBaseList(){
        MDItemVector vv=new MDItemVector();
        File ff=new File(dirName);
        fileRatingList.clear();
        if (!ff.isDirectory()) {
            return new DBItem[0]; 
            }
        File dd[]=ff.listFiles();
        int k=0;
        for(int i=0;i<dd.length;i++){
            String ss=dd[i].getName();
            if (!ss.endsWith(DBEntry.localExt)) continue;
            MDBaseUserBinFile xx = new MDBaseUserBinFile(dirName);
            try {
                DataInputStream dis = new DataInputStream(new FileInputStream(dirName+ss));
                xx.loadShort(dis);
                dis.close();
                boolean found=false;
                for(int j=0;j<vv.size();j++){
                    DBBases zz=(DBBases)vv.get(j);
                    if (zz.getDbName().equals(xx.entry.getDbName())){
                        found=true;
                        break;
                        }
                    }
                if (!found){
                    DBBases zz=new DBBases();
                    zz.setDbName(xx.entry.getDbName());
                    zz.setName(xx.entry.getName());
                    vv.add(zz);
                    }
                } catch (Throwable ee){}
            }
        return vv.toArray();
        }
    @Override
    public void getRatingList(int id, int mode) throws Throwable {
        if (mode!=1)
            ratingList = new DBItem[0];
        else
            loadFileRatingList(false);
            ratingList = fileRatingList.toArray();
        }
 
    @Override
    public MDEvent getEvent(boolean full) throws Throwable {
        return getEvent(full, new MDEvent(eventItem), getPropuskList(1));
    	}

    @Override
    public void deleteEvent() throws Throwable {
        if (rating==null || eventItem==null) return;
        int kk=rating.events.getIdxById(eventItem.getId());
        if (kk<lastEventSize) throw new BRSException(BRSException.other,"Можно удалять только собственные события");
        int kk2=rating.events.get(kk).getId();
        rating.events.remove(kk);
        for(int i=lastPropuskSize;i<rating.propusk.size();i++){
            DBPropusk vv=(DBPropusk)rating.propusk.get(i);
            if (vv.getIdEvent()!=kk2) continue;
            rating.propusk.remove(i);
            i--;
            }
        wasChanged=true;
        }

    @Override
    public int insertEvent(String ss, int cdate) throws Throwable {
        if (rating==null) return 0;
        int id = rating.insertEvent(ss, cdate);
        wasChanged=true;
        return id;
        }
    @Override
    public String testUser(DBTutor item, boolean ciuLogin) throws Throwable {
        return "";
        }
    @Override
    public double calcPropusk()throws Throwable{
        return calcPropusk(getPropuskList(2),studentItem);
    	}
    @Override 
    public double calcStudentRating() throws Throwable{
        if (rating==null || studentItem==null)  return 0;
        return calcStudentRating(getNoteList(2));
        }

    @Override
    public void changeEvent(int studId, boolean prop) throws Throwable {
        rating.changeEvent(studId, eventItem.getId(), prop);
        wasChanged=true;
    	}

    @Override
    public void changeNote(MDNote note) throws Throwable {
        if (!testStudentCell()) return;
        rating.changeNote(note);
        wasChanged=true;
        }

    @Override
    public void changeVariant(String value) throws Throwable {
        if (!testStudentCell()) return;
        rating.changeVariant(value, studentItem.getId(),cellItem.getId());
        wasChanged=true;
        }
    @Override
    public void changeFile(String value, int id, boolean full, boolean doc) throws Throwable {
        if (!testStudentCell()) return;
        if (doc)
            rating.docs.add(new DBDocFile(rating.getId(),studentItem.getId(),cellItem.getId(),id,value));
        else
            rating.archs.add(new DBArchFile(rating.getId(),studentItem.getId(),cellItem.getId(),id,value));
        wasChanged=true;
        }
    @Override
    public void changeBrigade(int val, boolean val2) throws Throwable {
        if (rating==null || studentItem==null) return;
        studentItem.brigade=val;
        studentItem.second=val2;
        studentItem.cDate=new OnlyDate().dateToInt();
        studentItem.studRatingChanged=true;
        wasChanged=true;
        }

    @Override
    public void changeWeek(int val1, int val2) throws Throwable {
        if (rating==null || cellItem==null) return;
        cellItem.week=val1;
        cellItem.week2=val2;
        cellItem.cDate=new OnlyDate().dateToInt();
        cellItem.cellRatingChanged=true;
        wasChanged=true;
    	}

    @Override
    public void loadStudentList() throws Throwable {
        // Уже загружен - установлен
        }

    @Override
    public void loadCellList() throws Throwable {
        // Уже загружен - установлен
        }

    @Override
    public void loadEventList() throws Throwable {
        // Уже загружен - установлен
        }

    @Override
    public void loadPermissionList() throws Throwable {
        // Уже загружен - установлен
    }
    @Override
    public int writeFile(File ff,boolean doc, int cdate) throws Throwable {
        FileInputStream in=null;
        int lnt=(int)ff.length();
        byte data[]=new byte[blockSize];
        String path=dirName+rating.getName();
        File dd=new File(path);
        if (!dd.exists()) dd.mkdir();
        String fname=getFirst(studentItem.getName())+"_"+cellItem.getName()+"_"+OnlyDate.dateFromInt(cdate)+"_"+ff.getName();
        FileOutputStream out=new FileOutputStream(path+"/"+fname);
        try {
            String ss=ff.getPath();
            in=new FileInputStream(ss);
            while(lnt!=0){
            	int sz=(lnt>blockSize ? blockSize : lnt);
            	lnt-=sz;
            	in.read(data,0,sz);
                out.write(data, 0, sz);
            	}
            in.close();
            out.close();
            } catch(Throwable ee){ 
                if (in!=null) in.close();
                throw new BRSException(ee);
                }
        return 0;
    	}

    @Override
    public void readFile(File ff,int id,boolean doc, int cdate) throws Throwable {
        byte data[]=new byte[blockSize];
        String path=dirName+rating.getName();
        File dd=new File(path);
        if (!dd.exists()) throw new BRSException(BRSException.io,"Каталог "+path+" не найден");
        FileOutputStream out=null;
        FileInputStream in=null;
        String fname=getFirst(studentItem.getName())+"_"+cellItem.getName()+"_"+OnlyDate.dateFromInt(cdate)+"_"+ff.getName();
        dd=new File(path+"/"+fname);
        if (!dd.exists()) throw new BRSException(BRSException.io,"Файл "+fname+" не найден");
        int lnt=(int)dd.length();
        try {
            out=new FileOutputStream(ff.getPath());
            in=new FileInputStream(dd.getPath());
            while(lnt!=0){
            	int sz=(lnt>blockSize ? blockSize : lnt);
            	lnt-=sz;
            	in.read(data,0,sz);
                out.write(data, 0, sz);
            	}
            in.close();
            out.close();
            } catch(Throwable ee){ 
                if (out!=null) out.close();
                throw new BRSException(ee);
                }
    	}

    //@Override
    //public int insert(DBItem item) throws Throwable {
    //    throw new BRSException(BRSException.nofunc);
    //    }

    @Override
    public void delete(Class table, int id) throws Throwable {
        throw new BRSException(BRSException.nofunc);
        }

    @Override
    public DBItem[] getTutorList() throws Throwable {
        DBItem vv[]=new DBItem[1];
        vv[0]=new DBTutor("без пароля","");
        return vv;
        }

    @Override
    public DBItem[] getGroupList() throws Throwable {
        throw new BRSException(BRSException.nofunc);    
        }
    
    @Override
    public DBItem[] getStudentList(int groupId) throws Throwable {
        throw new BRSException(BRSException.nofunc);    
        }

    @Override
    public String testStudent(DBStudent item, boolean ciuLogin) throws Throwable {
        throw new BRSException(BRSException.nofunc);    
        }

    @Override
    public void deleteFile(int id, int fid, boolean doc) throws Throwable {
        throw new BRSException(BRSException.nofunc);    
        }

    @Override
    public boolean testBase(String name) throws Throwable {
        return true;
        }

    @Override
    public DBIdentification getDBIdentification() throws Throwable {
        throw new BRSException(BRSException.nofunc);    
        }

    @Override
    public int getFileCount() throws Throwable {
        return 0;
        }
}
