package Client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by bxs863 on 05/03/19.
 */
public class SignupController {

    private Stage stage;
    private ExecutorService es = Executors.newFixedThreadPool(10, r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
    });

    @FXML
    private Pane root;
    @FXML
    private Button signupBtn;
    @FXML
    private TextField usrnameTf;
    @FXML
    private PasswordField pwdField;
    @FXML
    private PasswordField pwdAgainField;
    @FXML
    private RadioButton radBtn;
    @FXML
    private Label checkLabel;


    public void getBack(MouseEvent mouseEvent) {
        es.shutdown();
        stage = (Stage) root.getScene().getWindow();
        stage.setScene(LoginController.loginScene);
    }


    public void radioButtonChanged(ActionEvent actionEvent) {
        if (usrnameTf.getText().isEmpty() || pwdAgainField.getText().isEmpty() || pwdField.getText().isEmpty() || !radBtn.isSelected() || !pwdField.getText().equals(pwdAgainField.getText()))
            signupBtn.setDisable(true);
        else signupBtn.setDisable(false);
    }

    public void pwdkeyRelease(KeyEvent keyEvent) {
        if (usrnameTf.getText().isEmpty() || pwdAgainField.getText().isEmpty() || pwdField.getText().isEmpty() || !radBtn.isSelected() || !pwdField.getText().equals(pwdAgainField.getText()))
            signupBtn.setDisable(true);
        else signupBtn.setDisable(false);
    }

    public void keyRelease(KeyEvent keyEvent) {
        if (usrnameTf.getText().isEmpty() || pwdAgainField.getText().isEmpty() || pwdField.getText().isEmpty() || !radBtn.isSelected() || !pwdField.getText().equals(pwdAgainField.getText()))
            signupBtn.setDisable(true);
        else signupBtn.setDisable(false);

        if (Client.getClient().isConnected()) {
            es.execute(() -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "data");
                jsonObject.put("sub_type", "check_user");
                jsonObject.put("username", usrnameTf.getText());
                JSONObject result;
                Client.getClient().sendMessage(jsonObject);
                while ((result = Client.getClient().findNext("check_user_response")) == null) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (result.has("success")) {
                    if (result.get("success").equals("yes") && !result.getBoolean("reply")) {
                        Platform.runLater(() -> {
                            checkLabel.setText("OK");
                            checkLabel.setTextFill(Color.GREEN);
                        });
                    } else {
                        Platform.runLater(() -> {
                            checkLabel.setText("No");
                            checkLabel.setTextFill(Color.RED);
                        });
                    }
                }
            });
        }
    }

    public void signUpAction(ActionEvent actionEvent) {
        if (Client.getClient().isConnected()) {
            es.execute(() -> {
                Client.getClient().signup(usrnameTf.getText(),LoginControllerHelper.encrypt(pwdField.getText()));
                JSONObject result = Client.getClient().retreiveJson("signup_response");
                if (result.get("success").equals("yes")) {
                    Platform.runLater(() -> {
                        MsgBoxController.display("Succeed",result.getString("reply"));
                    });
                } else {
                    Platform.runLater(() -> {
                        MsgBoxController.display("Fail",result.getString("reply"));
                    });
                }
            });
        }
    }

    public void quitClicked(MouseEvent mouseEvent) {
        Platform.exit();
    }
}
