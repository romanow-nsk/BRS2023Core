/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.servlet.http.HttpServletRequest;
import me.romanow.brs.database.DBNote;
import me.romanow.brs.xml.XMLBoolean;

/**
 *
 * @author user
 */
public class MDNote extends DBNote{
    private String docFile="";
    private String archFile="";
    private String variant="";
    private int idDoc=0;
    private int idArch=0;
    private int archDate=0;
    private int docDate=0;
    public int getArchDate() { return archDate; }
    public int getDocDate() { return docDate; }
    public void setArchDate(int archDate) { this.archDate = archDate; }
    public void setDocDate(int docDate) { this.docDate = docDate; }
    //--------------------------------------------------------------------------
    // idDoc!=0 - файл БЫЛ в базе
    // docFile!=0 - файл ЕСТЬ в базе
    public int getIdDoc(){ return idDoc; }
    public int getIdArch(){ return idArch; }
    public String getDocFile(){ return docFile; }
    public String getArchFile(){ return archFile; }
    public String getVariant(){ return variant; }
    public void setDocFile(String ss){ docFile=ss; }
    public void setArchFile(String ss){ archFile=ss; }
    public void setIdDoc(int ss){ idDoc=ss; }
    public void setIdArch(int ss){ idArch=ss; }
    public void setVariant(String ss){ variant=ss; }
    public MDNote(int sid, int rid, int cid, double bl, int wk){
        super(sid,rid,cid,bl,wk);
        }
    public MDNote(int sid, int rid, int cid){
        super(sid,rid,cid);
        }
    public MDNote(){}
    public MDNote(DBNote xx){
        this.setWeek(xx.getWeek());
        this.setParams(xx.getParams());
        this.setId(xx.getId());
        this.setIdRating(xx.getIdRating());
        this.setIdStudent(xx.getIdStudent());
        this.setIdCell(xx.getIdCell());
        this.setCDate(xx.getCDate());
        this.setBall(xx.getBall());
        this.setRemoved(xx.getRemoved());
        }
    @Override
    public void loadURLParams(String req)throws Throwable {
        String zz=getURLParameter(req,"idd");
        if (zz!=null) setIdDoc(Integer.parseInt(zz));
        zz=getURLParameter(req,"ida");
        if (zz!=null) setIdArch(Integer.parseInt(zz));        
        zz=getURLParameter(req,"doc");
        if (zz!=null) setDocFile(zz);
        zz=getURLParameter(req,"arch");
        if (zz!=null) setArchFile(zz);
        zz=getURLParameter(req,"var");
        if (zz!=null) setVariant(zz);
        zz=getURLParameter(req,"ball");
        if (zz!=null) setBall(Double.parseDouble(zz));
        zz=getURLParameter(req,"week");
        if (zz!=null) setWeek(Integer.parseInt(zz));        
        zz=getURLParameter(req,"par");
        if (zz!=null) setParams(Integer.parseInt(zz));        
        zz=getURLParameter(req,"date");
        if (zz!=null) setCDate(Integer.parseInt(zz));        
        }
    public String saveURLParams()throws Throwable {
        String ss="&idd="+encode(""+getIdDoc())+"&ida="+encode(""+getIdArch())+"&doc="+encode(getDocFile()); 
        ss+="&arch="+encode(getArchFile())+"&var="+encode(getVariant());
        ss+="&ball="+encode(""+getBall())+"&week="+encode(""+getWeek());
        ss+="&par="+encode(""+getParams())+"&date="+encode(""+getCDate());
        return ss;
        }

}
