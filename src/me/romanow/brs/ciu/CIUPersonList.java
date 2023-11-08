package me.romanow.brs.ciu;

/**
 * Created by romanow on 01.12.2016.
 */
public class CIUPersonList {
    public CIUPerson[] data = new CIUPerson[0];
    public CIUPersonList(CIUPerson list[]){
        this.data = list;
    }
    public CIUPersonList(){
    }
    public String toString(){
        String ss="";
        for (int i=0;i<data.length;i++)
            ss+=data[i] + "\n";
        return ss;
    }
    public CIUPerson getTutor(){
        return getType("Сотрудник");
    }
    public CIUPerson getStudent(){
        return getType("Студент");
    }
    public CIUPerson getType(String type){
        for (int i=0;i<data.length;i++)
            if (data[i].TYPE.equals(type))
                return data[i];
    return null;
    }
}
