
package me.romanow.brs.controller;

import me.romanow.brs.model.MDBaseUser;
import me.romanow.brs.model.MDNote;
import me.romanow.brs.model.TableData;

public interface ViewControllerCommands {
    public double getSum();
    public MDNote getNoteItem();
    public void setState();
    public void retryState();
    public void close();
    public void updateAllLocalRatings();
    public void loadRating(int id) throws Throwable;
    public void loadFullRating(int id) throws Throwable;
    public void loadFullRatingSoft(int id) throws Throwable;
    public void setState(int newState);
    public void callBacks();
    public void setParamList(int indicies[]);
    public void setParamList(int par);
    public void changeParamList(int idx);
    public int getState();
    public void testState() throws Throwable;
    public void testState(int tState) throws Throwable;
    public void getNote();
    public void removeNote();
    public boolean insertNote(String ball, String week);
    public String createTable(final TableData tbl,boolean clicked);
    public TableData createRatingTable(Class proto);
    public TableData createPropuskTable(Class proto);
    public TableData createRatingCellTable(Class proto);
    public TableData  createRatingStudentTable(Class proto);
    public String getWriteFileName(boolean doc) throws Throwable;
    public String writeFile(boolean doc) throws Throwable;
    public String writeFile(String fname, boolean doc) throws Throwable;
    public String getReadFileName(boolean doc) throws Throwable;
    public String readFile(boolean doc) throws Throwable;
    public String readFile(boolean doc,MDBaseUser model2) throws Throwable;
    public String readFile(String fname, boolean doc) throws Throwable;
    public void changeWeek(String v1,String v2) throws Throwable;
    public void changeBrigade(String v1,boolean second) throws Throwable;
    public void changeVariant(String v1) throws Throwable;
    public void changeEvents();
    public void createRatingTable(TableData tableSource) throws Throwable;
    public void createPropuskTable(TableData tableSource) throws Throwable;
    public void changeEvent(int studId, boolean prop) throws Throwable; 
    public void insertEvent(String ss, int cdate) throws Throwable;
    public void deleteEvent() throws Throwable;
    public void setBack(ViewControllerListener back);
    public void testLocalRatingChanges();
}
