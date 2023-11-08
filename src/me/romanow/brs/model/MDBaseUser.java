/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.model;

import me.romanow.brs.xml.XMLBoolean;
import me.romanow.brs.xml.XMLInt;
import me.romanow.brs.xml.XMLDouble;
import me.romanow.brs.database.DBItem;
import me.romanow.brs.database.DBPropusk;

import java.io.File;
import java.util.Vector;
import me.romanow.brs.database.*;
import me.romanow.brs.interfaces.BRSException;
import me.romanow.brs.interfaces.DBConnect;

/**
 *
 * @author user
 */
public abstract class MDBaseUser extends DBNamedItem implements MDBaseFace{
    //-------------- Компоненты данных -----------------------------------------
    public MDRating rating=null;        // бизнес-объект - рейтинг
    public DBTutor tutor=null;          // бизнес-объект - преподаватель   
    public DBProfile entry=null;        // бизнес-объект - данные подключения 
    //-------------- Компоненты редактирования ---------------------------------
    public DBTutor []permission=null;   // массив id-ов разрешений для преподавателя
    public DBItem []ratingList=null;    // массив id-ов списка рейтингов
    public boolean editEnabled=false;   // разрешение редактирования рейтинга
    public MDStudent studentItem=null;  // бизнес-объект - выбранный студент
    public MDCell cellItem=null;        // бизнес-объект - выбранная единица контроля
    public DBEvent eventItem=null;      // бизнес-объект - котроля посещаемости (занятие)
    public boolean wasChanged=false;    // флаг изменения рейтинга
    public DBConnect getConnect(){ return null; }
    //--------------------------------------------------------------------------
    public void convert() throws Throwable{}
    public MDBaseUser (DBTutor tutor,DBProfile entry){ 
        this.tutor=tutor; 
        this.entry=entry;
        }
    public boolean isLocalFile(){
        return false;
        }
    public int createFileDataId() throws Throwable{
        return 0;
        }
    public String getRatingFileName(){
        return entry.getDbName()+"_"+entry.getIP()+"_"+rating.getId()+(tutor==null ? "" : "_"+tutor.getId());
        }
    DBItem getForStudentCell(Vector<DBItem> vv){
        for(int i=vv.size()-1; i>=0;i--){
            DBStudentCellItem xx=(DBStudentCellItem)vv.get(i);
            if (xx.getIdRating()==rating.getId() && xx.getIdStudent()==studentItem.getId() && xx.getIdCell()==cellItem.getId())
                return xx;
            }
        return null;
        }

    public void clear(){ 
        rating=null;
        editEnabled=false;
        studentItem=null;
        cellItem=null;
        eventItem=null;
        }
    public void calcBall(DBNote note){
        calcBall(note,cellItem,studentItem);
        }
    public void calcBall(DBNote note,MDCell cl){
        calcBall(note,cl,studentItem);
        }
    //------------- подсчет оценки для студента - единицы контроля
    public void calcBall(DBNote note,MDCell cl, MDStudent sr){
        byte bb[]=DBCell.NoteParamValues[cl.getCType()];    // параметры-разрешения для типа оценки
        if (bb[DBCell.idxManual]==1) return;                // ручная установка оценки
        double k=1;                                         // масштабный коэффициент
        if (bb[DBCell.idxCData]==1){                        // вычисляемая оценка
            int week=cl.week;                               // нормативная неделя сдачи
            if (rating.getSecond() && bb[DBCell.idxSecond]==1 && sr.second) 
                week=cl.week2;
            int dd=0;
            if (week!=0) dd = note.getWeek() - week;        // разница между нормативной
            int mx=rating.params.getMaxWeek();              // и фактической неделями
            if (dd >  mx) dd=mx;                            // учет границ
            if (dd < -mx) dd=-mx;
            k-=dd*rating.params.getWeekProc()/100.;         // масштабный коэфиициент
            }                                               // с учетом недели
        boolean vv[]=note.unpack();
        double proc=rating.params.getPlusProc()/100.;
        for (int i=0;i<DBCell.QualityTypes.length;i++){
            if (!vv[i]) continue;
            if (DBCell.QualityTypes[i].charAt(0)=='-')      // масштабный коэфиициент
                k-=proc;                                    // с учетом параметров качества
            else 
                k+=proc;
            }
        double ball2=cl.getBall()*k;                        // расчет балла от норматива
        note.setBall(ball2);                                // записать в объект-оценку
        }
    //--------------------------------------------------------------------------
    public boolean testStudentCell(){
        return rating!=null && cellItem !=null && studentItem !=null; 
        }

    //--------------------------------------------------------------------------
    private String format(String in,int sz){
        int n=in.length();
        if (n>sz) in=in.substring(0, sz);
        else while(n++!=sz) in+=" ";
        return in;
        }
    //---------- Округление в первом знаке------------------------------------
    private String ftoa(double dd){
        String ss="";
        dd+=0.05;
        int xx=(int)dd;
        if (xx/10!=0) ss+=(char)(xx/10+'0');
        ss+=(char)(xx%10+'0');
        dd-=xx;
        int vv=(int)(dd*10);
        if (vv!=0)
            ss+="."+(char)(vv+'0');
        return ss;
        }
    private final static int numberOfHeaderCols=8;
    private String []createCols(){
    	String out[]=new String[DBCell.QualityTypes.length+numberOfHeaderCols];
    	out[0]="Бригада";
    	out[1]="Вариант";
    	out[2]="Балл";
    	out[3]="Макс.балл";
    	out[4]="Неделя";
    	out[5]="Срок";
    	out[6]="Отчет";
    	out[7]="Исходник";
    	for(int i=0;i<DBCell.QualityTypes.length;i++)
    		out[i+numberOfHeaderCols]=DBCell.QualityTypes[i];
    	return out;
    	}
    //--------------------------------------------------------------------------    
    public TableData createNoteHistoryTable() throws Throwable{
        DBItem xx[]=this.getNoteHistory();
        if (xx==null) return null;
        byte bb[]=cellItem.getCellParams();
        TableData tableSource=new TableData(xx.length,createCols());
        tableSource.cols[1]="Удалено из БД";
        tableSource.title=rating.getName()+" "+cellItem.getName()+" "+studentItem.getName();
        int ii=0;
        for (int k=0,i=xx.length-1;i>=0;i--,k++){
            DBNote note=(DBNote)xx[i];
            double ball=note.getBall();
            tableSource.rows[k]=""+OnlyDate.dateFromInt(note.getCDate());
            tableSource.data[k][1]=note.getRemoved() ? "+" : " ";
            tableSource.data[k][0]=""+ftoa(ball);
            int week0=0;
            week0=cellItem.week;
            if (bb[DBCell.idxSecond]!=0 && studentItem.second && rating.getSecond())
            week0=cellItem.week2;
            if (week0!=0){
               	tableSource.data[k][2]=""+note.getWeek();
                tableSource.data[k][3]=""+week0;
                }
            //if (note.getDocFile().length()!=0) tableSource.data[k][5]="+";
            //if (note.getArchFile().length()!=0) tableSource.data[k][6]="+";
            boolean params[]=note.unpack();
            for(int j=0;j<DBCell.QualityTypes.length;j++){
               	 if(params[j]) tableSource.data[k][j+numberOfHeaderCols]="+";
                 }
            }
        return tableSource;
    	}
    //--------------------------------------------------------------------------           
    public void createRatingStudentTable(TableData tableSource) throws Throwable{
        if (rating==null || studentItem==null) 
            throw new BRSException(BRSException.warn,"Не выбраны параметры");
        MDStudentRating in=getStudentRating();
        int nn=0;
        tableSource.setTable(in.cellList.size(),createCols());
        tableSource.title=rating.getName()+" "+studentItem.getName();
        tableSource.bottoms=new String[in.prop.length];
        for(int i=0;i<in.prop.length;i++)
            tableSource.bottoms[i]=in.prop[i].getName()+" ("+in.prop[i].getEvtDate()+")";
        for (int kk=0;kk<in.cellList.size();kk++){
            String vv[]=tableSource.data[kk];
            DBNote note=in.notes[kk];
            MDCell cell=in.cellList.get(kk);
            DBVariant var=(DBVariant)rating.vars.getForStudentCell(note);
            if (var!=null)
                vv[1]=var.getVariant();
            tableSource.rows[kk]=cell.getName();
            vv[3]=""+cell.getBall();
            DBFile file=(DBFile)rating.docs.getForStudentCell(note);
            if (file!=null) vv[6]="+";
            file=(DBFile)rating.archs.getForStudentCell(note);
            if (file!=null) vv[7]="+";
            boolean params[]=note.unpack();
            if (note.getRemoved()) continue;
            double ball=note.getBall();
            vv[2]=""+ftoa(ball);
            int week0=getMaxWeek(cell,studentItem);
            if (week0>0){
              	int week=note.getWeek();
              	if (week==0) vv[3]="";
               	else
                    vv[4]=""+week;
                    vv[5]=""+week0;
                    }
            for(int j=0;j<DBCell.QualityTypes.length;j++){
             	if(params[j]) vv[j+numberOfHeaderCols]="+";
               	}
            }
        int cc=studentItem.brigade;
        String ss="";
        if (cc!=0){
            ss="Бригада: "+cc+" ";
            if (rating.getSecond())
                ss+="-"+(studentItem.second?2:1);
            }
        tableSource.bottom2=ss+"Пропусков: "+in.propusk;
        int vv=(int)in.ball;
        tableSource.bottom="Рейтинг: "+vv+"  ("+MDItem.ECTS(vv)+") Оценка: "+MDItem.note(vv);
   	}
    //--------------------------------------------------------------------------
    public void createRatingCellTable(TableData tableSource) throws Throwable{
        if (rating==null || cellItem==null)
            throw new BRSException(BRSException.warn,"Не выбраны параметры");
        MDCellRating in=getCellRating();
        MDStudentVector stud=rating.groups.students;
        byte bb[]=cellItem.getCellParams();
        int nn=0;
        tableSource.setTable(stud.size(),createCols());
        tableSource.title=rating.getName()+" "+cellItem.getName();
        for (int kk=0;kk<stud.size();kk++){
            String vv[]=tableSource.data[kk];
            MDStudent std=in.studentList.get(kk);
            DBNote note=in.notes[kk];
            int cc=std.brigade;
            if (cc!=0){
                String ss=""+cc;
                if (rating.getSecond())
                    ss+="-"+(std.second?2:1);
                vv[0]=ss;
                }
            DBVariant var=(DBVariant)rating.vars.getForStudentCell(note);
            if (var!=null)
                vv[1]=var.getVariant();
            tableSource.rows[kk]=stud.get(kk).getName();
            DBFile file=(DBFile)rating.docs.getForStudentCell(note);
            if (file!=null) vv[6]="+";
            file=(DBFile)rating.archs.getForStudentCell(note);
            if (file!=null) vv[7]="+";
            if (note.getRemoved()) continue;
            double ball=note.getBall();
            vv[3]=""+cellItem.getBall();
            vv[2]=""+ftoa(ball);
            int week0=getMaxWeek(cellItem,std);
            if (week0>0){
               	vv[4]=""+note.getWeek();
                vv[5]=""+week0;
                }
            boolean params[]=note.unpack();
            for(int j=0;j<DBCell.QualityTypes.length;j++){
               	 if(params[j]) vv[j+numberOfHeaderCols]="+";
                 }
            }
        String ss="";
        if (nn!=0) ss="Ср.балл: "+MDItem.printBall(in.ball);
        tableSource.bottom2=ss;
        tableSource.bottom="Сдали: "+nn+"  Не сдали: "+(stud.size()-nn);
    	}
    //--------------------------------------------------------------------------
    public void createRatingTable(TableData tableSource) throws Throwable{
            if (rating==null)
                throw new BRSException(BRSException.warn,"Не выбраны параметры");
            MDTotalRating in=getTotalRating();
            int n1=in.studentList.size();
            int n2=in.cellList.size();
            tableSource.title=rating.getName();
            tableSource.setTable(n2+4,n1+1);
            tableSource.title="Рейтинг "+rating.getName();
            for (int i=0;i<n1;i++)
            	tableSource.rows[i]=in.studentList.get(i).getName();
            tableSource.rows[n1]="";
            for (int i=0;i<n2;i++)
            	tableSource.cols[i+1]=in.cellList.get(i).getName();
            tableSource.cols[0]="Бригада";
            tableSource.cols[n2+1]="Рейтинг";
            tableSource.cols[n2+2]="ESTC";
            tableSource.cols[n2+3]="Оценка";
            for(int i=0;i<in.studentList.size();i++){
                for(int j=0;j<in.cellList.size();j++){
                    tableSource.select[i][j+1]=in.sel[i*n2+j].val;
                    double ball=in.ball[i*n2+j].val;
                    if(ball<=0) continue;
                    tableSource.data[i][j+1]=ftoa(ball);
                    }
                }
            for (int i=0;i<n1;i++){
                int cc=in.studentList.get(i).brigade;
                if (cc!=0){
                    String ss=""+cc;
                    if (rating.getSecond())
                        ss+="-"+(in.studentList.get(i).second?2:1);
                    tableSource.data[i][0]=ss;
                    }
            	}
            for (int i=0;i<n1;i++){
                double ball=in.sums[i].val;
                if (ball<=0) continue;
                int val=(int)(ball+0.5);
                tableSource.data[i][n2+1]=""+val;
                tableSource.data[i][n2+2]=""+MDItem.ECTS(val);
                tableSource.data[i][n2+3]=""+MDItem.note(val);
                }
            for (int i=0;i<n2;i++) {
                double ball=in.sumc[i].val;
                if (ball<=0) continue;
                tableSource.data[n1][i+1]=ftoa(ball);
                }
            tableSource.rows[n1]="Средний балл";
    	}
    //--------------------------------------------------------------------------
    public void createPropuskTable(TableData tableSource) throws Throwable{
            if (rating==null)
                throw new BRSException(BRSException.warn,"Не выбраны параметры");
            MDPropuskRating in=getPropuskRating();
            Vector<DBItem> cols=rating.events;
            MDStudentVector rows=rating.groups.students;
            int n1=rows.size();
            int n2=cols.size();
            tableSource.setTable(n2+2,n1+1);
            tableSource.title="Пропуски "+rating.getName();
            for (int i=0;i<n1;i++){
                MDStudent vv=rows.get(i);
            	tableSource.rows[i]=vv.getName();
                int cc=vv.brigade;
                if (cc!=0){
                    String ss=""+cc;
                    if (rating.getSecond())
                        ss+="-"+(vv.second?2:1);
                    tableSource.data[i][0]=ss;
                    }
                }
            tableSource.rows[n1]="";
            tableSource.cols[0]="Бригада";
            for (int i=0;i<n2;i++)
            	tableSource.cols[i+1]=((DBEvent)cols.get(i)).getName();
            tableSource.cols[n2+1]="Всего";
            tableSource.rows[n1]="Всего";
            tableSource.lastrow=false;
            for(int i=0;i<n1;i++){
                for(int j=0;j<n2;j++){
                    tableSource.select[i][j+1]=in.sel[i*n2+j].val;
                    if (in.val[i*n2+j].val)
                        tableSource.data[i][j+1]="+";
                    }
                }
            for (int i=0;i<n1;i++) 
            	tableSource.data[i][n2+1]=""+in.cnts[i].val;
            for (int i=0;i<n2;i++) {
                tableSource.data[n1][i+1]=""+in.cntc[i].val;
                }
    	}
    //----------------------------------------------------------------------
    public boolean []createPropuskMarks() throws Throwable{
        boolean bb[]=new boolean[rating.groups.students.size()];
        for(int i=0;i<bb.length;i++) bb[i]=false;
        MDEvent xx=getEvent(false);
    	for(int i=0;i<xx.propusk.size();i++){
            int idx=rating.groups.students.getIdxById(xx.propusk.get(i).getId());
            bb[idx]=true;
            }
  	return bb;
        }
    //--------------------- Срок сдачи -------------------------------------
    public int getMaxWeek(){
        return getMaxWeek(cellItem,studentItem);
    	}
    public int getMaxWeek(MDCell itemC, MDStudent itemS){
    	if (itemC==null || itemS==null) return -1;
    	byte xx[]=itemC.getCellParams();
    	if (xx[DBCell.idxCData]==0) return -1;
    	if (xx[DBCell.idxSecond]==0 || !rating.getSecond())
    		return itemC.week;
    	if (!itemS.second) return itemC.week;
    		return itemC.week2;
        }
    //----------  Для полиморфного вызова --------------------------------------
    public double calcPropusk()throws Throwable{ return 0; }
    public double calcStudentRating(DBItem xx[]) throws Throwable{
        double sum=0;
        if (rating==null || studentItem==null)  return 0;
        MDCellVector cell=rating.course.cells;
        for(int i=0;i<cell.size();i++) 
            cell.get(i).renew=false;
        for (int i=xx.length-1;i>=0;i--){
            DBNote dd=(DBNote)xx[i];
            int ii=cell.getIdxById(dd.getIdCell());
            if (ii==-1) continue;
            MDCell vv=cell.get(ii);
            if (vv.renew) continue;
            vv.renew=true;
            if(dd.getRemoved()) continue;
            calcBall(dd,vv);
            sum+=dd.getBall();
            }
        sum-=rating.params.getPropuskBall()*calcPropusk();	// Полиморфный вызов.............
        return sum;
        }
    //--------------------------------------------------------------------------
    public int calcPropusk(DBItem xx[], MDStudent st)throws Throwable{
    	int cnt=0;
        for(int i=0;i<rating.events.size();i++) rating.events.get(i).renew=false;        
        for (int i=xx.length-1;i>=0;i--){
            DBPropusk dd=(DBPropusk)xx[i];
            if (dd.getIdStudent()!=st.getId()) continue;
            int ii=-1;
            for(int kk=0;kk<rating.events.size();kk++)
                if (rating.events.get(kk).getId()==dd.getIdEvent())
                    {ii=kk; break; }
            if (ii==-1 || rating.events.get(ii).renew) continue;
            rating.events.get(ii).renew=true;
            if(dd.getRemoved()) continue;
            cnt++;
            }
    	return cnt;
        }
    //------------------ формат полного рейтинга для вывода --------------------
    public MDTotalRating getTotalRating() throws Throwable {
        MDTotalRating out=new MDTotalRating();
        out.studentList=rating.groups.students;
        out.cellList=rating.course.cells;
        int n2=rating.course.cells.size();
        int n1=rating.groups.students.size();
        int nn=n1*n2;
        out.ball=new XMLDouble[nn];
        for(int i=0;i<nn;i++) out.ball[i]=new XMLDouble(-1);
        out.sel=new XMLBoolean[nn];
        for(int i=0;i<nn;i++) out.sel[i]=new XMLBoolean(false);
        out.sums=new XMLDouble[n1];
        for(int i=0;i<n1;i++) out.sums[i]=new XMLDouble(0);
        out.sumc=new XMLDouble[n2];
        for(int i=0;i<n2;i++) out.sumc[i]=new XMLDouble(0);
        int cntc[]=new int[n2];
        for(int i=0;i<n2;i++) cntc[i]=0;
        for(int i=rating.notes.size()-1;i>=0;i--){
            DBNote note=(DBNote)rating.notes.get(i);
            int ii=out.studentList.getIdxById(note.getIdStudent());
            int jj=out.cellList.getIdxById(note.getIdCell());
            if (ii==-1 || jj==-1) continue;
            if (out.ball[ii*n2+jj].val!=-1){
                out.sel[ii*n2+jj].val=true;
                continue;
                }
            if (note.getRemoved()) {
                out.ball[ii*n2+jj].val=0;
                continue;
                }
            calcBall(note,out.cellList.get(jj),out.studentList.get(ii));
            double ball=note.getBall();
            out.ball[ii*n2+jj].val=ball;
            out.sums[ii].val+=ball;
            out.sumc[jj].val+=ball;
            cntc[jj]++;
            }
        for (int i=0;i<n1;i++)
            out.sums[i].val-=rating.params.getPropuskBall()*calcPropusk(rating.propusk.toArray(),out.studentList.get(i));
        for(int i=0;i<n2;i++) 
            if (cntc[i]!=0) out.sumc[i].val/=cntc[i];
        return out;
        }
    //----------------------------------------------------------------------------
    public int compressRating() throws Throwable {
        int cnt=0;
        if (rating==null || !editEnabled) return 0;
        this.loadFullRating(rating.getId());
        int n1=rating.groups.students.size();
        int n2=rating.course.cells.size();
        int nn=n1*n2;
        boolean sel[]=new boolean[nn];
        for(int i=0;i<nn;i++) sel[i]=false;
        for(int i=rating.notes.size()-1;i>=0;i--){
            DBNote note=(DBNote)rating.notes.get(i);
            int ii=rating.groups.students.getIdxById(note.getIdStudent());
            int jj=rating.course.cells.getIdxById(note.getIdCell());
            if (ii==-1 || jj==-1) continue;
            if (sel[ii*n2+jj]==false)
                sel[ii*n2+jj]=true;
            else{
                delete(DBNote.class,note.getId());
                cnt++;
                }
            }
        n1=rating.groups.students.size();
        n2=rating.events.size();
        nn=n1*n2;
        sel=new boolean[nn];
        for(int i=0;i<nn;i++) sel[i]=false;
        for(int i=rating.propusk.size()-1;i>=0;i--){
            DBPropusk note=(DBPropusk)rating.propusk.get(i);
            int ii=rating.groups.students.getIdxById(note.getIdStudent());
            int jj=rating.events.getIdxById(note.getIdEvent());
            if (ii==-1 || jj==-1) continue;
            if (sel[ii*n2+jj]==false)
                sel[ii*n2+jj]=true;
            else{
                delete(DBPropusk.class,note.getId());
                cnt++;
                }
            }
        return cnt;
        }
    //----------------------------------------------------------------------------
    public String getFirst(String ss){
        int k=ss.indexOf(" ");
        if (k==-1) return ss;
        return ss.substring(0,k);
        }
    public int getAndRemoveRatingFiles(String path) throws Throwable {
        loadFullRating(rating.getId());
        return getAndRemoveRatingFiles(path, true)+getAndRemoveRatingFiles(path, false);
        }
    private int getAndRemoveRatingFiles(String path, boolean doc) throws Throwable {
        int cnt=0;
        if (rating==null || !editEnabled) return 0;
        path=path+"/"+rating.getName();
        File dd=new File(path);
        if (!dd.exists()) dd.mkdir();
        Vector<DBItem> xx=doc ? rating.docs : rating.archs;
        for(int i=0;i<xx.size();i++){
            DBFile file=(DBFile)xx.get(i);
            if (!file.inArchive()) continue;
            int ii=rating.groups.students.getIdxById(file.getIdStudent());
            int jj=rating.course.cells.getIdxById(file.getIdCell());
            studentItem=rating.groups.students.get(ii);
            cellItem=rating.course.cells.get(jj);
            String fname=file.getFileName();
            if (fname.length()!=0){
                fname=getFirst(rating.groups.students.get(ii).getName())+"_"+rating.course.cells.get(jj).getName()+"_"+OnlyDate.dateFromInt(file.getCDate())+"_"+fname;
                readFile(new File(path+"/"+fname),file.getFileId(),doc, file.getCDate());
                deleteFile(file.getId(),file.getFileId(),doc);
                changeFile("",0,false,doc);
                cnt++;
                }
            }
        return cnt;
        }
    //--------------------------------------------------------------------------
    public MDCellRating getCellRating() throws Throwable {
        if (rating==null || cellItem==null) return null;
        MDCellRating out=new MDCellRating();
        loadCellRating(rating.getId(),cellItem.getId());
        out.studentList=rating.groups.students;
        MDStudentVector stud=rating.groups.students;
        for(int i=0;i<stud.size();i++) stud.get(i).renew=false;
        out.notes=new DBNote[stud.size()];
        for (int i=0;i<stud.size();i++) out.notes[i]=null;
        int kk=0;
        for (int i=0;i<stud.size();i++){
            DBNote note=null;
            MDStudent cl=stud.get(i);
            if (cl.renew) continue;
            for (int j=rating.notes.size()-1;j>=0;j--){
                DBNote vv=(DBNote)rating.notes.get(j);
                if (vv.getIdCell()!=cellItem.getId()) continue;
                if (vv.getIdStudent()==cl.getId())
                    { note=vv; break;}
                }
            cl.renew=true;
            if (note==null || note.getRemoved()){  
                out.notes[i]=new DBNote(cl.getId(),rating.getId(),cellItem.getId());
                out.notes[i].setRemoved(true);
                }
            else{
                calcBall(note,cellItem,cl);                
                out.notes[i]=note;
                out.ball+=note.getBall();
                kk++;
                }
            }
        if (kk!=0) out.ball/=kk;
        return out;
    }
    //--------------------------------------------------------------------------
    public MDStudentRating getStudentRating() throws Throwable{
        if (rating==null || studentItem==null) return null;
        loadStudentRating(rating.getId(),studentItem.getId());
        MDStudentRating out=new MDStudentRating();
        out.cellList=rating.course.cells;
        MDCellVector cell=rating.course.cells;
        for(int i=0;i<cell.size();i++) cell.get(i).renew=false;
        out.notes=new DBNote[cell.size()];
        for (int i=0;i<cell.size();i++) out.notes[i]=null;
        for (int i=0;i<cell.size();i++){
            DBNote note=null;
            MDCell cl=cell.get(i);
            if (cl.renew) continue;
            for (int j=rating.notes.size()-1;j>=0;j--){
                DBNote vv=(DBNote)rating.notes.get(j);
                if (vv.getIdStudent()!=studentItem.getId()) continue;
                if (vv.getIdCell()==cl.getId())
                    { note=vv; break;}
                }
            cl.renew=true;
            if (note==null){  
                out.notes[i]=new DBNote(studentItem.getId(),rating.getId(),cl.getId());
                out.notes[i].setRemoved(true);
                }
            else{
                calcBall(note,cl,studentItem);                
                out.notes[i]=note;
                out.ball+=note.getBall();
                }
            }
        for(int i=0;i<rating.events.size();i++) {
            DBItem vv=rating.events.get(i);
            vv.renew=false;
            vv.mark=false;
            }        
        for (int i=rating.propusk.size()-1;i>=0;i--){
            DBPropusk dd=(DBPropusk)rating.propusk.get(i);
            int jj=-1;
            for(int kk=0;kk<rating.events.size();kk++)
                if (rating.events.get(kk).getId()==dd.getIdEvent()){
                    jj=kk; break;
                    }
            if (jj==-1) continue;
            DBItem vv=rating.events.get(jj);
            if (vv.renew) continue;
            vv.renew=true;
            if(dd.getRemoved()) continue;
            vv.mark=true;
            out.propusk++;
            }
        out.prop=new DBEvent[out.propusk];
        for(int i=0,k=0;i<rating.events.size();i++){
            DBItem vv=rating.events.get(i);
            if (vv.mark)
                out.prop[k++]=(DBEvent)vv;
            }
        out.ball-=rating.params.getPropuskBall()*out.propusk;        
        return out;
   	}
    //--------------------------------------------------------------------------
    public MDPropuskRating getPropuskRating() throws Throwable {
        MDPropuskRating out=new MDPropuskRating();
        out.studentList=rating.groups.students;
        int n2=rating.events.size();
        int n1=rating.groups.students.size();
        out.eventList=new MDEventVector();
        for(int i=0;i<n2;i++)
            out.eventList.add((DBEvent)rating.events.get(i));
        int nn=n1*n2;
        boolean mark[]=new boolean[nn];
        for(int i=0;i<nn;i++) mark[i]=false;
        out.val=new XMLBoolean[nn];
        for(int i=0;i<nn;i++) out.val[i]=new XMLBoolean(false);
        out.sel=new XMLBoolean[nn];
        for(int i=0;i<nn;i++) out.sel[i]=new XMLBoolean(false);
        out.cnts=new XMLInt[n1];
        for(int i=0;i<n1;i++) out.cnts[i]=new XMLInt(0);
        out.cntc=new XMLInt[n2];
        for(int i=0;i<n2;i++) out.cntc[i]=new XMLInt(0);
        for(int i=rating.propusk.size()-1;i>=0;i--){
            DBPropusk note=(DBPropusk)rating.propusk.get(i);
            int ii=out.studentList.getIdxById(note.getIdStudent());
            int jj=out.eventList.getIdxById(note.getIdEvent());
            if (ii==-1 || jj==-1) continue;
            if (mark[ii*n2+jj]==true){
                out.sel[ii*n2+jj].val=true;
                continue;
                }
            out.val[ii*n2+jj].val=!note.getRemoved();
            mark[ii*n2+jj]=true;
            if (out.val[ii*n2+jj].val){
                out.cnts[ii].val++;
                out.cntc[jj].val++;
                }
            }
        return out;
        }
    //--------------------------------------------------------------------------
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
    public void sortCellList(){
        DBItem xx[] = rating.course.cells.toArray();
        for(int i=0;i<xx.length;i++)
            for(int j=i; j>0;j--){
                DBCell v1=(DBCell)xx[j];
                DBCell v2=(DBCell)xx[j-1];
                if (v1.getOrdNum() > v2.getOrdNum())
                    break;
                DBItem cc = xx[j]; xx[j]=xx[j-1]; xx[j-1]=cc;
                }
        rating.course.cells.clear();
        for(int i=0;i<xx.length;i++)
            rating.course.cells.add(xx[i]);
        }

}
