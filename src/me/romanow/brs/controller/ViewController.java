/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.util.Date;
import me.romanow.brs.Values;
import me.romanow.brs.database.DBCell;
import me.romanow.brs.model.MDNote;
import me.romanow.brs.interfaces.BRSException;
import me.romanow.brs.interfaces.TableCallBack;
import me.romanow.brs.model.MDBaseUser;
import me.romanow.brs.model.MDBaseUserBinFile;
import me.romanow.brs.model.MDItem;
import me.romanow.brs.model.MDTotalRating;
import me.romanow.brs.model.OnlyDate;
import me.romanow.brs.model.TableData;

public class ViewController implements ViewControllerCommands{
    //Состояния контроллера
    // 0 - rating==null
    // 1 - rating выбран
    // 2 - cellItem выбран
    // 3 - studentIten выбран
    // 4 - 2,3 выбраны, noteItem=null
    // 5 - noteItem выбран
    public final static int ratingTableType=0;
    public final static int studentTableType=1;
    public final static int cellTableType=2;
    public final static int propuskTableType=3;
    private int state=0;
    private MDBaseUser model=null;
    private ViewControllerListener back=null;
    private boolean noCash=false;
    //-------------- Параметры оценки ------------------------------------------
    private MDNote noteItem=null;
    private double sumBall=0;
    private byte noteType[];
    private int noteParams=0;
    private long clock=0;                   // Время обновления лок. копии
    private boolean fullRatingValid=false;  // Изменения оценок и вариантов отслкживаются вполном рейтинге
    //--------------------------------------------------------------------------
    public void setNoCash(){ noCash=true; }
    public double getSum(){ return sumBall; }
    public void setBack(ViewControllerListener back) {
        this.back = back;
        }
    public MDNote getNoteItem(){ return noteItem; }
    public void setState(){
        if (model.rating==null) state=0;
        else{
            if (model.cellItem!=null && model.studentItem!=null){
                if (noteItem==null) state=4;
                else state=5;
                }
            else{
                if (model.cellItem!=null) state=2;
                else state=3;
                }
            }
        if (back!=null) back.stateChanged(state);
        if (back!=null) back.setRatingVisible(state!=0);
        }
    public void retryState(){
        if (state>=4) getNote();
        callBacks();
        }
    public void close(){
        try {
            if (model.isLocalFile() && model.rating!=null)
                model.flush();
            if (Values.strongCash && !model.isLocalFile() && model.rating!=null)
                loadFullRating(model.rating.getId());
            } catch(Throwable ee){ 
                if (back!=null) back.fatal(ee);
                }
        }
    //--------------------------------------------------------------------------
    // локальые файлы перезаписываются в локаьном режиме методом flush при смене рейтинга 
    // (загрузке нового) и при закрытии контроллера
    // в режиме с БД - строгий - при смене рейтинга, нестрогий - по времени
    public void testLocalRatingChanges(){
        if (back==null)
            return;
        try {
            MDBaseUserBinFile out=new MDBaseUserBinFile(back.getFileDirectory());
            out.entry = model.entry;
            out.loadFileRatingList(true);
            int kk=0;
            for(int i=0;i<out.fileRatingList.size();i++){
                MDBaseUserBinFile xx = (MDBaseUserBinFile)out.fileRatingList.get(i);
                xx.load(xx.getRatingFileName());
                kk+=xx.testAllChanges();
                }
            if (kk!=0)
                if (back!=null) back.setMessage("Изменения в локальных копиях рейтинга: "+kk,Values.stateGreen);            
            } catch (Throwable ee){
                if (back!=null) back.fatal(ee);
                };
        }
    public void updateAllLocalRatings(){
        if (model.isLocalFile() || back==null)
            return;
        try {
            MDBaseUserBinFile out=new MDBaseUserBinFile(back.getFileDirectory());
            out.entry = model.entry;
            //-------------- Скопировать параметры -----------------------------
            out.loadFileRatingList(true);
            int kk=0;
            for(int i=0;i<out.fileRatingList.size();i++){
                MDBaseUserBinFile xx = (MDBaseUserBinFile)out.fileRatingList.get(i);
                xx.load(xx.getRatingFileName());
                int k2=xx.testAllChanges();
                if (k2!=0){
                    model.loadRating(xx.getId());    // Рейтинг в БД сервера
                    kk += updateLocalRating(xx);
                    }
                }
            if (kk!=0)
                if (back!=null) back.setMessage("Синхронизированы изменения из локальных копий рейтинга ("+kk+")",Values.stateGreen);            
            } catch (Throwable ee){
                if (back!=null) back.fatal(ee);
                };
        }
    private int updateLocalRating(MDBaseUserBinFile out) throws Throwable{
        String s1=out.getKeyString(),s2=model.getDBIdentification().getKeyString();
        if (!s1.equals(s2)){
            if (back!=null) back.setMessage("Не совпадают идентификационные коды файла и БД - локальные данные утеряны",Values.stateRed);
            return 0;
            }
        int kk=out.testAllChanges();
        if (kk==0)
            return 0;
        out.synchDataUpDown(model);
        return kk;
        }
    public void loadRating(int id) throws Throwable{
        if (model.isLocalFile() || noCash)
            model.loadRating(id);
        else{
            boolean b1 = new Date().getTime()-clock > Values.ratingRefreshIntervalMinute * 60 * 1000;
            boolean b2 = model.rating==null || model.rating.getId() !=id;       // Другой рейтинг
            if (b1 || b2 && Values.strongCash)
                loadFullRating(id);
            else{
                model.loadRating(id);
                fullRatingValid = false;
                }
            }
        }
    // Мягкое чтение рейтинга для работы с таблицами
    public void loadFullRatingSoft(int id) throws Throwable{    
        if (fullRatingValid && id == model.rating.getId())
            return;
        loadFullRating(id);
        }
    public void loadFullRating(int id) throws Throwable{
        model.loadFullRating(id);
        if (model.isLocalFile() || back==null || noCash)
            return;
        clock = new Date().getTime();       // Обновление локального кэша
        MDBaseUserBinFile out=new MDBaseUserBinFile(back.getFileDirectory());
        String ss=model.getDBIdentification().getKeyString();
        out.setKeyString(ss);
        out.save(model);
        fullRatingValid = true;        
        }
    //--------------------------------------------------------------------------
    public void setState(int newState){
        int oldState=state;
        state=newState;
        if (state==2 || state==4){
            int cType=model.cellItem.getCType();
            noteType=DBCell.NoteParamValues[cType];  
            }
        if (state==0) model.rating=null;
        if (state<=1){
            model.cellItem=null;
            model.studentItem=null;
            }
        if (state==2 && oldState>2) state=4;
        if (state==3 && (oldState==2 || oldState>3)) state=4;
        if (state!=5) noteItem=null;
        if (state<4) noteParams=0;
        callBacks();
        if (state==4) getNote();            // Попытка прочитать оценку
        }
        //-------------------- Обратные вызовы ---------------------------------
     public void callBacks(){
        if (back!=null) back.stateChanged(state);
        //--------------------------- Вывод параметров рейтинга и видимости-----
        if (back!=null) back.setRatingVisible(state!=0);
        if (back!=null) back.setNoteVisible(state>=4);
        boolean b2=model.studentItem!=null;
        if (back!=null) back.setStudentVisible(b2);
        boolean gr2=false;
        String s4="";
        boolean second=false;
        if (b2){
            gr2=model.rating.getSecond();
            if (model.studentItem.brigade!=0){
                s4=""+model.studentItem.brigade;
                second=model.studentItem.second;
                }
            }
        if (back!=null) back.setBrigadeVisible(b2, s4,b2 && gr2, second);
        boolean weekVisible=false;
        String s1="";
        String s2="";
        b2=model.cellItem!=null;
        if (back!=null) back.setCellVisible(b2);
        if (b2){
            weekVisible=(noteType[DBCell.idxCData]==1);
            if (model.cellItem.week!=0)
                s1=""+model.cellItem.week;
            if (model.cellItem.week2!=0)
                s2=""+model.cellItem.week2;
            }
        if (back!=null) back.setWeekVisible(weekVisible, s1);
        if (back!=null) back.setWeek2Visible(weekVisible && model.rating.getSecond(), s2);
        if (back!=null) back.setArchFileVisible(state==5 && noteItem.getArchFile().length()!=0);
        if (back!=null) back.setDocFileVisible(state==5 && noteItem.getDocFile().length()!=0);
        //--------------- Вывод параметров оценки ------------------------------
        String ss;
        if (state>=4){
            if (back!=null) back.setMaxBall(""+model.cellItem.getBall());
            ss="";
            ss=MDItem.printBall(sumBall)+" ("+MDItem.ECTS((int)sumBall)+")";
            if (back!=null) back.setSumBall(ss);
            ss="";
            int k=0;
            if (model.studentItem.brigade!=0) k=model.studentItem.brigade;
            if (k!=0) ss=""+k;
            if (model.rating.getSecond()) ss+="("+(model.studentItem.second ? "2)":"1)");
            if (back!=null) back.setBrigade(ss);
            ss="";
            int week=0;
            if (noteType[DBCell.idxCData]!=0){
                week=model.cellItem.week;
                if (noteType[DBCell.idxSecond]!=0 && model.studentItem.second && model.rating.getSecond())
                    week=model.cellItem.week2;
                if (week!=0) ss=""+week;            
                }
            if (back!=null) back.setWeek0(ss);
            ss="";
            if (state==5) ss=noteItem.getVariant();
            if (back!=null) back.setVariant(ss);
            ss="";
            boolean bb=noteType[DBCell.idxParams]!=0;
            if (bb){
                if (back!=null) back.setParamsVisible(bb,createParamList(noteParams),noteParams);
                }
            else{
                if (back!=null) back.setParamsVisible(bb,"",0);
                }
            ss="";
            boolean b3 = noteType[DBCell.idxManual]!=0;
            if (state==5 && !noteItem.getRemoved()){
                model.calcBall(noteItem);
                double vv=noteItem.getBall();
                if (b3)
                    ss=""+(int)vv;
                else
                    ss=MDItem.printBall(vv);
                }
            if (back!=null) back.setBall(ss, b3);
            ss="";
            if (state==5 && noteType[DBCell.idxCData]!=0 && !noteItem.getRemoved() && noteItem.getWeek()!=0)
                ss=""+noteItem.getWeek();
            if (back!=null) back.setWeek(ss, noteType[DBCell.idxCData]!=0);
            }
        if (back!=null) back.lastCallBack();
        }
    //--------------------------------------------------------------------------
    private String createParamList(int vv){
        String ss="";
        for(int i=0;i<DBCell.QualityTypes.length;i++){
            if ((vv & 1)!=0) 
                ss+=DBCell.QualityTypes[i]+"\n";
            vv>>=1;
            }
        return ss;
        }

    public void setParamList(int indicies[]){
        int par=0;
        for(int i=0;i<indicies.length;i++)
            par|=(1<<indicies[i]);
        noteParams = par;
        if (back!=null) back.setParamsVisible(true,createParamList(noteParams),noteParams);
        }
    public void setParamList(int par){
        noteParams = par;
        if (back!=null) back.setParamsVisible(true,createParamList(noteParams),noteParams);
        }
    public void changeParamList(int idx){
        noteParams ^=(1<<idx);
        if (back!=null) back.setParamsVisible(true,createParamList(noteParams),noteParams);
        }
    //--------------------------------------------------------------------------
    public int getState(){ return state; }
    public void testState() throws Throwable{ testState(-1); }
    public void testState(int tState) throws Throwable{
        if (state!=0 && model.rating==null) new BRSException(BRSException.bug,"rating==null");
        if (state>=3 && model.studentItem==null) new BRSException(BRSException.bug,"studentItem==null");        
        if ((state>3 || state==2) && model.cellItem==null) new BRSException(BRSException.bug,"cellItem==null");        
        if (state==5 && noteItem==null) new BRSException(BRSException.bug,"noteItem==null");        
        if (tState==-1) return;
        if (tState==5 && state!=5) new BRSException(BRSException.warn,"Отсутствует оценка"); 
        if (tState>0 && state==0) new BRSException(BRSException.warn,"Не выбран рейтинг"); 
        if (tState==4 && state<4) new BRSException(BRSException.warn,"Не выбраны уч.единица или студент"); 
        if (tState==2 && (state<2 || state==3)) new BRSException(BRSException.warn,"Не выбрана уч.единица"); 
        if (tState==3 && state<3) new BRSException(BRSException.warn,"Не выбран студент"); 
        }
    public ViewController(MDBaseUser md, ViewControllerListener lsn){
        back=lsn;
        model=md;
        state=0;
        model.clear();
        clock = new Date().getTime();
        }
    public void getNote(){
        try {
            testState(4);
            noteItem=model.getNote();
            sumBall=model.calcStudentRating();
            if (noteItem!=null) {
                if (!noteItem.getRemoved())
                    noteParams=noteItem.getParams();
                setState(5);
                }
            else callBacks();
            } catch (Throwable ee){ if (back!=null) back.fatal(ee);}
        }
    public void removeNote(){
        try {
            testState(5);
            noteItem.setRemoved(true);
            model.changeNote(noteItem);
            noteItem=model.getNote();
            setState(5);
            sumBall=model.calcStudentRating();
            if (back!=null) back.ratingIsChanged();
            } catch (Throwable ee){ if (back!=null) back.fatal(ee);}
        }
    public boolean insertNote(String ball, String week){
        try {
            testState(4);
            int wk=0,bl=0;
            if (noteType[DBCell.idxManual]!=0) 
            try {
                bl=Integer.parseInt(ball);
                } catch(Throwable e1){ 
                    if (back!=null) back.fatal(new BRSException("Нечисловое значение балла")); 
                    return false;
                    }
            if (noteType[DBCell.idxCData]!=0)
            try {
                wk=Integer.parseInt(week);
                } catch(Throwable e1){ 
                    if (back!=null) back.fatal(new BRSException("Нечисловое значение недели")); 
                    return false;
                    }
                noteItem=new MDNote(
                    model.studentItem.getId(),
                    model.rating.getId(),
                    model.cellItem.getId(),
                    bl,wk);
                if (noteType[DBCell.idxParams]!=0) 
                    noteItem.setParams(noteParams);
                if (noteType[DBCell.idxManual]==0){
                    model.calcBall(noteItem);
                    }
            model.changeNote(noteItem);
            if (fullRatingValid && !model.isLocalFile())
                model.rating.changeNote(noteItem);      // Только локальные изменения
            noteItem=model.getNote();
            sumBall=model.calcStudentRating();
            setState(5);
            if (back!=null) back.ratingIsChanged();
            } catch (Throwable ee){ 
                if (back!=null) back.fatal(ee);
                return false;
                }
        return true;
        }
    public String createTable(final TableData tbl,boolean clicked){
        if (tbl==null || back==null) return "";
        String fname = "";
        String ext=tbl.getExtention();
        File dst=null;
        BufferedWriter out=null;
        try {
            if (ext!=null){
                fname=back.getOutputFileName("Выберите файл отчета", "Рейтинг "+tbl.title+"."+ext);
                tbl.fileName = fname;
                dst=new File(fname);
                }
            if (!clicked) tbl.createTable(dst,null);
            else
            tbl.createTable(dst, new TableCallBack(){
                @Override
                public void rowSelected(int row) {}
                @Override
                public void colSelected(int col) {}
                @Override
                public void cellSelected(int row, int col) {
                    if (back!=null) back.selectTableCell(tbl, row, col);
                    }
                @Override
                public void onClose() {
                    if (back!=null) back.onTableClose(tbl);
                    }
                });
            } 
        catch(Throwable ee){ 
            try { out.close(); } catch(Throwable e2){}; 
            if (back!=null) back.fatal(ee); return ""; }
        return fname;
        } 
    public TableData createRatingTable(Class proto){
        TableData tableSource=null;
        try {
            testState(1);
            tableSource=(TableData)proto.newInstance();
            loadFullRatingSoft(model.rating.getId());
            model.createRatingTable(tableSource);
            tableSource.setTableType(ratingTableType);
            createTable(tableSource,true);
            } 
        catch(Throwable e2){ if (back!=null) back.fatal(e2); return null; }
        return tableSource;
        }
    public TableData createPropuskTable(Class proto){
        TableData tableSource=null;
        try {
            testState(1);
            tableSource=(TableData)proto.newInstance();
            tableSource.setTableType(propuskTableType);
            loadFullRating(model.rating.getId());
            model.createPropuskTable(tableSource);
            createTable(tableSource,true);
            } 
        catch(Throwable e2){ if (back!=null) back.fatal(e2); return null;} 
        return tableSource;
        }
    public TableData createRatingCellTable(Class proto){
        TableData tableSource=null;
        try {
            testState(2);
            tableSource=(TableData)proto.newInstance();
            tableSource.setTableType(cellTableType);
            model.createRatingCellTable(tableSource);
            createTable(tableSource,true);
            } 
        catch(Throwable e2){ if (back!=null) back.fatal(e2); return null;} 
        return tableSource;
        }
    public TableData  createRatingStudentTable(Class proto){
        TableData tableSource=null;
        try {
            testState(3);
            tableSource=(TableData)proto.newInstance();
            tableSource.setTableType(studentTableType);
            model.createRatingStudentTable(tableSource);
            createTable(tableSource,true);
            } 
        catch(Throwable e2){ if (back!=null) back.fatal(e2); return null;} 
        return tableSource;
        }
    public String getWriteFileName(boolean doc) throws Throwable{
        if (back==null) 
            return "";
        testState(4);
        String fname=back.getInputFileName("Выберите файл отчета/архива","*.*");
        if (fname==null) throw new BRSException(BRSException.msg, "Файл не выбран");
        return fname;
        }
    public String writeFile(boolean doc) throws Throwable{
        String fname = getWriteFileName(doc);
        return writeFile(fname,doc);
        }
    public int writeFileDB(String fname, boolean doc) throws Throwable{
        int id=model.createFileDataId();
        model.changeFile(fname,id,true,doc);
        fullRatingValid = false;
        if (back!=null)
            back.ratingIsChanged();
        retryState();
        return id;
        }
    public String writeFile(String fname, boolean doc) throws Throwable{
        int idx=fname.lastIndexOf("/");
        String sname=fname.substring(idx+1);
        File ff=new File(fname);
        int id=model.writeFile(ff,doc,new OnlyDate().dateToInt());
        model.changeFile(sname,id,true,doc);
        fullRatingValid = false;
        if (back!=null)
            back.ratingIsChanged();
        retryState();
        return sname;
        }
    //------------------------------- Некрасиво, но статический контекс --------
    private MDNote _note=null;
    private String ext="";
    private int cdate=0;
    public String getReadFileName(boolean doc) throws Throwable{
        if (back==null) 
            return "";
        testState(5);
        _note=model.getNote();
        ext=doc ? _note.getDocFile() : _note.getArchFile();
        cdate=doc ? _note.getDocDate(): _note.getArchDate();
        if (ext.length()==0) throw new BRSException(BRSException.msg, "Нет файла на сервере");
        String fname=back.getOutputFileName("Выберите файл отчета/архива",ext);
        return fname;
        }
    public String readFile(boolean doc) throws Throwable{
        return readFile(doc,model);
        }
    public String readFile(boolean doc,MDBaseUser model2) throws Throwable{
        String fname = getReadFileName(doc);
        File ff=new File(fname);
        model2.readFile(ff,doc ? _note.getIdDoc() : _note.getIdArch(),doc,cdate);
        return ext;
        }
    public String readFile(String fname, boolean doc) throws Throwable{
        File ff=new File(fname);
        model.readFile(ff,doc ? _note.getIdDoc() : _note.getIdArch(),doc,cdate);
        return ext;
        }
    //--------------------------------------------------------------------------
    public void changeWeek(String v1,String v2) throws Throwable{
        testState(2);
        int w1=0;
        int w2=0;
        try {
            w1=Integer.parseInt(v1);
            if (model.rating.getSecond()) w2=Integer.parseInt(v2);
            } catch(Throwable e1){ throw new BRSException("Нечисловое значение"); }
        model.cellItem.week=w1;
        model.cellItem.week2=w2;
        model.changeWeek(w1,w2);
        if (back!=null) back.ratingIsChanged();
        //model.loadCellList();
        fullRatingValid = false;
        if (back!=null)
            back.ratingIsChanged();
        retryState();
        }
    public void changeBrigade(String v1,boolean second) throws Throwable{
        testState(3);
        int cc=0;
        try {
            cc=Integer.parseInt(v1);
            } catch(Throwable e1){ throw new BRSException("Нечисловое значение"); }
        model.studentItem.brigade=cc;
        model.studentItem.second=second;
        model.changeBrigade(cc,second);
        //model.loadStudentList();
        fullRatingValid = false;
        if (back!=null)
            back.ratingIsChanged();
        retryState();
        }
    public void changeVariant(String v1) throws Throwable{
        testState(3);
        model.changeVariant(v1);
        if (fullRatingValid && !model.isLocalFile())
            model.rating.changeVariant(v1, model.studentItem.getId(), model.cellItem.getId());
        if (back!=null)
            back.ratingIsChanged();
        retryState();
        }
   public void createRatingTable(TableData tableSource) throws Throwable{
        loadFullRatingSoft(model.rating.getId());
        model.createRatingTable(tableSource);
        }
   public void createPropuskTable(TableData tableSource) throws Throwable{
        loadFullRatingSoft(model.rating.getId());
        model.createPropuskTable(tableSource);
        }
    @Override
    public void changeEvent(int studId, boolean prop) throws Throwable {
        model.changeEvent(studId,prop);
        if (fullRatingValid && !model.isLocalFile())
            model.rating.changeEvent(studId,model.eventItem.getId(),prop);
            }
    public void changeEvents(){
        if (back!=null)
            back.ratingIsChanged();
        retryState();
        }
    public void insertEvent(String ss, int cdate) throws Throwable {
        model.insertEvent(ss, cdate);
        if (fullRatingValid && !model.isLocalFile())
            model.rating.insertEvent(ss, cdate);
        if (back!=null)
            back.ratingIsChanged();
        retryState();
        }
    public void deleteEvent() throws Throwable{
        model.deleteEvent();
        fullRatingValid = false;
        if (back!=null)
            back.ratingIsChanged();
        retryState();
        }
}
