/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.connect;

import me.romanow.brs.interfaces.DBServerType;
import java.util.*;
import java.sql.*;
import me.romanow.brs.Values;
import me.romanow.brs.interfaces.BRSException;
import me.romanow.brs.database.DBEntry;
import me.romanow.brs.database.DBProfile;
import me.romanow.brs.interfaces.DBRecordCallBack;

/**
 *
 * @author user
 */
public class DBServerJDBC implements DBServerType{
    @Override
    public void connect(String file) throws Throwable {
        throw new BRSException(BRSException.nofunc);
    }
    public int newRecord(String tname) throws SQLException {
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

    class AliveThread extends Thread{
    	AliveThread(){ start(); }
        public void run(){
        	while(true){
        		try { sleep(connectLiveTime*1000); } catch (Exception ex){}
        		synchronized(DBServerJDBC.this){
        			//--------- Поток другой или прерван или соединение закрыто
    				if(state==0 || this!=aliveThread || this.isInterrupted()) {
                                    state=0; return;
                                    }
    				if (state==1) {
        				System.out.println("Отсчет тайм-аута");
    					state=2;
    					}
    				else
    				if (state==2){
    					try { dbConn.close();} catch (Exception ex) {}
        				dbConn=null;
        				System.out.println("Заснул");
        				state=3;
        				}
                	}
        		}
        	}
        }
    //---------------------------------------------------------------------------
    private int loginTimeOut=30;        // Тайм-аут логина (библиотечный)
    private int queryTimeOut=30;        // Тайм-аут логина (библиотечный)
    private int connectLiveTime=240;	// Время "жизни" соединения
    private int state=0;                // 0 - отключено, 1-соединение, 2-тайм-аут паузы 3- спит
    private Thread aliveThread=null;    // Поток keepAlive
    private String dbName="";
    private DBProfile pars=null;
    public String dbName(){ return dbName; }
    private Connection dbConn=null;          
    private Statement stm=null;
    private Properties  setProp(DBProfile pars){
        Properties properties=new Properties();
        properties.setProperty("user",Values.dbUser);
        properties.setProperty("password",Values.dbPass);
        properties.setProperty("useUnicode","true");
        properties.setProperty("characterEncoding","UTF-8");
        properties.setProperty("encrypt","false");
        properties.setProperty("trustServerCertificate","true");
        properties.setProperty("integratedSecurity","true");
        properties.setProperty("loginTimeout",""+loginTimeOut);
        properties.setProperty("elideSetAutoCommits","false");
        if (pars.isProxyOn()){
            properties.put("proxy_host", pars.getProxyIP());
            properties.put("proxy_port", ""+pars.getProxyPort());
            properties.put("proxy_user", "[proxy user]");
            properties.put("proxy_password", "[proxy password]");
            }
        return properties;
    	}

    public DBServerJDBC(){ dbConn=null; state=0;}
    public synchronized boolean isConnected(){ 
        boolean bb =  (state!=0); 
        return bb;
        }
    public void reconnect()throws Throwable{
        if (pars==null) throw new BRSException(BRSException.dbase,"Повторное соединение без регистрации");
        close();
        connect();
        }

    public void connect(DBProfile entry)throws SQLException{
    	pars=entry;
    	if (pars==null) pars=new DBProfile();
        aliveThread=new AliveThread();
    	connect();
        System.out.println("Присоединился");
    	}
    public void connect()throws SQLException{
        dbConn=null;
        state=0;
        try {
        	Class.forName("com.mysql.jdbc.Driver");
        	} catch(ClassNotFoundException ee){ throw new SQLException("Нет драйвера БД"); }
      	String connectionUrl = "jdbc:mysql://"+pars.getIP()+":"+pars.getPort()+"/"+pars.getDbName();
       	DriverManager.setLoginTimeout(loginTimeOut);
        System.out.println(connectionUrl);
       	dbConn = DriverManager.getConnection(connectionUrl,setProp(pars));
       	if (dbConn == null) throw new SQLException("Нет соединения с БД");
        stm=dbConn.createStatement();
        stm.setQueryTimeout(queryTimeOut);
        state=1;
       	}
    //---- Вызывается только из синхронизированного кода ---------------------------
    public void testConnect() throws SQLException {
    	switch (state){
    case 0:
            throw new SQLException("Нет соединения с БД");
    case 2:
            System.out.println("Новый тайм-аут "+connectLiveTime+" сек.");
            state=1;
    	break;
    case 3:
            connect();
            System.out.println("Проснулся");
    	break;
      	}}    
    
    public synchronized void open() throws SQLException{
    	testConnect();
        }
    
    public synchronized void close() throws SQLException{
        //----------------- ЗАСАДА С СОСТОЯНИЯМИ В Android----------------------
    	if (state==1 || state==2){
                stm.close();
    		dbConn.close();
    		dbConn=null;
    		}
    	if (state!=0 && aliveThread!=null){
       		aliveThread.interrupt();
       		aliveThread=null;
    		}
        System.out.println("Отсоединился");
    	state=0;
        }
        
    public synchronized void execSQL(String sql) throws SQLException{
    	testConnect();
        stm.execute(sql);
        }
    public synchronized int insert(String sql) throws SQLException{
    	testConnect();
        stm.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
        ResultSet rs = stm.getGeneratedKeys();
        if (rs.next())
            return rs.getInt(1);
        else
            throw new SQLException("Не получен id записи");
        }
    public synchronized String  []selectOne(String sql) throws SQLException{
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
}
