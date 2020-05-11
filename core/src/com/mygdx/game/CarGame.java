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
 * [X] Dibujar fondo con calzada y lineas divisorias (de la calzada)
 * [X] Situar en la pantalla algunos coches enemigos y el coche del jugador
 * [X] Colocar y mover las lineas divisorias de carriles arriba y abajo
 * [X] Mover el coche del jugador usando los recuadros grises
 * [X] Mover el coche del jugador arrastrando el dedo
 * [X] Hacer que el coche enemigo se mueva solo
 * [X] Situar número de vidas en pantalla
 * [X] Detectar colisiones, reducir vidas y controlar fin de juego
 * [X] Ir sacando coches cuando los otros cuatro desaparezcan
 * [X] Controlar/mostrar puntuaciones
 *
 * OTROS
 * [X] Pantalla Pause
 * [X] Pantalla Start
 * [X] Pantalla Game Over
 * [X] Modo inmortal
 * [X] Modo debug
 *
 * FIXME / TODO
 * 	- Optimizar juego -> agrupar en métodos
 * 	- Cambiar idioma código/juego: o inglés o castellano, no ambos
 * 	- Comentar
 *
 */
public class CarGame extends ApplicationAdapter implements InputProcessor{
	static float CARRIL1;
	static float CARRIL2;
	static float CARRIL3;
	static float CARRIL4;
    static int SCREEN_WIDTH;
	static int SCREEN_HEIGHT;

	private static final int START_LIVES = 4;
	private static final int START_SCORE = 0;
//	private static final int START_SPEED = 1;
	private static final int START_SPEED = 4;
	private static final int START_MOVTAPPED = 35;
//	private static final int START_MOVDRAGGED = 10;
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
	private ArrayList<Rectangle> recEnemyCars;

	private BitmapFont fontHeader;
	private BitmapFont fontSubHeader;
	private BitmapFont fontInfo;

	static int score;
	private int lives;
	static final int SCORE_JUMP = 20;
	private long startTime;
	private long currentTime;
	private long startTimePause;
	private long timeElapsedPause;
	static float velocidad;
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
	static ArrayList<EnemyCar> listEnemyCars;

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
		velocidad = START_SPEED;

		// Se obtiene el tamaño de la pantalla (ancho y alto)
		SCREEN_WIDTH = Gdx.graphics.getWidth();
		SCREEN_HEIGHT = Gdx.graphics.getHeight();

		// Tamaño de cada carril (la pantalla se divide en 6 (4 calzada + 2 césped))
		laneWidth = (float)SCREEN_WIDTH / 6;
		// Tamaño de la linea divisoria de la calzada (linea que separa césped de calzada)
		lineWidth = laneWidth / 15;

		// FIXME:
		//		- Modificar el tamaño del coche dependiendo del tamaño de la pantalla,
		//			deberá ser potencia de 2.

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

		crearTexturas();

		batch = new SpriteBatch();
		sRenderer = new ShapeRenderer();

		CARRIL1 = (float)(laneWidth * 1.175);
		CARRIL2 = (float)((laneWidth * 2) * 1.075);
		CARRIL3 = (float)((laneWidth * 3) * 1.065);
		CARRIL4 = (float)((laneWidth * 4) * 1.055);

		defXOwnCar = (float)(SCREEN_WIDTH / 2) - CAR_W;
		defYOwnCar = interactionBarHeight * 2;
		ownCar = new Rectangle(defXOwnCar, defYOwnCar, CAR_W, CAR_H);

		defXCar1 = CARRIL1;
		defXCar2 = CARRIL2;
		defXCar3 = CARRIL3;
		defXCar4 = CARRIL4;
		defYCar1 = (float)(SCREEN_HEIGHT * 0.20);
		defYCar2 = (float)(SCREEN_HEIGHT * 0.65);
		defYCar3 = (float)(SCREEN_HEIGHT * 0.40);
		defYCar4 = (float)(SCREEN_HEIGHT * 0.95);
		recEnemyCars = new ArrayList<>();
		recEnemyCars.add(new Rectangle(defXCar1, defYCar1, CAR_W, CAR_H));
		recEnemyCars.add(new Rectangle(defXCar2, defYCar2, CAR_W, CAR_H));
		recEnemyCars.add(new Rectangle(defXCar3, defYCar3, CAR_W, CAR_H));
		recEnemyCars.add(new Rectangle(defXCar4, defYCar4, CAR_W, CAR_H));

		listEnemyCars = new ArrayList<>();
		tEnemy1 = new EnemyCar(SCREEN_HEIGHT, velocidad, recEnemyCars.get(0));
		tEnemy2 = new EnemyCar(SCREEN_HEIGHT, velocidad, recEnemyCars.get(1));
		tEnemy3 = new EnemyCar(SCREEN_HEIGHT, velocidad, recEnemyCars.get(2));
		tEnemy4 = new EnemyCar(SCREEN_HEIGHT, velocidad, recEnemyCars.get(3));
		listEnemyCars.add(tEnemy1);
		listEnemyCars.add(tEnemy2);
		listEnemyCars.add(tEnemy3);
		listEnemyCars.add(tEnemy4);
		for (EnemyCar ec : listEnemyCars) {
			ec.start();
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
		colocarLineas(true);
		for (LaneSeparator ls : laneSeparators) {
			ls.start();
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.4f, 1, 0.4f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		comprobarColisiones();

		// Número de vidas, si el juego ha empezado, ...
		otrasComprobaciones();

		dibujarCalzada();
		dibujarLineasYPause();
		dibujarCoches();
		dibujarPanelInfo();
        dibujarPanelDirecciones();

        // Pantalla 'Pause', 'GameOver', 'Start'
        dibujarPantallasOpcionales();
	}

	private void dibujarPantallasOpcionales() {
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
			pararCoches();
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
	private void otrasComprobaciones() {
		// Se acaba la partida, no hay más vidas
		if (lives == 0 && started) {
			gameOver = true;
			inmortalizer.finish();

			colocarLineas(false);
		}

		if (!started) {
			posicionarCochesInicio();
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

	private void colocarLineas(boolean inicializar) {
		int numLines = (SCREEN_HEIGHT + imgLine.getHeight()) / imgLine.getHeight();
		// Dividimos por dos porque se muestra linea si, linea no, linea sí, ...
		numLines /= 2;
		int posAnterior = SCREEN_HEIGHT + imgLine.getHeight();
		ArrayList<Float> x = new ArrayList<>();
		x.add(laneWidth * 2);
		x.add(laneWidth * 3);
		x.add(laneWidth * 4);
		for (int i = 0; i < numLines; i++) {
			int posActual = posAnterior - (imgLine.getHeight() * 2);
			if (inicializar) {
				laneSeparators.add(new LaneSeparator(imgLine.getHeight(), x, posActual));
			} else {
				laneSeparators.get(i).setY(posActual);
			}
			posAnterior = posActual;
		}
	}

	private void comprobarColisiones() {
	    if (!inmortal) {
            for (EnemyCar ec : listEnemyCars) {
    			if (hayColision(ownCar, ec.getRecCar())) {
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

	private void dibujarCalzada() {
		// Dibujamos la calzada
		sRenderer.begin(ShapeRenderer.ShapeType.Filled);
		sRenderer.setColor(0.4f, 0.4f, 0.4f, 1);
		sRenderer.rect(laneWidth, 0, SCREEN_WIDTH - (laneWidth * 2), SCREEN_HEIGHT);
		sRenderer.end();

		// Dibujamos las lineas que marcan el límite de la calzada
		sRenderer.begin(ShapeRenderer.ShapeType.Filled);
		sRenderer.setColor(Color.LIGHT_GRAY);
		// lim izq
		sRenderer.rect(laneWidth, 0, lineWidth, SCREEN_HEIGHT);
		// lim der
		sRenderer.rect(SCREEN_WIDTH - laneWidth, 0, lineWidth, SCREEN_HEIGHT);
		sRenderer.end();
	}

	private void posicionarCochesInicio() {
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
	}

	private void dibujarLineasYPause() {
		batch.begin();
		// Dibujamos el botón de pause
		batch.draw(imgPause, (float)(laneWidth * 0.5 - (imgPause.getWidth() / 2)), (float)(SCREEN_HEIGHT / 2));
		// Dibujamos las lineas
		for (LaneSeparator ls : laneSeparators) {
			// Se dibujan todas los separadores de las lineas (3 por linea)
			for (float x : ls.getX()) {
				batch.draw(imgLine, x, ls.getY());
			}
		}
		batch.end();
	}

	private void dibujarCoches() {
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

	private void dibujarPanelInfo() {
		// Dibujamos el fondo del panel de info
		sRenderer.begin(ShapeRenderer.ShapeType.Filled);
		sRenderer.setColor(Color.BLACK);
		sRenderer.rect(0, interactionBarHeight, SCREEN_WIDTH, interactionBarHeight);
		sRenderer.end();
		// Dibujamos la info
		batch.begin();
		fontInfo.getData().setScale(2.75f);
		// tiempo
		fontInfo.draw(batch, "TIEMPO: " + timeFormatter(), (float)(divInfoWidth * 0.025), (float)(interactionBarHeight * 1.70));
		// coches
		fontInfo.draw(batch, "COCHES: " + score, (float)(divInfoWidth*1.125), (float)(interactionBarHeight * 1.70));
		// vidas
		float x = -1;
		for (int i = 0; i < lives; i++) {
			if (i > 0) {
				if (x == -1) {
					// Se coloca el primer coche a al derecha de todo
					x = (float)(SCREEN_WIDTH * 0.875);
					batch.draw(imgLives.get(i), x, (float)(interactionBarHeight * 1.15));
				} else {
					// Los coches siguientes se van colocando a la izquierda del anterior
					// tal que sí (en caso de tener todas las vidas)
					//			  [1] <- primera iteracion
					//		   [2][1] <- segunda iteracion
					//		[3][2][1] <- tercera y última iteracion
					x -= divInfoWidth / 3;
					batch.draw(imgLives.get(i), x, (float)(interactionBarHeight * 1.15));
				}
			}
		}
		batch.end();
	}

	private void dibujarPanelDirecciones() {
		// Dibujamos el fondo del panel de direcciones
		sRenderer.begin(ShapeRenderer.ShapeType.Filled);
		sRenderer.setColor(Color.GRAY);
		sRenderer.rect(0, 0, SCREEN_WIDTH, interactionBarHeight);
		sRenderer.end();
		// Dibujamos cada cuadro de direcciones
		sRenderer.begin(ShapeRenderer.ShapeType.Filled);
		sRenderer.setColor(Color.LIGHT_GRAY);
		// izq
		sRenderer.rect((float)(directionBarWidth * 0.025), (float)(directionBarHeight * 0.20), directionBarWidth, directionBarHeight);
		// der
		sRenderer.rect((float)(SCREEN_WIDTH / 2) + (float)(directionBarWidth * 0.025), (float)(directionBarHeight * 0.20), directionBarWidth, directionBarHeight);
		sRenderer.end();
	}

	private void pararCoches() {
		for (EnemyCar ec : listEnemyCars) {
			ec.setGo(false);
		}
		for (LaneSeparator ls : laneSeparators) {
			ls.setGo(false);
		}
	}

	private void reloadGame() {
		randomizer.reload();
		posicionarCochesInicio();
		lives = START_LIVES;
		score = START_SCORE;
		velocidad = START_SPEED;
		moveTapped = START_MOVTAPPED;
		moveDragged = START_MOVDRAGGED;
	}

	private void arrancarCoches() {
		for (EnemyCar ec : listEnemyCars) {
			ec.setGo(true);
		}

		for (LaneSeparator ls : laneSeparators) {
			ls.setGo(true);
		}
	}

	private void crearTexturas() {
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

	/**
	 * Es lo mismo que el overlaps pero con un poco más de margen (es más difícil tocarse)
	 * @param player	Coche del jugador
	 * @param other		Coche enemigo
	 * @return			Si se ha tocado o no
	 */
	private boolean hayColision(Rectangle player, Rectangle other) {
		boolean hayColision = false;

		if ((player.x * 1.04) < other.x + other.width &&
				player.x + player.width > (other.x * 1.04) &&
				(player.y * 1.04) < other.y + other.height &&
				player.y + player.height > (other.y * 1.04)) {
			hayColision = true;
		}

		return hayColision;
	}
	// =============== OTROS MÉTODOS ===============


	// =============== METODOS INPUTPROCESSOR ===============
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
			// Presionamos el control para mover hacia izq
			if (screenX < (directionBarWidth * 1.025) && SCREEN_HEIGHT - screenY < directionBarHeight){
				if (ownCar.x - moveTapped > laneWidth*1.1) {
					ownCar.x -= moveTapped;
				}
			}

			// Se pulsa el control para mover hacia der
			if (screenX > (directionBarWidth*1.050) && SCREEN_HEIGHT - screenY < directionBarHeight){
				if (ownCar.x + moveTapped < (SCREEN_WIDTH - (laneWidth*1.75))) {
					ownCar.x += moveTapped;
				}
			}

            // Se toca el botón de pause
            if ((screenX > laneWidth * 0.1 && screenX < (float)(laneWidth * 0.5 + (imgPause.getWidth() / 2))) &&
                    (screenY < SCREEN_HEIGHT / 2 && screenY > SCREEN_HEIGHT / 2 - imgPause.getWidth())) {
            	startTimePause = System.currentTimeMillis();
                paused = true;
                pararCoches();
            }
		} else {
		    if (screenX < SCREEN_WIDTH && screenY < SCREEN_HEIGHT) {
		    	if (paused) {
		    		timeElapsedPause = System.currentTimeMillis() - startTimePause;
		        	paused = false;
		        	arrancarCoches();
				} else if (!started) {
		    		started = true;
		    		reloadGame();
		    		arrancarCoches();
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
			// Si se toca la pantalla por encima de las interactionBar (altura de la calzada para arriba) ...
            if (screenY < (SCREEN_HEIGHT - interactionBarHeight * 2)) {
				// Comprueba el lado hacia donde se está arrastrando el coche
				// y en caso de poder moverlo sin salirse de la calzada, lo hace
                // izq
                if (screenX < ownCar.x + (ownCar.width / 2)) {
                    if (ownCar.x - moveDragged > laneWidth*1.1) {
                        ownCar.x -= moveDragged;
                    }
                // der
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
	// =============== METODOS INPUTPROCESSOR ===============
}
