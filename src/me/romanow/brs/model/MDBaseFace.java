package me.romanow.brs.model;

import java.io.File;
import me.romanow.brs.database.DBIdentification;
import me.romanow.brs.database.DBItem;
import me.romanow.brs.database.DBStudent;
import me.romanow.brs.database.DBTutor;


public interface MDBaseFace {
    public DBIdentification getDBIdentification() throws Throwable;
    public void connect() throws Throwable;             // подключение к источнику данных
    public void reconnect() throws Throwable;           // переподключение к источнику данных
    public void close() throws Throwable;               // закрыть источник данных
    public boolean isConnected() throws Throwable;      // проверка подключени к источнику
    public void loadRating(int id) throws Throwable;    // загрузка рейтинга в бизнес-модель
    public void loadPermissionList() throws Throwable;  // загрузка списка разрешений 
    public void loadStudentList() throws Throwable;     // загрузка списка студентов тек. рейтинга
    public void loadCellList() throws Throwable;        // загрузка списка дисциплин тек. рейтинга
    public void loadEventList() throws Throwable;       // загрузка списка контроля посещаемости
    public void loadFullRating(int id) throws Throwable;// загрузка рейтинга с оценками и пропусками
    public void loadStudentRating(int id,int sid) throws Throwable;
    public void loadCellRating(int id,int cid) throws Throwable;
    public void testEdit() throws Throwable;            // загрузка оценок по студенту и ед.контроля
    public MDNote getNote() throws Throwable;           // получить оценки тек. студента и ед. контроля
    public DBItem []getNoteHistory() throws Throwable;  // получить историю изменения оценки
    public void getRatingList(int id, int mode) throws Throwable;
    public MDEvent getEvent(boolean full) throws Throwable;
    public void deleteEvent() throws Throwable;
    public int insertEvent(String name,int cdate) throws Throwable;
    public void changeEvent(int studId,boolean prop) throws Throwable;
    public String  testUser(DBTutor item, boolean ciuLogin) throws Throwable;
    public double calcStudentRating() throws Throwable;
    public void changeNote(MDNote note) throws Throwable;
    public void changeVariant(String value) throws Throwable;
    public void changeFile(String value, int id, boolean full ,boolean doc) throws Throwable;
    public void changeBrigade(int val,boolean val2) throws Throwable;
    public void changeWeek(int val1, int val2) throws Throwable;
    public int writeFile(File ff,boolean doc, int cdate) throws Throwable;
    public void readFile(File ff,int id,boolean doc, int cdate) throws Throwable;
    public void deleteFile(int id, int fid, boolean doc) throws Throwable;
    public boolean testBase(String name) throws Throwable;
    public void flush() throws Throwable; 
    public int getFileCount() throws Throwable; 
    public double calcPropusk()throws Throwable;
    //--------------------------------------------------------------------------
    // public int insert(DBItem item) throws Throwable;
    public void delete(Class table, int id) throws Throwable;
    //--------------------------------------------------------------------------
    public DBItem []getTutorList() throws Throwable;
    public DBItem []getGroupList() throws Throwable;
    public DBItem []getStudentList(int groupId) throws Throwable;
    public String  testStudent(DBStudent item, boolean ciuLogin) throws Throwable;
    public boolean isLocalFile();
}
