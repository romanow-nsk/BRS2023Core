/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.interfaces;

import me.romanow.brs.model.TableData;

/**
 *
 * @author user
 */
public interface TableCallBack {
    public void rowSelected(int row);
    public void colSelected(int col);
    public void cellSelected(int row, int col);
    public void onClose();
}
