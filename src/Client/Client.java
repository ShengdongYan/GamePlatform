package Client;

import org.json.JSONObject;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by bxs863 on 27/02/19.
 */
public class Client {
    private static Client client = null;


    private AtomicBoolean connected = new AtomicBoolean(false);
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private static String ip = "localhost";
    private static int port = 4399;
    private ExecutorService es = Executors.newFixedThreadPool(5, r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
    });
    private Queue<JSONObject> messageQueue = new LinkedBlockingDeque<>();


    /**
     * Get the client with singleton
     * @return
     */
    public static Client getClient(){
        if(client == null){
            client = new Client();
        }
        return client;
    }

    /**
     * refresh the ip and the port. It will receive the broadcast message from the server and get the ip and the port.
     */
    private static void refreshIpAndPort(){
        Thread t = new Thread(()->{
            try {
                MulticastSocket socket = new MulticastSocket(12345);
                socket.joinGroup(InetAddress.getByName("230.0.0.1"));
                while(true) {
                    byte[] buf = new byte[512];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String[] ipAndPort = new String(buf).split(":");
                    ip = ipAndPort[0];
                    port = Integer.valueOf(ipAndPort[1].trim());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    /**
     * Make the constructor private and use singleton to get it
     */
    private Client() {
        es.execute(()->{
            tryToConnect();
            while(true){
                try {
                    String messageStr = in.readLine();
                    if(messageStr==null){
                        connected.set(false);
                        tryToConnect();
                        Thread.sleep(100);
                        continue;
                    }
                    if(messageQueue.size()>=1000){
                        for(int i = 0;i<500;i++){
                            messageQueue.remove();
                        }
                    }
                    messageQueue.add(new JSONObject(messageStr));
                } catch (IOException e) {
                    connected.set(false);
                    tryToConnect();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Try to connect the server. If it fails. connect again.
     */
    private void tryToConnect() {
        refreshIpAndPort();
        while(!connected.get()) {
            try {
                socket = new Socket(ip, port);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                connected.set(true);
            } catch (IOException e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


    /**
     * Check if it's connected
     * @return
     */
    public boolean isConnected(){
        return connected.get();
    }

    /**
     * Find the message in the message list. and remove it if it has been used.
     * @param type
     * @return
     */
    public synchronized JSONObject findNext(String type){
        messageQueue.removeIf(obj->!obj.has("type"));
        for(JSONObject obj : messageQueue){
            if(obj.getString("type").equals(type)){
                messageQueue.remove(obj);
                return obj;
            }
        }
        return null;
    }

    /**
     * retreive Json. But it will grantee that it has message.
     * @param type
     * @return
     */
    public synchronized JSONObject retreiveJson(String type){
        JSONObject result;
        while ((result = Client.getClient().findNext(type)) == null){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * send login message
     * @param username
     * @param password
     */
    public void login(String username,String password){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type","login");
        jsonObject.put("username",username);
        jsonObject.put("password",password);
        sendMessage(jsonObject);
    }

    public void logout(String username){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type","logout");
        jsonObject.put("username",username);
        sendMessage(jsonObject);
    }

    /**
     * send sign up message
     * @param username
     * @param password
     */
    public void signup(String username, String password){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type","signup");
        jsonObject.put("username",username);
        jsonObject.put("password",password);
        sendMessage(jsonObject);
    }


    public String receiveMessage(){
        try {
            String message = in.readLine();
            return message;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Send message
     * @param message
     * @return
     */
    public Client sendMessage(String message){
        out.println(message);
        return this;
    }

    public Client sendMessage(JSONObject jsonObject){
        sendMessage(jsonObject.toString());
        return this;
    }

}
