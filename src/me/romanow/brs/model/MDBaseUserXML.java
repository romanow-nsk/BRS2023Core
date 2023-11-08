/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.romanow.brs.model;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import me.romanow.brs.database.*;
import me.romanow.brs.interfaces.*;
import me.romanow.brs.xml.XMLAnswer;
import me.romanow.brs.xml.XMLArray;
import me.romanow.brs.xml.XMLCmd;
import me.romanow.brs.xml.XMLParser;

public class MDBaseUserXML extends MDBaseUser{
    private XMLParser pars=new XMLParser();
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
    private XMLAnswer ans=null;
    private boolean binary=false;
    //---------------------------------------------------------------------------   
    private void httpConnect() throws Throwable{
        httpConnect(null);
        }
    private void httpConnect(String req) throws Throwable{
        state=0;
        StringBuffer sb=new StringBuffer();
        String url="http://"+entry.getIP()+":"+entry.getPort()+"/"+servletDirectiry+"/"+servletName;
        if (req!=null) url+="?"+req;
	try {
            URL theURL=new URL(url);
            if (entry!=null && entry.isProxyOn()){
                Proxy httpProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(entry.getProxyIP(), entry.getProxyPort()));
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
            //conn.setRequestProperty("Content-Type", "application/BRS");
            conn.setRequestProperty("Content-Type", "text/html;charset=Windows-1251");
            //conn.setRequestProperty("Content-Type", "text/html;charset=UTF-8");
            //----- Устанавливается, если приемнику это ЯВНО требуется -----------------
            // Если есть договоренность по формату - то не надо !!!!!!!
            // По сети передаются байты файла без перекодировки, только при приеме - в ЮНИКОД
            // Окончание файла = по закрытию потока !!!!!!!!!!!!!!!!!!!
            // connect.setRequestProperty("Content-Length",""+fname.length());
            conn.connect();
            //dataOS = new PrintWriter(conn.getOutputStream());
            //dataOS = new DataOutputStream(conn.getOutputStream());
            state=1;
            } catch(Exception ee){ throw new BRSException(BRSException.net,ee.getMessage()); } 
       	}
    @Override
    public void connect() throws Throwable {
        httpConnect();
        conn.disconnect();    
        }
    @Override
    public void reconnect() throws Throwable {
        httpConnect();
        conn.disconnect();    
        }
    @Override
    public void close() throws Throwable {
        state=0;
        }
    @Override
    public boolean isConnected() throws Throwable {
        return state!=0;
        }
    //--------------------------------------------------------------------------------------
    private Object sendCmd(int code, int id, int id2, int id3, int id4, String val) throws Throwable{
        return sendCmd(code,id,id2,id3,id4,val,null);
        }
    private Object sendCmd(int code, int id, int id2, int id3, int id4) throws Throwable{
        return sendCmd(code,id,id2,id3,id4,"",null);
        }
    private Object sendCmd(int code, int id, int id2, int id3, String ss) throws Throwable{
        return sendCmd(code,id,id2,id3,0,ss,null);
        }
    private Object sendCmd(int code, int id, int id2, int id3) throws Throwable{
        return sendCmd(code,id,id2,id3,0,"",null);
        }
    private Object sendCmd(int code, int id, int id2) throws Throwable{
        return sendCmd(code,id,id2,0,0,"",null);
        }
    private Object sendCmd(int code, int id) throws Throwable{
        return sendCmd(code,id,0,0,0,"",null);
        }
    private Object sendCmd(int code) throws Throwable{
        return sendCmd(code,0,0,0,0,"",null);
        }
    private Object sendCmd(int code, int id, int id2, int id3, int id4, String vv, DBItem obj) throws Throwable{
        Object res=null;
        try {
            if (binary){    // Двоичный поток туда-обратно
                httpConnect();
                XMLCmd cmd=new XMLCmd(code,id,id2,id3,id4,vv);
                DataOutputStream out=new DataOutputStream(conn.getOutputStream());
                out.writeUTF(pars.toXML(cmd));
                if (obj!=null) {
                    String ss=pars.toXML(obj);
                    out.writeUTF(pars.toXML(obj));
                    }
                out.flush();
                is=conn.getInputStream();
                //rd=new InputStreamReader(is,"Cp1251");
                DataInputStream dis=new DataInputStream(is);
                ans=(XMLAnswer)pars.fromXML(dis.readUTF());
                if (ans.code>0) throw new BRSException(BRSException.serv,ans.message);
                if (ans.code==XMLAnswer.noobj) res=null;
                else{
                    int sz=dis.readInt();
                    char cc[]=new char[sz];
                    for(int i=0;i<sz;i++) cc[i]=dis.readChar();
                    String ss=new String(cc);
                    System.out.println("Client:"+ss);
                    res=pars.fromXML(ss);
                    if (res instanceof XMLAnswer)
                        throw new BRSException(BRSException.serv,((XMLAnswer)res).message);
                    }
                }
            else{
                String req="code="+code;
                if (id!=0) req+="&id="+id;
                if (id2!=0) req+="&id2="+id2; 
                if (id3!=0) req+="&id3="+id3; 
                if (id3!=0) req+="&id4="+id4; 
                if (vv!=null && vv.length()!=0){
                    vv=URLEncoder.encode(vv, "Cp1251");
                    req+="&str="+vv;
                    } 
                if (obj!=null) req+=obj.saveURLParams();
                System.out.println(req);
                httpConnect(req);
                wr=new OutputStreamWriter(conn.getOutputStream(),"Cp1251");
                wr.flush();
                is=conn.getInputStream();
                rd=new InputStreamReader(is,"Cp1251");
                ByteArrayOutputStream bb=new ByteArrayOutputStream();
                int nn=0;
                String hh="";
                //--------- Прочитать СТРОКУ ответа, т.к. парсер из потока ЖРЕТ ВСЕ - свое и чужое --------
                while((nn=rd.read())!='\n' && nn!=-1)  
                    hh+=(char)nn;
                System.out.println(hh);
                if (nn==-1) throw new BRSException(BRSException.bug,"Обрыв команды:"+hh);
                //ByteArrayInputStream buf=new ByteArrayInputStream(tmp);
                ans=(XMLAnswer)pars.fromXML(hh);
                if (ans.code>0) throw new BRSException(BRSException.serv,ans.message);
                if (ans.code==XMLAnswer.noobj) res=null;
                else{
                    res=pars.fromXML(rd);
                    if (res instanceof XMLAnswer)
                        throw new BRSException(BRSException.serv,((XMLAnswer)res).message);
                    }
                }
            } catch(Throwable ee){ 
                conn.disconnect();                   
                throw new BRSException(ee);
                }
            conn.disconnect();
            return res;
        }

    public MDBaseUserXML(DBTutor tutor, DBProfile profile){ 
        super(tutor,profile);
        state=0;
        }
    //--------------------------------------------------------------------------
    @Override
    public void testEdit() throws Throwable{
        editEnabled=false;
        if (tutor==null || rating==null) return;
        sendCmd(XMLCmd.testEdit,rating.getId(),tutor.getId());
        editEnabled=(ans.id==2);
        }
    @Override
    public void loadRating(int id) throws Throwable{
        rating=(MDRating)sendCmd(XMLCmd.loadRating,id);
        }
    @Override
    public void loadFullRating(int id) throws Throwable {
        rating=(MDRating)sendCmd(XMLCmd.loadFullRating,id);
    }
    @Override
    public void loadStudentRating(int id, int sid) throws Throwable {
        rating=(MDRating)sendCmd(XMLCmd.loadStudentRating,id,sid);
    }

    @Override
    public void loadCellRating(int id, int cid) throws Throwable {
        rating=(MDRating)sendCmd(XMLCmd.loadCellRating,id,cid);
    }

    //--------------------------------------------------------------------------------------------
    @Override
    public MDNote getNote() throws Throwable{
        if (!testStudentCell()) return null;
        return (MDNote)sendCmd(XMLCmd.getNote,rating.getId(),studentItem.getId(),cellItem.getId());
        }
    
    @Override
    public DBItem []getNoteHistory() throws Throwable{
        if (!testStudentCell()) return new DBItem[0];
        return ((XMLArray)sendCmd(XMLCmd.getNoteHistory,rating.getId(),studentItem.getId(),cellItem.getId(),null)).list;
    	}
    
    @Override    
    public void deleteEvent() throws Throwable{
        if (rating==null || eventItem==null) return;
        sendCmd(XMLCmd.deleteEvent,rating.getId(),eventItem.getId());
        loadEventList();
        }
    @Override
    public int insertEvent(String ss,int cdate) throws Throwable{
        if (rating==null) return 0;
        sendCmd(XMLCmd.insertEvent,rating.getId(),cdate,0,ss);
        loadEventList();
        return ans.id;
        }
    @Override
    public String testUser(DBTutor item, boolean ciuLogin) throws Throwable{
        sendCmd(XMLCmd.testUser,item.getId(),ciuLogin?1:0,0,0,"",item);
        return ans.message;
        }
    @Override
    public void getRatingList(int id, int mode) throws Throwable {
        ratingList=((XMLArray)sendCmd(XMLCmd.getRatingList,id,mode)).list;
        }
    @Override
    public double calcStudentRating() throws Throwable {
        if (rating==null || studentItem==null) return 0;
        sendCmd(XMLCmd.calcStudentRating,rating.getId(),studentItem.getId());
        return ans.id;
        }

    @Override
    public MDEvent getEvent(boolean full) throws Throwable {
        if (rating==null || eventItem==null) return null;
        return (MDEvent)sendCmd(XMLCmd.getEvent,rating.getId(),eventItem.getId(),full ? 1:0);
        }

    @Override
    public void changeEvent(int studId, boolean prop) throws Throwable {
        if (rating==null || eventItem==null) return;
        sendCmd(XMLCmd.changeEvent,rating.getId(),eventItem.getId(),studId, prop ? 1:0);
    }

    @Override
    public void changeNote(MDNote note) throws Throwable {
        if (!testStudentCell()) return;
        sendCmd(XMLCmd.changeNote,rating.getId(),studentItem.getId(),cellItem.getId(),0,"",note);
    }

    @Override
    public void changeVariant(String value) throws Throwable {
        if (!testStudentCell()) return;
        sendCmd(XMLCmd.changeVariant,rating.getId(),studentItem.getId(),cellItem.getId(),value);
    }

    @Override
    public void changeFile(String value,int id, boolean full, boolean doc) throws Throwable {
        if (!testStudentCell()) return;
        int cmd=doc ? XMLCmd.changeDocFile : XMLCmd.changeArchFile;
        sendCmd(cmd,rating.getId(),studentItem.getId(),cellItem.getId(), id, value);
        }

    @Override
    public void changeBrigade(int val, boolean val2) throws Throwable {
        if (rating==null || studentItem==null) return;
        sendCmd(XMLCmd.changeBrigade,rating.getId(),studentItem.getId(),val, val2 ? 1:0);
        loadRating(rating.getId());
        }

    @Override
    public void changeWeek(int val, int val2) throws Throwable {
        if (rating==null || cellItem==null) return;
        sendCmd(XMLCmd.changeWeek,rating.getId(),cellItem.getId(),val,val2);
        loadRating(rating.getId());
        }

    @Override
    public void loadStudentList() throws Throwable {
        if (rating==null) return;
        rating.groups=(MDGroups)sendCmd(XMLCmd.loadStudentList,rating.getId());
        }

    @Override
    public void loadCellList() throws Throwable {
        if (rating==null) return;
        rating.course=(MDCourse)sendCmd(XMLCmd.loadCellList,rating.getId());
        }

    @Override
    public void loadEventList() throws Throwable {
        if (rating==null) return;
        XMLArray xx=(XMLArray)sendCmd(XMLCmd.loadEventList,rating.getId());
        rating.events.clear();
        for(int i=0;i<xx.list.length;i++)
            rating.events.add((DBEvent)xx.list[i]);
        }
    @Override
    public int writeFile(File ff, boolean doc, int cdate)throws Throwable {
        if (binary) return writeFileBinary(ff,doc);
        else return writeFileText(ff,doc);
        }
    private int writeFileBinary(File ff, boolean doc)throws Throwable {
        FileInputStream in=null;
        if (!testStudentCell()) return 0;
        int lnt=(int)ff.length();
        httpConnect();
        try {
            XMLCmd cmd=new XMLCmd(XMLCmd.writeFile,lnt,doc?1:0);
            DataOutputStream out=new DataOutputStream(conn.getOutputStream());
            out.writeUTF(pars.toXML(cmd));
            out.flush();
            String ss=ff.getPath();
            in=new FileInputStream(ss);
            for(int i=0;i<lnt;i++) out.write(in.read());
            out.flush();
            DataInputStream is=new DataInputStream(conn.getInputStream());
            ans=(XMLAnswer)pars.fromXML(is.readUTF());
            if (ans.code!=XMLAnswer.noobj) throw new BRSException(BRSException.serv,ans.message);
            } catch(Throwable ee){ 
                conn.disconnect();                   
                if (in!=null) in.close();
                throw new BRSException(ee);
                }
        conn.disconnect();
        return ans.id;
        }

    private int writeFileText(File ff, boolean doc)throws Throwable {
        FileInputStream in=null;
        if (!testStudentCell()) return 0;
        int lnt=(int)ff.length();
        try {
            String req="code="+XMLCmd.writeFile+"&id="+lnt+"&id2="+(doc?"1":"0");
            System.out.println(req);
            httpConnect(req);
            OutputStream out=conn.getOutputStream();
            String ss=ff.getPath();
            in=new FileInputStream(ss);
            for(int i=0;i<lnt;i++) out.write(in.read());
            out.flush();
            rd=new InputStreamReader(conn.getInputStream(),"Cp1251");
            ans=(XMLAnswer)pars.fromXML(rd);
            if (ans.code!=XMLAnswer.noobj) throw new BRSException(BRSException.serv,ans.message);
            } catch(Throwable ee){ 
                conn.disconnect();                   
                if (in!=null) in.close();
                throw new BRSException(ee);
                }
        conn.disconnect();
        return ans.id;
        }
    @Override
    public void readFile(File ff, int id, boolean doc, int cdate)throws Throwable {
        if (binary) readFileBinary(ff, id, doc);
        else readFileText(ff, id, doc);
        }
    private void readFileBinary(File ff, int id,boolean doc)throws Throwable {
        FileOutputStream out2=null;
        if (!testStudentCell()) return;
        httpConnect();
        try {
            XMLCmd cmd=new XMLCmd(XMLCmd.readFile,id, doc?1:0);
            DataOutputStream out=new DataOutputStream(conn.getOutputStream());
            out.writeUTF(pars.toXML(cmd));
            out.flush();
            DataInputStream is=new DataInputStream(conn.getInputStream());
            XMLAnswer ans=(XMLAnswer)pars.fromXML(is.readUTF());
            if (ans.code!=XMLAnswer.noobj) throw new BRSException(BRSException.serv,ans.message);
            System.out.println("Client:"+ans.id);
            int lnt=ans.id;
            String ss=ff.getPath();
            out2=new FileOutputStream(ss);
            while(lnt--!=0){
                int vv=is.read();
                if (vv==-1) throw new BRSException(BRSException.net,"Короткий файл");
                out2.write(vv);
                }
            out2.close();
            } catch(Throwable ee){ 
                conn.disconnect();                   
                if (out2!=null) out2.close();
                throw new BRSException(ee);
                }
        conn.disconnect();
        }

    private void readFileText(File ff, int id,boolean doc)throws Throwable {
        FileOutputStream out2=null;
        if (!testStudentCell()) return;
        try {
            String req="code="+XMLCmd.readFile+"&id="+id+"&id2="+(doc?"1":"0");
            System.out.println(req);
            httpConnect(req);
            OutputStream os=conn.getOutputStream();
            os.flush();
            String ss=ff.getPath();
            is=conn.getInputStream();
            int nn=0;
            String hh="";
            //--------- Прочитать СТРОКУ ответа, т.к. парсер из потока ЖРЕТ ВСЕ - свое и чужое --------
            // ИЗ ДВУХ ПОТОКОВ ОДНОВРЕМЕННО ЧИТАТЬ НЕЛЬЗЯ (буферизует????)
            ByteArrayOutputStream bb=new ByteArrayOutputStream();
            while((nn=is.read())!='\n' && nn!=-1)  
                bb.write(nn);
            hh=new String(bb.toByteArray(),"Cp1251");
            System.out.println(hh);
            if (nn==-1) throw new BRSException(BRSException.bug,"Обрыв команды:"+hh);;
            ans=(XMLAnswer)pars.fromXML(hh);
            if (ans.code!=XMLAnswer.noobj) throw new BRSException(BRSException.serv,ans.message);
            System.out.println("Client:"+ans.id);
            int lnt=ans.id;
            out2=new FileOutputStream(ss);
            while(lnt--!=0){
                int vv=is.read();
                if (vv==-1) throw new BRSException(BRSException.net,"Короткий файл");
                out2.write(vv);
                }
            out2.close();
            } catch(Throwable ee){ 
                conn.disconnect();                   
                if (out2!=null) out2.close();
                throw new BRSException(ee);
                }
        conn.disconnect();
        }
    
    //--------------------------------------------------------------------------
    //@Override
    //public int insert(DBItem item) throws Throwable {
    //    sendCmd(XMLCmd.insert,0,0,0,0,item.getClass().getSimpleName(),item);
    //    return ans.id;
    //    }
    
    @Override
    public void delete(Class table, int id) throws Throwable {
        sendCmd(XMLCmd.delete,id,0,0,table.getName());
        }

    @Override
    public DBItem[] getTutorList() throws Throwable {
        return ((XMLArray)sendCmd(XMLCmd.getTutorList)).list;
        }

    @Override
    public DBItem[] getGroupList() throws Throwable {
        return ((XMLArray)sendCmd(XMLCmd.getGroupsList)).list;
        }

    @Override
    public DBItem[] getStudentList(int groupId) throws Throwable {
        return ((XMLArray)sendCmd(XMLCmd.getStudentList,groupId)).list;
        }

    @Override
    public String testStudent(DBStudent item, boolean ciuLogin) throws Throwable {
        sendCmd(XMLCmd.testStudent,item.getId(),ciuLogin ?1:0,0,0,"",item);
        return ans.message;
        }    

    @Override
    public void deleteFile(int id, int fid, boolean doc) throws Throwable {
        sendCmd(XMLCmd.deleteFile,id,fid, doc ? 1:0);
        }
    @Override
    public boolean testBase(String name) throws Throwable {
        sendCmd(XMLCmd.testBase,0,0,0,0,name);
        return ans.id==2;
        }
    @Override
    public DBIdentification getDBIdentification() throws Throwable {
         throw new BRSException(BRSException.nofunc);    
        }
    @Override
    public void flush() throws Throwable {
        }

    @Override
    public int getFileCount() throws Throwable {
        return 0;
    }

    @Override
    public void loadPermissionList() throws Throwable {
        throw new BRSException(BRSException.nofunc);    
        }
}
