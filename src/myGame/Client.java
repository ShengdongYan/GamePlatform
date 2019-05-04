package myGame;

import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.util.Calendar;

/**

 * This is the client class for Game part
 * This class will keep the newest message and keep refreshing the message as soon as possible.
 * It also use
 * @author bxs863
 *
 */
public class Client {

    public class Message{
        private long time=0;
        private String message = "{}";

        public synchronized void setNewMessage(long time,String message){
            if(time>this.time){
                this.time = time;
                this.message = message;
            }
        }

        public String getMessage() {
            return message;
        }
        public JSONObject getMessageAsJson(){
            return new JSONObject(message);
        }
    }


    DatagramSocket socket;
    private Message newMessage = new Message();
    private static Client client = null;
    private String gameID = "";
    private String username = "";
    private String ip = "localhost";
    private int port = 4399;

    public static Client getClient(){
        if(client==null){
            client = new Client();
        }
        return client;
    }

    private Client() {
        refreshIpAndPort();
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Thread t = new Thread(() -> {
            while(true){
                String msg = receive();
                JSONObject jsonObject = new JSONObject(msg);
                long timeStamp = jsonObject.getLong("time");
                newMessage.setNewMessage(timeStamp,msg);
            }
        });
        t.setDaemon(true);
        t.start();

    }

    /**
     * get the ip and the port
     */
    private void refreshIpAndPort(){
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
     * Receive the message
     * @return
     */
    private String receive(){
        byte[] buf = new byte[5*1024];
        DatagramPacket packet = new DatagramPacket(buf,buf.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(buf);
    }

    /**
     * Send the message
     * @param msg
     */
    public void sendMessage(String msg){
        JSONObject jsonObject = new JSONObject(msg);
        jsonObject.put("gameID",gameID);
        jsonObject.put("username",username);
        jsonObject.put("time", Calendar.getInstance().getTimeInMillis());
        String message = jsonObject.toString();
        Thread t = new Thread(() -> {
            try {
                DatagramPacket packet = new DatagramPacket(message.getBytes(),message.getBytes().length, InetAddress.getByName(ip), port);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public Message getNewMessage() {
        return newMessage;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
