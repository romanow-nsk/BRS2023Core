/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.model;

import me.romanow.brs.database.DBEvent;
import me.romanow.brs.xml.XMLBoolean;
import me.romanow.brs.xml.XMLInt;

/**
 *
 * @author user
 */
//------------------ формат полного рейтинга для вывода --------------------
public class MDPropuskRating {
    public MDStudentVector studentList=null;
    public MDEventVector eventList=null;
    public XMLBoolean val[]=null;
    public XMLBoolean sel[]=null;
    public XMLInt cnts[]=null;
    public XMLInt cntc[]=null;
}
