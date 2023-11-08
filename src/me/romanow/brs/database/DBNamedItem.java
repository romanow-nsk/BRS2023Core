package me.romanow.brs.database;

public class DBNamedItem extends DBItem{
    private String name="";
    public void setName(String id){ name=id;}	
    public String getName(){ return name;}
    public String orderBy(){ return "name"; }
    public DBNamedItem(int id,String ss){ super(id); name=ss; }
    public DBNamedItem(String ss){ name=ss; }
    public DBNamedItem(){ super(); } 
    public String toString(){ return name; }
    public String twoWord(){
        char c[]=name.toCharArray();
        int k=0;
        for(int i=1;i<c.length;i++)
            if (c[i]==' ' && c[i-1]!=' '){
                k++;
                if (k==2) return name.substring(0, i);
                }
        return name;
        }
}
