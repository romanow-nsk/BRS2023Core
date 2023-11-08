
package me.romanow.brs.database;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class DBIdentification extends DBItem{
    private String keyString="";
    private final static int keySize=20;
    public static String generateKey(){
        String ss="";
        for(int i=0;i<keySize;i++){
            char cc=0;
            int k=(int)(Math.random()*3);
            if (k==0) cc=(char)('A'+(int)(Math.random()*26));
            if (k==1) cc=(char)('a'+(int)(Math.random()*26));
            if (k==2) cc=(char)('0'+(int)(Math.random()*10));
            ss+=cc;
            }
        return ss;
        }

    public String getKeyString() {
        return keyString;
        }

    public void setKeyString(String key) {
        this.keyString = key;
        }   
    public DBIdentification(){ keyString=generateKey(); }
    public static void main(String argv[]){
        System.out.println(generateKey());
        }
    public void save(DataOutputStream os) throws Throwable{
        os.writeUTF(keyString);
        }
    public void load(DataInputStream is) throws Throwable{
        keyString=is.readUTF();
        }
}
