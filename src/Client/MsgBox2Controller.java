package Client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by bxs863 on 18/03/19.
 */
public class MsgBox2Controller {

    @FXML
    private Label title;
    @FXML
    private Text context;
    @FXML
    private Pane root;


    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setContext(String context) {
        this.context.setText(context);
    }

    @FXML
    public void initialize() {
        title.setMaxWidth(Double.MAX_VALUE);
        AnchorPane.setLeftAnchor(title, 0.0);
        AnchorPane.setRightAnchor(title, 0.0);
        title.setAlignment(Pos.CENTER);
    }

    private static String result = "no";
    public static String display(String title, String content){
            try {
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                FXMLLoader loader = new FXMLLoader(MsgBoxController.class.getResource("View/MsgBox2.fxml"));
                Parent calcRoot = loader.load();
                MsgBox2Controller controller = loader.getController();
                controller.setTitle(title);
                controller.setContext(content);
                Scene scene = new Scene(calcRoot);
                stage.setScene(scene);
                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
    }

    @FXML
    private void yesClick(MouseEvent event){
        result = "yes";
        Stage stage = (Stage) (root.getScene().getWindow());
        stage.close();
    }

    @FXML
    private void noClick(MouseEvent event){
        result = "no";
        Stage stage = (Stage) (root.getScene().getWindow());
        stage.close();
    }

}
