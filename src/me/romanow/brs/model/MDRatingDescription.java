/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import me.romanow.brs.database.DBEntry;
import me.romanow.brs.database.DBItem;
import me.romanow.brs.database.DBProfile;
import me.romanow.brs.database.DBRating;

/**
 *
 * @author user
 */
public class MDRatingDescription extends DBItem{
    public DBRating rating;
    public DBProfile entry=null;
    public int changesCount=0;			
    public MDRatingDescription(){
        }
    public MDRatingDescription(DBRating rt,DBProfile ent){
        entry=ent;
        rating=rt;
        }
    public void save(DataOutputStream os) throws Throwable{
        rating.saveDBValues(os);
        entry.saveDBValues(os);
        os.writeInt(changesCount);
        }
    public void load(DataInputStream is) throws Throwable{
        rating =new DBRating();
        rating.loadDBValues(is);
        entry=new DBProfile();
        entry.loadDBValues(is);
        changesCount=is.readInt();
        } 
}
