/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.romanow.brs.database;

/**
 *
 * @author romanow
 */
public class DBLogFile extends DBItem{
    private String cdate="";
    private String ctime="";
    private String application="";
    private String dBase="";
    private String login="";
    private int type=0;
    private String data="";
    public String getCDate() {
        return cdate;
        }
    public void setCDate(String cdate) {
        this.cdate = cdate;
        }
    public String getCTime() {
        return ctime;
        }
    public void setCTime(String ctime) {
        this.ctime = ctime;
        }
    public String getdBase() {
        return dBase;
        }
    public void setdBase(String dBase) {
        this.dBase = dBase;
        }
    public String getLogin() {
        return login;
        }
    public void setLogin(String login) {
        this.login = login;
        }
    public int getType() {
        return type;
        }
    public void setType(int type) {
        this.type = type;
        }
    public String getData() {
        return data;
        }
    public void setData(String data) {
        this.data = data;
        }
    public String getApplication() {
        return application;
        }
    public void setApplication(String application) {
        this.application = application;
        }
    public String toString(){
        return cdate + " " + ctime + " "+ dBase + " "+ application +" "+login+"\n"+data;
        }
}
