package me.romanow.brs.database;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.sql.SQLException;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;

public class DBTutor extends DBUser{
    private String ciuName="";
    public void setCiuName(String id){ ciuName=id;}	
    public String getCiuName(){ return ciuName;}
    /*
    private String pass="";
    private boolean admin=false;
    public void setCiuName(String id){ ciuName=id;}	
    public void setPass(String id){ pass=id;}	
    public String getPass(){ return pass;}
    public String getCiuName(){ return ciuName;}
    public DBTutor(int id0){ super(id0,"");   }
    public DBTutor(String nm, String pass0){ super(nm); pass=pass0; }
    public DBTutor(){ super(); }
    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }
    */
    public DBTutor(int id0){ super("","",false);  this.setId(id0); }
    public DBTutor(String nm, String pass0){ super(nm,pass0,false); }
    public DBTutor(){ super(); }
    @Override
    public void loadURLParams(String req)throws Throwable {
        String zz=getURLParameter(req,"name");
        if (zz!=null) setName(zz);
        zz=getURLParameter(req,"pass");
        if (zz!=null) setPass(zz);
        }
    public String saveURLParams()throws Throwable {
        return "&name="+encode(getName())+"&pass="+encode(getPass()); 
        }
    public void save(DataOutputStream os) throws Throwable{
        os.writeInt(getId());
        os.writeUTF(getName());
        }
    public void load(DataInputStream os) throws Throwable{
        setId(os.readInt());
        setName(os.readUTF());
        }
}
