package me.romanow.brs.database;

import java.sql.SQLException;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import static me.romanow.brs.database.DBItem.getURLParameter;

public class DBStudent extends DBNamedItem{
    public DBItem emptyCopy(){ return new DBStudent(); }
    public DBStudent(){ idGroups=0;  }
    public DBStudent(int id0){ super(id0,"");   }
    public DBStudent(String nm, int gid){ super(nm); idGroups=gid; }
    private int idGroups=0;
    private String ciuLogin="";
    transient public boolean off=false;
    private String pass="";
    public String getPass(){ return pass; }
    public void setPass(String ss){ pass=ss; }
    public int getIdGroups(){ return idGroups; }
    public void setIdGroups(int id){ idGroups=id; }
    public String getCiuLogin(){ return ciuLogin; }
    public void setCiuLogin(String id){ ciuLogin=id; }
    @Override
    public void loadURLParams(String req)throws Throwable {
        String zz=getURLParameter(req,"name");
        if (zz!=null) setName(zz);
        zz=getURLParameter(req,"pass");
        if (zz!=null) setPass(zz);
        zz=getURLParameter(req,"login");
        if (zz!=null) setCiuLogin(zz);
        }
    public String saveURLParams()throws Throwable {
        return "&name="+encode(getName())+"&pass="+encode(getPass())+"&login="+encode(getCiuLogin()); 
        }
}
