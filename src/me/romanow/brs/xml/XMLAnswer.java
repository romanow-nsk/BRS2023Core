/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.xml;

/**
 *
 * @author user
 */
public class XMLAnswer {
    public int code=noobj;
    public String message="";
    public int id=0;
    public XMLAnswer(){}
    public XMLAnswer(int code){ this.code=code; }
    public XMLAnswer(int code, String message){ this.code=code; this.message=message; }
    public XMLAnswer(String val){ this.message=val; }
    public XMLAnswer(boolean mode){ code=success; }
    public XMLAnswer(boolean mode, int val){ id=val; }
    public final static int success=0;
    public final static int noobj=-1;
}
