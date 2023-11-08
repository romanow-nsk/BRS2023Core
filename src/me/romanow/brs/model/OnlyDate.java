package me.romanow.brs.model;

import java.util.Date;

public class OnlyDate extends Date{
	public OnlyDate(){
		super.setHours(0);
		super.setMinutes(0);
		super.setSeconds(0);
		}
        public int dateToInt(){ return day()+month()*35+year()*35*15; }
        public static String dateFromInt(int vv){ 
            int dd=vv%35;
            vv/=35;
            int mm=vv%15;
            vv/=15;
            return ""+dd+"."+twoDigit(mm)+"."+vv;
            }
        public OnlyDate(String ss){ super(); parseString(ss); }
	public OnlyDate(int dd,int mm,int yy){
		super();
		super.setDate(dd);
		super.setMonth(mm-1);
		if (yy<100)
			super.setYear(yy+100);
		else
			super.setYear(yy-2000+100);
		}
	public int year(){
		return super.getYear()-100+2000;
		}
	public int month(){
		return super.getMonth()+1;
		}
	public int day(){
		return super.getDate();
		}
	public void year(int year){
		if (year<100)
			setYear(year+100);
		else
			setYear(year+100-2000);
		}
	public void month(int mnt){
		setMonth(mnt-1);
		}
	public void day(int dd){
		setDate(dd);
		}	
	private static String twoDigit(int vv){
	      return ""+(char)(vv/10+'0')+(char)(vv%10+'0');
	      }
	public String toString(){ 
	      return ""+day()+"."+twoDigit(month())+"."+year();
      	}	
	public void parseString(String ss){
	      int k=ss.indexOf(".");
	      if (k==-1) return;
	      day(parseInt(ss));
	      ss=ss.substring(k+1);
	      k=ss.indexOf(".");
	      if (k==-1) return;
	      month(parseInt(ss));
	      ss=ss.substring(k+1);
	      year(parseInt(ss));
          }
	  public int parseInt(String ss){
	      char cc[]=ss.toCharArray();
	      int vv=0,i=0;
	      for (i=0;i<cc.length && !(cc[i]>='0' && cc[i]<='9'); i++);
	      for (;i<cc.length && (cc[i]>='0' && cc[i]<='9'); i++)
	          vv=vv*10+cc[i]-'0';
	      return vv;
	      }
	  public void decYear(){ setYear(getYear()-1);}
	  public void incYear(){ setYear(getYear()+1);}
	  public void incMonth(){
		  int month=getMonth();
	      month++;
	      if (month==13) { month=1; incYear(); }
	      setMonth(month);
	      }
	  public void decMonth(){
		  int month=getMonth();
	      month--;
	      if (month==0) { month=12; decYear(); }
	      setMonth(month);
	      }
	  public int dayOfWeek(){
	      int nn=getDay();
	      if (nn==0) nn=6;
	      else nn--;
	      return nn;
	      }
	  public long differ(OnlyDate two){
		return (getTime()-two.getTime())/1000/(60*60*24);  
	  	}
}
