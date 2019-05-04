package Server;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bxs863 on 26/02/19.
 */
public class MessageHandlerHelper {

    /**
     * Get the result of the message
     * @param json The json object
     * @return The type
     */
    public static MessageType getType(JSONObject json){
        MessageType result = MessageType.None;
        try {
            String type = json.getString("type");
            switch (type.toLowerCase()) {
                case "signup":
                    result = MessageType.SignUp;
                    break;
                case "login":
                    result = MessageType.LogIn;
                    break;
                case "logout":
                    result = MessageType.LogOut;
                    break;
                case "forward":
                    result = MessageType.Forward;
                    break;
                case "data":
                    result = MessageType.Data;
                    break;
                default:
                    result = MessageType.None;
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }



}
