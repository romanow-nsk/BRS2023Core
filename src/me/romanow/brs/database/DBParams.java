package me.romanow.brs.database;

public class DBParams  extends DBItem{
	private int maxWeek=5;
	private int weekProc=10;
	private double propuskBall=0.5;
	private int plusProc=10;
	private String semestr="3.9.2012";
	private int idTutor=-1;						// Чья БД!!!!!!!!!!!!!!!!!!
	public int getMaxWeek(){ return maxWeek; }
	public void setMaxWeek(int vv){ maxWeek=vv;; }
	public int getWeekProc(){ return weekProc; }
	public void setWeekProc(int vv){ weekProc=vv;; }
	public double getPropuskBall(){ return propuskBall; }
	public void setPropuskBall(double vv){ propuskBall=vv;; }
	public int getPlusProc(){ return plusProc; }
	public void setPlusProc(int vv){ plusProc=vv;; }
	public String getSemestr(){ return semestr; }
	public void setSemestr(String vv){ semestr=vv;; }
	public int getIdTutor(){ return idTutor; }
	public void setIdTutor(int vv){ idTutor=vv;; }
        public String toString(){ return semestr+" ("+weekProc+"%)"; }
}
