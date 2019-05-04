package Server;

import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by bxs863 on 26/02/19.
 */
public class ForwardMessageHandler extends MessageHandler {
    public ForwardMessageHandler(String message) {
        super(message);
    }

    @Override
    String process() {
        JSONObject response = new JSONObject();
        response.put("type","forward_response");
            if(jsonObject.getString("sub_type").equals("forward")){
                String toUser = jsonObject.getString("to_user");
                String message = jsonObject.getString("message");
                String fromUser = jsonObject.getString("from_user");
                JSONObject forward_msg = new JSONObject();
                forward_msg.put("type","forward_new_message");
                forward_msg.put("from_user",fromUser);
                forward_msg.put("to_user",toUser);
                forward_msg.put("message",message);
                try {
                    if(Server.getInstance().getClients().containsKey(toUser)) {
                        new PrintWriter(Server.getInstance().getClients().get(toUser).getOutputStream(), true).println(forward_msg.toString());
                        response.put("sub_type","forward_response");
                        response.put("success", "yes");
                        response.put("reply", "Your message has been sent.");
                    }
                    else{
                        response.put("sub_type","forward_response");
                        response.put("success", "no");
                        response.put("reply", "User has logged out.");
                    }
                } catch (IOException e) {
                    response.put("sub_type","forward_response");
                    response.put("success","no");
                    response.put("reply","Your message has not been sent.");
                    e.printStackTrace();
                }

            }
        return response.toString();
    }
}
