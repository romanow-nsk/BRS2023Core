/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.database;

import java.io.*;
import me.romanow.brs.Values;

public class DBEntry  extends DBNamedItem{
    public final static int jdbc=0;
    public final static int http=1;
    public final static int sqlite=2;
    public final static int local=3;
    public final static String sqliteExt=".sqlite";
    public final static String localExt=".rating";
    private String user="";
    private String pass="";
    private String dbName="";
    private String ip=Values.dbNstuCloudIP;
    private int port=Values.dbDefaultPort;
    private int type=jdbc;

    //-------------------- Нужно DBMAnager в правах
    public String getDbName() {return dbName;}
    public void setDbName(String dbName) { this.dbName = dbName;}
    private boolean selected=false;
    public String getUser(){ return user; }
    public String getPass(){ return pass; }
    public String getIP(){ return ip; }
    public int getPort(){ return port; }
    public int getType(){ return type; }
    public boolean getSelected(){ return selected; }
    public void setUser(String ss){ user=ss; }
    public void setPass(String ss){ pass=ss; }
    public void setIP(String ss){ ip=ss; }
    public void setPort(int ss){ port=ss; }
    public void setType(int bb){ type=bb; }
    public void setSelected(boolean bb){ selected=bb; }
    public DBEntry(){ super("БРС"); dbName="brs"; }
    public String toString(){
        String ss="";
        switch(type){
            case jdbc:  ss="mysql"; break;
            case http:  ss="web"; break;
            case sqlite:  ss="sqlite"; break;
            case local:  ss="files"; break;
            }
        return getName()+"("+getIP()+") "+ss;
        }
    public String twoWord(){ return toString(); }
    public void save(DataOutputStream out) throws Exception{
    	out.writeUTF(getName());
    	out.writeUTF(getDbName());
    	out.writeUTF(ip);
    	out.writeInt(port);
        out.writeBoolean(selected);
        out.writeInt(type);
    	}
    public void load(DataInputStream out) throws Exception{
        setName(out.readUTF());
        setDbName(out.readUTF());
        ip=out.readUTF();
        port=out.readInt();
        selected=out.readBoolean();
        type=out.readInt();
    	}
    }
