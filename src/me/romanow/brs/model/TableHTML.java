/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import me.romanow.brs.interfaces.TableCallBack;

/**
 *
 * @author user
 */
public class TableHTML extends TableData{
    final static int htmlTextSize=2;
    final static int htmlCellWidth=25;
    final static int htmlCellHight=25;
    @Override
    public String getExtention(){ return "htm"; }
    @Override
    public void createTable(File dst, TableCallBack back ) throws Exception{
        StringBuffer out=new StringBuffer();
        out.append("<html><head><meta http-equiv=\"Content-Language\" content=\"en-us\"><meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1251\">");
        out.append("\n");
        out.append("<title>"+title+"</title>");
        out.append("\n");
        out.append("</head><body><font face=\"Arial\" size=\""+(htmlTextSize+1)+"\">");
        out.append("\n");
        out.append("<b>"+title+"</b><br><br>");
        out.append("\n");
        out.append("<table border=\"1\" style=\"border-collapse: collapse\" bordercolor=\"#000000\">");
        out.append("\n");
        String cellHeight=" height=\""+htmlCellHight+"\"";
        String cellSize="width=\""+htmlCellWidth+"\""+cellHeight;
        for(int i=0;i<ncol;i++){
            out.append("<tr><td></td>");
            out.append("\n");
            for(int j=0;j<i;j++)out.append("<td></td>");
            out.append("<td "+cellHeight+" colspan=\""+(ncol-i)+"\"><font face=\"Arial\" size=\""+(htmlTextSize+1)+"\"><b>");
            out.append(cols[i]);
            //for(int j=0;j<cols[i].length();j++){
            //    if (j!=0) out.append("<br>");
            //    out.append(cols[i].charAt(j));
            //    }
            out.append("</b></td>");
            out.append("\n");
            out.append("</tr>");
            out.append("\n");
            }
        for(int i=0;i<nrow;i++){
            out.append("<tr>");
            out.append("\n");
            out.append("<td "+cellHeight+"><font face=\"Arial\" size=\""+htmlTextSize+"\"><b>"+rows[i]+"</b></td>");
            out.append("\n");
            for(int j=0;j<ncol;j++){
                out.append("<td "+cellSize+" ");
                if (select[i][j]) out.append("bgcolor=\"#CCCCCC\"");
                out.append("><p align=\"center\"><font face=\"Arial\" size=\""+htmlTextSize+"\"><b>");
                out.append(data[i][j]);
                out.append("</b></td>");
                out.append("\n");
                }
            out.append("</tr>");
            out.append("\n");                
            }
        if (bottom.length()!=0){
            out.append("<tr><td"+cellHeight+"><font face=\"Arial\" size=\""+htmlTextSize+"\"><b>"+bottom+"</b></td></tr>");
            out.append("\n");
            }
        if (bottom2.length()!=0){
            out.append("<tr><td"+cellHeight+"><font face=\"Arial\" size=\""+htmlTextSize+"\"><b>"+bottom2+"</b></td></tr>");
            out.append("\n");
            }
        if (bottoms!=null){
            for(int i=0;i<bottoms.length;i++){
                out.append("<tr><td"+cellHeight+"><font face=\"Arial\" size=\""+htmlTextSize+"\"><b>"+bottoms[i]+"</b></td></tr>");
                out.append("\n");
                }
            }
        out.append("</table>");
        out.append("</body></html>");
        out.append("\n");
        OutputStreamWriter zz=new OutputStreamWriter(new FileOutputStream(dst),"Windows-1251");
        zz.write(out.toString());
        zz.close();
        }     
}
