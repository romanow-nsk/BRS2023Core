/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.ciu;

import com.thoughtworks.xstream.XStream;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import me.romanow.brs.Values;
import me.romanow.brs.database.DBProfile;
import me.romanow.brs.interfaces.BRSException;

public class CIUConnection {
    private XStream pars=new XStream();
    private String servletDirectiry="BRSWeb";
    private String servletName="MDXMLCommand";
    private Proxy connectionProxy = Proxy.NO_PROXY;
    private int connectionTimeoutMillis	= 10*1000;
    private int readTimeoutMillis = 10*1000*20;     //?????????? 2
    private HttpURLConnection conn=null;
    private int state=0;
    private OutputStreamWriter wr=null;
    private InputStreamReader rd=null;
    private InputStream is=null;
    private boolean binary=false;
    
    
    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());     
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
                }
            return sb.toString();
            } catch (java.security.NoSuchAlgorithmException e) {
        }
    return null;
    }
    
    //---------------------------------------------------------------------------   
    public Object get(String user, String pass) throws Throwable{
        return get(Values.dbNstuCiuDir,user,pass,null,null);
        }
    public Object get(String dir, String user, String pass, String res, DBProfile profile) throws Throwable{
        Object oo=null;
        String url=Values.dbNstuCiuIP+dir;
        String param="login="+user+"&password="+MD5(pass);
        if (res!=null) param+="&"+res;
	try {
            URL theURL=new URL(url+"?"+param);
            //------------------------------------------2
            //Properties props = System.getProperties();
            //props.put("http.proxyPort","8080"); 
            //props.put("http.proxyHost","217.71.138.1"); 
            //props.put("http.proxySet", "true");
            //-------------------------------------------1
            if (profile!=null && profile.isProxyOn()){
                Proxy httpProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(profile.getProxyIP(), profile.getProxyPort()));
                conn=(HttpURLConnection)theURL.openConnection(httpProxy);
                }
            else
                conn=(HttpURLConnection)theURL.openConnection();
            conn.setConnectTimeout(connectionTimeoutMillis);
            conn.setReadTimeout(readTimeoutMillis);
            conn.setAllowUserInteraction(false);
            conn.setDefaultUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/html;charset=Windows-1251");
            conn.addRequestProperty("User-Agent", "Opera");
            conn.addRequestProperty("Referer", "ditest.edu.nstu.ru");
            conn.setRequestProperty("Content-Length", ""+param.length());
            conn.connect();
            System.out.println(conn.usingProxy());
            rd=new InputStreamReader(conn.getInputStream(),"Cp1251");
            pars.alias("teacher",CIUTeacher.class);
            pars.alias("student",CIUStudent.class);
            pars.alias("error",CIUError.class);
            pars.alias("kaf",CIUKafedra.class);
            pars.useAttributeFor(CIUKafedra.class, "id");
            pars.useAttributeFor(CIUKafedra.class, "dolz");
            int k=0;
            String ss="";
            while((k=rd.read())!=-1) ss+=(char)k;
            System.out.print(ss);
            conn.disconnect();
            if (ss.indexOf("<")!=-1){
                oo=pars.fromXML(ss);
                if (oo instanceof CIUError){
                    ss = ss.replace("<error>", "").replace("</error>", "");
                    throw new BRSException(BRSException.msg,ss);
                    }
                }
            else 
                throw new BRSException(BRSException.msg,ss);
            } catch(Throwable ee){ throw new BRSException(ee); } 
        return oo;
       	}


    public CIUConnection(){}
    public static void main(String argv[]){
        try {
            CIUConnection ciu =  new CIUConnection();
            Object oo=ciu.get(Values.dbNstuCiuDir,"romanov@corp.nstu.ru","Longlivernr123",null,null);
            //Object oo=(new CIUConnection()).get(Values.dbNstuCiuDir,"am809_popov","628mgs",null,null);
            System.out.println(oo);
            //Object oo=(new CIUConnection()).get("/isu/xml_export","vt_romanov","598kuh","name=chair_ido");
            //System.out.println(oo);
            //System.out.println(URLDecoder.decode(URLEncoder.encode("Иванов & 12 letters", "Cp1251"),"Cp1251"));
            } catch(Throwable ee){ 
                System.out.println(ee.getMessage());
                }
        }
}
