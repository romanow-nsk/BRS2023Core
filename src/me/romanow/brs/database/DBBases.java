/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.database;

import java.sql.SQLException;
import java.util.Vector;

public class DBBases extends DBNamedItem{
    private String dbName="";
    public DBBases(String nm, String dbnm){ 
        super(nm); 
        dbName=dbnm; 
        }
    public DBBases(){ super(""); }
    public String getDbName() {
        return dbName;
        }
    public void setDbName(String dbName) {
        this.dbName = dbName;
        }    
}
