package Client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * Created by bxs863 on 05/03/19.
 */
public class MsgBoxController {


    @FXML
    private Label title;
    @FXML
    private Text context;
    @FXML
    private AnchorPane root;


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


    public static void display(String title, String content) {
        try {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            FXMLLoader loader = new FXMLLoader(MsgBoxController.class.getResource("View/MsgBox.fxml"));
            Parent calcRoot = loader.load();
            MsgBoxController controller = loader.getController();
            controller.setTitle(title);
            controller.setContext(content);
            Scene scene = new Scene(calcRoot);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void quit(MouseEvent mouseEvent) {
        Stage stage = (Stage) (root.getScene().getWindow());
        stage.close();
    }
}