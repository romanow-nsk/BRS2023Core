package me.romanow.brs.database;

import me.romanow.brs.interfaces.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;

public class DBItem implements Cloneable{
    //---------- Обязательные параметры объекта БД ----------------------------
    transient public boolean mark=false;	// Маркер для отметки при селекции
    transient public boolean renew=false;       // Маркер изменения при селекции
    transient private Vector<DBField> fld=null;
    private int id=0;
    public void setId(int id0){ id=id0;}	
    public int getId(){ return id;}	
    public DBItem(){ id=0; mark=false; }
    public DBItem(int id0){ id=id0; mark=false; }
    public boolean isValid(){ return id!=0; }
    public void setFields(Vector<DBField> fld0){ fld=fld0; }
    public void save(DataOutputStream os) throws Throwable{}
    public void load(DataInputStream os) throws Throwable{}
    //--------------------------------------------------------------------------
    public static String getURLParameter(String str, String name) throws Throwable{
        int k=str.indexOf(name+"=");
        if (k==-1) return null;
        str=str.substring(k+name.length()+1);
        int m=str.indexOf("&");
        if (m!=-1) str=str.substring(0, m);
        return URLDecoder.decode(str, "Cp1251");
        }
    //--------------------------------------------------------------------------
    public static String dbTypes[]={"int","String","double","boolean","short","long","java.lang.String"};
    public final static byte dbInt=0,dbString=1,dbDouble=2,dbBoolean=3,dbShort=4,dbLong=5,dbString2=6;  //  ID-ы сериализуемых типов
    public int getDbTypeId(String ss){
        int i;
        for (i=0;i<dbTypes.length;i++)
            if (dbTypes[i].equals(ss)) return i;
        return -1;
        }
    //--------------------------------------------------------------------------
    //  Берет имя текущего и базового классов 
    final public String getTableName() throws Throwable{
    	String ss=this.getClass().getName();
	int m=ss.lastIndexOf(".");
	if (m!=-1) ss=ss.substring(m+1);
	if (!ss.startsWith("DB")) {
        	ss=this.getClass().getSuperclass().getName();
                m=ss.lastIndexOf(".");
                if (m!=-1) ss=ss.substring(m+1);
                if (!ss.startsWith("DB")) {
                    throw new BRSException(BRSException.dbase,"Недопустимый класс для БД: "+ss);
                }
            }
	return ss.substring(2).toLowerCase();
	}
    final public String getLinkName() throws Throwable{
        return "id"+getTableName();
        }
    final public DBField[] getFieldsGetSet(){
        Vector<DBField> vv=new Vector();
        Method[] mtds = getClass().getMethods();
        for (int i=mtds.length-1; i>=0; i--) {
            Method mtdget=mtds[i];
            String ss=mtdget.getName();
            if (!ss.startsWith("get")) continue;
            ss=ss.substring(3).toLowerCase();
            Class zz=mtdget.getReturnType();
            if (zz.isArray()) continue;
            String type=zz.getSimpleName();
            for (int j=mtds.length-1; j>=0; j--) {
                Method mtdset=mtds[j];
            String ss1=mtdset.getName();
            if (!ss1.startsWith("set")) continue;
            ss1=ss1.substring(3).toLowerCase();
            if (!ss.equals(ss1)) continue;
            vv.add(new DBField(ss,getDbTypeId(type),mtdget,mtdset));
            break;
            }
        }
        DBField xx[]=new DBField[vv.size()];
        for (int i=0;i<xx.length;i++)
            xx[i]=vv.get(i);
        return xx;
	}
    //----------------------------- Реализация через Fields -------------------
    final public Vector<DBField> getFields() throws SQLException{
        boolean idFound=false;
        Class cls=getClass();
        Vector<DBField> out=new Vector();
        for(;cls!=null;cls=cls.getSuperclass()){    // Цикл по текущему и базовым
            if (!cls.getSimpleName().startsWith("DB")) 
                continue;                           // Только классы DB
            Field fld[]=cls.getDeclaredFields();    // 
            for(int i=0;i<fld.length;i++){          // Перебор всех полей
                fld[i].setAccessible(true);         // Сделать доступными private-поля
                String tname=fld[i].getType().getName();
                int type=getDbTypeId(tname);
                String name=fld[i].getName();
                if (name.equals("id")) idFound=true;
                if (type==-1) continue;
                if ((fld[i].getModifiers() & Modifier.TRANSIENT)!=0) continue;
                if ((fld[i].getModifiers() & Modifier.STATIC)!=0) continue;
                out.add(new DBField(name,type,fld[i]));
                }
            }
        if (!idFound) throw new SQLException("id не найден в классе "+cls.getSimpleName());
        return out;
        }
    //--------------------------------------------------------------------------
    final public void saveDBValues(DataOutputStream out) throws Throwable{
        DBField ff=null;
        if (fld==null) fld=this.getFields();
        try {
            for(int i=0;i<fld.size();i++){
                ff=fld.get(i);
                switch(ff.type){
    	case dbInt:	out.writeInt(ff.field.getInt(this)); break;
    	case dbShort:	out.writeShort(ff.field.getShort(this)); break;
    	case dbLong:	out.writeLong(ff.field.getLong(this)); break;
    	case dbDouble:	out.writeDouble(ff.field.getDouble(this)); break;
    	case dbBoolean: out.writeBoolean(ff.field.getBoolean(this)); break;
    	case dbString2:	
        case dbString:	out.writeUTF(""+ff.field.get(this)); break;
                        }
                    }
                }
            catch(IOException ee){ throw new BRSException(BRSException.dbase,getTableName()+"["+getId()+"] "+"Ошибка записи поля "+getTableName()+":"+ff.name);  }
            catch(Exception ee){ throw new BRSException(BRSException.dbase,getTableName()+"["+getId()+"] "+"Ошибка результата метода get "+getTableName()+":"+ff.name);  }
        }

    final public void loadDBValues(DataInputStream out) throws Throwable{
        DBField ff=null;
        if (fld==null) fld=this.getFields();
        try {
            for(int i=0;i<fld.size();i++){
                ff=fld.get(i);
                switch(ff.type){
    	case dbInt:	ff.field.setInt(this, out.readInt()); break;
    	case dbShort:	ff.field.setShort(this, out.readShort()); break;
    	case dbLong:	ff.field.setLong(this, out.readLong()); break;
    	case dbDouble:	ff.field.setDouble(this, out.readDouble()); break;
    	case dbBoolean: ff.field.setBoolean(this, out.readBoolean()); break;
    	case dbString2:	
        case dbString:	ff.field.set(this, out.readUTF()); break;
                    }
                } 
            }
        catch(IOException ee){ throw new BRSException(BRSException.dbase,getTableName()+" ["+getId()+"] "+"Ошибка чтения поля "+getTableName()+":"+ff.name);  }
        catch(Exception ee){ throw new BRSException(BRSException.dbase,getTableName()+"["+getId()+"] "+"Ошибка результата метода set "+getTableName()+":"+ff.name);  }
        }

    final public void loadDBValues(ResultSet out) throws Throwable{
        DBField ff=null;
        if (fld==null) fld=this.getFields();
        try {
            for(int i=0;i<fld.size();i++){
                ff=fld.get(i);
                switch(ff.type){
    	case dbInt:	ff.field.setInt(this, out.getInt(i+1)); break;
    	case dbShort:	ff.field.setShort(this, out.getShort(i+1)); break;
    	case dbLong:	ff.field.setLong(this, out.getLong(i+1)); break;
    	case dbDouble:	ff.field.setDouble(this, out.getDouble(i+1)); break;
    	case dbBoolean: ff.field.setBoolean(this, out.getBoolean(i+1)); break;
    	case dbString2:	
        case dbString:	ff.field.set(this, out.getString(i+1)); break;
                    }
                } 
            }
        catch(Exception ee){ throw new BRSException(BRSException.dbase,getTableName()+"["+getId()+"] "+"Ошибка результата метода set "+getTableName()+":"+ff.name);  }
        }
    final public void setDBValues(boolean quote) throws Throwable{
        DBField ff=null;
        if (fld==null) fld=this.getFields();
        try {
            for(int i=0;i<fld.size();i++){
                ff=fld.get(i);
                switch(ff.type){
    	case dbInt:	ff.value=""+ff.field.getInt(this); break;
    	case dbShort:	ff.value=""+ff.field.getShort(this); break;
    	case dbLong:	ff.value=""+ff.field.getLong(this); break;
    	case dbDouble:	ff.value=""+ff.field.getDouble(this); break;
    	case dbBoolean: ff.value=ff.field.getBoolean(this) ? "1":"0";break;
    	case dbString2:	
        case dbString:	ff.value=""+ff.field.get(this); 
                        if (quote) ff.value="'"+ff.value+"'";
                        break;
                        }
                    }
                }
            catch(Exception ee){ throw new BRSException(BRSException.dbase,getTableName()+"["+getId()+"] "+"Ошибка результата метода get "+getTableName()+":"+ff.name);  }
        }
    public String toString(){ return getClass().getSimpleName()+"/id="+id; }
    public String twoWord(){ return toString(); }
    final public void parseFromData() throws Throwable{
    	parseFromData(null);
    	}
    final public void parseFromData(String data[]) throws Throwable{
        DBField ff=null;
        if (fld==null) fld=this.getFields();
            for(int i=0;i<fld.size();i++){
                ff=fld.get(i);
    		if (data!=null) ff.value=data[i];
                try {
                switch(ff.type){
    	case dbInt: 	ff.field.setInt(this, Integer.parseInt(ff.value)); break;
    	case dbDouble:	ff.field.setDouble(this, Double.parseDouble(ff.value)); break;
    	case dbBoolean: if (ff.value.equals("true")) ff.field.setBoolean(this, true);
                        else
                        if (ff.value.equals("false")) ff.field.setBoolean(this, false);
                        else
                        ff.field.setBoolean(this, Integer.parseInt(ff.value)!=0); 
                        break;
    	case dbString2:	
        case dbString:	ff.field.set(this, ff.value); break;
    			}
    		} catch(Exception ee){ throw new BRSException(BRSException.dbase,getTableName()+"["+getId()+"] "+"Ошибка конвертации "+ff.name+":"+data[i]); }
            }
    	}
    public String orderBy(){ return "id"; }
    //======================================================================================
	/*------------------- Пример запроса создания таблицы --------------------------
	CREATE TABLE `groups` (
			  `id` int(11) NOT NULL,
			  `name` varchar(45) DEFAULT NULL,
			  PRIMARY KEY (`id`),
			  UNIQUE KEY `name_UNIQUE` (`name`)
			) ENGINE=InnoDB DEFAULT CHARSET=utf8$$
    */
    public String encode (String ss)throws Throwable
        { return URLEncoder.encode(ss, "Cp1251"); }
    public String decode (String ss)throws Throwable
        { return URLDecoder.decode(ss, "Cp1251"); }
    public void loadURLParams(String str)throws Throwable {
        throw new BRSException(BRSException.bug,"Не передаются параметры класса "+this.getClass().getSimpleName());
        }
    public String saveURLParams()throws Throwable {
        throw new BRSException(BRSException.bug,"Не передаются параметры класса "+this.getClass().getSimpleName());
        }
}
