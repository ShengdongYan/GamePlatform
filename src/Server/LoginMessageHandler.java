package Server;

import Database.Database;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by bxs863 on 26/02/19.
 * process->check->checkIfLoggedIn->tryToLogin
 *
 */
public class LoginMessageHandler extends MessageHandler {
    Socket socket = null;

    /**
     * The constructor for the class.
     * @param message
     * @param socket
     */
    public LoginMessageHandler(String message,Socket socket) {
        super(message);
        this.socket = socket;
    }

    /**
     * Process all the log in message.
     * @return
     */
    @Override
    String process() {
        JSONObject response = new JSONObject();
        try{
            response.put("type","login_response");
            if(check(jsonObject,response)){ // Check the login message.
                Server.getInstance().getClients().put(this.jsonObject.getString("username"),socket);
                sendNewContactList();
            }
        }
        catch (Exception e){

        }
        return response.toString();
    }

    /**
     * Check if the message contains username and password
     * @param jsonObject
     * @param response
     * @return
     */
    private static boolean check(JSONObject jsonObject,JSONObject response){

        if(jsonObject.has("username") && jsonObject.has("password")){
            return checkIfLoggedIn(jsonObject, response); // Check if the user has logged in.
        }
        else{
            try {
                response.put("success","no");
                response.put("reply","You should provide the username and the password");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Check if the user has logged in.
     * @param jsonObject
     * @param response
     * @return
     */
    private static boolean checkIfLoggedIn(JSONObject jsonObject,JSONObject response){
        try {
            if(!Server.getInstance().getClients().containsKey(jsonObject.getString("username"))){
                return tryToLogin(jsonObject,response);
            }
            else{
                response.put("success","no");
                response.put("reply","You have logged in!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;

    }


    /**
     * Try to log in. If it successes, add the user. If it's not, tell the user something wrong
     * @param jsonObject
     * @param response
     * @return
     */
    private static boolean tryToLogin(JSONObject jsonObject, JSONObject response) {
        // Connect with Database to check if logged in
        if(Database.getInstance().userMatch("user_info",jsonObject.getString("username"),jsonObject.getString("password"))){
            response.put("success","yes");
            response.put("reply","Log in successfully");
            return true;
        }
        else{
            response.put("success","no");
            response.put("reply","You username or password is in correct");
        }
        return false;
    }

    /**
     * If some others have logged in. The serve will send the new contact list initiative
     * @throws IOException
     */
    private void sendNewContactList() throws IOException {
        JSONObject response = new JSONObject();
        response.put("type","contact_list");
        response.put("contact_names",Server.getInstance().getClients().keySet());
        for(Socket socket:Server.getInstance().getClients().values()){
            new PrintWriter(socket.getOutputStream(),true).println(response.toString());
        }
    }

}
