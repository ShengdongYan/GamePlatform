package myGame.Objects;

import javafx.scene.canvas.GraphicsContext;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import myGame.Frames.Constants;


/**
 *This class is used to generate snake objects
 * @author Shengdong Yan
 * @version 2019-03-07
 */
public class Snake {
  public enum DIRECTIONS {
      DIR_UP, DIR_DOWN, DIR_LEFT, DIR_RIGHT, DIR_LEFTUP, DIR_LEFTDOWN, DIR_RIGHTUP, DIR_RIGHTDOWN
    }
    private double x;
    private double y;
    private double height;
    private double width;
    private DIRECTIONS direction ;
    private boolean visible;
    private boolean updated;
    private int length;
    private Color color;
    private Node node;
    private SnakeBody snakeBody;
    private int Score;
    private String userName;
    private double initX, initY;

    /**
     * @param x the x value of snake head
     * @param y the y value of snake head
     * @param node the node on the map
     */
    public Snake(double x, double y,Node node ) {
        this.initX = x;
        this.x=x;
        this.initY = y;
        this.y = y;
        this.height = Constants.UnitHeight;
        this.width = Constants.UnitWidth;
        this.setDirection(DIRECTIONS.DIR_UP);
        this.length = Constants.DEFAULT_LENGTH;
        this.node = node;
        Score = 0;


    }


    /**
     * draw the snake head as a oval, and draw the eyes relatively.
     * @param gc the tools to draw the snake on canvas
     */
    public  void  drawSnake(GraphicsContext gc){
        gc.setFill(getColor());
        gc.fillOval(getX(),getY(),getWidth(),getHeight());

        if (this.getDirection() == DIRECTIONS.DIR_DOWN || this.getDirection() == DIRECTIONS.DIR_UP) {
            gc.setFill(Color.web("#fff"));
            gc.fillOval(getX() - 2,getY() + 3, getWidth() / 2, getHeight() / 2);
            gc.fillOval(getX() + getWidth()/2, getY() + 3, getWidth() / 2, getHeight() / 2);

            gc.setFill(Color.web("#333"));
            gc.fillOval(getX() - 2 + 2,getY() + 3 +2, getWidth() / 3, getHeight() / 3);
            gc.fillOval(getX() + getWidth()/2 +2, getY() + 3 +2, getWidth() / 3, getHeight() / 3);
        } else if (this.getDirection() == DIRECTIONS.DIR_LEFT || this.getDirection() == DIRECTIONS.DIR_RIGHT) {
            gc.setFill(Color.web("#fff"));
            gc.fillOval(getX() + 3, getY() - 1,getWidth() / 2, getHeight() / 2);
            gc.fillOval(getX() + 3, getY() + getWidth()/2, getWidth() / 2, getHeight() / 2);

            gc.setFill(Color.web("#333"));
            gc.fillOval(getX() + 3 + 2, getY() + 2,getWidth() / 3, getHeight() / 3);
            gc.fillOval(getX() + 3 + 2, getY() + getWidth()/2 + 2, getWidth() / 3, getHeight() / 3);
        } else if (this.getDirection() == DIRECTIONS.DIR_LEFTUP || this.getDirection() == DIRECTIONS.DIR_RIGHTDOWN) {
            gc.setFill(Color.web("#fff"));
            gc.fillOval(getX() + 2,                getY() + 1.1 * getWidth()/2,                getWidth() / 2, getHeight() / 2);
            gc.fillOval(getX() + 2 + getWidth()/2, getY() + 1.1 * getWidth()/2 - getWidth()/2, getWidth() / 2, getHeight() / 2);

            gc.setFill(Color.web("#333"));
            gc.fillOval(getX() + 2 + 2,getY() + 1.1 * getWidth() / 2 + 2,getWidth() / 3, getHeight() / 3);
            gc.fillOval(getX() + 2 + getWidth() / 2 + 2, getY() + 1.1 * getWidth() / 2 - getWidth() / 2 + 2, getWidth() / 3, getHeight() / 3);

        } else if (this.getDirection() == DIRECTIONS.DIR_LEFTDOWN || this.getDirection() == DIRECTIONS.DIR_RIGHTUP) {
            gc.setFill(Color.web("#fff"));
            gc.fillOval(getX(), getY(), getWidth() / 2, getHeight() / 2);
            gc.fillOval(getX() + getWidth() / 2, getY() + getWidth() / 2, getWidth() / 2, getHeight() / 2);

            gc.setFill(Color.web("#333"));
            gc.fillOval(getX() + 2, getY() + 2, getWidth() / 3, getHeight() / 3);
            gc.fillOval(getX() + getWidth() / 2 + 2, getY() + getWidth() / 2 + 2, getWidth() / 3, getHeight() / 3);

        }
    }

    /**
     * upadte the x and y of snake according to the snake direction and speed.
     */
    public  void  update(){
      //  if(this.isReachBorder()){rebirth();}
        if (this.getDirection() == DIRECTIONS.DIR_UP) {
            moveY(-Constants.speed);
        } else if (this.getDirection() == DIRECTIONS.DIR_DOWN) {
            moveY(Constants.speed);
        } else if (this.getDirection() == DIRECTIONS.DIR_LEFT) {
            moveX(-Constants.speed);
        } else if (this.getDirection() == DIRECTIONS.DIR_RIGHT) {
            moveX(Constants.speed);
        } else if (this.getDirection() == DIRECTIONS.DIR_LEFTUP) {
            moveX(-Constants.speed * Math.sqrt(2)/2);
            moveY(-Constants.speed * Math.sqrt(2)/2);
        } else if (this.getDirection() == DIRECTIONS.DIR_LEFTDOWN) {
            moveX(-Constants.speed * Math.sqrt(2)/2);
            moveY(Constants.speed * Math.sqrt(2)/2);
        } else if (this.getDirection() == DIRECTIONS.DIR_RIGHTUP) {
            moveX(Constants.speed * Math.sqrt(2)/2);
            moveY(-Constants.speed * Math.sqrt(2)/2);
        } else if (this.getDirection() == DIRECTIONS.DIR_RIGHTDOWN) {
            moveX(Constants.speed * Math.sqrt(2)/2);
            moveY(Constants.speed * Math.sqrt(2)/2);
        }

    }

    /**
     * change the direction
     * @param event key event
     */
    public void onKeyPressed(KeyEvent event) {

        KeyCode tmpCode = event.getCode();
        // prevent snake from moving in opposite direction
        if ((tmpCode == KeyCode.UP && direction == DIRECTIONS.DIR_DOWN)
                || (tmpCode == KeyCode.DOWN && direction == DIRECTIONS.DIR_UP)
                || (tmpCode == KeyCode.RIGHT && direction == DIRECTIONS.DIR_LEFT)
                || (tmpCode == KeyCode.LEFT && direction == DIRECTIONS.DIR_RIGHT)
        ) {
            return;
        }

        updateDir(tmpCode, direction);
    }
    public void updateDir(KeyCode keyCode, DIRECTIONS dir) {

        if (keyCode == KeyCode.UP) {
            switch (dir) {
                case DIR_LEFTDOWN:
                    direction = DIRECTIONS.DIR_LEFT;
                    break;
                case DIR_LEFT:
                    direction = DIRECTIONS.DIR_LEFTUP;
                    break;
                case DIR_LEFTUP:
                    direction = DIRECTIONS.DIR_UP;
                    break;
                case DIR_RIGHTDOWN:
                    direction = DIRECTIONS.DIR_RIGHT;
                    break;
                case DIR_RIGHT:
                    direction = DIRECTIONS.DIR_RIGHTUP;
                    break;
                case DIR_RIGHTUP:
                    direction = DIRECTIONS.DIR_UP;
                    break;
                default:
                    break;
            }
        } else if (keyCode == KeyCode.DOWN) {
            switch (dir) {
                case DIR_LEFTDOWN:
                    direction = DIRECTIONS.DIR_DOWN;
                    break;
                case DIR_LEFT:
                    direction = DIRECTIONS.DIR_LEFTDOWN;
                    break;
                case DIR_LEFTUP:
                    direction = DIRECTIONS.DIR_LEFT;
                    break;
                case DIR_RIGHTDOWN:
                    direction = DIRECTIONS.DIR_DOWN;
                    break;
                case DIR_RIGHT:
                    direction = DIRECTIONS.DIR_RIGHTDOWN;
                    break;
                case DIR_RIGHTUP:
                    direction = DIRECTIONS.DIR_RIGHT;
                    break;
                default:
                    break;
            }
        } else if (keyCode == KeyCode.LEFT) {
            switch (dir) {
                case DIR_LEFTUP:
                    direction = DIRECTIONS.DIR_LEFT;
                    break;
                case DIR_LEFTDOWN:
                    direction = DIRECTIONS.DIR_LEFT;
                    break;
                case DIR_UP:
                    direction = DIRECTIONS.DIR_LEFTUP;
                    break;
                case DIR_DOWN:
                    direction = DIRECTIONS.DIR_LEFTDOWN;
                    break;
                case DIR_RIGHTUP:
                    direction = DIRECTIONS.DIR_UP;
                    break;
                case DIR_RIGHTDOWN:
                    direction = DIRECTIONS.DIR_DOWN;
                    break;
                default:
                    break;
            }
        } else if (keyCode == KeyCode.RIGHT) {
            switch (dir) {
                case DIR_LEFTUP:
                    direction = DIRECTIONS.DIR_UP;
                    break;
                case DIR_LEFTDOWN:
                    direction = DIRECTIONS.DIR_DOWN;
                    break;
                case DIR_UP:
                    direction = DIRECTIONS.DIR_RIGHTUP;
                    break;
                case DIR_DOWN:
                    direction = DIRECTIONS.DIR_RIGHTDOWN;
                    break;
                case DIR_RIGHTUP:
                    direction = DIRECTIONS.DIR_RIGHT;
                    break;
                case DIR_RIGHTDOWN:
                    direction = DIRECTIONS.DIR_RIGHT;
                    break;
                default:
                    break;
            }
        }
    }
     public void rebirth(){
        this.setX(initX);
        this.setY(initY);
        this.setLength(Constants.DEFAULT_LENGTH);
        this.setDirection(DIRECTIONS.DIR_UP);
        this.setScore(0);

    }
    public boolean isReachBorder(){
        if (getX() + getWidth() >= Constants.WIDTH || getY() + getHeight() >= Constants.HEIGHT
                || getX() < 0 || getY() < 0){
            rebirth();
            return true;
        }
        return false;
    }

    public boolean isGetNode(){
        if (getX() + getWidth() > node.getX() && getX() < node.getX() + node.getWidth()
                && getY() + getHeight()  > node.getY() && getY() < node.getY() + node.getHeight()){

            return true;
        }
        return false;
    }


   public boolean isCollisionWithSnake(Snake snake) {
        for (int i =0; i< snake.getLength();i++){
            if (getX() + getWidth() >= snake.getSnakeBody().getPointlist().get(i).getX() && getX() < snake.getSnakeBody().getPointlist().get(i).getX() + snake.getWidth()
                    && getY() + getHeight()  > snake.getSnakeBody().getPointlist().get(i).getY() && getY() < snake.getSnakeBody().getPointlist().get(i).getY() + snake.getHeight() ) {
                return true;
            }
        }
        return false;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public SnakeBody getSnakeBody() {
        return this.snakeBody;
    }

    public void setSnakeBody(SnakeBody snakeBody) {
        this.snakeBody = snakeBody;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
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

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public boolean isUpdated() {
        return updated;
    }

    public DIRECTIONS getDirection() {
        return direction;
    }

    public void setDirection(DIRECTIONS direction) {
        this.direction = direction;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setColor(Color color) {
        this.color = color;
    }
    public Color getColor() {
        return this.color;
    }

    public int getScore() {
        return Score;
    }

    public void setScore(int score) {
        Score = score;
    }

    public void moveX(double x) {
        this.setX(getX() + x);
    }

    public void moveY(double y) {
        this.setY(getY() + y);
    }



}
