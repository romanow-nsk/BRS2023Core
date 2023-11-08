package me.romanow.brs.database;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DBField {
	public String name,value="";
        public int type=-1;
	public Method get,set;
        public Field field;
	DBField(String nm, int tp, Method get0, Method set0){ name=nm; type=tp; set=set0; get=get0; field=null; }
	DBField(String nm, int tp, Field fld0){ name=nm; type=tp; set=null; get=null; field=fld0; }
	}
