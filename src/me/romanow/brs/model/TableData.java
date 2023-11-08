package me.romanow.brs.model;

import java.io.File;
import java.io.IOException;
import me.romanow.brs.interfaces.TableCallBack;

public class TableData {
    public String fileName="";
    private int tableType=0;
    public int getTableType(){ return tableType; }
    public void setTableType(int vv){ tableType=vv; }
	public int ncol,nrow;
	public boolean lastrow;
	public String title="", bottom="",bottom2="";
	public String cols[]=null;
	public String rows[]=null;
	public String data[][]=null;
	public boolean select[][]=null;
    public String bottoms[]=null;
    public boolean isValid(){ return ncol!=0; }
    public TableData(){ ncol=nrow=0; }
    public TableData(int nc,int nr){
        this();
        setTable(nc,nr);
        }
	public TableData(int nr,String cols0[]){
        this();
        setTable(nr,cols0);
        }
	public void setTable(int nc,int nr){
		ncol=nc; nrow=nr;
		lastrow=true;
		cols=new String[ncol];
		rows=new String[nrow];
		data=new String[nrow][];
		select=new boolean[nrow][];
		for(int i=0;i<nrow;i++){
			data[i]=new String[ncol];
			select[i]=new boolean[ncol];
			for(int j=0;j<ncol;j++) {
				data[i][j]=""; select[i][j]=false; 
				}
			}
        }
	public void setTable(int nr,String cols0[]){
		ncol=cols0.length; 
		nrow=nr;
		lastrow=false;
		cols=cols0;
		rows=new String[nrow];
		data=new String[nrow][];
		select=new boolean[nrow][];
		for(int i=0;i<nrow;i++){
			data[i]=new String[ncol];
			select[i]=new boolean[ncol];
			for(int j=0;j<ncol;j++) { 
				data[i][j]=""; select[i][j]=false; 
				}
			}
	}
        //--------------- Переопределяемая заготовка --------------------------
        public void createTable(File dst, TableCallBack back ) throws Exception{}
        public String getExtention(){ return null; }
        public void refreshTable(){}
        public void closeTable(){}
}
