package me.romanow.brs.database;

//--------------------------------------------------------------------------

import me.romanow.brs.model.OnlyDate;

// idDoc!=0 - файл БЫЛ в базе
// docFile!=0 - файл ЕСТЬ в базе
public class DBFile extends DBStudentCellItem{
    private String fileName="";
    private int fileId=0;
    public DBFile(){}
    public DBFile(int idr, int ids, int idc, int fid, String name){
        super(idr,ids,idc);
        fileName=name;
        fileId=fid;
        }
    private int cDate=new OnlyDate().dateToInt();
    public String getFileName(){ return fileName; }
    public int getFileId(){ return fileId; }
    public void setFileName(String var){ fileName=var; }
    public void setFileId(int var){ fileId=var; }
    public int getCDate(){ return cDate; }
    public void setCDate(int id0){ cDate=id0; }
    public boolean inArchive(){ return fileName.length()!=0; }
}
