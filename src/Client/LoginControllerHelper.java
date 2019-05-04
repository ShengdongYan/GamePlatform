package Client;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import org.json.JSONObject;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bxs863 on 02/03/19.
 */
public class LoginControllerHelper {

    public static void empyInput(TextField textField, Control control){
        Timer time = new Timer(true);
        time.schedule(new TimerTask() {
            @Override
            public void run() {
                textField.setStyle("-fx-background-color:#393E46;-fx-text-inner-color: white;");
                control.setDisable(true);
                Platform.runLater(textField::requestFocus);
            }
        },200);
        textField.setStyle("-fx-background-color:#ffcc00");
    }

    public static JSONObject createJsonobject(String... args){
        if(args.length%2==0) throw new IllegalArgumentException("Please pass even number arguments");
        JSONObject jsonObject = new JSONObject();
        for(int i =0;i<args.length-1;i+=2){
            jsonObject.put(args[i],args[i+1]);
        }
        return jsonObject;
    }

    public static String encrypt(String password){
        String pwd = "";
        try {
            byte[] salt = "This is dummy salt".getBytes();
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKeyFactory fac = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = fac.generateSecret(spec).getEncoded();
            Base64.Encoder encoder = Base64.getEncoder();
            pwd = encoder.encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return pwd;
    }



}
