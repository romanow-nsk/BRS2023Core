/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.ciu;

import com.thoughtworks.xstream.XStream;
import me.romanow.brs.database.DBProfile;
import me.romanow.brs.interfaces.BRSException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class CIUUniConnection {
    private XStream pars=new XStream();
    private String servletDirectiry = "BRSWeb";
    private String servletName="MDXMLCommand";
    private Proxy connectionProxy = Proxy.NO_PROXY;
    private int connectionTimeoutMillis	= 10*1000;
    private int readTimeoutMillis = 10*1000*20;     //?????????? 2
    private HttpsURLConnection conn=null;
    private int state=0;
    private OutputStreamWriter wr=null;
    private InputStreamReader rd=null;
    private InputStream is=null;
    private boolean binary=false;
    private static String CIU1="https://login.nstu.ru/ssoservice/json/authenticate";
    private static String CIUStudent="https://api.ciu.nstu.ru/v1.0/data/simple/student";
    private static String CIUPerson="https://api.ciu.nstu.ru/v1.0/data/simple/staff_inf";
    private static String CIUMail="@corp.nstu.ru";

    public String getToken(String user, String pass, DBProfile profile) throws Throwable{
        String zz= null;
	try {
            URL theURL=new URL(CIU1);
            if (profile!=null && profile.isProxyOn()){
                Proxy httpProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(profile.getProxyIP(), profile.getProxyPort()));
                conn=(HttpsURLConnection)theURL.openConnection(httpProxy);
                }
            else
                conn=(HttpsURLConnection)theURL.openConnection();
            conn.setConnectTimeout(connectionTimeoutMillis);
            conn.setReadTimeout(readTimeoutMillis);
            conn.setAllowUserInteraction(false);
            conn.setDefaultUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("X-OpenAM-Username",user+CIUMail);
            conn.setRequestProperty("X-OpenAM-Password",pass);
            conn.connect();
            int status = conn.getResponseCode();
            rd=new InputStreamReader(conn.getInputStream(),"Cp1251");
            int k=0;
            String ss="";
            while((k=rd.read())!=-1) ss+=(char)k;
            System.out.println(ss);
            conn.disconnect();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode nd = mapper.readTree(ss);
            zz = nd.get("tokenId").toString();
            System.out.println(zz);
            } catch(Throwable ee){
                throw new BRSException(ee);
                }
        return zz;
       	}

    public CIUPersonList getPerson(String token, DBProfile profile) throws Throwable{
        CIUPerson list[]= new CIUPerson[0];
        try {
            URL theURL=new URL(CIUPerson);
            if (profile!=null && profile.isProxyOn()){
                Proxy httpProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(profile.getProxyIP(), profile.getProxyPort()));
                conn=(HttpsURLConnection)theURL.openConnection(httpProxy);
            }
            else
                conn=(HttpsURLConnection)theURL.openConnection();
            conn.setConnectTimeout(connectionTimeoutMillis);
            conn.setReadTimeout(readTimeoutMillis);
            conn.setAllowUserInteraction(false);
            conn.setDefaultUseCaches(false);
            conn.setDoInput(true);
            //В Android НАДО УБРАТЬ !!!!!!!!!!!!!!!!!!
            //conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("Cookie","NstuSsoToken="+token);
            conn.connect();
            System.out.println(conn.usingProxy());
            int status = conn.getResponseCode();
            rd=new InputStreamReader(conn.getInputStream(),"Cp1251");
            int k=0;
            String ss="";
            while((k=rd.read())!=-1) ss+=(char)k;
            System.out.println(ss);
            conn.disconnect();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode nd = mapper.readTree(ss);
            list = new CIUPerson[nd.size()];
            for(int i=0;i<nd.size();i++)
                list[i]= mapper.readValue(nd.get(i),CIUPerson.class);
            for(int i=0;i<list.length;i++)
                System.out.println(list[i]);
        } catch(Throwable ee){
            throw new BRSException(ee);
            }
        return new CIUPersonList(list);
    }


    public CIUUniConnection(){}
    public static void main(String argv[]){
        try {
            CIUUniConnection ciu =  new CIUUniConnection();
            String zz =ciu.getToken("xxx","xxx",null);
            System.out.println(zz);
            CIUPersonList zz2 = ciu.getPerson(zz,null);
            System.out.println(zz2);
            } catch(Throwable ee){
                System.out.println(ee.getMessage());
                }
        }
}
