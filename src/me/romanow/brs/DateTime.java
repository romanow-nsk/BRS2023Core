
package me.romanow.brs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

public class DateTime{
    //----  Год в абсолютном выражении =2014
    // sum=-1 - текущие дата и время
    // sum=0  - отсутствует
    // hour = 24 - время отсутствует
    private long sum=-1; 
    transient private int day=0;         //e_Int = 0;    
    transient private int month=0;       //f_Int = 0;     
    transient private int year=0;        //g_Int = 0; 
    transient private int hour=0;       //h_Int = 0;
    transient private int min=0;         //i_Int = 0;
    transient private int sec=0;
    transient private boolean done=false;
    private void test(){
        if (sum==0) 
            return;
        if (sum==-1)
            done = false;
        if (!done)
            fromSum();
        done = true;
        }
    private void fromSum(){
        day=month=year=hour=min=sec=0;
        if (sum==0) 
            return;
        if (sum==-1){
            Date xx= new Date();
            day=xx.getDate();
            month=xx.getMonth()+1;
            year=xx.getYear()-100+2000;
            hour=xx.getHours();
            min=xx.getMinutes();
            sec=xx.getSeconds();
            }
        else{
            long vv = sum;
            sec = (int)(vv % 60);
            vv = vv / 60;
            min = (int)(vv % 60);
            vv = vv / 60;
            hour = (int)(vv % 25);
            vv = vv / 25;
            day = (int)(vv % 32);
            vv = vv / 32;
            month = (int)(vv % 13);
            year = (int)(vv / 13);
            }
        }
    private void toSum(){
        toSum(false);
        }
    private void toSum(boolean immer){
        done = false;
        if (!immer && year == 0){
            sum = 0;
            return;
            }
        sum = sec + 60*(min+60*(hour+25*(day+32*(month+13*(long)year))));
        }
    final transient public static int fsize=6;  // Для  ГЕОЛОКАЦИИ  
    public int day(){
        test();
        return day;
        }
    public int month() {
        test();
        return month;
        }
    public int year() {
        test();
        return year;
        }
    public int hour() {
        test();
        return (hour == 24) ? 0 : hour;
        }
    public int min() {
        test();
        return (hour == 24) ? 0 : min;
        }
    public int sec() {
        test();
        return (hour == 24) ? 0 : sec;
        }
    @Override
    public boolean equals(Object oo){
        if (oo instanceof DateTime) {
            DateTime nn=(DateTime)oo;
            return sum == nn.sum;
            }
        return false;
        }
    public int dayOfWeek(){
        test();
        Date xx=new Date();
        xx.setDate(day);
        xx.setMonth(month-1);
        xx.setYear(year-2000+100);
        int nn=xx.getDay();
        if (nn==0) nn=6;
        else nn--;
        return nn;
        }
    private void clear(){ 
        sum=0;
        done = false;
        }  
    public void parseString(String ss){
        int k=ss.indexOf(".");
        if (k!=-1) {
            day=Integer.parseInt(ss.substring(0,k));
            ss=ss.substring(k+1);
            k=ss.indexOf(".");
            if (k==-1) { clear(); return; }
            month=Integer.parseInt(ss.substring(0,k));
            ss=ss.substring(k+1);
            k=ss.indexOf(" ");
            if (k==-1) { clear(); return; }
            year=Integer.parseInt(ss.substring(0,k));
            if (year<100) year+=2000;
            ss=ss.substring(k+1);
            }
        k=ss.indexOf(":");
        if (k==-1) hour=24;
        else{
            hour=Integer.parseInt(ss.substring(0,k));
            ss=ss.substring(k+1);
            min=Integer.parseInt(ss);
            }
        toSum();
        done = true;
        }
    public boolean isDateValid(){
        return sum!=0; 
        }
    public boolean isTimeValid(){ return hour!=24; }    
    public void min(int d0){ 
        if (sum==-1)
            return;
        test();
        min=d0; 
        toSum();
        }    

    public void sec(int d0) {
        if (sum==0)
            return;
        test();
        sec=d0; 
        toSum();
        }
    public void hour(int d0) { 
        if (sum==0)
            return;
        test();
        hour=d0; 
        toSum();
        }
    public void day(int d0) { 
        if (sum==0)
            return;
        test();
        day=d0; 
        toSum();
        }
    public void month(int d0){ 
        if (sum==0)
            return;
        test();
        month=d0; 
        toSum();
        }
    public void year(int d0){
        if (sum==0)
            return;
        test();
        year=d0; 
        toSum();
        }
    public void incMonth(){
        if (sum==-1)
            return;
        test();
        month++;
        if (month==13) { month=1; year++; }
        toSum();
        }
    public void decMonth(){
        if (sum==-1)
            return;
        test();
        month--;
        if (month==0) { month=12; year--; }
        toSum();
        }    
    public DateTime(String ss){ 
        parseString(ss); 
        }
    public DateTime(){}
    public DateTime(boolean cur){
        sum = (cur ? -1 : 0);
        }
    private static String twoDigit(int vv){
        return ""+(char)(vv/10+'0')+(char)(vv%10+'0');
        }
    public String onlyDate(){ 
        test();
        if (isDateValid()) return ""+day+"."+twoDigit(month)+"."+year;
        return "";
        }
    public String onlyDate2(){ 
        test();
        if (isDateValid()) return ""+day+"."+twoDigit(month);
        return "";
        }
    public String onlyTime(){ 
        test();
        if (isTimeValid()) return ""+twoDigit(hour)+":"+twoDigit(min);
        return "";
        }
    public String onlyTime2(){ 
        test();
        if (isTimeValid()) return ""+twoDigit(hour)+":"+twoDigit(min)+":"+twoDigit(sec);
        return "";
        }
    public String shortDateTime(){
        String ss=onlyDate2()+" "+onlyTime();
        return ss;
        }
    public String toString(){
        String ss=onlyDate()+" "+onlyTime();
        return ss;
        }
    public String toString2(){
        String ss=onlyDate()+" "+onlyTime2();
        return ss;
        }
    private int timeToInt(){ 
        return (int)(sum /60) % (60*25); 
        }
    private void timeFromInt(int tm){
        test();
        hour=tm/60;
        min=tm%60;
        sec=0;
        toSum();
        }
    // Оставшееся время в мин. с учетом перехода четез сутки при отсутствии даты
    public int dateTimeToInt(){
        if (sum==0) 
            return 0;
        if (sum==-1){
            DateTime xx = new DateTime(true);
            xx.fromSum();
            xx.toSum(true);
            return (int)(xx.sum/60);
            }
        else
            return (int)(sum/60);
        }
    public int restTimeInMin(){           
        DateTime cur=new DateTime(true);
        int t1=dateTimeToInt()-cur.dateTimeToInt();
        return t1;
        }
    public String restTime(){
        if (!isTimeValid()) return "";
        int t=restTimeInMin();
        if (t<0) t=-t;
        return ""+t/60+":"+twoDigit(t%60);
        }
    public boolean lostTime(){
        if (!isTimeValid()) return false;
        return restTimeInMin()<0;
        }
    public void save(DataOutputStream out)throws IOException{
        fromSum();
        out.writeByte(day);
        out.writeByte(month);
        out.writeInt(year);
        out.writeByte(hour);
        out.writeByte(min);
        }
    public void saveWapTaxi(DataOutputStream out)throws IOException{
        fromSum();
        out.writeByte(day);
        out.writeByte(month);
        out.writeByte(year-2000);
        out.writeByte(hour);
        out.writeByte(min);
        out.writeByte(0);
        }
    public void load(DataInputStream out)throws IOException{
        day=out.readByte();
        month=out.readByte();
        year=out.readInt();
        hour=out.readByte();
        min=out.readByte();
        toSum(true);
        }
    //---------------------------------------------------------------
    public static void main(String argv[]){
        DateTime t1=new DateTime();
        System.out.println(t1.timeToInt()+" "+t1.onlyDate()+" "+t1.onlyDate2()+" "+t1.onlyTime()+" "+t1.onlyTime2()+" "+t1.restTime()+" "+t1.shortDateTime()+" "+t1.toString()+" "+t1.toString2());
        t1.hour(4);
        System.out.println(t1.timeToInt()+" "+t1.onlyDate()+" "+t1.onlyDate2()+" "+t1.onlyTime()+" "+t1.onlyTime2()+" "+t1.restTime()+" "+t1.shortDateTime()+" "+t1.toString()+" "+t1.toString2());
        }
}
