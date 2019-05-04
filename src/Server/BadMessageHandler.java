package Server;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bxs863 on 26/02/19.
 */
public class BadMessageHandler extends MessageHandler {
    BadMessageHandler(String message) {
        super(message);
    }

    @Override
    String process() {
        JSONObject response = new JSONObject();
        try {
            response.put("type","bad_message_response");
            response.put("reply","The message contains unrecognized content!");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return response.toString();
    }
}
