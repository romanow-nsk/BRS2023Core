/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.interfaces;

import me.romanow.brs.database.DBProfile;

/**
 *
 * @author user
 */
public interface DBServerType {
    public boolean isConnected();
    public void connect(DBProfile entry)throws Throwable;
    public void reconnect()throws Throwable;
    public void connect(String file)throws Throwable;
    //---- Вызывается только из синхронизированного кода ---------------------------
    public void testConnect() throws Throwable;    
    public void open() throws Throwable;
    public void close() throws Throwable;
    public void execSQL(String sql) throws Throwable;
    public int insert(String sql) throws Throwable;
    public String  []selectOne(String sql) throws Throwable;
    public void selectOne(String sql,DBRecordCallBack back) throws Throwable;
    public void selectMany(String sql,DBRecordCallBack back)throws Throwable; 
    public int newRecord(String tname)  throws Throwable;
    }
