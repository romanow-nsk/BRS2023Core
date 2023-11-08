/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.ciu;

/**
<teacher>
<id>1395</id>
<f>ИВАНОВ</f>
<n>АНАТОЛИЙ</n>
<o>ВАСИЛЬЕВИЧ</o>
<kaf id="1205" dolz="Профессор">Автоматики</kaf>
<unique_id>10096</unique_id>
</teacher>

 */
public class CIUTeacher {
    public int id=0;
    public String f="";
    public String n="";
    public String o="";
    public int unique_id=0;
    public CIUKafedra kaf=null;
    public String toString(){ return f+" "+n+" "+o+" "+unique_id+"/"+id; }
}
