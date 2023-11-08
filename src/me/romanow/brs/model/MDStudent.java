/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import me.romanow.brs.database.DBPropusk;
import me.romanow.brs.database.DBStudRating;
import me.romanow.brs.database.DBStudent;

/**
 *
 * @author user
 */
public class MDStudent extends DBStudent{
    transient public boolean studRatingChanged=false;
    public int brigade=0;
    public int cDate=0;
    public boolean second=false;
    public void save(DataOutputStream os) throws Throwable{
        saveDBValues(os);
        os.writeBoolean(studRatingChanged);
        os.writeInt(brigade);
        os.writeInt(cDate);
        os.writeBoolean(second);
        }
    public void load(DataInputStream is) throws Throwable{
        loadDBValues(is);
        studRatingChanged=is.readBoolean();
        brigade=is.readInt();
        cDate=is.readInt();
        second=is.readBoolean();
        } 
}
