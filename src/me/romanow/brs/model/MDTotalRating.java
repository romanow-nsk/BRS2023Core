
package me.romanow.brs.model;

import me.romanow.brs.xml.XMLBoolean;
import me.romanow.brs.xml.XMLDouble;

//------------------ формат полного рейтинга для вывода --------------------
public class MDTotalRating {
    public MDStudentVector studentList=null;
    public MDCellVector cellList=null;
    public XMLDouble ball[]=null;
    public XMLBoolean sel[]=null;
    public XMLDouble sums[]=null;
    public XMLDouble sumc[]=null;
}
