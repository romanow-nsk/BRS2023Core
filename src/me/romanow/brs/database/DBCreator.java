package me.romanow.brs.database;

import me.romanow.brs.model.MDNote;
import me.romanow.brs.interfaces.BRSException;
import me.romanow.brs.interfaces.DBConnect;
import java.sql.SQLException;

public class DBCreator {
    public final static Class DBClasses[]={
        DBParams.class,
        DBTutor.class,
        DBGroups.class,
        DBCourse.class,
        DBCell.class,
        DBNote.class,
        DBVariant.class,
        DBDocFile.class,
        DBArchFile.class,
        DBPermission.class,
        DBRating.class,
        DBStudent.class,
        DBCellRating.class,
        DBStudRating.class,  
        DBEvent.class,
        DBPropusk.class,    
        DBFileData.class,    
        DBIdentification.class    
        };
    public void DBCreate(DBConnect conn, boolean newId) throws Throwable{
        for(int i=0;i<DBClasses.length;i++)
            conn.createTable(DBClasses[i]);
        if (newId)
            conn.insert(new DBIdentification());
    	}
}
