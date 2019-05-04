package Server;

import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * Created by bxs863 on 16/03/19.
 */
public class GameServer {

    private static GameServer server = new GameServer();
    private Hashtable<String,Hashtable<String,User>> games = new Hashtable<>(); // GameID || Username |User
    private Hashtable<String,Integer> gameIDs = new Hashtable<>();

    class User{
        public InetAddress address;
        public int port;

        public User(InetAddress address,int port){
            this.address = address;
            this.port = port;
        }
    }

    /**
     * The constructor of the Game Server
     */
    private GameServer(){

        Thread watcher = new Thread(()->{ // Start a new thread to assign the gameID
            while (true) {
                for (Map.Entry<String, Integer> id : gameIDs.entrySet()) {
                    id.setValue(id.getValue() - 1);
                }
                gameIDs.entrySet().removeIf(x -> x.getValue() <= 0);
                games.entrySet().removeIf(x->!gameIDs.containsKey(x.getKey()));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        watcher.setDaemon(true);
        watcher.start();

        Thread t = new Thread(()->{  // new Thread to receive the message
            try {
                DatagramSocket socket = new DatagramSocket(Server.getPort());
                while(true){
                    byte[] buf = new byte[1024*5];
                    DatagramPacket packet = new DatagramPacket(buf,buf.length);
                    socket.receive(packet);
                    addOrUpdate(packet);
                    sendToOthers(packet,socket);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
//        t.setDaemon(false);
        t.start();
    }

    public static GameServer getGameServer(){
        return server;
    }


    /**
     * Add the new address of the user to the dictionary
     * @param packet
     */
    private void addOrUpdate(DatagramPacket packet){
        byte[] buf = packet.getData();
        JSONObject jsonObject = new JSONObject(new String(buf));
        String gameID = jsonObject.getString("gameID");
        String username = jsonObject.getString("username");
        games.get(gameID).put(username,new User(packet.getAddress(),packet.getPort()));
        gameIDs.put(gameID,3600);
    }

    /**
     * Send the message to others rather than me
     * @param packet The packet of the message
     * @param socket The socket
     * @throws IOException
     */
    private void sendToOthers(DatagramPacket packet, DatagramSocket socket) throws IOException {
        byte[] buf = packet.getData();
        JSONObject jsonObject = new JSONObject(new String(buf));
        String username = jsonObject.getString("username");
        String gameID = jsonObject.getString("gameID");
        for(Map.Entry<String,User> user : games.get(gameID).entrySet()){
            if(user.getKey().equals(username)) continue;
            packet.setAddress(user.getValue().address);
            packet.setPort(user.getValue().port);
            socket.send(packet);
        }
    }

    /**
     * Create a new ID
     * @return The game id
     */
    public String createGameID(){
        int i = 0;
        while(gameIDs.containsKey(String.valueOf(i))){
            i++;
        }
        games.put(String.valueOf(i),new Hashtable<>());
        gameIDs.put(String.valueOf(i),60);
        return ""+i;
    }

}
