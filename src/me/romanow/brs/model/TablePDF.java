/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.model;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import me.romanow.brs.interfaces.TableCallBack;

/**
 *
 * @author user
 */
public class TablePDF extends TableData{
    protected  String fontPath="c:/windows/fonts/arial.ttf";
    protected Font titleFont;
    protected Font cellHeaderFont;
    protected Font cellRegularFont;
    protected static int cellHeight=15;
    protected static int cellWidth=20;
    protected static int cellWidthFirst=120;
    protected static int cellWidthLast=30;
    @Override
    public String getExtention(){ return "pdf"; }    
    @Override   
    public void createTable(File dst, TableCallBack back) throws Exception{
        BaseFont russianFont = BaseFont.createFont(fontPath, "cp1251", BaseFont.EMBEDDED);
        titleFont = new Font(russianFont, 12, Font.BOLD);
        cellHeaderFont = new Font(russianFont, 10, Font.NORMAL);
        cellRegularFont = new Font(russianFont, 9, Font.NORMAL);
        Document document = new Document();
        document.setMargins(50, 20, 20, 20);
        PdfWriter.getInstance(document, new FileOutputStream(dst));
        document.open();
        addMetaData(document);
        document.add(new Paragraph(title, titleFont));
        addEmptyLine(document);
        final PdfPTable table = new PdfPTable(ncol+1);
        table.setWidthPercentage(100);
        int ww[]=new int[ncol+1];
        for(int i=0;i<ww.length;i++) ww[i]=cellWidth;
        ww[0]=cellWidthFirst;
        ww[ww.length-1]=cellWidthLast;
        table.setWidths(ww);
        addHeaderCell(table,"", false);
        for(int i=0;i<ncol;i++)
            addHeaderCell(table,cols[i],true);
        for(int i=0;i<nrow;i++){
            addRegularCell(table, rows[i],false);
            for(int j=0;j<ncol;j++){
                addRegularCell(table, data[i][j],select[i][j]);
                }
            }
        document.add(table);
        addEmptyLine(document);
        if (bottom.length()!=0){
            document.add(new Paragraph(bottom, cellRegularFont));
            }
        if (bottom2.length()!=0){
            document.add(new Paragraph(bottom2, cellRegularFont));
            }
        if (bottoms!=null){
            for(int i=0;i<bottoms.length;i++)
                document.add(new Paragraph(bottoms[i], cellRegularFont));
            }
        document.close();
        } 

    private void addHeaderCell(PdfPTable table, String cellText, boolean vertical) {
        PdfPCell cell = new PdfPCell(new Phrase(cellText, cellHeaderFont));
        if (vertical) cell.setRotation(90);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        //cell.setPaddingBottom(3);
        table.addCell(cell);
        }

    protected void addRegularCell(PdfPTable table, String cellText,boolean select) {
        PdfPCell cell = new PdfPCell(new Phrase(cellText, cellRegularFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setFixedHeight(cellHeight);
        //cell.setPaddingBottom(3);
        if (select) cell.setBackgroundColor(new BaseColor(0xe0e0e0));
        table.addCell(cell);
        }

    private void addMetaData(Document document) {
        document.addTitle("Система учета рейтинга");
        document.addSubject("Система учета рейтинга");
        document.addAuthor("Евгений Романов");
        document.addCreator("Евгений Романов");
        }

    private void addEmptyLine(Document document) throws DocumentException {
        document.add(new Paragraph(" "));
        }
}
