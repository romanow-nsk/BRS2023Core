package me.romanow.brs.ftpclient;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class FTPClient{
    //----------------- Вложенный класс-поток ----------------------------------
    public class ServerAnswerThread extends Thread{
        private boolean connected;
        public boolean isConnected(){ return connected; }
        public ServerAnswerThread(){
            connected = true;
            start();
            }
        public void parseAnswer(String answer){
            System.out.println(answer);
            int code = Integer.parseInt(answer.substring(0, 3));
            if (code == 221) connected = loggedIn = false;
            if (code == 230) loggedIn = true;
            if (code == 250) cwd();
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
    private ServerSocket dataSocket;
    private String func;
    private boolean isRun=false;

    public DataThread(int part1, int part2)throws Exception{
        int port = part1 * 256 + part2;
        func = "";
        dataSocket = new ServerSocket(port);
        dataSocket.setSoTimeout(5000);
        }

    public void execute(String func){
        if (!this.func.equalsIgnoreCase("")) return;
        this.func = func;
        isRun=true;
        this.start();
        }
    public boolean isRun(){ return isRun; }
    
    @Override
    public void run(){
        try {
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
        if (dataSocket.isClosed()) new IOException("Сокет данных закрыт");
        Socket socket = dataSocket.accept();
        if (!socket.isConnected()) new IOException("Ошибка соединения с сервером");
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
           socket.close();
           dataSocket.close();
           }

    private void download(String filename)throws Exception,Error{
        if (dataSocket.isClosed()) new IOException("Сокет данных закрыт");
        FileOutputStream fos = new FileOutputStream(localFolderName + "\\" + (localFileName!=null ? localFileName : filename));
        Socket socket = dataSocket.accept();
        System.out.println("Connected!!!!");
        if (!socket.isConnected()) new IOException("Ошибка соединения с сервером");
        DataInputStream dis = new DataInputStream(socket.getInputStream());
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
        socket.close();
        dataSocket.close();
        if (!success) new IOException("Ошибка приема данных");
    }

    private void upload(String filename)throws Exception,Error{
        if (dataSocket.isClosed()) new IOException("Сокет данных закрыт");
        FileInputStream fis = new FileInputStream(localFolderName + "\\" + (localFileName!=null ? localFileName : filename));
        Socket socket = dataSocket.accept();
        System.out.println("Connected!!!!");
        if (!socket.isConnected()) new IOException("Ошибка соединения с сервером");
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        int amount;
        byte[] tmp = new byte[1024];
        boolean success=true;
        try {
            while ((amount = fis.read(tmp)) != -1)
                dos.write(tmp, 0, amount);
            }
            catch(EOFException ex) {success=false; }
        dos.close();
        fis.close();
        socket.close();
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
    public static String ftpFileName="andbook.pdf";
    private boolean connected=false;
    private boolean loggedIn=false;
    private FTPListener lsn;
    private Socket socket;
    private PrintWriter pw;
    private String localIP="";
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

    public FTPClient(FTPListener lsn0){
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
    public boolean isLogged(){
        return loggedIn;
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
        if (disconnect(true) && lsn!=null)
            lsn.onError(message);
        }
    //------------------------- Может вызываться из потоков --------------------
    public void disconnect(){ disconnect(false); }
    synchronized public boolean disconnect(boolean interrupt){
        if (!connected) return true;
        try {
            if (interrupt){
                if (dataThread != null) dataThread.interrupt();
                log.interrupt();
            }
            connected=loggedIn=false;
            remoteFolderName = "Нет подключения";
            localFolderName = "";
            state = remoteFolderNameChange | notActualRemoteData;
            while (log.isConnected());
            pw.close();
            socket.close();
            return true;
        }
        catch(Exception ex){             
            connected=loggedIn=false;
            if (lsn!=null) lsn.onError(ex.getMessage());
            return false;
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
            disconnect(false);
            return;
            }
        if (!loggedIn)  return;
        if (command_name.equalsIgnoreCase("cwd")){
            command_buffer = command;
            return;
            }
        if (command_name.equalsIgnoreCase("port")){
            String arr[] = command.split(",");
            if (dataThread != null) dataThread.interrupt();
            dataThread = new DataThread(Integer.parseInt(arr[arr.length - 2]), Integer.parseInt(arr[arr.length - 1]));
            return;
            }
        if (command_name.equalsIgnoreCase("list")){
            if (dataThread == null) throw new IOException("Нет потока для обмена данными");
            dataThread.execute(command_name);
            if (command.split(" ").length > 1)
                state |= remoteFolderChange | notActualRemoteData;
            else
                state &= ~notActualRemoteData;
            return;
            }
        if (command_name.equalsIgnoreCase("nlst")){
            if (dataThread == null) throw new IOException("Нет потока для обмена данными");
            dataThread.execute(command_name);
            return;
            }
        if (command_name.equalsIgnoreCase("retr")){
            if (dataThread == null) throw new IOException("Нет потока для обмена данными");
            int space = command.indexOf(" ");
            command = command.substring(space + 1);
            dataThread.execute(command_name + "///" + command);
            return;
            }
        if (command_name.equalsIgnoreCase("stor")){
            if (dataThread == null) throw new IOException("Нет потока для обмена данными");
            int space = command.indexOf(" ");
            command = command.substring(space + 1);
            dataThread.execute(command_name + "///" + command);
            return;
            }
        }

    public void sendDataPort(int port) throws Exception{
        sendCommand("port "+localIP+"," + port/256+","+port%256);
        }
    public void sendCommand(String command) throws Exception{
        if (!connected)  throw new IOException("Команда для  неустановленного соединения");
        System.out.println(command);
        pw.println(command);
        parseCommand(command);
        }
    //-------------------------------------------------------------------------------
    public void copyFile(final boolean up,final String host, final int port, final int port2,
            final String user, final String pass, 
            final String path, final String locname,final String remname){
        Thread tt=new Thread(){
            public void run(){
                try {
                    if (!connect(host, port)) return;
                    sendCommand("user "+user);
                    sendCommand("pass "+pass);
                    while(!isLogged()){
                        sleep(100);
                        if (!isConnected()) return;
                    }
                    sendCommand("type I");
                    sendDataPort(port2);
                    setLocalFolder(path);
                    setLocalFile(locname);
                    sendCommand((up ? "stor " : "retr ")+remname);
                    while (dataThread.isRun()){ sleep(1000); }
                    sendCommand("quit");
                    while(isLogged()){  sleep(1000); }
                    // disconnect(false);
                    if (lsn!=null) lsn.onFinish("Операция выполнена");
                    } 
                catch(Exception ee){ fatal(ee.getMessage()); }
                catch(Error ee){ fatal(ee.getMessage()); }
        
        }};
        tt.start();
    }
    //-------------------------------------------------------------------------------
    static FTPClient cl;
    public static void main(String argv[]){
        cl=new FTPClient(null);
        FTPListener two=new FTPListener(){
                public void onError(String message) {
                    System.out.println(message);
                   }
                public void onFinish(String message) {
                    System.out.println(message);
                    cl.disconnect();
                }
            };
        FTPListener one=new FTPListener(){
                public void onError(String message) {
                    System.out.println(message);
                   }
                public void onFinish(String message) {
                    System.out.println(message);
                    cl.copyFile(false,ftpHost, ftpPort, ftpDataPort, ftpUser, ftpPass, ftpDownFolder, null,ftpFileName);
                }
            };
        cl.copyFile(true,ftpHost, ftpPort, ftpDataPort, ftpUser, ftpPass, ftpUpFolder, null, ftpFileName);
    }
}
