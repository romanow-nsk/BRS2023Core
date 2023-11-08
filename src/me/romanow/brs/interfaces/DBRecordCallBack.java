/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.interfaces;

import java.sql.ResultSet;

/**
 *
 * @author user
 */
public interface DBRecordCallBack {
    public void procRecord(ResultSet rs);
}
