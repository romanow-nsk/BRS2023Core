/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.connect;

import me.romanow.brs.interfaces.DBServerType;
import java.net.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import me.romanow.brs.interfaces.BRSException;
import me.romanow.brs.database.DBEntry;
import me.romanow.brs.database.DBProfile;
import me.romanow.brs.interfaces.DBRecordCallBack;

/**
 *
 * @author user
 */
public class DBSQLiteJDBC implements DBServerType{

    public int newRecord(String tname) throws Throwable {
    	testConnect();
    	String ss[]=null;
        int id=1;
        try {
            dbConn.setAutoCommit (false);   
            ss=selectOne("SELECT MAX(id) FROM "+tname+";");
            if (ss!=null) id=Integer.parseInt(ss[0])+1;
            String sql="INSERT INTO "+tname+" (id)  VALUES ("+id+");";
            execSQL(sql);
            dbConn.commit();             
            dbConn.setAutoCommit(true);    
            } catch (Exception ee){
                dbConn.rollback ();    
                dbConn.setAutoCommit(true);
                throw new SQLException(ee.getMessage());
                }
            return id;
        }
    //---------------------------------------------------------------------------
    private int loginTimeOut=15;                // Тайм-аут логина (библиотечный)
    private int queryTimeOut=15;                // Тайм-аут логина (библиотечный)
    private int state=0;			// 0 - отключено, 1-соединение, 2-тайм-аут паузы 3- спит
    private Connection dbConn=null;          
    private Statement stm;
    private Properties  setProp(DBProfile pars){
        Properties properties=new Properties();
        if (pars!=null){
            properties.setProperty("user",pars.getUser());
            properties.setProperty("password",pars.getPass());
            }
        properties.setProperty("useUnicode","true");
        properties.setProperty("characterEncoding","UTF-8");
        properties.setProperty("encrypt","false");
        properties.setProperty("trustServerCertificate","true");
        properties.setProperty("integratedSecurity","true");
        properties.setProperty("loginTimeout",""+loginTimeOut);
        properties.setProperty("elideSetAutoCommits","false");
        return properties;
    	}

    public DBSQLiteJDBC(){ dbConn=null; state=0;}
    public synchronized boolean isConnected(){ 
        return dbConn!=null; 
        }
    @Override    
    public void connect(DBProfile entry)throws Throwable{
        connect(entry.getIP()+entry.getDbName()+".sqlite");
    	}
    @Override
    public void connect(String file) throws Throwable {
        dbConn=null;
        state=0;
        try {
        	Class.forName("org.sqlite.JDBC");
        	} catch(ClassNotFoundException ee){ throw new SQLException("Нет драйвера БД"); }
      	dbConn = DriverManager.getConnection("jdbc:sqlite:"+file,setProp(null));
       	if (dbConn == null) throw new SQLException("Нет соединения с БД");
       	stm = dbConn.createStatement();
        stm.setQueryTimeout(queryTimeOut); 
       	}
    
    public synchronized void testConnect() throws SQLException{
    	if (dbConn==null) throw new SQLException("Нет соединения с БД");
        }
    
    public synchronized void open() throws SQLException{
    	testConnect();
        }
    
    public synchronized void close() throws SQLException{
        if (dbConn==null) return;
        dbConn.close();
    	dbConn=null;
        System.out.println("Отсоединился");
        }
    
    public synchronized String  []selectOne(String sql)throws Throwable{
    	testConnect();
        Vector<String> data = new Vector();
        ResultSet rs = null;
        ResultSetMetaData rs2;
        try {
            rs = stm.executeQuery(sql);
            if (!rs.next()) { rs.close(); return null; }
            rs2=rs.getMetaData();
            int n=rs2.getColumnCount();
            for (int i=1;i<=n;i++){
                String ss=rs.getString(i);
                if (ss==null) { rs.close(); return null; }
                data.add(ss);
                }
            rs.close();
        } catch (SQLException e){ 
            if (rs != null) rs.close(); 
            throw new SQLException(e.toString());
            }
        int n=data.size();
        String xx[]=new String[n];
        for (int i=0;i<n;i++) xx[i]=data.get(i);
        return xx;
        }
//------------------------------------------------------------------------------
        public synchronized void selectOne(String sql, DBRecordCallBack back) throws SQLException{
    	testConnect();
        ResultSet rs = null;
        try {
            rs = stm.executeQuery(sql);
            if (!rs.next()) { rs.close(); return; }
            back.procRecord(rs);
            rs.close();
        } catch (SQLException e){ 
            if (rs != null) rs.close(); 
            throw new SQLException(e.toString());
            }
        }
    
    public synchronized void selectMany(String sql, DBRecordCallBack back)throws SQLException{
    	testConnect();
        ResultSet rs = null;
        try {
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                back.procRecord(rs);
                }
            rs.close();
            } 
        catch (SQLException e){ 
            if (rs != null) rs.close(); 
            throw new SQLException(e.toString());
            }
        }

    @Override
    public void reconnect() throws Throwable {
        throw new BRSException(BRSException.nofunc);
        }
    
    public synchronized void execSQL(String sql) throws SQLException{
    	testConnect();
        stm.execute(sql);
        }
    public synchronized int insert(String sql) throws SQLException{
        stm.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
        ResultSet rs = stm.getGeneratedKeys();
        if (rs.next())
            return rs.getInt(1);
        else
            throw new SQLException("Не получен id записи");
        }

}
