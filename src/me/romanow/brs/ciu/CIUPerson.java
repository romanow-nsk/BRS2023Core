package me.romanow.brs.ciu;

/**
 * Created by romanow on 01.12.2016.
 */
public class CIUPerson {
    public String FAMILY_NAME="";
    public String NAME = "";
    public String STUDY_GROUP="";
    public int ID_CARD=0;
    public String PATRONYMIC_NAME="";
    public int ID_GROUP=0;
    public String TYPE="";
    public int ID=0;
    public String toString(){
        return FAMILY_NAME+" "+NAME+" "+TYPE;
    }
}
