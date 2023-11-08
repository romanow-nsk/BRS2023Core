/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.controller;

import me.romanow.brs.model.MDBaseUser;
import me.romanow.brs.model.TableData;

/**
 *
 * @author user
 */
public interface ViewControllerListener {
    public String getInputFileName(String title, String defName) throws Throwable;
    public String getOutputFileName(String title, String defName) throws Throwable;
    public void fatal(Throwable ee);
    public void stateChanged(int state);
    public void setRatingVisible(boolean enb);
    public void setCellVisible(boolean enb);
    public void setStudentVisible(boolean enb);
    public void setNoteVisible(boolean enb);
    public void setWeekVisible(boolean enb, String s);
    public void setWeek2Visible(boolean enb, String s);
    public void setBrigadeVisible(boolean enb, String s, boolean enb2, boolean two);
    public void setArchFileVisible(boolean enb);
    public void setDocFileVisible(boolean enb);
    public void setMaxBall(String var);
    public void setBall(String var,boolean editable);
    public void setSumBall(String var);
    public void setVariant(String var);
    public void setWeek0(String var);                   //=0 - не отображать
    public void setWeek(String var,boolean editable);   //=0 - не отображать
    public void setBrigade(String var);
    public void setParamsVisible(boolean enb,String var, int par);
    public void selectTableCell(TableData tbl,int row, int col);
    public void onTableClose(TableData tbl);
    public void ratingIsChanged();
    public void setMessage(String message, int state);
    public String getFileDirectory();
    public void lastCallBack();
}
