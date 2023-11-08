package me.romanow.brs.database;

import me.romanow.brs.interfaces.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

public class DBProfile extends DBEntry{
	private final static String DBProfileFile="brs_config.dat";
        private String proxyIP="217.71.138.1";
        private int proxyPort=8080;
        private String dirName="";
        private boolean proxyOn=false;
        public boolean isProxyOn() {
            return proxyOn;
            }
        public String getProxyIP() {
            return proxyIP;
            }
        public int getProxyPort() {
            return proxyPort;
            }
        public void setProxyIP(String proxyIP) {
            this.proxyIP = proxyIP;
            }
        public void setProxyPort(int proxyPort) {
            this.proxyPort = proxyPort;
            }    
        public void setProxyOn(boolean proxyOn) {
            this.proxyOn = proxyOn;
            }
        private void testDir() throws Throwable{
            File dir=new File(dirName);
            if (!dir.exists()) dir.mkdir();
            }
        public DBProfile(String dir){
            dirName=dir;
            }
        public DBProfile(){
            this("");
            }	
        public void save() throws Throwable{
            DataOutputStream out=null;
            testDir();
            try {
                String fname=dirName+(dirName.length()==0 ? "":"/")+DBProfileFile;
                out=new DataOutputStream(new FileOutputStream(fname));
                super.save(out);
                out.writeUTF(proxyIP);
                out.writeInt(proxyPort);
                out.writeBoolean(proxyOn);
            } catch(IOException e2){
                int vv=0;
                }
            finally { out.close(); }
            }
        public boolean load() throws Throwable{
            DataInputStream out=null;
            testDir();
            try {
                out=new DataInputStream(new FileInputStream(dirName+(dirName.length()==0 ? "":"/")+DBProfileFile));
		} catch(Exception ee){ return false;  }
            try {
                super.load(out);
                proxyIP=out.readUTF();
                proxyPort=out.readInt();
                proxyOn=out.readBoolean();
            }catch(IOException e2){}
            finally { out.close(); }
            return true;
            }
}
