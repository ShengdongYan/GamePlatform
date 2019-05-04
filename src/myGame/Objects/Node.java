package myGame.Objects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Glow;
import javafx.scene.effect.MotionBlur;
import javafx.scene.paint.Color;
import myGame.Frames.Constants;
import java.util.Random;

/**
 *This class is used to generate the 'food' for snakes every time the node be eaten, another node would be generated randomly.
 * @author Shengdong Yan
 * @version 2019-03-07
 */
public class Node {
    private Color color;
    private int height, width, x,y;
    private boolean visible;




    public  Node(){
        this.init();
    }

    public void init(){
       this.setX((int) Constants.WIDTH/2);
       this.setY((int)Constants.HEIGHT/2);
       this.height = Constants.UnitHeight;
       this.width = Constants.UnitWidth;
       this.setVisible(true);
       this.color = Color.WHITE;

    }
    public void draw(GraphicsContext gc){
        Glow glow = new Glow(2);
        gc.setEffect(glow);
        gc.setFill(color);
        gc.fillOval(getX(),getY(),getWidth(),getHeight());
        gc.setEffect(null);

    }
    public void update(){
        if (!isVisible()) {return;}

        this.setX((int)(30 + Math.random() * (Constants.WIDTH-60)));
        this.setY((int)(30 + Math.random() * (Constants.HEIGHT-60)));


        // random colour that will always be a light colour, eg > 0.3
        Random random = new Random();
        double red = random.nextFloat() / 2f + 0.3;
        double green = random.nextFloat() / 2f + 0.3;
        double blue = random.nextFloat() / 2f + 0.3;



        this.color = Color.color(red, green, blue);
    }

    public Color getColor() {
        return color;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }


}
