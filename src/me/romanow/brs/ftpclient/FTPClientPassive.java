package me.romanow.brs.ftpclient;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class FTPClientPassive{
    //----------------- Вложенный класс-поток ----------------------------------
    public class ServerAnswerThread extends Thread{
        private boolean connected;
        public boolean isConnected(){ return connected; }
        public ServerAnswerThread(){
            connected = true;
            start();
            }
        private boolean isDigit(char cc){
            return cc>='0' && cc<='9';
            }
        private String []getDigits(String ss){
            char cc[]=ss.toCharArray();
            int ns=0;
            for (int i=0;i<cc.length;i++)
                if (isDigit(cc[i]) && (i==0 || !isDigit(cc[i-1])))
                    ns++;
            String zz[]=new String[ns];
            ns=-1;
            for (int i=0;i<cc.length;i++){
                if (isDigit(cc[i]) && (i==0 || !isDigit(cc[i-1]))){
                    ns++;
                    zz[ns]="";
                    }
                if (isDigit(cc[i])) zz[ns]+=cc[i];                
                }
            return zz;           
            }
        public void parseAnswer(String answer){
            System.out.println(answer);
            int code = Integer.parseInt(answer.substring(0, 3));
            if (code == 221) { 
                closeAll(); connected=false; }
            if (code == 230) loggedIn = true;
            if (code == 250) cwd();
            if (code == 227){
                    String arr[] = getDigits(answer.substring(3));
                    remotePort=Integer.parseInt(arr[4])*256+Integer.parseInt(arr[5]);
                    remoteIP=arr[0]+"."+arr[1]+"."+arr[2]+"."+arr[3];
                }
            }
        @Override
        public void run(){
            try{
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message;
                while (connected){
                    message = br.readLine();
                    if (message != null){
                       parseAnswer(message);
                    }
                }
                br.close();
            }   catch(Exception ex){ fatal(ex.getMessage()); }
        }
        
        @Override
        public void interrupt(){ super.interrupt(); connected = false; }
    }
    //---------------- Вложенный класс для второго соединения -----------------------------
    public class DataThread extends Thread{
    private Socket dataSocket;
    private String func;
    private boolean isRun=false;

    public DataThread(String cmd) throws Exception{
        func=cmd;
        if (func.equalsIgnoreCase("")) return;
        isRun=true;
        start();
        }
    public boolean isRun(){ return isRun; }
    
    @Override
    public void run(){
        try {
            dataSocket=new Socket();
            dataSocket.connect(new InetSocketAddress(remoteIP,remotePort),15000);
            System.out.println("Connected!!!!");
            if (!socket.isConnected()) new IOException("Ошибка соединения с сервером");
            String name = func.split("///")[0];
            if (name.equalsIgnoreCase("list")){
                list(true);
                }
            if (name.equalsIgnoreCase("nlst")){
                list(false);
                }
            if (name.equalsIgnoreCase("retr")){
                download(func.split("///")[1]);
                }
            if (name.equalsIgnoreCase("stor")){
                upload(func.split("///")[1]);
                }
            }
        catch(Exception ex) { fatal(ex.getMessage()); }
        isRun=false;
        }

    @Override
    public void interrupt(){
        super.interrupt();
        if (dataSocket.isClosed()) return;
        try { 
            dataSocket.close(); }
        catch(IOException ex){ fatal(ex.getMessage()); }
        }

    private void list(boolean parse)throws Exception,Error{
        remoteFolder.clear();
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String message;
        String type = "", name = "", arr[], size = "", date = "", today = (new Date()).toString().split(" ")[1] + " " + (new Date()).toString().split(" ")[2];
        while ((message = br.readLine()) != null){
            if (parse){
                name = "";
                arr = message.split(" ");
                type = arr[0].startsWith("d") ? "dir" : "file";
                int i = 4;
                for (; arr[i].equals(""); i++);
                size = type.equals("dir") ? "" : arr[i];
                i++;
                date = arr[i] + " " + arr[i + 1];
                if (!arr[i + 2].equalsIgnoreCase("") || type.equals("dir")){
                    date += " " + arr[i + 2];
                    i += 3;
                    }
                else
                    {
                    date += " " + arr[i + 3];
                    i += 4;
                    }
                for (; i < arr.length; i++){
                    name += (name.equals("") ? arr[i] : (" " + arr[i]));
                    }
                remoteFolder.add(new Record(name, type, size, date));
                }
           }
           if (!remoteFolderName.equals("/")){
                remoteFolder.add(0, new Record("..", "dir", "", ""));
                }
           state |= remoteFolderChange;
           br.close();
           dataSocket.close();
           }

    private void download(String filename)throws Exception,Error{
    	char cc=(linux ? '/':'\\');
        FileOutputStream fos = new FileOutputStream(localFolderName + cc + (localFileName!=null ? localFileName : filename));
        DataInputStream dis = new DataInputStream(dataSocket.getInputStream());
        boolean success=true;
        try {
            int amount;
            byte[] tmp = new byte[1024];
            while ((amount = dis.read(tmp)) != -1)
                fos.write(tmp, 0, amount);
            }
            catch(EOFException ex) {success=false; }
        fos.close();
        dis.close();
        dataSocket.close();
        if (!success) new IOException("Ошибка приема данных");
    }

    private void upload(String filename)throws Exception,Error{
    	char cc=(linux ? '/':'\\');
        FileInputStream fis = new FileInputStream(localFolderName + cc + (localFileName!=null ? localFileName : filename));
        DataOutputStream dos = new DataOutputStream(dataSocket.getOutputStream());
        int amount;
        byte[] tmp = new byte[1024];
        boolean success=true;
        try {
            while ((amount = fis.read(tmp)) != -1){
                dos.write(tmp, 0, amount);
                System.out.println(amount);
                }
            }
            catch(EOFException ex) {
                success=false; 
                }
        dos.close();
        fis.close();
        dataSocket.close();
        if (!success) new IOException("Ошибка передачи данных");
        }
    }
    //-------------------------------------------------------------------------------------
    public static String ftpHost="romanow-eugene.ftp.narod.ru";
    public static int ftpPort=21;
    public static int ftpDataPort=5000;
    public static String ftpUser="romanow-eugene";
    public static String ftpPass="longlivernr";
    public static String ftpUpFolder="F:\\NBAndroid";
    public static String ftpDownFolder="F:\\";
    public static String ftpFileName="flomat.doc";
    private boolean connected=false;
    private boolean loggedIn=false;
    private FTPListener lsn;
    private Socket socket;
    private PrintWriter pw;
    private String localIP="";
    private String remoteIP="";
    private int remotePort=0;
    private ServerAnswerThread log;
    private String command_buffer;
    private DataThread dataThread;
    private List<Record> remoteFolder;
    private int state;
    private String remoteFolderName="";
    private String localFolderName="";
    private String localFileName=null;
    public final int remoteFolderChange = 1;
    public final int remoteFolderNameChange = 2;
    public final int notActualRemoteData = 4;
    private boolean linux=false;

    public FTPClientPassive(FTPListener lsn0){
    	this(lsn0,false);
    	}
    public FTPClientPassive(FTPListener lsn0,boolean lin){
    	linux=lin;
        lsn=lsn0;
        connected=false;
        loggedIn=false;
        localFileName=null;
        remoteFolderName = "Нет подключения";
        state = remoteFolderNameChange | notActualRemoteData;
        }

    public void setFTPListener(FTPListener lsn0){
        lsn=lsn0;
        }

    public void setLocalFolder(String fld){
        localFolderName=fld;
        }
    public void setLocalFile(String fld){
        localFileName=fld;
        }
    public boolean isConnected(){
        return connected;
        }
    public boolean isLogged() throws Exception,Error{
        if (!connected) throw new IOException("Соединение разорвано");
        return loggedIn;
        }
    public boolean isRemoteIPValid() throws Exception,Error{
        if (!connected) throw new IOException("Соединение разорвано");
        return remotePort!=0;
        }
    
    public boolean connect(String host, int port){
        if (connected) { 
            if (lsn!=null) lsn.onError("Выполняется операция с сервером");
            return false; 
            }
        try {
            socket = new Socket(host, port);
            if (socket.isConnected()){
                connected=true;
                localIP=socket.getLocalAddress().getHostAddress();
                localIP=localIP.replace('.', ',');
                pw = new PrintWriter(socket.getOutputStream(), true);
                log = new ServerAnswerThread();
                remoteFolder = Collections.synchronizedList(new ArrayList<Record>());
                remoteFolder.add(new Record("Нет данных", "", "", ""));
                remoteFolderName = "/";
                state = remoteFolderChange | remoteFolderNameChange | notActualRemoteData;
            }
            return true;
        }
        catch(Exception ex){
            connected=loggedIn=false;
            remoteFolderName = "Нет подключения";
            state = remoteFolderNameChange | notActualRemoteData;
            if (lsn!=null) lsn.onError(ex.getMessage());
            return false;
            }
        }
    
    public void fatal(String message){
        if (!connected) return;
        if (dataThread != null) dataThread.interrupt();
        log.interrupt();
        closeAll();
        if (lsn!=null) lsn.onError(message);
        }
    //------------------------- Может вызываться из потоков --------------------
    private Thread closeTimeOut=null;
    synchronized private void closeAll(){
        try {
            if(!connected) return;
            if (closeTimeOut!=null){
                closeTimeOut.interrupt();
                closeTimeOut=null;
                }
            connected=loggedIn=false;
            remoteFolderName = "Нет подключения";
            localFolderName = "";
            state = remoteFolderNameChange | notActualRemoteData;
            pw.close();
            socket.close();
            } catch(Exception ee){}
        }
    synchronized public void disconnect(){ 
        try { 
            if (dataThread != null) dataThread.interrupt();
            } catch(Exception ee){}
        try {
            sendCommand("quit");        // Закрытие послед ответа на команду
            closeTimeOut=new Thread(){
                public void run(){      // Тайм-аут команды quit
                    try { sleep(10000); } catch(Exception ee){}
                    closeAll();         // Принудительное закрытие
                    }
                };
            closeTimeOut.start();
            } catch(Exception ee){ 
                if (lsn!=null) lsn.onError(ee.getMessage());
                closeAll(); 
                }
        }

    //--------------- Обработка ответа сервера на команду --- вызов из потока ---
    public void cwd(){
        if (!command_buffer.substring(0, 3).equalsIgnoreCase("cwd")) return;
        int space = command_buffer.indexOf(" ");
        command_buffer = command_buffer.substring(space + 1);
        String arr[] = (remoteFolderName + "/" + command_buffer).split("/");
        remoteFolderName = "";
        for (int i = 0; i < arr.length; i++){
            if (arr[i].equals("") || arr[i].equals("."))
                continue;
            if (arr[i].equals("..")){
                int last = remoteFolderName.lastIndexOf("/");
                if (last != -1){
                    remoteFolderName += "//";
                    remoteFolderName = remoteFolderName.replaceFirst(remoteFolderName.substring(last), "");
                    }
                continue;
                }
            remoteFolderName += "/" + arr[i];
        }
        if (remoteFolderName.equals(""))
            remoteFolderName = "/";
        state |= remoteFolderNameChange | notActualRemoteData;
    }

    private void waitForDataThread(){
        while (connected && dataThread.isRun());
        }
    //------------ Действия после основной команды -----------------------------
    private void parseCommand(String command) throws Exception{
        String command_name = command.split(" ")[0];
        if (command_name.equalsIgnoreCase("quit")){
            // disconnect(false);
            return;
            }
        if (!loggedIn)  return;
        if (command_name.equalsIgnoreCase("cwd")){
            command_buffer = command;
            return;
            }
        if (command_name.equalsIgnoreCase("port")){
            return;
            }
        if (command_name.equalsIgnoreCase("list")){
            if (dataThread != null && dataThread.isRun)
                throw new IOException("Поток обмена данными уже выполняется");
            dataThread=new DataThread(command_name);
            if (command.split(" ").length > 1)
                state |= remoteFolderChange | notActualRemoteData;
            else
                state &= ~notActualRemoteData;
            return;
            }
        if (command_name.equalsIgnoreCase("nlst")){
            if (dataThread != null && dataThread.isRun)
                throw new IOException("Поток обмена данными уже выполняется");
            dataThread=new DataThread(command_name);
            return;
            }
        if (command_name.equalsIgnoreCase("retr")){
            if (dataThread != null && dataThread.isRun)
                throw new IOException("Поток обмена данными уже выполняется");
            int space = command.indexOf(" ");
            command = command.substring(space + 1);
            dataThread=new DataThread(command_name+ "///" + command);
            return;
            }
        if (command_name.equalsIgnoreCase("stor")){
            if (dataThread != null && dataThread.isRun)
                throw new IOException("Поток обмена данными уже выполняется");
            int space = command.indexOf(" ");
            command = command.substring(space + 1);
            dataThread=new DataThread(command_name+ "///" + command);
            return;
            }
        }

    public void sendCommand(String command) throws Exception{
        if (!connected)  throw new IOException("Команда для  неустановленного соединения");
        System.out.println(command);
        pw.println(command);
        parseCommand(command);
        }
    //-------------------------------------------------------------------------------
    public void copyFileNoThread(boolean up,String host,int port,
            String user,String pass, 
            String path,String locname,String remDir,String remname)throws Exception,Error{
    			if (!connect(host, port)) return;
    			sendCommand("user "+user);
    			sendCommand("pass "+pass);
    			while(!isLogged()){
    					Thread.sleep(100);
    					if (!isConnected()) return;
    			}	
    			sendCommand("type I");
    			sendCommand("cwd /"+remDir);
    			sendCommand("pasv");
    			while (!isRemoteIPValid()){ Thread.sleep(1000); }
    			setLocalFolder(path);
    			setLocalFile(locname);
    			sendCommand((up ? "stor " : "retr ")+remname);
    			while (dataThread.isRun()){ Thread.sleep(1000); }
    			sendCommand("quit");
    			while(isConnected()){  Thread.sleep(1000); }
    			if (lsn!=null) lsn.onFinish("Операция выполнена");
    		}

    public void copyFile(final boolean up,final String host, final int port,
        final String user, final String pass, 
        final String path, final String locname,final String remDir,final String remname){
        Thread tt=new Thread(){
            public void run(){
                try {
                	copyFileNoThread(up,host,port,user,pass,path,locname,remDir,remname);
                    } 
                catch(Exception ee){ fatal(ee.getMessage()); }
                catch(Error ee){ fatal(ee.getMessage()); }
        
        }};
        tt.start();
    }
    //-------------------------------------------------------------------------------
    static FTPClientPassive cl;
    public static void main(String argv[]){
        cl=new FTPClientPassive(null);
        final FTPListener two=new FTPListener(){
                public void onError(String message) {
                    System.out.println(message);
                   }
                public void onFinish(String message) {
                    System.out.println(message);
                }
            };
        FTPListener one=new FTPListener(){
                public void onError(String message) {
                    System.out.println(message);
                   }
                public void onFinish(String message) {
                    System.out.println(message);
                    cl.setFTPListener(two);
                    cl.copyFile(false,ftpHost, ftpPort, ftpUser, ftpPass, ftpDownFolder, null,"",ftpFileName);
                }
            };
        cl.setFTPListener(one);
        cl.copyFile(true,ftpHost, ftpPort, ftpUser, ftpPass, ftpUpFolder, null, "",ftpFileName);
    }
}
