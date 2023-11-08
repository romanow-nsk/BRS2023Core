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
public class DBUser extends DBNamedItem{
    private String pass="";
    private boolean admin=false;
    public DBUser(String name,String pass, boolean admin){
        super(0,name);
        this.pass = pass;
        this.admin = admin;
        }
    public DBUser(){
        this("","",false);
        }
    public String getPass() {
        return pass;
        }
    public void setPass(String pass) {
        this.pass = pass;
        }
    public boolean isAdmin() {
        return admin;
        }
    public void setAdmin(boolean admin) {
        this.admin = admin;
        }
    public boolean isCiu() {
        return pass.length()==0;
        }    
}
