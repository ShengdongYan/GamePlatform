package myGame.Frames;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Light;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import myGame.Objects.Node;
import myGame.Objects.Snake;
import myGame.Objects.SnakeBody;
import org.json.JSONObject;
import sun.awt.image.ImageWatched;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


/**
 *This class is used to refresh and draw the objects on the canvas,
 * after add this canvas to the main scene, the objects can move dynamically
 * @author Shengdong Yan
 * @version 2019-3-9
 */
public class MyCanvas extends Canvas {

    private Snake snakeA;
    private Snake snakeB;
    private Node node;
    private GraphicsContext gc;
    private Timeline timeline;
    private KeyFrame keyFrame;
    private SnakeBody snakeBodyA;
    private SnakeBody snakeBodyB;
    private TimeCounter timeCounter;
    private Constants.GameState gameState;
    private Group root;
    private Label label;
    private  boolean isMaster;
    private String username;
    private boolean flag;




    public MyCanvas(Group root){
         this.root = root;
    }


    /**
     * use this method to initialize the two snakes and one node on the canvas,
     * for every player, locally he views himself as snakeA,
     * and if the player is master he would birth in the right part of the canvas
     */
    public void  initial(){
        node = new Node();
        this.setHeight(Constants.HEIGHT);
        this.setWidth(Constants.WIDTH);
        gc = this.getGraphicsContext2D();
        timeCounter = new TimeCounter(Constants.GameDuration);

        if(isMaster()){
            snakeA = new Snake(Constants.userAX,Constants.userAY,node);
            snakeBodyA = new SnakeBody(snakeA);
            snakeA.setColor(Color.BLUE);
            snakeA.setUserName(username);  // Update the userName here
            snakeBodyA.initBody();
            snakeA.setSnakeBody(snakeBodyA);

            snakeB = new Snake(Constants.userBX,Constants.userBY,node);
            snakeBodyB = new SnakeBody(snakeB);
            snakeB.setColor(Color.YELLOW);
            snakeB.setUserName("");
            snakeBodyB.initBody();
            snakeB.setSnakeBody(snakeBodyB);

        }
         else{
            snakeA = new Snake(Constants.userBX,Constants.userBY,node);
            snakeBodyA = new SnakeBody(snakeA);
            snakeA.setColor(Color.YELLOW);
            snakeA.setUserName("");
            snakeBodyA.initBody();
            snakeA.setSnakeBody(snakeBodyA);

            snakeB = new Snake(Constants.userAX,Constants.userAY,node);
            snakeBodyB = new SnakeBody(snakeB);
            snakeB.setColor(Color.BLUE);
            snakeB.setUserName(username);  // Update the userName here
            snakeBodyB.initBody();
            snakeB.setSnakeBody(snakeBodyB);

        }

        initTimeLine(gc);
//        drawStart();
        setGameState(Constants.GameState.RUN);

    }


    /**
     * use this method to deal with the key event
     * @param event the key pressed by gamer would be past here
     */
    public void onKeyPressed(KeyEvent event){
        KeyCode key = event.getCode();
        // change the gamestate into Puase when player press SPACE key and back when user press SPACE key again
        if(this.getGameState() == Constants.GameState.RUN){
            if(key==KeyCode.SPACE){
                setGameState(Constants.GameState.PAUSE);
                timeCounter.stopTimer();
            }
            else if(key == KeyCode.ESCAPE){
                    setGameState(Constants.GameState.TIMEOUT);
                    timeCounter.setTime(0);
            }
            else{
                snakeA.onKeyPressed(event);
            }
        }

        else if(this.getGameState() == Constants.GameState.PAUSE) {   // 在暂停状态，再一次空格回到run状态
            if (key == KeyCode.SPACE) {
                setGameState(Constants.GameState.RUN);
                timeCounter.startTimer();
            }
        }
    }


    /**
     * use this method to initialize a time line, which can refresh the canvas in a constant frequency
     * and detect the game state as well as receive message.
     * @param gc the main tools used to draw on the canvas
     */
    public void initTimeLine( GraphicsContext gc) {

        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        keyFrame = new KeyFrame(Duration.millis(Constants.duration), event -> {
                gc.setEffect(null);
                gc.clearRect(0, 0, getWidth(), getHeight());
                drawGridding(gc);

                JSONObject jsonObject = myGame.Client.getClient().getNewMessage().getMessageAsJson();
                JSONObject jsonObject1 = snakeToJSON(snakeA,snakeB);
                myGame.Client.getClient().sendMessage(jsonObject1.toString());
                if(jsonObject.has("time") && Calendar.getInstance().getTimeInMillis()-jsonObject.getLong("time")>5000) {
                    drawTimeout(gc);
                }
                else {
                    if (jsonObject.has("gameState") && Constants.GameState.valueOf(jsonObject.getString("gameState")) == Constants.GameState.RUN) {
                        if (getGameState() == Constants.GameState.RUN) {
                            draw(gc);
                            update();

                        } else if (getGameState() == Constants.GameState.PAUSE) {
                            drawPause(gc);
                        } else if (getGameState() == Constants.GameState.TIMEOUT) {
                            drawTimeout(gc);
                        }
                    } else if (jsonObject.has("gameState") && Constants.GameState.valueOf(jsonObject.getString("gameState")) == Constants.GameState.PAUSE) {
                        drawPause(gc);
                    } else if (jsonObject.has("gameState") && Constants.GameState.valueOf(jsonObject.getString("gameState")) == Constants.GameState.TIMEOUT) {
                         this.stop();
                        drawTimeout(gc);
                    }
                }
        });
        timeline.getKeyFrames().add(keyFrame);
    }


    /**
     * start the update timeline
     */
    public void start() {
        InfoPane.stopThread.set(true);
        timeline.play();
        if(isMaster){
        timeCounter.start();}
    }

    /**
     * pause the update timeline
     */
    public void pause() {
        timeline.pause();
    }

    /**
     * stop the update timeline
     */
    public void stop() {
        timeline.stop();

    }



    public void drawGridding(GraphicsContext gc){

        for (int i = 0; i < Constants.WIDTH; i += 18) {
            gc.setStroke(Color.web("#111", 0.5));
            gc.strokeLine(i,0, i, Constants.HEIGHT);
        }
        for (int i = 0; i < Constants.HEIGHT; i += 18) {
            gc.setStroke(Color.web("#111", 0.5));
            gc.strokeLine(0, i, Constants.WIDTH, i);

        }
    }


    public void drawScoreBoard(String usernameA, String usernameB){
        String InfoA = String.format("%-14s", usernameA)+": "+ snakeA.getScore();
        String InfoB = String.format("%-14s", usernameB)+": "+ snakeB.getScore();

        label = new Label(InfoA+"\n"+ InfoB);
        label.setLayoutX(Constants.WIDTH - 300);
        label.setLayoutY(Constants.HEIGHT - 800);
        label.setStyle("-fx-font: 25 arial; -fx-font-weight: bold; -fx-text-fill: Gray;  ");
        root.getChildren().add(label);
    }

    public void drawPause(GraphicsContext gc){
        gc.setFont(Font.font("Impact", FontWeight.BOLD, 80));
        gc.setFill(Color.YELLOW);
        gc.fillText("Game Paused", Constants.WIDTH/2 - 320, Constants.HEIGHT/2 -100);
        gc.setFont(Font.font("Impact", FontWeight.BOLD, 40));
        gc.setFill(Color.GREEN);
        gc.fillText("Press Space again to back", Constants.WIDTH / 2 - 300, Constants.HEIGHT / 2 + 20);
        gc.setFont(Font.font("Impact", FontWeight.BOLD, 40));
        gc.setFill(Color.RED);
        gc.fillText("Press Esc to Give up", Constants.WIDTH/2 - 220, Constants.HEIGHT/2 + 120);

    }

    public  void drawTimeout(GraphicsContext gc){
        String winningMessage = "Game Over";
        if (snakeA.getScore() > snakeB.getScore()){
            winningMessage += "\n " + snakeA.getUserName() + "you are Winner!";
            winningMessage += "\n Score:       " + snakeA.getScore();
        } else {
            winningMessage +="\n "+ snakeA.getUserName() + " you are loser";
            winningMessage += "\n Score:       " + snakeA.getScore();
        }

        gc.setFont(Font.font("Times", FontWeight.BOLD, 40));
        gc.setFill(Color.YELLOW);
        gc.fillText(winningMessage, Constants.WIDTH / 2 - 300, Constants.HEIGHT / 2);

        Button button = new Button();
        button.setText("Ok,Return");
        button.setLayoutX(Constants.WIDTH / 2 - 75);
        button.setLayoutY(Constants.HEIGHT - 150);
        button.setMinWidth(150);
        button.setMinHeight(70);
        button.setStyle("-fx-font: 24 arial; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: #ff4e4e; -fx-background-radius: 20; ");
        root.getChildren().add(button);

        button.setOnMouseClicked(event -> {
            System.exit(snakeA.getScore()>snakeB.getScore()?1:0);
        });

          this.stop();
    }


    /*
    * draw everything into the canvas
    * */
    public void draw(GraphicsContext gc){
        root.getChildren().remove(label);
        snakeBodyA.draw(gc);
        snakeA.drawSnake(gc);

        snakeBodyB.draw(gc);
        snakeB.drawSnake(gc);
        node.draw(gc);
        timeCounter.draw(gc);

        //if(snakeB.isCollisionWithSnake(snakeA)){snakeB.rebirth();snakeBodyB.initBody();}

    }

  /*update all the objects information,(read information of another snake from network and send self's data out)
  *
  * */
    public  void  update(){

        if(isMaster){

            JSONObject snakeBJson = myGame.Client.getClient().getNewMessage().getMessageAsJson();
            snakeB.setX(getX(snakeBJson));
            snakeB.setY(getY(snakeBJson));
            snakeB.setDirection(getDirection(snakeBJson));
            snakeBodyB.setPointlist(getSnakeBody(snakeBJson));
            snakeB.setScore(getScore(snakeBJson));
            snakeB.setUserName(getUsername(snakeBJson));

            if(node.getX()!= getNodeX(snakeBJson))
                node.setX(getNodeX(snakeBJson));
            if(node.getY()!=getNodeY(snakeBJson))
                node.setY(getNodeY(snakeBJson));


            if (timeCounter.getTime()<=0) {this.setGameState(Constants.GameState.TIMEOUT);  }
            if (snakeA.isGetNode()) {
                 node.update();
                 snakeA.setLength(snakeA.getLength()+1);
                 snakeA.setScore(snakeA.getScore()+1);
                snakeBodyA.update();  }
            if (snakeA.isReachBorder()){snakeA.rebirth(); snakeBodyA.initBody();}
            snakeA.setUserName(username);
            snakeA.update();
            snakeBodyA.update();
            JSONObject jsonObject = snakeToJSON(snakeA,snakeB);
            myGame.Client.getClient().sendMessage(jsonObject.toString());

            if(snakeA.isCollisionWithSnake(snakeB)){snakeA.rebirth();snakeBodyA.initBody();}
            drawScoreBoard(getUsername(snakeBJson),snakeA.getUserName());
        }
        else{


            JSONObject snakeBJson = myGame.Client.getClient().getNewMessage().getMessageAsJson();
            snakeB.setX(getX(snakeBJson));
            snakeB.setY(getY(snakeBJson));
            snakeB.setDirection(getDirection(snakeBJson));
            snakeBodyB.setPointlist(getSnakeBody(snakeBJson));
            snakeB.setScore(getScore(snakeBJson));
            snakeB.setUserName(getUsername(snakeBJson));
            timeCounter.setTime(getTimeCounter(snakeBJson));

            node.setX(getNodeX(snakeBJson));
            node.setY(getNodeY(snakeBJson));

             if(snakeA.isCollisionWithSnake(snakeB)){snakeA.rebirth();snakeBodyA.initBody();}
             if (snakeA.isGetNode()) {
                node.update();
                snakeA.setLength(snakeA.getLength()+1);
                snakeA.setScore(snakeA.getScore()+1);
                snakeBodyA.update();  }
            if (snakeA.isReachBorder()){snakeA.rebirth(); snakeBodyA.initBody();}
                 snakeA.update();
                 snakeBodyA.update();


            JSONObject jsonObject = snakeToJSON(snakeA,snakeB);
            myGame.Client.getClient().sendMessage(jsonObject.toString());
            drawScoreBoard(this.username,getUsername(snakeBJson));

        }


    }



    public Constants.GameState getGameState() {
        return gameState;
    }

    public void setGameState(Constants.GameState gameState) {
        this.gameState = gameState;

    }

    public JSONObject snakeToJSON(Snake snake,Snake snakeB){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("direction",snake.getDirection());
        jsonObject.put("snake_body",snake.getSnakeBody().getPointlistForN());
        jsonObject.put("x",snake.getX());
        jsonObject.put("y",snake.getY());
        jsonObject.put("score",snake.getScore());
        jsonObject.put("gameState",this.gameState.toString());
        jsonObject.put("nodeX",node.getX());
        jsonObject.put("nodeY",node.getY());
        jsonObject.put("timeCounter",timeCounter.getTime());
        jsonObject.put("length",snakeB.getLength());
        jsonObject.put("flag",flag);

        return jsonObject;
    }

    public double getX(JSONObject jsonObject){
        return jsonObject.getDouble("x");
    }

    public double getY(JSONObject jsonObject){
        return jsonObject.getDouble("y");
    }

    public int getScore(JSONObject jsonObject){
        return jsonObject.getInt("score");
    }

    int getNodeX(JSONObject jsonObject){return jsonObject.getInt("nodeX");}
    int getNodeY(JSONObject jsonObject){ return  jsonObject.getInt("nodeY");}
    int getTimeCounter(JSONObject jsonObject){return jsonObject.getInt("timeCounter");}
    String getUsername(JSONObject jsonObject){return jsonObject.getString("username");}
    int getLength(JSONObject jsonObject){return jsonObject.getInt("length");}
    boolean getFlag(JSONObject jsonObject){return jsonObject.getBoolean("flag");}

    public LinkedList<Light.Point> getSnakeBody(JSONObject jsonObject){
        List<Object> sb = jsonObject.getJSONArray("snake_body").toList();
        List<Light.Point> temp = sb.stream().map(x->stringToPoint(String.valueOf(x))).collect(Collectors.toList());
        LinkedList<Light.Point> result = new LinkedList<>();
        result.addAll(temp);
        return result;
    }

    public Snake.DIRECTIONS getDirection(JSONObject jsonObject){
        return Snake.DIRECTIONS.valueOf(jsonObject.getString("direction"));
    }

    public Light.Point stringToPoint(String str){
        String[] s = str.split(" ");
        Light.Point p = new Light.Point();
        p.setX(Double.valueOf(s[0]));
        p.setY(Double.valueOf(s[1]));
        return p;
    }

    public void setMaster(boolean isMaster){
        this.isMaster = isMaster;
    }
    public  boolean isMaster(){
        return this.isMaster;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
