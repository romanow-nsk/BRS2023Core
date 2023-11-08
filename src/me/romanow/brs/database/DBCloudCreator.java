package me.romanow.brs.database;

import java.sql.ResultSet;
import me.romanow.brs.Values;
import me.romanow.brs.connect.DBConnection;
import me.romanow.brs.interfaces.DBRecordCallBack;

public class DBCloudCreator {
    public final static Class DBClasses[]={
        DBBases.class,
        DBTutor.class,
        DBLogFile.class,
        };
    public void DBCreate() throws Throwable{
        DBConnection cloudConnect = new DBConnection();
        DBProfile cloud = new DBProfile();
        cloud.setIP(Values.dbNstuCloudIP);
        cloud.setName("bases");
        cloudConnect.connect(cloud);
        for(int i=0;i<DBClasses.length;i++)
            cloudConnect.createTable(DBClasses[i]);
        DBTutor adm = new DBTutor();
        adm.setName("romanow");
        adm.setPass("schwanensee");
        cloudConnect.insert(adm);
        cloudConnect.close();
    	}
    public void showDatabases() throws Throwable{
        DBConnection cloudConnect = new DBConnection();
        DBProfile cloud = new DBProfile();
        cloud.setIP(Values.dbNstuCloudIP);
        cloud.setName("bases");
        cloudConnect.connect(cloud);
        cloudConnect.selectMany("SHOW DATABASES", new DBRecordCallBack(){
            @Override
            public void procRecord(ResultSet rs) {
                try{
                    System.out.println(rs);
                    } catch(Throwable ee){
                        System.out.println("1111111111");
                        }
                }
            });
        cloudConnect.close();
    	}
    public static void main(String argv[]){
        try {
            new DBCloudCreator().DBCreate();
            } catch (Throwable ee){
                System.out.println(ee);
                }
        }
}
