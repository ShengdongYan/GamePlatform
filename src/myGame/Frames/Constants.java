package myGame.Frames;


/**
 *This class is used to store the constants which would be used during the game process, like the width and height of the stage
 * @author Shengdong Yan
 * @version 2019-03-07
 */
public class Constants {
	/**
	 * The four kinds of game states
	 */
	public enum GameState{READY, PAUSE, RUN, TIMEOUT};

	/**
	 * The size of the stage
	 */
	public static final int WIDTH = 1000, HEIGHT = 800;
	/**
	 * The size of every point of snake body.
	 */
	public static final int UnitHeight = 22, UnitWidth = 22;
	/**
	 * The distance of the points in snakebody
	 */
	public static final int DISTANCE = 8;

	/*
	* The defualt birth palce of Snake. Here userAX means the x,y values of the master player's birth place.
	* */
	public static final int userAX = WIDTH * 3 / 4;
	public static final int userAY = HEIGHT - 150;

	public static final int userBX = WIDTH / 4;
	public static final int userBY = HEIGHT - 150;

	public static final int DEFAULT_LENGTH = 6;   // defualt snake length

	public static final int GameDuration = 120; //  length of game in seconds

	public static final int duration = 80;  //   refresh rate

	public static final double speed = 12;  // player move speed

}
