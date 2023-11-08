package me.romanow.brs.database;

public class DBFileData extends DBItem{
	private String data="";
	private int idFileData=0;
	public void setData(String ss){ data=ss; }
	public String getData(){ return data; }
	public void setIdFileData(int id){ idFileData=id; }
	public int getIdFileData(){ return idFileData; }
}
