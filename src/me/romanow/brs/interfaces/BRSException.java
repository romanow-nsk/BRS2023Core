/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.interfaces;

import java.io.IOException;
import java.sql.SQLException;

/**
 *
 * @author user
 */
public class BRSException extends Exception{
    private static String mess[]={
        "",                         // 0
        "Ошибка программы",         // 1
        "Ошибка SQL-запроса",       // 2
        "Ошибка сети",              // 3
        "Ошибка структуры БД",      // 4
        "Функция не реализована",   // 5
        "Нераспознанная ошибка",    // 6
        "Ошибка сервера",           // 7
        "Ошибка настройки",         // 8
        "Ошибка в/в",               // 9
        ""                          // 10 - предупреждение
        };
    public static int bug=1;
    public static int sql=2;
    public static int net=3;
    public static int dbase=4;
    public static int nofunc=5;
    public static int other=6;
    public static int serv=7;
    public static int set=8;
    public static int io=9;
    public static int msg=0;
    public static int warn=10;
    private String message="";
    private int code=0;
    public int getCode(){ return code; }
    public String getMessage(){
        if (code==msg) return message;
        String ss=""+code+": "+mess[code];
        if (message.length()!=0) ss+="("+message+")";
        return ss;
        }
    public boolean isProgrammBug(){
        return code==bug || code==sql || code==nofunc || code==other;
        }
    public BRSException(Throwable ee){
    	super(ee);
        this.message="";
        if (ee instanceof BRSException) { 
        	code=((BRSException)ee).code;
        	message=((BRSException)ee).message;
        	}
        else{
        	message=ee.toString();
        	if (ee instanceof SQLException) { code=sql; }
        	else
        	if (ee instanceof IOException) { code=net; }
        	else
        	if (ee instanceof Exception) { code=bug; }
        	else
        	if (ee instanceof Error) { code=bug; }
        	else
        		code=other;
        	}
        }
    public BRSException(int code,String message){ 
        this.code=code; 
        this.message=message; 
        }
    public BRSException(int code){
        this(code,""); 
        }
    public BRSException(String message){ 
        this.code=msg; 
        this.message=message; 
        }
    public String toString(){
        String ss = super.getMessage();
        return getMessage()+(ss==null ? "" : "/"+ss);
        }
}
