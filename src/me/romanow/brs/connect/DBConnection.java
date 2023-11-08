package me.romanow.brs.connect;

import java.util.*;
import java.sql.*;
import me.romanow.brs.interfaces.*;
import me.romanow.brs.database.DBEntry;
import me.romanow.brs.database.DBField;
import me.romanow.brs.database.DBItem;
import me.romanow.brs.database.DBProfile;
import me.romanow.brs.interfaces.DBServerType;

public class DBConnection implements DBConnect{
    DBServerType srv=null;
    public DBConnection(){
        srv=new DBServerJDBC();
        }
    public DBConnection(DBServerType srv0){
        srv=srv0;
        }    
    public void reconnect()throws Throwable{
        srv.reconnect();
        }
    public void close()throws Throwable { srv.close(); }
    public boolean isConnected(){ 
        boolean bb =  (srv!=null && srv.isConnected()); 
        return bb;
        }
    public void connect(DBProfile pars)throws Throwable{ 
        srv.connect(pars); 
        }
    public void connect(String fname) throws Throwable {
        srv.connect(fname); 
    }

    //=====================================================================
    private Throwable eout=null;
    private DBItem[] getRecords(DBItem item, final Vector<DBField> ff, String sql)throws Throwable{
        eout=null;
        final Class cls=item.getClass();
        final Vector<DBItem> out=new Vector();
        srv.selectMany(sql, new DBRecordCallBack(){
            @Override
            public void procRecord(ResultSet rs) {
                try {
                    DBItem it=(DBItem)cls.newInstance();
                    it.setFields(ff);
                    it.loadDBValues(rs);
                    out.add(it);
                    } catch(Throwable ee){ eout=ee; }
                }
            });
        if (eout!=null) throw new SQLException(eout.getMessage());
        DBItem xx[]=new DBItem[out.size()];
        out.toArray(xx);
        return xx;
    	}
    //---------------------------------------------------------------------------
    public void copyTable(final DBItem item, final DBConnection dst)throws Throwable{
        dst.createTable(item.getClass());
        eout=null;
    	final Vector<DBField> ff=item.getFields();
        item.setFields(ff);
        String tName=item.getTableName();
        String sql="SELECT ";
        for (int i=0;i<ff.size();i++){
        	if (i!=0) sql+=",";
        	sql+=ff.get(i).name;
            }
        sql+=" FROM "+tName+" ORDER BY "+item.orderBy()+";";       
        srv.selectMany(sql, new DBRecordCallBack(){
            @Override
            public void procRecord(ResultSet rs) {
                try {
                    item.loadDBValues(rs);
                    dst.insert(item,false);
                    } catch(Throwable ee){ eout=ee; }
                }
            });
        if (eout!=null) throw new SQLException(eout.getMessage());
        
        }
    //------------- Старый интерфейс --------------------------------------------
    private void deleteLinked(DBItem item, String fld, int id0, String fld2, int id2, String fld3, int id3) throws Throwable {
        Vector<DBField> ff=item.getFields();
        String tName=item.getTableName();
        String sql="DELETE FROM "+tName+" WHERE "+fld+"="+id0;
        if (fld2!=null) sql+=" AND "+fld2+"="+id2;
        if (fld3!=null) sql+=" AND "+fld3+"="+id3;
        sql+=";";
        srv.execSQL(sql);
	}
    private DBItem[] getAll(DBItem item) throws Throwable {
    	Vector<DBField> ff=item.getFields();
        String tName=item.getTableName();
        String sql="SELECT ";
        for (int i=0;i<ff.size();i++){
        	if (i!=0) sql+=",";
        	sql+=ff.get(i).name;
            }
        sql+=" FROM "+tName+" ORDER BY "+item.orderBy()+";";
        return getRecords(item,ff,sql);
	}
    private DBItem[] getLinked(DBItem item, String fld, int id0, String fld2,
		int id2, String fld3, int id3) throws Throwable {
    	Vector<DBField> ff=item.getFields();
        String tName=item.getTableName();
        String sql="SELECT ";
        for (int i=0;i<ff.size();i++){
        	if (i!=0) sql+=",";
        	sql+=ff.get(i).name;
            }
        sql+=" FROM "+tName+" WHERE "+fld+"="+id0;
        if (fld2!=null) sql+=" AND "+fld2+"="+id2;
        if (fld3!=null) sql+=" AND "+fld3+"="+id3;
        sql+=" ORDER BY "+item.orderBy()+";";
        return getRecords(item,ff,sql);
        }
    private int[] getLinkedId(DBItem item, String fld, int id0, String fld2,
		int id2, String fld3, int id3) throws Throwable {
    	Vector<DBField> ff=item.getFields();
        String tName=item.getTableName();
        String sql="SELECT id ";
        sql+=" FROM "+tName+" WHERE "+fld+"="+id0;
        if (fld2!=null) sql+=" AND "+fld2+"="+id2;
        if (fld3!=null) sql+=" AND "+fld3+"="+id3;
        sql+=" ORDER BY "+item.orderBy()+";";
        final Vector<Integer> out=new Vector();
        srv.selectMany(sql, new DBRecordCallBack(){
            @Override
            public void procRecord(ResultSet rs) {
                try {
                    out.add(new Integer(rs.getInt(1)));
                    } catch(Throwable ee){ eout=ee; }
                }
            });
        int xx[]=new int[out.size()];
        for(int i=0;i<xx.length;i++) xx[i]=out.get(i).intValue();
        return xx;
        }
    //--------------------------------------------------------------------------
    public DBItem[] getJoined(DBItem src, DBItem item, DBItem links) throws Throwable {
    	Vector<DBField> ff=item.getFields();
        String tName=item.getTableName();
        String lName=links.getTableName();
        String sName=src.getTableName();
        String sql="SELECT ";
        for (int i=0;i<ff.size();i++){
        	if (i!=0) sql+=",";
        	sql+=tName+"."+ff.get(i).name;
            }
        sql+=" FROM "+tName+" INNER JOIN " + lName + 
                " ON "+tName+".id = "+lName+".id"+tName+
                " WHERE "+lName+".id"+sName+" = "+src.getId() ;
        sql+=" ORDER BY "+item.orderBy()+";";
        return getRecords(item,ff,sql);
        }
    //---------------------------------------------------------------------------
    public void dropTable(Class proto) throws Throwable {
        DBItem item=(DBItem)proto.newInstance();
        String sql="DROP TABLE `"+item.getTableName()+"`;";
        srv.execSQL(sql);
        }
    public void createTable(Class proto) throws Throwable {
        try { dropTable(proto); } catch(Exception ee){}
        DBItem item=(DBItem)proto.newInstance();
        String sql="CREATE TABLE "+item.getTableName()+" (";
        Vector<DBField> xx=item.getFields();
        for (int i=0; i<xx.size();i++){
        	DBField ff=xx.get(i);
            if (ff.name.equals("id")){
                sql+="id INT NOT NULL AUTO_INCREMENT ,";
                continue;
                }
        	String zz=" "+ff.name+" ";
        	switch(ff.type){
	case DBItem.dbInt:      zz+="int,"; sql+=zz; break;
	case DBItem.dbDouble:	zz+="real,"; sql+=zz; break;
	case DBItem.dbBoolean:	zz+="tinyint(1),"; sql+=zz; break;
	case DBItem.dbShort:	zz+="tinyint(2),"; sql+=zz; break;
	case DBItem.dbLong:     zz+="long,"; sql+=zz; break;
	case DBItem.dbString2:	
	case DBItem.dbString:	
                            if (!ff.name.equals("data"))
                                zz+="varchar(50),"; 
                            else
                                zz+="mediumtext,"; 
                            sql+=zz; 
                            break;
        	}
        }
        sql+="PRIMARY KEY (id));";
        //sql+=");";
        srv.execSQL(sql);
	}
    public DBItem getById(Class proto, int id) throws Throwable {
        DBItem item=(DBItem)proto.newInstance();
        Vector<DBField> ff=item.getFields();
        String tName=item.getTableName();
        String sql="SELECT ";
        for (int i=0;i<ff.size();i++) {
        	if (i!=0) sql+=",";
        	sql+=ff.get(i).name;
        	}
        sql+=" FROM "+tName+" WHERE id="+id+";";
        String data[]=srv.selectOne(sql);
        if (data==null) return null;
        // throw new BRSException(BRSException.sql,"Не найдена запись в "+tName+" по id="+id);
        item.setFields(ff);
        item.parseFromData(data);
        return item;
	}
    public DBItem getFirst(Class proto) throws Throwable {
        DBItem item=(DBItem)proto.newInstance();
    	Vector<DBField> ff=item.getFields();
        String tName=item.getTableName();
        String sql="SELECT ";
        for (int i=0;i<ff.size();i++){
        	if (i!=0) sql+=",";
        	sql+=ff.get(i).name;
            }
        sql+=" FROM "+tName+" ORDER BY "+item.orderBy()+";";
        String data[]=srv.selectOne(sql);
        item.setId(0);
        if (data==null) return null;
        // throw new Throwable(tName+":"+id+" не найдено");
        item.setFields(ff);
        item.parseFromData(data);
        return item;
	}

	
    public DBItem[] getByName(Class proto, String name) throws Throwable {
        DBItem item=(DBItem)proto.newInstance();
   	Vector<DBField> ff=item.getFields();
        String tName=item.getTableName();
        String sql="SELECT ";
        for (int i=0;i<ff.size();i++){
        	if (i!=0) sql+=",";
        	sql+=ff.get(i).name;
        	}
        sql+=" FROM "+tName+" WHERE name='"+name+"'";
        return getRecords(item,ff,sql);
	}
    public void insert(DBItem item) throws Throwable {
        insert(item,true);
        }
    public void insert(DBItem item, boolean newid) throws Throwable {
        Vector<DBField> ff=item.getFields();
        String tName=item.getTableName();
        //if (newid){
        //    item.setId(srv.newRecord(tName));
        //    update(item);
        //    return;
        //    }
        int idIdx=0;
        String sql="INSERT INTO "+tName+" (";
        for (int i=0,j=0;i<ff.size();i++){
            String name = ff.get(i).name;
            if (newid && name.equals("id")){
                idIdx=i;
                continue;
                }
            if (j!=0) sql+=",";
            sql+=name;
            j++;
            }
        sql+=") VALUES (";
        item.setFields(ff);
        item.setDBValues(true);
        for (int i=0,j=0;i<ff.size();i++){
            if (newid && i==idIdx){
                continue;
                }
            if (j!=0) sql+=",";
            sql+=ff.get(i).value;
            j++;
            }
        sql+=");";  
        if (!newid)
            srv.execSQL(sql);
        else
            item.setId(srv.insert(sql));
        }
    public void update(DBItem item) throws Throwable {
        Vector<DBField> ff=item.getFields();
        String tName=item.getTableName();
        String sql="UPDATE "+tName+" SET ";
        item.setFields(ff);
        item.setDBValues(true);
        for (int i=0;i<ff.size();i++){ 
            if (i!=0) sql+=",";
            sql+=ff.get(i).name+"="+ff.get(i).value;
            }
        sql+=" WHERE id="+item.getId()+";";
        srv.execSQL(sql);
	}
    public void delete(Class table, int id) throws Throwable {
        String tName=((DBItem)table.newInstance()).getTableName();
        String sql="DELETE FROM "+tName+" WHERE id="+id+";";
        srv.execSQL(sql);
	}
    public boolean isRemote() {
    	return true;
	}
    public DBItem[] getCondition(Class proto, String cond) throws Throwable {
        DBItem item=(DBItem)proto.newInstance();
        Vector<DBField> ff=item.getFields();
        String tName=item.getTableName();
        String sql="SELECT ";
        for (int i=0;i<ff.size();i++){
        	if (i!=0) sql+=",";
        	sql+=ff.get(i).name;
        	}
        sql+=" FROM "+tName+" WHERE "+cond+";";
        return getRecords(item,ff,sql);
	}

    public DBItem[] getByIdGreater(Class proto, int cond) throws Throwable {
        return getCondition(proto,"id>"+cond);
	}
    public DBItem[] getByIdLetter(Class proto, int cond) throws Throwable {
        return getCondition(proto,"id<"+cond);
	}
    //-------------------------------------------------------------------------
    public void deleteAll(Class proto) throws Throwable {
        DBItem item=(DBItem)proto.newInstance();
    	String tName=item.getTableName();
    	String sql="DELETE FROM "+tName+";";
    	srv.execSQL(sql);
	}
    public int getMaxId(Class proto) throws Throwable {
        DBItem item=(DBItem)proto.newInstance();
        String ss[]=srv.selectOne("SELECT MAX(id) FROM "+item.getTableName()+";");
        if (ss==null) return 0;
        return Integer.parseInt(ss[0]);
        }
    public DBItem []getList(Class proto)  throws Throwable{ 
        return  getAll((DBItem)proto.newInstance());
        }
    public DBItem []getList(Class proto, DBItem link) throws Throwable{
        return getLinked((DBItem)proto.newInstance(),link.getLinkName(),link.getId(),null,0,null,0);
        }
    public DBItem []getList(Class proto, DBItem link, DBItem link2) throws Throwable{
        return getLinked((DBItem)proto.newInstance(),link.getLinkName(),link.getId(),link2.getLinkName(),link2.getId(),null,0);
        }
    public DBItem []getList(Class proto, DBItem link, DBItem link2, DBItem link3) throws Throwable{
        return getLinked((DBItem)proto.newInstance(),link.getLinkName(),link.getId(),link2.getLinkName(),link2.getId(),link3.getLinkName(),link3.getId());
        }
    public void deleteLinked(Class proto, DBItem note) throws Throwable{
        deleteLinked((DBItem)proto.newInstance(), note.getLinkName(), note.getId(),null,0,null,0);
        }
    public void deleteLinked(Class proto, DBItem note,DBItem note2) throws Throwable{
        deleteLinked((DBItem)proto.newInstance(), note.getLinkName(), note.getId(),note2.getLinkName(), note2.getId(),null,0);
        }
    public void deleteLinked(Class proto, DBItem note,DBItem note2,DBItem note3) throws Throwable{
        deleteLinked((DBItem)proto.newInstance(), note.getLinkName(), note.getId(),note2.getLinkName(), note2.getId(),note3.getLinkName(), note3.getId());
        }

    @Override
    public int[] getListId(Class proto, DBItem link) throws Throwable {
        return getLinkedId((DBItem)proto.newInstance(), link.getLinkName(), link.getId(),null,0,null,0);
        }

    @Override
    public void execSQL(String sql) throws Throwable {
        srv.execSQL(sql);
        }

    @Override
    public void selectMany(String sql, DBRecordCallBack back) throws Throwable {
        srv.selectMany(sql, back);
    }
}

