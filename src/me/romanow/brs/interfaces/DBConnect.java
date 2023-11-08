package me.romanow.brs.interfaces;

import java.sql.SQLException;
import me.romanow.brs.database.DBEntry;
import me.romanow.brs.database.DBItem;
import me.romanow.brs.database.DBProfile;

public interface DBConnect {
    public boolean isConnected();
	public void connect(DBProfile pars)throws Throwable;
	public void connect(String fname)throws Throwable;
    public void reconnect()throws Throwable;
	public void close() throws Throwable;
	public boolean isRemote();
    //----------------------------------------------------------------------
    public void execSQL(String sql) throws Throwable;
    public void selectMany(String sql, DBRecordCallBack back)throws Throwable;
	public void dropTable(Class proto) throws Throwable;
	public void createTable(Class proto) throws Throwable;
	public int getMaxId(Class proto) throws Throwable;
    public DBItem []getList(Class proto) throws Throwable;
    public DBItem []getList(Class proto, DBItem link) throws Throwable;
    public DBItem []getList(Class proto, DBItem link,DBItem link2) throws Throwable;
    public DBItem []getList(Class proto, DBItem link,DBItem link2,DBItem link3) throws Throwable;
    public int []getListId(Class proto, DBItem link) throws Throwable;
	public DBItem []getByName(Class proto, String name) throws Throwable;
	public DBItem []getByIdGreater(Class proto, int val) throws Throwable;	
	public DBItem []getByIdLetter(Class proto, int val) throws Throwable;	
	public DBItem getFirst(Class proto) throws Throwable;
	public DBItem getById(Class proto,int id) throws Throwable;
    public void insert(DBItem note) throws Throwable;
    public void insert(DBItem note,boolean newid) throws Throwable;
    public void update(DBItem note) throws Throwable;
    public void delete(Class table, int id) throws Throwable;
    public void deleteAll(Class proto) throws Throwable;
    public void deleteLinked(Class proto, DBItem note) throws Throwable;
    public void deleteLinked(Class proto, DBItem note,DBItem note2) throws Throwable;
    public void deleteLinked(Class proto, DBItem note,DBItem note2,DBItem note3) throws Throwable;
	public DBItem[] getCondition(Class proto, String cond) throws Throwable;	
	//----------------------------------------------------------------------
}
