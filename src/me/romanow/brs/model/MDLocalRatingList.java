package me.romanow.brs.model;

import me.romanow.brs.interfaces.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import me.romanow.brs.database.DBEntry;
import me.romanow.brs.database.DBItem;

public class MDLocalRatingList {
	private final static String MDRatingListFile="brs_ratings.dat";
	private Vector<MDRatingDescription> ent=new Vector();
    private String dirName="";
    private void testDir() throws Throwable{
            File dir=new File(dirName);
            if (!dir.exists()) dir.mkdir();
            }
    public MDRatingDescription getById(int rid, int eid){
            for(int i=0;i<ent.size();i++){
                MDRatingDescription x=ent.get(i);
                if (x.rating.getId()==rid && x.entry.getId()==eid)
                    return x;
                }
            return null;
            }
    public MDRatingDescription getByName(String name){
            for(int i=0;i<ent.size();i++){
                MDRatingDescription x=ent.get(i);
                if (x.rating.getName().equals(name))
                    return x;
                }
            return null;
            }
	public MDLocalRatingList(String dir){
            dirName=dir;
            ent.clear();
            }
	public DBItem[] getList(){
            DBItem xx[]=new DBItem[ent.size()];
            for (int i=0;i<xx.length;i++)
            	xx[i]=ent.get(i).rating;
            return xx;
            }
        public void save() throws Throwable{
            DataOutputStream out=null;
            testDir();
            try {
                out=new DataOutputStream(new FileOutputStream(dirName+"/"+MDRatingListFile));
                out.writeInt(ent.size());
                for (int i=0;i<ent.size();i++)
                    ent.get(i).save(out);
            } catch(IOException e2){}
            finally { out.close(); }
            }
        public boolean load() throws Throwable{
            DataInputStream out=null;
            testDir();
            try {
                out=new DataInputStream(new FileInputStream(dirName+"/"+MDRatingListFile));
		} catch(Exception ee){ return false;  }
            try {
                int nn=out.readInt();
                ent.clear();
                while(nn--!=0){
                    MDRatingDescription xx=new MDRatingDescription();
                    xx.load(out);
                    ent.add(xx);
                    }
            }catch(IOException e2){}
            finally { out.close(); }
            return true;
            }
    public int size(){ return ent.size(); }
    public void add(MDRatingDescription xx){
    	ent.add(xx);
    	}
    public void remove(int idx){
    	ent.remove(idx);
    	}
    public MDRatingDescription get(int idx){
    	return ent.get(idx);
    	}
}
