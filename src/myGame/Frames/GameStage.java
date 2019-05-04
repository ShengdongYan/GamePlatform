package myGame.Frames;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.Light;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 *This class is used to generate the main stage of the game, the key idea is use a waiting scene to show the ready infomation pane
 *  and a startGameScene to show the game pane.
 * @author Shengdong Yan
 * @version 2019-03-10
 */
public class GameStage extends Application {



    /**
     * @param list  use to store the points values of a snakebody
     * @param gameCanvas  use to refresh and draw objects like snake and food.
     * @param gameInfo  to show the hints and information of the two players
     * @param isMaster  to decide whether local player is the master of the game
     */
    public  Stage stage = new Stage();
    public  Group root;
    public  LinkedList<Light.Point> list = new LinkedList<Light.Point>();
    public static MyCanvas gameCanvas;
    public InfoPane gameInfo;
    Scene waitingScene, StartGameScene;
    public static boolean isMaster;
    public static String username;


    /**
     * This method is mainly used to do some initialization and play the ready scene.
     * @param primaryStage the main stage of the game
     */
    @Override
    public void start(Stage primaryStage)  {

        this.stage = primaryStage;
        stage.setResizable(false);
        primaryStage.setTitle("Greedy Snake!");
        root = new Group();

        // Game Setup
        gameCanvas = new MyCanvas(root);
        gameCanvas.setMaster(isMaster);
        gameCanvas.setUsername(username);
        root.getChildren().add(gameCanvas);
        StartGameScene = new Scene(root);
        StartGameScene.setFill(Color.rgb(60,60,60));
        StartGameScene.setOnKeyPressed(event -> gameCanvas.onKeyPressed(event));
        gameCanvas.initial();
        // Waiting Screen Setup
        gameInfo = new InfoPane(stage,StartGameScene,gameCanvas);
        gameInfo.setUser1Info("","0", "0");
        gameInfo.setUser2Info("","0","0");

        gameInfo.setMaster(isMaster);
        gameInfo.initialize();
        waitingScene = new Scene(gameInfo.gameInfo);
        primaryStage.setScene(waitingScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        myGame.Client.getClient().setGameID(args[0]);

        myGame.Client.getClient().setUsername(args[1]);    // 1 is username of current player; 2 is the master name of the game.
        isMaster = args[1].equals(args[2]);               // decide whether is master or not

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type","register");
        myGame.Client.getClient().sendMessage(jsonObject.toString());
        username = args[1];

        launch(args);
    }
}
