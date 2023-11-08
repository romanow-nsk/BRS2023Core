package me.romanow.brs.database;

public class DBLastId  extends DBItem{
	private int noteId=0;
	private int eventId=0;
	private int propuskId=0;
	private int docId=0;
	private int archId=0;
	private int varId=0;
	private int studRatingId=0;
	private int cellRatingId=0;
	public int getNoteId(){ return noteId; }
	public int getEventId(){ return eventId; }
	public int getPropuskId(){ return propuskId; }
	public int getDocId(){ return docId; }
	public int getArchId(){ return archId; }
	public int getVarId(){ return varId; }
	public int getStudRatingId(){ return studRatingId; }
	public int getCellRatingId(){ return cellRatingId; }
	public void setNoteId(int vv){ noteId=vv; }
	public void setEventId(int vv){ eventId=vv; }
	public void setPropuskId(int vv){ propuskId=vv; }
	public void setDocId(int vv){ docId=vv; }
	public void setArchId(int vv){ archId=vv; }
	public void setVarId(int vv){ varId=vv; }
	public void setStudRatingId(int vv){ studRatingId=vv; }
	public void setCellRatingId(int vv){ cellRatingId=vv; }
}
