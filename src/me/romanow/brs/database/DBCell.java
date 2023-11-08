package me.romanow.brs.database;

import java.sql.SQLException;
import java.util.Vector;

public class DBCell extends DBNamedItem{
    public final static String NoteTypes[]={"Лаба ","Защита ","Контр ","РГР","КР","Зач","Экз","Индив"};
    public final static String QualityTypes[]={
        "-Оформление",
        "-Ошибки в работе",
        "-Не всё задание",
        "+Оригинальность",
        "+Объем работы",
        "+Сложность",
        };
    //----------- Это порядок следования признаков -----------------------------
    public final static int idxPropusk=0;	// Учет посещаемости
    public final static int idxManual=1;	// Ручная установка баллов
    public final static int idxCData=2;		// Учет срока сдачи
    public final static int idxSecond=3;        // Выполнение в подгруппах
    public final static int idxParams=4;	// Параметры качества
    //---------------------- Параметры подсчета оценки ------------------------
    public final static byte NoteParamValues[][]={
        {1,0,1,1,1},	//Лаба
        {0,0,1,1,0},	//Защита
        {0,0,1,0,1},	//Контр
        {0,1,0,0,0},	//РГР
        {0,1,0,0,0},	//К.Р.		
        {0,1,0,0,0},	//Зач
        {0,1,0,0,0},    //Экз
        {0,1,0,0,0}     //Индив
        };
    //----------------- Коэффициент снижения за каждый вид штрафа
    public DBCell(){ }
    public DBCell(int id){ super(id,""); }
    public DBCell(String nm, int gid, int ct, int bl, int num){ 
        super(nm); 
        idCourse=gid; 
        cType=ct;
        ball=bl;
        ordNum=num;
        }
    private int idCourse=0;
    private int cType=0;
    private int ball=0;
    private int ordNum=0;
    public String orderBy(){ return "ordNum"; }
    public int getIdCourse(){ return idCourse; }
    public int getCType(){ return cType; }
    public int getBall(){ return ball; }
    public int getOrdNum(){ return ordNum; }
    public void setIdCourse(int id){ idCourse=id; }
    public void setCType(int ct){ cType=ct; }
    public void setBall(int ct){ ball=ct; }
    public void setOrdNum(int num){ ordNum=num; }
    public byte []getCellParams(){ return NoteParamValues[cType]; }
}
