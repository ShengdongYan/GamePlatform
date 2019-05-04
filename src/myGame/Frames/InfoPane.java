package myGame.Frames;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import myGame.Objects.Snake;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * This class is for when players where players of the game wait for each other to be ready,
 * The user clicks the ready button, once both users click this button the game begins.
 * @author Greg Field
 */

public class InfoPane {
    private Snake snakeA;
    private Snake snakeB;
    private Button user1Button, user2Button;
    private Circle user1Circle, user2Circle;
    private String username1, totalGames1, gamesWon1;
    private String username2, totalGames2, gamesWon2;
    private MyCanvas myCanvas;
    public static AtomicBoolean stopThread = new AtomicBoolean(false);
    // reference to games stage and scene
    private Stage stage;
    private Scene scene;
    private boolean master;

    Pane gameInfo;

    private int buttonWidth = 200;
    private int buttonHeight = 50;
    private Font font = new Font("Impact", 30);

    public AtomicBoolean user1isReady = new AtomicBoolean(false);
    public AtomicBoolean user2isReady = new AtomicBoolean(false);


    public InfoPane(Stage stage, Scene scene, MyCanvas myCanvas) {
        this.gameInfo = new Pane();
        gameInfo.setMinSize(Constants.WIDTH, Constants.HEIGHT);
        this.stage = stage;
        this.scene = scene;
        gameInfo.setStyle("-fx-background-color: #393E46");
        this.myCanvas = myCanvas;
    }

    public void initialize() {
        double user1X;
        double user1Y;
        double user2X;
        double user2Y;
        if(!GameStage.isMaster) {
            user1X = gameInfo.getMinWidth() / 2 - 350;
            user1Y = gameInfo.getMinHeight() / 2;
            user2X = gameInfo.getMinWidth() / 2 + 150;
            user2Y = gameInfo.getMinHeight() / 2;
        }
        else{
            user2X = gameInfo.getMinWidth() / 2 - 350;
            user2Y = gameInfo.getMinHeight() / 2;
            user1X = gameInfo.getMinWidth() / 2 + 150;
            user1Y = gameInfo.getMinHeight() / 2;
        }

        // Create Buttons to let the game know each player is ready
        user1Button = new Button();
        user1Button.setLayoutX(user1X + 50);
        user1Button.setLayoutY(user1Y);
        user1Button.setPrefWidth(buttonWidth);
        user1Button.setPrefHeight(buttonHeight);
        user1Button.setStyle("-fx-background-color: #00ADB5; -fx-border-width: 5px; " +
                "-fx-text-fill: White; -fx-font-weight: Bold; -fx-font-size: 28");
        user1Button.setText("Ready");

        user2Button = new Button();
        user2Button.setLayoutX(user2X - 50);
        user2Button.setLayoutY(user2Y);
        user2Button.setPrefWidth(buttonWidth);
        user2Button.setPrefHeight(buttonHeight);
        user2Button.setStyle("-fx-background-color: #00ADB5; -fx-border-width: 5px; " +
                "-fx-text-fill: White; -fx-font-weight: Bold; -fx-font-size: 28");
        user2Button.setText("Ready");

        // if both players are ready start game
        user1Button.setOnAction(e -> {

            user1isReady.set(true);
            user1Button.setStyle("-fx-background-color: Green; -fx-font-weight: Bold; -fx-font-size: 28; ");
            user1Button.setDisable(true);
            user1Button.setText("Waiting");
//            if (user1isReady.get() && user2isReady.get()) {
//
//                stage.setScene(scene);
//                myCanvas.start();
//            }
        });
        Text userNameB = new Text("User Name: " + username2 + "\n");
           Thread t = new Thread(() -> {
            while (true) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("isReady", user1isReady.get());
                myGame.Client.getClient().sendMessage(jsonObject.toString());
                JSONObject message = myGame.Client.getClient().getNewMessage().getMessageAsJson();

                if(message.has("username"))
                    Platform.runLater(()->{userNameB.setText("User Name: " +message.getString("username")+"\n");});
                if(message.has("time")&& Calendar.getInstance().getTimeInMillis()-message.getLong("time")>20000){
                    System.exit(0);
                }
//                snakeB.setUserName(message.has("username")?message.getString("username"):"no name");
                if (message.has("isReady") && message.getBoolean("isReady")) {
                    user2isReady.set(true);
                    user2Button.setStyle("-fx-background-color: Green; -fx-font-weight: Bold; -fx-font-size: 28; ");
                    Platform.runLater(()->{
                        user2Button.setDisable(true);
                        user2Button.setText("Waiting");
                    });
                    if (user1isReady.get() && user2isReady.get()) {
                        Platform.runLater(() -> {
                            stage.setScene(scene);
                            myCanvas.setUsername(GameStage.username);
                            myCanvas.start();
                        });
                    }
                }
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(stopThread.get()){
                    break;
                }
            }
        });
        t.setDaemon(true);
        t.start();


        // Users info goes here
             totalGames1 ="0";
             totalGames2 ="0";
             gamesWon1 = "0";
             gamesWon2 = "0";


        username1= GameStage.username;
        Text userNameA = new Text("User Name: " + username1 + "\n");
        Text totalGamesA = new Text("Total Games: " + totalGames1 + "\n");
        Text gamesWonA = new Text("Games Won: " + gamesWon1 + "\n");

        userNameA.setFont(font);
        userNameA.setFill(Color.rgb(255, 255, 255));
        totalGamesA.setFont(font);
        totalGamesA.setFill(Color.rgb(255, 255, 255));
        gamesWonA.setFont(font);
        gamesWonA.setFill(Color.rgb(255, 255, 255));

        TextFlow textBoxA = new TextFlow(userNameA, totalGamesA, gamesWonA);
        textBoxA.setTextAlignment(TextAlignment.CENTER);
        textBoxA.setPrefSize(400, 0);
        textBoxA.setLayoutX(100);
        textBoxA.setLayoutY(user1Y + 100);
        textBoxA.setStyle("-fx-border-radius: 10 10 0 0;");
        textBoxA.setStyle("-fx-background-radius: 10 10 0 0;");
        textBoxA.setStyle("-fx-background-color: rgb(48, 48, 48)");
        textBoxA.setLineSpacing(40);


        Text totalGamesB = new Text("Total Games: " + totalGames2 + "\n");
        Text gamesWonB = new Text("Games Won: " + gamesWon2 + "\n");

        userNameB.setFont(font);
        userNameB.setFill(Color.rgb(255, 255, 255));
        totalGamesB.setFont(font);
        totalGamesB.setFill(Color.rgb(255, 255, 255));
        gamesWonB.setFont(font);
        gamesWonB.setFill(Color.rgb(255, 255, 255));

        TextFlow textBoxB = new TextFlow(userNameB, totalGamesB, gamesWonB);
        textBoxB.setTextAlignment(TextAlignment.CENTER);
        textBoxB.setPrefSize(400, 100);
        textBoxB.setLayoutX(gameInfo.getMinWidth() / 2);
        textBoxB.setLayoutY(user1Y + 100);
        textBoxB.setStyle("-fx-padding: 50");
        textBoxB.setStyle("-fx-border-radius: 10 10 0 0;");
        textBoxB.setStyle("-fx-background-radius: 10 10 0 0;");
        textBoxB.setStyle("-fx-background-color: rgb(48, 48, 48)");
        textBoxB.setLineSpacing(40);


        displayGameRules();

        // add to pane
        gameInfo.getChildren().addAll(user1Button, user2Button, textBoxA, textBoxB);
    }

    void setUser1Info(String username, String totalGames, String gamesWon) {
        username1 = username;
        totalGames1 = totalGames;
        gamesWon1 = gamesWon;
    }

    void setUser2Info(String username, String totalGames, String gamesWon) {
        username2 = username;
        totalGames2 = totalGames;
        gamesWon2 = gamesWon;
    }
  public void setUsername1(String username1){
        this.username1=username1;
  }
  public void setUsername2(String username2){
      this.username2= username2;
  }

    public void setGamesWon1(String gamesWon1) {
        this.gamesWon1 = gamesWon1;
    }

    public String getGamesWon1() {
        return gamesWon1;
    }

    public void setGamesWon2(String gamesWon2) {
        this.gamesWon2 = gamesWon2;
    }

    public String getGamesWon2() {
        return gamesWon2;
    }

    public void setTotalGames1(String totalGames1) {
        this.totalGames1 = totalGames1;
    }

    public void setTotalGames2(String totalGames2) {
        this.totalGames2 = totalGames2;
    }

    public String getTotalGames1() {
        return totalGames1;
    }

    public String getTotalGames2() {
        return totalGames2;
    }

    private void displayGameRules() {
        Font titleFont = new Font("Impact", 82);
        Text gameTitle = new Text("Greedy Snake");
        gameTitle.setFont(titleFont);
        gameTitle.setLayoutX(Constants.WIDTH / 2 - 230);
        gameTitle.setLayoutY(80);
        gameTitle.setTextAlignment(TextAlignment.CENTER);
        gameTitle.setFill(Color.WHITE);


        titleFont = new Font("Impact", 52);
        Text gameRulesTitle = new Text("Game Rules\n");
        gameRulesTitle.setFont(titleFont);
        gameRulesTitle.setFill(Color.WHITE);
        Text gameRules1 = new Text("Use SPACE to pause the game\n");
        gameRules1.setFont(font);
        gameRules1.setFill(Color.WHITE);
        Text gameRules2 = new Text("Arrow keys to move the snake\n");
        gameRules2.setFont(font);
        gameRules2.setFill(Color.WHITE);
        Text gameRules3 = new Text("Eat the food to gain points and grow\n");
        gameRules3.setFont(font);
        gameRules3.setFill(Color.WHITE);
        Text gameRules4 = new Text("You will lose your points if you hit another snake\n");
        gameRules4.setFont(font);
        gameRules4.setFill(Color.WHITE);
        Text gameRules5 = new Text("OR if you go outside the game area\n");
        gameRules5.setFont(font);
        gameRules5.setFill(Color.WHITE);
        TextFlow gameRulesField = new TextFlow(gameRulesTitle, gameRules1, gameRules2, gameRules3,
                gameRules4, gameRules5);

        gameRulesField.setTextAlignment(TextAlignment.CENTER);
        gameRulesField.setLayoutX(Constants.WIDTH / 2 - 290);
        gameRulesField.setLayoutY(Constants.HEIGHT / 2 - 275);
        gameInfo.getChildren().addAll(gameRulesField, gameTitle);
    }

    public void setMaster(boolean master) {
        this.master = master;
    }
    public boolean isMaster(){
        return this.master;
    }
}
