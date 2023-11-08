/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import me.romanow.brs.database.DBCell;
import me.romanow.brs.database.DBCellRating;
import me.romanow.brs.database.DBStudRating;

/**
 *
 * @author user
 */
public class MDCell extends DBCell{
    public int week=0;
    public int week2=0;
    public int cDate=0;
    transient public boolean cellRatingChanged=false;
    public void save(DataOutputStream os) throws Throwable{
        saveDBValues(os);
        os.writeBoolean(cellRatingChanged);
        os.writeInt(week);
        os.writeInt(week2);
        os.writeInt(cDate);
        }
    public void load(DataInputStream is) throws Throwable{
        loadDBValues(is);
        cellRatingChanged=is.readBoolean();
        week=is.readInt();
        week2=is.readInt();
        cDate=is.readInt();
    }
}
