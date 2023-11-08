package me.romanow.brs.database;

public class DBBasePermission extends DBPermission{
	public DBBasePermission(DBPermission src){
		setName(src.getName());
		setIdRating(src.getIdRating());
		setIdTutor(src.getIdTutor());
	}
	public DBBasePermission(){}	
}
