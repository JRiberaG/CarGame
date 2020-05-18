package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * TODO:
 * 	- Comment
 */
public class CarGame extends ApplicationAdapter implements InputProcessor{
	static float LANE1;
	static float LANE2;
	static float LANE3;
	static float LANE4;
    static int SCREEN_WIDTH;
	static int SCREEN_HEIGHT;

	private static final int START_LIVES = 4;
	private static final int START_SCORE = 0;
	private static final int START_SPEED = 4;
	private static final int START_MOVTAPPED = 35;
	private static final int START_MOVDRAGGED = 20;
	private static final int CAR_W = 128;
	private static final int CAR_H = 256;

	private SpriteBatch batch;
	private ArrayList<Texture> imgCars;
	private ArrayList<Texture> imgLives;
	private Texture imgLine;
	private Texture imgPause;

	private ShapeRenderer sRenderer;

	private Rectangle ownCar;

	private BitmapFont fontHeader;
	private BitmapFont fontSubHeader;
	private BitmapFont fontInfo;

	static int score;
	private int lives;
	static final int SCORE_JUMP = 10;
	private long startTime;
	private long currentTime;
	private long startTimePause;
	private long timeElapsedPause;
	static float speed;
	private float laneWidth;
	private float lineWidth;
	private float defXOwnCar;
	private float defYOwnCar;
	private float defXCar1;
	private float defXCar2;
	private float defXCar3;
	private float defXCar4;
	private float defYCar1;
	private float defYCar2;
	private float defYCar3;
	private float defYCar4;
	static float moveTapped;
	static float moveDragged;
	static final float multiplierTapped = 5f;
	static final float multiplierDragged = 2.5f;

	private float interactionBarHeight;
	private float divInfoWidth;
	private float directionBarHeight;
	private float directionBarWidth;

	private FreeTypeFontGenerator generatorHeader;
	private FreeTypeFontGenerator generatorSubHeader;
	private FreeTypeFontGenerator generatorInfo;

	private EnemyCar tEnemy1;
	private EnemyCar tEnemy2;
	private EnemyCar tEnemy3;
	private EnemyCar tEnemy4;
	private static ArrayList<EnemyCar> listEnemyCars;

	private boolean debug;
	private boolean paused;
	private boolean started;
	private boolean gameOver;
	private boolean inmortal;
	private String inputForDebug;

	static Randomizer randomizer;
	private static Inmortalizer inmortalizer;
	private static ArrayList<LaneSeparator> laneSeparators;

	@Override
	public void create () {
		Gdx.input.setInputProcessor(this);
		score = START_SCORE;
		lives = START_LIVES;
		speed = START_SPEED;

		// The width and height of the screen is gotten
		SCREEN_WIDTH = Gdx.graphics.getWidth();
		SCREEN_HEIGHT = Gdx.graphics.getHeight();

		// Size of each lane (screen is divided in 6 (4 road + 2 lawn))
		laneWidth = (float)SCREEN_WIDTH / 6;
		// Size of each dividing line of the road (line that separates the road from the lawn)
		lineWidth = laneWidth / 15;

		fontHeader = new BitmapFont();
		fontSubHeader = new BitmapFont();
		fontInfo = new BitmapFont();
		generatorHeader = new FreeTypeFontGenerator(Gdx.files.internal("fonts/righteous_regular.ttf"));
		generatorSubHeader = new FreeTypeFontGenerator(Gdx.files.internal("fonts/lato_regular.ttf"));
		generatorInfo = new FreeTypeFontGenerator(Gdx.files.internal("fonts/newscycle_regular.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontHeader = generatorHeader.generateFont(parameter);
		fontSubHeader = generatorSubHeader.generateFont(parameter);
		fontInfo = generatorInfo.generateFont(parameter);

		interactionBarHeight = (float)SCREEN_HEIGHT / 20;
		divInfoWidth = (float)(SCREEN_WIDTH / 3);
		directionBarHeight = (float)(interactionBarHeight * 0.70);
		directionBarWidth = (float)((SCREEN_WIDTH / 2) * 0.95);

		createTextures();

		batch = new SpriteBatch();
		sRenderer = new ShapeRenderer();

		LANE1 = (float)(laneWidth * 1.175);
		LANE2 = (float)((laneWidth * 2) * 1.075);
		LANE3 = (float)((laneWidth * 3) * 1.065);
		LANE4 = (float)((laneWidth * 4) * 1.055);

		defXOwnCar = (float)(SCREEN_WIDTH / 2) - CAR_W;
		defYOwnCar = interactionBarHeight * 2;
		ownCar = new Rectangle(defXOwnCar, defYOwnCar, CAR_W, CAR_H);

		defXCar1 = LANE1;
		defXCar2 = LANE2;
		defXCar3 = LANE3;
		defXCar4 = LANE4;
		defYCar1 = (float)(SCREEN_HEIGHT * 0.15);
		defYCar2 = (float)(SCREEN_HEIGHT * 0.7);
		defYCar3 = (float)(SCREEN_HEIGHT * 0.40);
		defYCar4 = (float)(SCREEN_HEIGHT * 0.95);
		ArrayList<Rectangle> recEnemyCars = new ArrayList<>();
		recEnemyCars.add(new Rectangle(defXCar1, defYCar1, CAR_W, CAR_H));
		recEnemyCars.add(new Rectangle(defXCar2, defYCar2, CAR_W, CAR_H));
		recEnemyCars.add(new Rectangle(defXCar3, defYCar3, CAR_W, CAR_H));
		recEnemyCars.add(new Rectangle(defXCar4, defYCar4, CAR_W, CAR_H));

		listEnemyCars = new ArrayList<>();
		tEnemy1 = new EnemyCar(SCREEN_HEIGHT, speed, recEnemyCars.get(0));
		tEnemy2 = new EnemyCar(SCREEN_HEIGHT, speed, recEnemyCars.get(1));
		tEnemy3 = new EnemyCar(SCREEN_HEIGHT, speed, recEnemyCars.get(2));
		tEnemy4 = new EnemyCar(SCREEN_HEIGHT, speed, recEnemyCars.get(3));
		listEnemyCars.add(tEnemy1);
		listEnemyCars.add(tEnemy2);
		listEnemyCars.add(tEnemy3);
		listEnemyCars.add(tEnemy4);
		for (EnemyCar enemyCar : listEnemyCars) {
			enemyCar.start();
		}
		paused = false;
		started = false;
		gameOver = false;
		moveTapped = START_MOVTAPPED;
		moveDragged = START_MOVDRAGGED;

		inmortal = false;
		debug = false;
		inputForDebug = " ";

		randomizer = new Randomizer();
		randomizer.start();

		inmortalizer = new Inmortalizer();
		inmortalizer.start();

		startTimePause = -1;
		timeElapsedPause = -1;

		laneSeparators = new ArrayList<>();
		putLines(true);
		for (LaneSeparator laneSeparator : laneSeparators) {
			laneSeparator.start();
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.4f, 1, 0.4f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		checkCollisions();

		// Number of lives, if the game has started...
		otherChecks();

		drawRoad();
		drawLinesAndPauseButton();
		drawCars();
		drawInfoPanel();
        drawDirectionsPanel();

		// Pause screen, 'GameOver' screen and 'Start' screen
        drawoptionalScreens();
	}

	private void drawoptionalScreens() {
		if (paused) {
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			sRenderer.begin(ShapeRenderer.ShapeType.Filled);
			sRenderer.setColor(new Color(0.3f, 0.3f, 0.3f, 0.90f));
			sRenderer.rect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
			sRenderer.end();
			batch.begin();
			fontHeader.getData().setScale(4.5f);
			fontHeader.draw(batch, "GAME PAUSED", SCREEN_WIDTH / 3.75f, (float)(SCREEN_HEIGHT / 2));
			fontSubHeader.getData().setScale(3.5f);
			fontSubHeader.setColor(0.75f, 0.75f, 0.75f, 1);
			fontSubHeader.draw(batch, "Tap anywhere to unpause", SCREEN_WIDTH / 4.65f, (float)(SCREEN_HEIGHT / 2.4));
			batch.end();
			Gdx.gl.glDisable(GL20.GL_BLEND);
		} else if (!started) {
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			sRenderer.begin(ShapeRenderer.ShapeType.Filled);
			sRenderer.setColor(new Color(0.3f, 1f, 0.3f, 0.85f));
			sRenderer.rect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
			sRenderer.end();
			batch.begin();
			fontHeader.getData().setScale(4.5f);
			fontHeader.draw(batch, "TAP TO START", SCREEN_WIDTH / 4.3f, (float)(SCREEN_HEIGHT / 2));
			batch.end();
			Gdx.gl.glDisable(GL20.GL_BLEND);
		} else if (gameOver) {
			stopCars();
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			sRenderer.begin(ShapeRenderer.ShapeType.Filled);
			sRenderer.setColor(new Color(1f, 0.3f, 0.3f, 0.90f));
			sRenderer.rect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
			sRenderer.end();
			batch.begin();
			fontHeader.getData().setScale(4.5f);
			fontHeader.draw(batch, "GAME OVER", SCREEN_WIDTH / 3.25f, (float)(SCREEN_HEIGHT / 2));
			fontSubHeader.getData().setScale(3.5f);
			fontSubHeader.setColor(0.75f, 0.75f, 0.75f, 1);
			fontSubHeader.draw(batch, "Tap anywhere to play again", SCREEN_WIDTH / 5.25f, (float)(SCREEN_HEIGHT / 2.4));
			batch.end();
			Gdx.gl.glDisable(GL20.GL_BLEND);
		}
	}

	// =============== OTROS MÉTODOS ===============
	private void otherChecks() {
		// Se acaba la partida, no hay más vidas
		// The game is over, there are not more lives
		if (lives == 0 && started) {
			gameOver = true;
			inmortalizer.finish();

			putLines(false);
		}

		if (!started) {
			positionInitialCars();
		}

		if (!paused && started && !gameOver) {
			if (startTimePause != -1 && timeElapsedPause != -1) {
				startTime += timeElapsedPause;
				currentTime = (System.currentTimeMillis() - startTime) - timeElapsedPause;

				startTimePause = -1;
				timeElapsedPause = -1;
			} else {
				currentTime = System.currentTimeMillis() - startTime;
			}
		}
	}

	private void putLines(boolean initialize) {
		int numLines = (SCREEN_HEIGHT + imgLine.getHeight()) / imgLine.getHeight();
		// Dividimos por dos porque se muestra linea si, linea no, linea sí, ...
		numLines /= 2;
		int previousPos = SCREEN_HEIGHT + imgLine.getHeight();
		ArrayList<Float> x = new ArrayList<>();
		x.add(laneWidth * 2);
		x.add(laneWidth * 3);
		x.add(laneWidth * 4);
		for (int i = 0; i < numLines; i++) {
			int currentPos = previousPos - (imgLine.getHeight() * 2);
			if (initialize) {
				laneSeparators.add(new LaneSeparator(imgLine.getHeight(), x, currentPos));
			} else {
				laneSeparators.get(i).setY(currentPos);
			}
			previousPos = currentPos;
		}
	}

	private void checkCollisions() {
	    if (!inmortal) {
            for (EnemyCar enemyCar : listEnemyCars) {
    			if (checkCollision(ownCar, enemyCar.getRecCar())) {
                    ownCar.x = defXOwnCar;
                    ownCar.y = defYOwnCar;
                    lives--;

                    inmortalizer.count();
                    inmortal = true;
                }
            }
        } else {
	        if (inmortalizer.isReady()) {
	            inmortal = false;
            }
        }
	}

	private void drawRoad() {
		// Road is drawn
		sRenderer.begin(ShapeRenderer.ShapeType.Filled);
		sRenderer.setColor(0.4f, 0.4f, 0.4f, 1);
		sRenderer.rect(laneWidth, 0, SCREEN_WIDTH - (laneWidth * 2), SCREEN_HEIGHT);
		sRenderer.end();

		// Lines are drawn
		sRenderer.begin(ShapeRenderer.ShapeType.Filled);
		sRenderer.setColor(Color.LIGHT_GRAY);
		// boundary left line
		sRenderer.rect(laneWidth, 0, lineWidth, SCREEN_HEIGHT);
		// boundary right line
		sRenderer.rect(SCREEN_WIDTH - laneWidth, 0, lineWidth, SCREEN_HEIGHT);
		sRenderer.end();
	}

	private void positionInitialCars() {
		tEnemy1.getRecCar().x = defXCar1;
		tEnemy2.getRecCar().x = defXCar2;
		tEnemy3.getRecCar().x = defXCar3;
		tEnemy4.getRecCar().x = defXCar4;
		tEnemy1.getRecCar().y = defYCar1;
		tEnemy2.getRecCar().y = defYCar2;
		tEnemy3.getRecCar().y = defYCar3;
		tEnemy4.getRecCar().y = defYCar4;
		ownCar.y = defYOwnCar;
		ownCar.x = defXOwnCar;
		for (EnemyCar ec : listEnemyCars) {
			ec.setSpeed(START_SPEED);
		}
	}

	private void drawLinesAndPauseButton() {
		batch.begin();
		// Pause Button is drawn
		batch.draw(imgPause, (float)(laneWidth * 0.5 - (imgPause.getWidth() / 2)), (float)(SCREEN_HEIGHT / 2));
		// Lines are drawn (lane separators)
		for (LaneSeparator ls : laneSeparators) {
			// All the lane separators are drawn (3 for lane)
			for (float x : ls.getX()) {
				batch.draw(imgLine, x, ls.getY());
			}
		}
		batch.end();
	}

	private void drawCars() {
		if (debug) {
			// enemyCar num 1
			sRenderer.begin(ShapeRenderer.ShapeType.Filled);
			sRenderer.setColor(Color.RED);
			sRenderer.rect(tEnemy1.getRecCar().x, tEnemy1.getRecCar().y, CAR_W, CAR_H);
			sRenderer.end();

			// num 2
			sRenderer.begin(ShapeRenderer.ShapeType.Filled);
			sRenderer.setColor(Color.GREEN);
			sRenderer.rect(tEnemy2.getRecCar().x, tEnemy2.getRecCar().y, CAR_W, CAR_H);
			sRenderer.end();

			// num 3
			sRenderer.begin(ShapeRenderer.ShapeType.Filled);
			sRenderer.setColor(Color.YELLOW);
			sRenderer.rect(tEnemy3.getRecCar().x, tEnemy3.getRecCar().y, CAR_W, CAR_H);
			sRenderer.end();

			// num 4
			sRenderer.begin(ShapeRenderer.ShapeType.Filled);
			sRenderer.setColor(Color.BLUE);
			sRenderer.rect(tEnemy4.getRecCar().x, tEnemy4.getRecCar().y, CAR_W, CAR_H);
			sRenderer.end();

			// own
			sRenderer.begin(ShapeRenderer.ShapeType.Filled);
			sRenderer.setColor(Color.BLACK);
			sRenderer.rect(ownCar.getX(), ownCar.getY(), CAR_W, CAR_H);
			sRenderer.end();
		} else {
			batch.begin();
			batch.draw(imgCars.get(0), tEnemy1.getRecCar().x, tEnemy1.getRecCar().y);
			batch.draw(imgCars.get(1), tEnemy2.getRecCar().x, tEnemy2.getRecCar().y);
			batch.draw(imgCars.get(2), tEnemy3.getRecCar().x, tEnemy3.getRecCar().y);
			batch.draw(imgCars.get(3), tEnemy4.getRecCar().x, tEnemy4.getRecCar().y);
            if (inmortal) {
                batch.draw(imgCars.get(5), ownCar.getX(), ownCar.getY());
            } else {
                batch.draw(imgCars.get(4), ownCar.getX(), ownCar.getY());
            }
			batch.end();
		}
	}

	private void drawInfoPanel() {
		// Background of the info panel is drawn
		sRenderer.begin(ShapeRenderer.ShapeType.Filled);
		sRenderer.setColor(Color.BLACK);
		sRenderer.rect(0, interactionBarHeight, SCREEN_WIDTH, interactionBarHeight);
		sRenderer.end();
		// Info is drawn
		batch.begin();
		fontInfo.getData().setScale(2.75f);
		// time
		fontInfo.draw(batch, "TIME: " + timeFormatter(), (float)(divInfoWidth * 0.025), (float)(interactionBarHeight * 1.70));
		// cars
		fontInfo.draw(batch, "SCORE: " + score, (float)(divInfoWidth*1.125), (float)(interactionBarHeight * 1.70));
		// lives
		float x = -1;
		for (int i = 0; i < lives; i++) {
			if (i > 0) {
				if (x == -1) {
					// The first car is put at the right
					x = (float)(SCREEN_WIDTH * 0.875);
					batch.draw(imgLives.get(i), x, (float)(interactionBarHeight * 1.15));
				} else {
					// The following cars are being put to the left of the previous one
					// just like that (in case there are any lives)
					//			  [1] <- first iteration
					//		   [2][1] <- second iteration
					//		[3][2][1] <- third and last iteration
					x -= divInfoWidth / 3;
					batch.draw(imgLives.get(i), x, (float)(interactionBarHeight * 1.15));
				}
			}
		}
		batch.end();
	}

	private void drawDirectionsPanel() {
		// Background of the direction panel is drawn
		sRenderer.begin(ShapeRenderer.ShapeType.Filled);
		sRenderer.setColor(Color.GRAY);
		sRenderer.rect(0, 0, SCREEN_WIDTH, interactionBarHeight);
		sRenderer.end();
		// Each rectangle of the direction is drawn (one for turning left, other for turning right)
		sRenderer.begin(ShapeRenderer.ShapeType.Filled);
		sRenderer.setColor(Color.LIGHT_GRAY);
		// left
		sRenderer.rect((float)(directionBarWidth * 0.025), (float)(directionBarHeight * 0.20), directionBarWidth, directionBarHeight);
		// right
		sRenderer.rect((float)(SCREEN_WIDTH / 2) + (float)(directionBarWidth * 0.025), (float)(directionBarHeight * 0.20), directionBarWidth, directionBarHeight);
		sRenderer.end();
	}

	private void stopCars() {
		for (EnemyCar enemyCar : listEnemyCars) {
			enemyCar.setGo(false);
		}
		for (LaneSeparator laneSeparator : laneSeparators) {
			laneSeparator.setGo(false);
		}
	}

	private void reloadGame() {
		randomizer.reload();
		lives = START_LIVES;
		score = START_SCORE;
		speed = START_SPEED;
		moveTapped = START_MOVTAPPED;
		moveDragged = START_MOVDRAGGED;
		positionInitialCars();
	}

	private void startCars() {
		for (EnemyCar enemyCar : listEnemyCars) {
			enemyCar.setGo(true);
		}

		for (LaneSeparator laneSeparator : laneSeparators) {
			laneSeparator.setGo(true);
		}
	}

	private void createTextures() {
		imgCars = new ArrayList<>();
		imgCars.add(new Texture("cotxe1_final.png"));
		imgCars.add(new Texture("cotxe2_final.png"));
		imgCars.add(new Texture("cotxe3_final.png"));
		imgCars.add(new Texture("cotxe4_final.png"));
		imgCars.add(new Texture("cotxe5_final.png"));
		imgCars.add(new Texture("cotxe5_transparente.png"));
		imgLine = new Texture("separ_carril.png");
		imgLives = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			imgLives.add(new Texture("cotxe_vides.png"));
		}
		imgPause = new Texture("pause.png");
	}

	private String timeFormatter() {
		return new SimpleDateFormat("mm:ss:SS").format(currentTime);
	}

	private boolean checkCollision(Rectangle player, Rectangle other) {
		boolean thereIsCollision = false;

		if ((player.x * 1.04) < other.x + other.width &&
				player.x + player.width > (other.x * 1.04) &&
				(player.y * 1.04) < other.y + other.height &&
				player.y + player.height > (other.y * 1.04)) {
			thereIsCollision = true;
		}

		return thereIsCollision;
	}
	// =============== OTHER METHODS ===============


	// =============== METHODS INPUTPROCESSOR ===============
	@Override
	public void dispose () {
		batch.dispose();
		sRenderer.dispose();
		for (Texture t : imgCars) {
			t.dispose();
		}
		imgLine.dispose();
		imgPause.dispose();
		for (Texture t : imgLives) {
			t.dispose();
		}
		fontHeader.dispose();
		fontSubHeader.dispose();
		fontInfo.dispose();
		generatorInfo.dispose();
		generatorHeader.dispose();
		generatorSubHeader.dispose();
	}

	@Override
	public boolean keyDown(int keycode){
		switch(keycode) {
			case Input.Keys.D:
				if (inputForDebug.charAt(inputForDebug.length() - 1) == ' ') {
					inputForDebug += "d";
				} else {
					inputForDebug = " ";
				}
				break;
			case Input.Keys.E:
				if (inputForDebug.charAt(inputForDebug.length() - 1) == 'd') {
					inputForDebug += "e";
				} else {
					inputForDebug = " ";
				}
				break;
			case Input.Keys.B:
				if (inputForDebug.charAt(inputForDebug.length() - 1) == 'e') {
					inputForDebug += "b";
				} else {
					inputForDebug = " ";
				}
				break;
			case Input.Keys.U:
				if (inputForDebug.charAt(inputForDebug.length() - 1) == 'b') {
					inputForDebug += "u";
				} else {
					inputForDebug = " ";
				}
				break;
			case Input.Keys.G:
				if (inputForDebug.charAt(inputForDebug.length() - 1) == 'u') {
					inputForDebug += "g";

					inputForDebug = " ";
					debug = !debug;
				} else {
					inputForDebug = " ";
				}
				break;
			default:
				inputForDebug = " ";
				break;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (!paused && started && !gameOver) {
			// The control to move left is pressed
			if (screenX < (directionBarWidth * 1.025) && SCREEN_HEIGHT - screenY < directionBarHeight){
				if (ownCar.x - moveTapped > laneWidth*1.1) {
					ownCar.x -= moveTapped;
				}
			}

			// The control to move right is pressed
			if (screenX > (directionBarWidth*1.050) && SCREEN_HEIGHT - screenY < directionBarHeight){
				if (ownCar.x + moveTapped < (SCREEN_WIDTH - (laneWidth*1.75))) {
					ownCar.x += moveTapped;
				}
			}

			// The button to pause the game is pressed
            if ((screenX > laneWidth * 0.1 && screenX < (float)(laneWidth * 0.5 + (imgPause.getWidth() / 2))) &&
                    (screenY < SCREEN_HEIGHT / 2 && screenY > SCREEN_HEIGHT / 2 - imgPause.getWidth())) {
            	startTimePause = System.currentTimeMillis();
                paused = true;
                stopCars();
            }
		} else {
		    if (screenX < SCREEN_WIDTH && screenY < SCREEN_HEIGHT) {
		    	if (paused) {
		    		timeElapsedPause = System.currentTimeMillis() - startTimePause;
		        	paused = false;
		        	startCars();
				} else if (!started) {
		    		started = true;
		    		reloadGame();
		    		startCars();
		    		startTime = System.currentTimeMillis();
				} else if (gameOver) {
		    		gameOver = false;
		    		started = false;
				}
            }
        }

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (!paused) {
			// If the screen is touched above the interactionBar ...
            if (screenY < (SCREEN_HEIGHT - interactionBarHeight * 2)) {
            	// Checks the side the car is being dragged to and if the car
				// is able to be moved without getting it out of the road, it's moved
                // left side
                if (screenX < ownCar.x + (ownCar.width / 2)) {
                    if (ownCar.x - moveDragged > laneWidth*1.1) {
                        ownCar.x -= moveDragged;
                    }
                // right side
                } else if (screenX > ownCar.x) {
                    if (ownCar.x + moveDragged < (SCREEN_WIDTH - (laneWidth*1.75))) {
                        ownCar.x += moveDragged;
                    }
                }
            }
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
	// =============== METHODS INPUTPROCESSOR ===============
}
