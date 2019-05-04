package Server;

import Database.Database;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bxs863 on 26/02/19.
 */
public class SignupMessageHandler extends MessageHandler {
    public SignupMessageHandler(String message) {
        super(message);
    }

    /**
     * This function will start the process of processing the sign up request.
     * @return The response message.
     */
    @Override
    String process() {
        JSONObject response = new JSONObject();
        try {
            response.put("type", "signup_response");
            signUp(jsonObject, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    /**
     * The method to handle all the sign up message. It will check the database.
     * @param jsonObject
     * @param response
     */
    private void signUp(JSONObject jsonObject, JSONObject response) {
        try{
            if (jsonObject.has("username") && jsonObject.has("password")) {
                String username = jsonObject.getString("username");
                String password = jsonObject.getString("password");
                if(Database.getInstance().checkExist("user_info","username",username)){
                    response.put("success","no");
                    response.put("reply","Your username has been registered, please use a new username");
                }
                else{
                    response.put("success","yes");
                    response.put("reply","You have signed up successfully.");
                    Database.getInstance().addUserInfo("user_info",username,password);
                }
            }
            else{
                response.put("success","no");
                response.put("reply","You didn't provide the username or password");
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }
}
