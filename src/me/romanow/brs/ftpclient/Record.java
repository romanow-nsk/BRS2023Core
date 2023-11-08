package me.romanow.brs.ftpclient;

public class Record{
    public String name;
    public String type;
    public String size;
    public String date;

    public Record(String name, String type, String size, String date){
        this.name = name;
        this.type = type;
        this.size = size;
        this.date = date;
    }
}