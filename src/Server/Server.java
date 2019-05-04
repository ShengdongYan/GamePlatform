package Server;

import myGame.Frames.GameStage;

import java.io.IOException;
import java.net.*;
import java.util.Hashtable;

/**
 * Created by bxs863 on 26/02/19.
 */
public class Server {
    private Hashtable<String,Socket> clients = new Hashtable<>();
    private static Server server = null;
    private static int port = 5555;
    private Server(){

    }

    /**
     * This is the method for singleton
     * @return The instance of the Server
     */
    public static Server getInstance(){
        if(server == null){
            server = new Server();
        }
        return server;
    }

    /**
     * Get the client
     * @return A hash table to store the clients.
     */
    public Hashtable<String, Socket> getClients() {
        return clients;
    }


    /**
     * Start the server. It will accept all the requests,and store the socket channel into the collection.
     * @param port The port number of server.
     */
    public void Start(int port){
        try {
            ServerSocket ss = new ServerSocket(port);
            while(true){
                new SocketHandler(ss.accept()).start();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * In case of changing the ip of the server, so that the client can't get the ip of the server. The server will
     * broadcast its ip and port to everyone in the network. Like a big guy screams, hi I am here.
     */
    private static void broadServerIP(){
        try {
            MulticastSocket socket = new MulticastSocket();
            InetAddress mip = InetAddress.getByName("230.0.0.1");
            socket.joinGroup(mip);
            Thread t = new Thread(() -> {

                try {
                    while (true) {
                        InetAddress inetAddress = InetAddress.getLocalHost();
                        String ipAndPort = inetAddress.getHostAddress() + ":" + port;
                        byte[] buf = ipAndPort.getBytes();
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, mip, 12345);
                        socket.send(packet);
                        Thread.sleep(1000);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            });
            t.setDaemon(true);
            t.start();

            t = new Thread(() -> {
                try {
                    MulticastSocket socket1 = new MulticastSocket(12345);
                    socket1.joinGroup(InetAddress.getByName("230.0.0.1"));
                    while (true) {
                        byte[] buf = new byte[512];
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);
                        socket1.receive(packet);
                        String temp = new String(buf);
                        if (!InetAddress.getLocalHost().getHostAddress().equals(temp.split(":")[0])) {
                            System.exit(0);
                        }
                        Thread.sleep(1000);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            });
            t.setDaemon(true);
            t.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static int getPort() {
        return port;
    }


    /**
     * The entry of the server
     * @param args
     */
    public static void main(String[] args) {
        broadServerIP();
        Server.getInstance().Start(port);
    }
}
