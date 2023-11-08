/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.model;

/**
 *
 * @author user
 */
public class MDStudentVector extends MDItemVector{
    public MDStudent get(int i){ return (MDStudent)super.get(i); }
    public MDStudentVector(MDStudent src[]){ super(src); }
    public MDStudentVector(){ super(); }
}
