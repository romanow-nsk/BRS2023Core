
package me.romanow.brs.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Vector;
import me.romanow.brs.database.DBField;
import me.romanow.brs.database.DBItem;
import me.romanow.brs.database.DBStudentCellItem;

/**
 *
 * @author user
 */
public class MDItem {

    public static DBItem getById(DBItem xx[], int id){
    	for(int i=0;i<xx.length;i++)
            if (xx[i].getId()==id) return xx[i];
    	return null;
        }
    public static int getIdxById(DBItem xx[], int id){
    	for(int i=0;i<xx.length;i++)
    		if (xx[i].getId()==id) return i;
    	return -1;
        }

    /* 
    public static DBItem []loadDBItemArray(Class proto,DataInputStream in) throws Throwable{
    	int sz=in.readInt();
        DBItem xx[]=new DBItem[sz];
        Vector<DBField> ff=((DBItem)proto.newInstance()).getFields();
        for(int i=0;i<sz;i++){
            xx[i]=(DBItem)proto.newInstance();
            xx[i].setFields(ff);
            xx[i].loadDBValues(in);
            }
        return xx;
    	}
    public static void saveDBItemArray(DBItem xx[], DataOutputStream out) throws Throwable{
        out.writeInt(xx.length);
        if (xx.length==0) return;
        Vector<DBField> ff=xx[0].getFields();
        for(int i=0;i<xx.length;i++){
            xx[i].setFields(ff);
            xx[i].saveDBValues(out);
            }
    	} 
    public static int getLastStudentCell(DBItem xx[],int sid, int cid){
        for(int i=xx.length-1;i>=0;i--){
            DBStudentCellItem zz=(DBStudentCellItem)xx[i];
            if(zz.getIdCell()==cid && zz.getIdStudent()==sid) return i;
            }
        return -1;
        }
     */ 
  public static String printBall(double dd){
        String ss=""+(int)dd+".";
        dd-=(int)dd;
        ss+=(char)((int)(dd*10)+'0');
        return ss;
        }
    public static String note(int  x ){
    	if (x==0) return "--";
        if (x < 50) return "2";
        if (x < 73) return "3";
        if (x < 87) return "4";
        return "5"; }
    public static String ECTS(int x){
    	if (x==0) return "";
        if (x >= 98) return "A+";
        if (x >= 93) return "A";
        if (x >= 90) return "A-";
        if (x >= 87) return "B+";
        if (x >= 83) return "B";
        if (x >= 80) return "B-";
        if (x >= 77) return "C+";
        if (x >= 73) return "C";
        if (x >= 70) return "C-";
        if (x >= 67) return "D+";
        if (x >= 63) return "D";
        if (x >= 60) return "D-";
        if (x >= 50) return "E";
        if (x >= 25) return"FX";
        return "F";  }
}
