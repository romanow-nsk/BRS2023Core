/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.xml;

import me.romanow.brs.database.DBNote;
import me.romanow.brs.model.MDNote;
import com.thoughtworks.xstream.XStream;
import me.romanow.brs.database.*;
import me.romanow.brs.model.*;

/**
 *
 * @author user
 */
public class XMLParser extends XStream{
    public XMLParser(){
        super();
        //---- Включить значение поля в атрибуты тега --------------
        alias("list", XMLArray.class);
        alias("int", XMLInt.class);
        alias("string", XMLString.class);
        alias("bool", XMLBoolean.class);
        alias("real", XMLDouble.class);
        alias("cmd", XMLCmd.class);
        alias("ans", XMLAnswer.class);
        alias("rating", DBRating.class);
        alias("mrating", MDRating.class);
        alias("tutor", DBTutor.class);
        alias("course", MDCourse.class);
        alias("student", MDStudent.class);
        alias("event", MDEvent.class);
        alias("cell", MDCell.class);
        alias("note2", MDNote.class);
        alias("note", DBNote.class);
        alias("var", DBVariant.class);
        alias("doc", DBDocFile.class);
        alias("arch", DBArchFile.class);
        alias("prop", DBPropusk.class);
        alias("prating", MDPropuskRating.class);
        alias("srating", MDStudentRating.class);
        alias("crating", MDCellRating.class);
        alias("trating", MDTotalRating.class);
        useAttributeFor(DBItem.class, "id");
        useAttributeFor(DBItem.class, "mark");
        useAttributeFor(DBItem.class, "renew");
        useAttributeFor(DBNamedItem.class, "name");
        useAttributeFor(XMLInt.class, "val");
        useAttributeFor(XMLString.class, "val");
        useAttributeFor(XMLBoolean.class, "val");
        useAttributeFor(XMLDouble.class, "val");
        useAttributeFor(XMLCmd.class, "code");
        useAttributeFor(XMLCmd.class, "id");
        useAttributeFor(XMLCmd.class, "id2");
        useAttributeFor(XMLCmd.class, "id3");
        useAttributeFor(XMLCmd.class, "id4");
        useAttributeFor(XMLCmd.class, "str");
        useAttributeFor(XMLAnswer.class, "code");
        useAttributeFor(XMLAnswer.class, "message");
        useAttributeFor(XMLAnswer.class, "id");
        useAttributeFor(DBVariant.class, "variant");
        useAttributeFor(DBVariant.class, "cDate");
        useAttributeFor(DBDocFile.class, "fileName");
        useAttributeFor(DBDocFile.class, "fileId");
        useAttributeFor(DBDocFile.class, "cDate");
        useAttributeFor(DBArchFile.class, "fileName");
        useAttributeFor(DBArchFile.class, "fileId");
        useAttributeFor(DBArchFile.class, "cDate");
        useAttributeFor(MDNote.class, "week");
        useAttributeFor(MDNote.class, "ball");
        useAttributeFor(MDNote.class, "params");
        useAttributeFor(MDNote.class, "cDate");
        useAttributeFor(MDNote.class, "removed");
        useAttributeFor(MDNote.class, "docFile");
        useAttributeFor(MDNote.class, "archFile");
        useAttributeFor(MDNote.class, "variant");
        useAttributeFor(MDNote.class, "idDoc");
        useAttributeFor(MDNote.class, "idArch");
        useAttributeFor(DBPropusk.class, "idStudent");
        useAttributeFor(DBPropusk.class, "idEvent");
        useAttributeFor(DBPropusk.class, "cDate");
        useAttributeFor(DBPropusk.class, "removed");
        useAttributeFor(DBStudent.class, "idGroups");
        useAttributeFor(DBStudent.class, "off");
        useAttributeFor(MDCell.class, "idCourse");
        useAttributeFor(MDCell.class, "cType");
        useAttributeFor(MDCell.class, "ball");
        useAttributeFor(MDCell.class, "ordNum");
        useAttributeFor(MDCell.class, "week");
        useAttributeFor(MDCell.class, "week2");
        useAttributeFor(MDCell.class, "cDate");
        useAttributeFor(MDStudent.class, "brigade");
        useAttributeFor(MDStudent.class, "cDate");
        useAttributeFor(MDStudent.class, "second");       
        useAttributeFor(MDStudent.class, "pass");     
        useAttributeFor(MDStudent.class, "ciuLogin");     
        useAttributeFor(DBEvent.class, "idRating");
        useAttributeFor(DBEvent.class, "evtDate");
        useAttributeFor(DBEvent.class, "removed");
        useAttributeFor(DBStudentCellItem.class, "idRating");
        useAttributeFor(DBStudentCellItem.class, "idStudent");
        useAttributeFor(DBStudentCellItem.class, "idCell");
        useAttributeFor(DBRating.class, "idCourse");
        useAttributeFor(DBRating.class, "idGroups");
        useAttributeFor(DBRating.class, "idParams");
        useAttributeFor(DBRating.class, "second");
        useAttributeFor(DBParams.class, "maxWeek");
        useAttributeFor(DBParams.class, "weekProc");
        useAttributeFor(DBParams.class, "propuskBall");
        useAttributeFor(DBParams.class, "plusProc");
        useAttributeFor(DBParams.class, "semestr");
        useAttributeFor(DBParams.class, "idTutor");
        useAttributeFor(DBTutor.class, "ciuName");
        useAttributeFor(DBUser.class, "pass");
        aliasAttribute(DBNote.class, "cDate","date");
        aliasAttribute(DBNote.class, "removed","rem");
        aliasAttribute(DBNote.class, "params","par");
        aliasAttribute(DBVariant.class, "variant","var");
        aliasAttribute(DBVariant.class, "cDate","date");
        aliasAttribute(DBDocFile.class, "fileName","name");
        aliasAttribute(DBDocFile.class, "fileId","fid");
        aliasAttribute(DBDocFile.class, "cDate","date");
        aliasAttribute(DBArchFile.class, "fileName","name");
        aliasAttribute(DBArchFile.class, "fileId","fid");
        aliasAttribute(DBArchFile.class, "cDate","date");
        aliasAttribute(MDNote.class, "docFile","doc");
        aliasAttribute(MDNote.class, "archFile","arch");
        aliasAttribute(MDNote.class, "idDoc","idd");
        aliasAttribute(MDNote.class, "idArch","ida");
        aliasAttribute(MDNote.class, "variant","var");
        aliasAttribute(DBPropusk.class, "idStudent","ids");
        aliasAttribute(DBPropusk.class, "idEvent","ide");
        aliasAttribute(DBPropusk.class, "removed","rem");
        aliasAttribute(DBPropusk.class, "cDate","date");
        aliasAttribute(DBStudent.class, "idGroups","idg");
        aliasAttribute(MDCell.class, "idCourse","idc");
        aliasAttribute(MDCell.class, "ordNum","ord");
        aliasAttribute(MDCell.class, "cDate","date");
        aliasAttribute(MDStudent.class, "brigade","brig");
        aliasAttribute(MDStudent.class, "second","sec");
        aliasAttribute(MDStudent.class, "cDate","date");     
        aliasAttribute(MDStudent.class, "ciuLogin","ciu");     
        aliasAttribute(DBEvent.class, "idRating","idr");
        aliasAttribute(DBEvent.class, "evtDate","date");
        aliasAttribute(DBEvent.class, "removed","rem");
        aliasAttribute(DBStudentCellItem.class, "idRating","idr");
        aliasAttribute(DBStudentCellItem.class, "idStudent","ids");
        aliasAttribute(DBStudentCellItem.class, "idCell","idu");
        aliasAttribute(DBRating.class, "idCourse","idc");
        aliasAttribute(DBRating.class, "idGroups","idg");
        aliasAttribute(DBRating.class, "idParams","idp");
        aliasAttribute(DBRating.class, "second","sec");
        //---- Исключить имя массива из XML ------------------------
        addImplicitCollection(XMLArray.class, "list");
        //---- Исключить имя ВЕКТОРА из XML ------------------------
        //addImplicitArray(DBCourseList.class, "vector");
        //----------------------------------------------------------------------
    }
    public static void main(String argv[]){
        XMLParser pars = new XMLParser();
        }
}
