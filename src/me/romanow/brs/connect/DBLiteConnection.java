/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.connect;

import me.romanow.brs.interfaces.BRSException;
import me.romanow.brs.interfaces.DBConnect;
import me.romanow.brs.database.DBEntry;
import me.romanow.brs.database.DBItem;
import me.romanow.brs.database.DBProfile;
import me.romanow.brs.interfaces.DBRecordCallBack;

/**
 *
 * @author romanow
 */
public class DBLiteConnection implements DBConnect{

    @Override
    public boolean isConnected(){
        return false;
    }

    @Override
    public void connect(DBProfile pars) throws Throwable {
        throw new BRSException(BRSException.nofunc);
        }
    @Override
    public void connect(String fname) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }
    @Override
    public void reconnect()throws Throwable{
        throw new BRSException(BRSException.nofunc);
        }
    @Override
    public void close() throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public void dropTable(Class proto) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public void createTable(Class proto) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public int getMaxId(Class proto) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public DBItem[] getList(Class proto) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public DBItem[] getList(Class proto, DBItem link) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public DBItem[] getList(Class proto, DBItem link, DBItem link2) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public DBItem[] getList(Class proto, DBItem link, DBItem link2, DBItem link3) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public DBItem[] getByName(Class proto, String name) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public DBItem[] getByIdGreater(Class proto, int val) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public DBItem[] getByIdLetter(Class proto, int val) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public DBItem getFirst(Class proto) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public DBItem getById(Class proto,int id) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public void insert(DBItem note) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public void insert(DBItem note, boolean newid) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public void update(DBItem note) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public void delete(Class table, int id) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public void deleteAll(Class proto) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public void deleteLinked(Class proto, DBItem note) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public void deleteLinked(Class proto, DBItem note, DBItem note2) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public void deleteLinked(Class proto, DBItem note, DBItem note2, DBItem note3) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public DBItem[] getCondition(Class proto, String cond) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public int[] getListId(Class proto, DBItem link) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public void execSQL(String sql) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }

    @Override
    public void selectMany(String sql, DBRecordCallBack back) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }
 }
