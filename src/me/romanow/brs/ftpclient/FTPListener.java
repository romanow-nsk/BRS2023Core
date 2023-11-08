/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.ftpclient;

/**
 *
 * @author user
 */
public interface FTPListener {
    public void onError(String message);
    public void onFinish(String message);
}
