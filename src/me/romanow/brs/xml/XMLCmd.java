/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.xml;

/**
 *
 * @author user
 */
public class XMLCmd {
    public int code=0;
    public int id=0,id2=0,id3=0,id4=0;
    public String str="";
    public XMLCmd(){}
    public XMLCmd(int code,int id, int id2, int id3, int id4, String vv){ 
        this.code=code; 
        this.id=id; 
        this.id2=id2; 
        this.id3=id3; 
        this.id4=id4; 
        str=vv;
        }
    public XMLCmd(int code,int id, int id2, int id3, int id4){ this(code,id,id2,id3,id4, ""); }
    public XMLCmd(int code,int id, int id2, int id3){ this(code,id,id2,id3,0,""); }
    public XMLCmd(int code,int id, int id2){ this(code,id,id2,0,0,""); }
    public XMLCmd(int code,int id){ this(code,id,0,0,0,""); }
    //--------------------------------------------------------------------------
    public final static int ping=0;
    public final static int getRatingList=1;
    public final static int testUser=2;
    public final static int testEdit=3;
    public final static int loadParams=4;
    public final static int loadRating=5;
    public final static int loadFullRating=6;
    public final static int loadStudentList=7;
    public final static int loadCellList=8;
    public final static int loadEventList=9;
    public final static int calcStudentRating=10;
    public final static int loadStudentRating=11;
    public final static int loadCellRating=12;
    
    public final static int getNote=20;
    public final static int getNoteHistory=21;
    public final static int changeNote=22;
    public final static int changeDocFile=23;
    public final static int changeArchFile=24;
    public final static int changeVariant=25;
    public final static int getEvent=26;
    public final static int changeEvent=27;
    public final static int changeBrigade=28;
    public final static int changeWeek=29;
    public final static int insertEvent=30;
    public final static int deleteEvent=31;
    
    public final static int writeFile=60;
    public final static int readFile=62;
    public final static int deleteFile=64;
    
    public final static int insert=80;
    public final static int delete=81;

    public final static int getTutorList=101;
    public final static int getGroupsList=102;
    public final static int getStudentList=103;
    public final static int testStudent=104;
    public final static int testBase=105;
}
