package Server;

import Database.Database;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * Created by bxs863 on 26/02/19.
 */
public class DataMessageHandler extends MessageHandler {
    public DataMessageHandler(String message) {
        super(message);
    }
    @Override
    String process() {
        JSONObject response = new JSONObject();
        if(jsonObject.get("sub_type").equals("check_user")){
            checkUserExist(response);
        }
        else if(jsonObject.getString("sub_type").equals("start_game")){
            processNewGame(jsonObject,response);
        }
        else if(jsonObject.getString("sub_type").equals("start_game_answer")){
            processStartGameAnswer(jsonObject,response);
        }
        return response.toString();
    }

    /**
     * Transfer the game answer
     * @param jsonObject
     * @param response
     */
    private void processStartGameAnswer(JSONObject jsonObject, JSONObject response) {
        try {
            if (jsonObject.getString("reply").equals("yes")) {
                JSONObject answer = new JSONObject();
                answer.put("type", "start_game_answer");
                answer.put("reply", "yes");
                answer.put("master",jsonObject.getString("to_user"));
                answer.put("users", Arrays.asList(jsonObject.getString("to_user"), jsonObject.getString("from_user")));
                answer.put("game", jsonObject.getString("game"));
                String gid = GameServer.getGameServer().createGameID();
                answer.put("gameID",gid );
//                answer.put("gameID", "0");
                for (Object user : answer.getJSONArray("users").toList()) {
                    String user_str = user.toString();
                    new PrintWriter(Server.getInstance().getClients().get(user_str).getOutputStream(), true).println(answer.toString());
                }
            }
            else{
                JSONObject answer = new JSONObject();
                answer.put("type", "start_game_answer");
                answer.put("reply", "no");
                new PrintWriter(Server.getInstance().getClients().get(jsonObject.getString("to_user")).getOutputStream(), true).println(answer.toString());
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void checkUserExist(JSONObject response) {
        response.put("type","check_user_response");
        if (jsonObject.has("username")) {
            boolean result = Database.getInstance().checkExist("user_info", "username", jsonObject.getString("username"));
            response.put("success","yes");
            response.put("reply",result);
        }
        else{
            response.put("success","no");
            response.put("reply","You haven't provide the username");
        }
    }

    private void processNewGame(JSONObject jsonObject,JSONObject response){
        response.put("type","start_game_response");
        if(jsonObject.has("first_user")&&jsonObject.has("second_user")&&jsonObject.has("game")){
            try {
                String s = String.format("%s wants to play %s with you.",jsonObject.getString("first_user"),jsonObject.getString("game"));
                JSONObject invite = new JSONObject();
                invite.put("type","invitation");
                invite.put("from_user",jsonObject.getString("first_user"));
                invite.put("to_user",jsonObject.getString("second_user"));
                invite.put("game",jsonObject.getString("game"));
                invite.put("message",s);
                new PrintWriter(Server.getInstance().getClients().get(jsonObject.getString("second_user")).getOutputStream(),true).println(invite.toString());
                response.put("reply","Your invitation has been sent.");
            } catch (IOException e) {
                response.put("reply","Your invitation has not been sent.");
                e.printStackTrace();
            }
        }
    }



}
