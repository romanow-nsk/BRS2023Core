package me.romanow.brs.database;

import java.sql.SQLException;
import java.util.Vector;
import me.romanow.brs.database.DBCell;
import me.romanow.brs.model.MDNote;
import me.romanow.brs.database.DBStudentCellItem;
import me.romanow.brs.model.OnlyDate;
import me.romanow.brs.model.OnlyDate;

public class DBNote extends DBStudentCellItem{
    private double ball=0;
    private int week=0;
    private int params=0;
    private boolean removed=false;
    private int cDate=new OnlyDate().dateToInt();
    //--------------------------------------------------------------------------
    public void pack(boolean b[]){
    	params=0;
    	int msk=1;
    	for(int i=0;i<DBCell.QualityTypes.length;i++,msk<<=1)
    		if (b[i]) params|=msk;
    	}
    public boolean []unpack(){
		int vv=params,msk=1;;
		boolean b[]=new boolean[DBCell.QualityTypes.length];
		for(int i=0;i<DBCell.QualityTypes.length;i++,vv>>=1){
			b[i]=((vv & 1)!=0);
			}
		return b;
		}
    public DBNote(){ }
    public DBNote(int sid, int rid, int cid, double bl, int wk){ 
        super(rid,sid,cid); 
        ball=bl;
        week=wk;
        params=0;
        }
    public DBNote(int sid, int rid, int cid){ 
        super(rid,sid,cid); 
        ball=0;
        week=0;
        params=0;
        }
    public DBNote(MDNote src){ 
        super(src.getIdRating(),src.getIdStudent(),src.getIdCell()); 
        ball=src.getBall();
        week=src.getWeek();
        params=src.getParams();
        removed=src.getRemoved();
        }
    public int getCDate(){ return cDate; }
    public void setCDate(int id0){ cDate=id0; }
    public void changeDate(){ cDate=new OnlyDate().dateToInt(); }
    public double getBall(){ return ball; }
    public void setBall(double bl) { ball=bl; }
    public int getWeek(){ return week; }
    public void setWeek(int w0){ week=w0; }
    public int getParams(){ return params; }
    public void setParams(int id0){ params=id0; }
    public boolean getRemoved(){ return removed; }
    public void setRemoved(boolean bb){ removed=bb; }
}
