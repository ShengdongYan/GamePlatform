package myGame.Objects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Light;
import myGame.Frames.Constants;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SnakeBody {

    private LinkedList<Light.Point> pointlist = new LinkedList<>();
    private  Snake snake ;
    private boolean visible;
    private double x;
    private double y;
    private  double height;
    private  double width;
    private int length;


    /**
     *This class is used to generate the snake body, which is stored as a linked list
     * @author Shengdong Yan
     * @version 2019-03-07
     */
    public SnakeBody(Snake snake){

         this.snake = snake;
         initBody();
         setVisible(true);
         x=snake.getX();
         y=snake.getY();
         length= Constants.DEFAULT_LENGTH;
     }
     public void initBody(){
          pointlist.clear();
          length= Constants.DEFAULT_LENGTH;
           for (int i = 0; i < getLength(); i++) {
             Light.Point point = new Light.Point();
             point.setX(getX() );
             point.setY(getY()+ Constants.DISTANCE * i);
             pointlist.add(point);
         }
     }

 /*The theory is the snake body is a LinkedList,
  *every time the snake move to a new position
  * there will generate a new first point in the List
  * and the Last Point in the list will be removed
 * */
    public void draw(GraphicsContext gc) {
        gc.setFill(snake.getColor());
        for (Light.Point point : pointlist) {
            gc.fillOval(point.getX(), point.getY(), getWidth(), getHeight());
        }
    }

    public void update() {

        if (!isVisible()) {
            initBody();
            setVisible(true);
            return;
        }

        Light.Point firstPoi = pointlist.getFirst();

        if (firstPoi.getX() + Constants.DISTANCE <= snake.getX() || firstPoi.getX() - Constants.DISTANCE >= snake.getX()
                || firstPoi.getY() + Constants.DISTANCE <= snake.getY() || firstPoi.getY() - Constants.DISTANCE >= snake.getY()) {
            Light.Point poi = new Light.Point();
            poi.setX(getX());
            poi.setY(getY());
            // add a new first point
            pointlist.addFirst(poi);
            // if the snake get a food the last point will not be removed.
            if (this.length < snake.getLength()) {
                this.length = this.length + 1;
            } else {
                // remove the last point
                pointlist.removeLast();
            }
        }

    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Snake getSnake() {
        return snake;
    }

    public boolean isVisible() {
        return visible;
    }

    public LinkedList<Light.Point> getPointlist() {
        return pointlist;
    }

    public LinkedList<String> getPointlistForN(){
        LinkedList<String> result = new LinkedList<>();
        List<String> l = pointlist.stream().map(this::pointToString).collect(Collectors.toList());
        result.addAll(l);
        return result;
    }

    public void setPointlist(LinkedList<Light.Point> pointlist) {
        this.pointlist = pointlist;
    }

    public void setSnake(Snake snake) {
        this.snake = snake;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public double getX() {
        return snake.getX();
    }

    public double getY() {
        return snake.getY();
    }

    public double getHeight() {
        return snake.getHeight();
    }

    public double getWidth() {
        return snake.getWidth();
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public String pointToString(Light.Point p){
        String str = "";
        str = String.valueOf(p.getX())+" "+String.valueOf(p.getY());
        return str;
    }


}
