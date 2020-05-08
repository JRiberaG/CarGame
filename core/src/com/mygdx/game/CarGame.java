package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
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
 * [-] Colocar y mover las lineas divisorias de carriles arriba y abajo
 * [X] Mover el coche del jugador usando los recuadros grises
 * [X] Mover el coche del jugador arrastrando el dedo
 * [X] Hacer que el coche enemigo se mueva solo
 * [X] Situar número de vidas en pantalla
 * [ ] Detectar colisiones, reducir vidas y controlar fin de juego
 * [-] Ir sacando coches cuando los otros cuatro desaparezcan
 * [-] Controlar/mostrar puntuaciones
 *
 * OTROS
 * [X] Funcionalidad go/pause
 *
 * FIXME
 * 	- Cambiar a una fuente más estrecha
 * 	- Optimizar colisiones
 * 	- Optimizar cronómetro -> cuando se pausa el juego, sigue corriendo
 * 	- Optimizar pause -> ahora para despausar se tiene que clickar el boton pause. Que no sea así,
 * 			que una vez el juego esté pausado, con clickar cualquier lado se despause
 *
 */
public class CarGame extends ApplicationAdapter implements InputProcessor, Runnable{
	public static float CARRIL1;
	public static float CARRIL2;
	public static float CARRIL3;
	public static float CARRIL4;
    public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;

	private SpriteBatch batch;
	private ArrayList<Texture> imgCars;
	private ArrayList<Texture> imgLives;
	private Texture imgLine;
	private Texture imgPauseGo;

	private ShapeRenderer sRenderer;

	private Rectangle ownCar;
	private ArrayList<Rectangle> recEnemyCars;

	private BitmapFont fontTimer;
	private BitmapFont fontCars;

	static int score;
	private int lives;
	private long startTime;
	private long currentTime;
	private long startTimePaused;
	private long currentTimePaused;
	static int velocidad;
	private int carsWidth;
	private int carsHeight;
	private float laneWidth;
	private float lineWidth;
	private float ownCarStartX;
	private float ownCarStartY;

	private float interactionBarHeight;
	private float divInfoWidth;
	private float directionBarHeight;
	private float directionBarWidth;

	private FreeTypeFontGenerator generator;

	private ArrayList<LineaThread> threads;
	private ThreadGestorLines threadGestor;
	private EnemyCar tEnemy1;
	private EnemyCar tEnemy2;
	private EnemyCar tEnemy3;
	private EnemyCar tEnemy4;
	private ArrayList<EnemyCar> listEnemyCars;

	private boolean paused;

	@Override
	public void create () {
		Gdx.input.setInputProcessor(this);
		score = 0;
		lives = 4;
		velocidad = 4;
//		startTime = System.currentTimeMillis();

		// Se obtiene el tamaño de la pantalla (ancho y alto)
		SCREEN_WIDTH = Gdx.graphics.getWidth();
		SCREEN_HEIGHT = Gdx.graphics.getHeight();

		// Tamaño de cada carril (la pantalla se divide en 6 carriles)
		laneWidth = (float)SCREEN_WIDTH / 6;
		// Tamaño de la linea divisoria de la calzada
		lineWidth = laneWidth / 15;

		// Le damos valor al tamaño de los coches
		// FIXME:
		//		- Modificar el tamaño del coche dependiendo del tamaño de la pantalla,
		//			deberá ser potencia de 2.
		carsWidth = 64;
		carsHeight = 64;

		fontTimer = new BitmapFont();
		generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/lato_regular.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontTimer = generator.generateFont(parameter);
		fontCars = generator.generateFont(parameter);


		interactionBarHeight = (float)SCREEN_HEIGHT / 20;
		divInfoWidth = (float)(SCREEN_WIDTH / 3);
		directionBarHeight = (float)(interactionBarHeight * 0.70);
		directionBarWidth = (float)((SCREEN_WIDTH / 2) * 0.95);

		crearTextures();

		batch = new SpriteBatch();
		sRenderer = new ShapeRenderer();

		CARRIL1 = (float)(laneWidth * 1.175);
		CARRIL2 = (float)((laneWidth * 2) * 1.075);
		CARRIL3 = (float)((laneWidth * 3) * 1.065);
		CARRIL4 = (float)((laneWidth * 4) * 1.055);

		ownCarStartX = (float)(SCREEN_WIDTH / 2) - carsWidth;
		ownCarStartY = interactionBarHeight * 2;
		ownCar = new Rectangle(ownCarStartX, ownCarStartY, carsWidth, carsHeight);
		recEnemyCars = new ArrayList<>();
		// coche 1
		recEnemyCars.add(new Rectangle(CARRIL1, (float)(SCREEN_HEIGHT * 0.25), carsWidth, carsHeight));
		// Coche 2
		recEnemyCars.add(new Rectangle(CARRIL2, (float)(SCREEN_HEIGHT * 0.60), carsWidth, carsHeight));
		// Coche 3
		recEnemyCars.add(new Rectangle(CARRIL3, (float)(SCREEN_HEIGHT * 0.40), carsWidth, carsHeight));
		// Coche 4
		recEnemyCars.add(new Rectangle(CARRIL4, (float)(SCREEN_HEIGHT * 0.85), carsWidth, carsHeight));

		listEnemyCars = new ArrayList<>();
//		tEnemy1 = new EnemyCar(SCREEN_HEIGHT, carsHeight, recEnemyCars.get(0).getY(), CARRIL1, 3);
//		tEnemy2 = new EnemyCar(SCREEN_HEIGHT, carsHeight, recEnemyCars.get(1).getY(), CARRIL2, 3);
//		tEnemy3 = new EnemyCar(SCREEN_HEIGHT, carsHeight, recEnemyCars.get(2).getY(), CARRIL3, 3);
//		tEnemy4 = new EnemyCar(SCREEN_HEIGHT, carsHeight, recEnemyCars.get(3).getY(), CARRIL4, 3);
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
//			ec.setGo(true);
		}
//		Thread t = new Thread(this);
//		t.start();
//		threadGestor = new ThreadGestorLines(SCREEN_HEIGHT, imgLine.getHeight(), 3);
//		threadGestor.run();
//		threads = new ArrayList<>();
//		for (int i = 0; i < 5; i++) {
//			threads.add(new LineaThread(SCREEN_HEIGHT, imgLine.getHeight(), 3));
//			threads.get(i).start();
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {}
//		}
		paused = true;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.4f, 1, 0.4f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//		boolean overlaped = false;
		for (EnemyCar ec : listEnemyCars) {
			if (hayColision(ownCar, ec.getRecCar())) {
				ownCar.x = ownCarStartX;
				ownCar.y = ownCarStartY;
				lives--;
			}
//			if (ownCar.overlaps(ec.getRecCar())) {
//				ownCar.x = ownCarStartX;
//				ownCar.y = ownCarStartY;
//			}
		}
//		if (ownCar.overlaps(tEnemy1.getRecCar())) {
//		}

		if (!paused) {
			currentTime = System.currentTimeMillis() - startTime;
		}

		// Dibujamos la calzada
		sRenderer.begin(ShapeRenderer.ShapeType.Filled);
		sRenderer.setColor(Color.WHITE);
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


		// Dibujamos lineas y coches
		batch.begin();
		batch.draw(imgPauseGo, (float)(laneWidth * 0.1), (float)(SCREEN_HEIGHT / 2));
//		for (LineaThread lt : threadGestor.getThreads()) {
//			batch.draw(imgLine, laneWidth * 2, lt.getY());
//			batch.draw(imgLine, laneWidth * 3, lt.getY());
//			batch.draw(imgLine, laneWidth * 4, lt.getY());
//		}
		// Obtenemos el número de lineas totales que caben en la calzada (por carril)
		int numLines = (SCREEN_HEIGHT - (int)(interactionBarHeight * 2)) / imgLine.getHeight();
		// Dividimos por dos porque se muestra linea si, linea no, linea sí, ...
		numLines /= 2;
		int posAnterior = SCREEN_HEIGHT;
		for (int i = 0; i < numLines; i++) {
			int posActual = posAnterior - (imgLine.getHeight() * 2);
			batch.draw(imgLine, laneWidth * 2, posActual);
			batch.draw(imgLine, laneWidth * 3, posActual);
			batch.draw(imgLine, laneWidth * 4, posActual);
			posAnterior = posActual;
		}
//		batch.draw(imgLine, laneWidth * 2, linesY);
//		batch.draw(imgLine, laneWidth * 3, linesY);
//		batch.draw(imgLine, laneWidth * 4, linesY);
//		batch.draw(imgCars.get(0), tEnemy1.getX(), tEnemy1.getY());
//        batch.draw(imgCars.get(1), tEnemy2.getX(), tEnemy2.getY());
//        batch.draw(imgCars.get(2), tEnemy3.getX(), tEnemy3.getY());
//        batch.draw(imgCars.get(3), tEnemy4.getX(), tEnemy4.getY());
		batch.draw(imgCars.get(0), tEnemy1.getRecCar().x, tEnemy1.getRecCar().y);
		batch.draw(imgCars.get(1), tEnemy2.getRecCar().x, tEnemy2.getRecCar().y);
		batch.draw(imgCars.get(2), tEnemy3.getRecCar().x, tEnemy3.getRecCar().y);
		batch.draw(imgCars.get(3), tEnemy4.getRecCar().x, tEnemy4.getRecCar().y);
//		if (ownCar.overlaps(recEnemyCars.get(0))) {
//			batch.draw(imgCars.get(4), ownCarStartX, ownCarStartY);
//		} else {
			batch.draw(imgCars.get(4), ownCar.getX(), ownCar.getY());
//		}
//		batch.draw(imgCars.get(0), enemyCars.get(0).getX(), enemyCars.get(0).getY());
//        batch.draw(imgCars.get(1), enemyCars.get(1).getX(), enemyCars.get(1).getY());
//        batch.draw(imgCars.get(2), enemyCars.get(2).getX(), enemyCars.get(2).getY());
//        batch.draw(imgCars.get(3), enemyCars.get(3).getX(), enemyCars.get(3).getY());
		batch.end();

		// Dibujamos el fondo del panel de info
		sRenderer.begin(ShapeRenderer.ShapeType.Filled);
		sRenderer.setColor(Color.BLACK);
		sRenderer.rect(0, interactionBarHeight, SCREEN_WIDTH, interactionBarHeight);
		sRenderer.end();
		// Dibujamos la info
		batch.begin();
//		time = System.currentTimeMillis();
		fontTimer.getData().setScale(2.75f);
		// tiempo
		fontTimer.draw(batch, "TIEMPO: " + timeFormatter(), (float)(divInfoWidth * 0.025), (float)(interactionBarHeight * 1.70));
		fontCars.getData().setScale(2.75f);
		// coches
		fontCars.draw(batch, "COCHES: " + score, (float)(divInfoWidth*1.125), (float)(interactionBarHeight * 1.70));
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

		if (paused) {
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			sRenderer.begin(ShapeRenderer.ShapeType.Filled);
			sRenderer.setColor(new Color(0.3f, 0.3f, 0.3f, 0.90f));
			sRenderer.rect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
			sRenderer.end();
			batch.begin();
			fontCars.getData().setScale(4.5f);
			// FIXME cambira fuente
			fontCars.draw(batch, "GAME PAUSED", SCREEN_WIDTH / 4f, SCREEN_HEIGHT / 2);
			batch.end();
			Gdx.gl.glDisable(GL20.GL_BLEND);

		}
	}

	@Override
	public void dispose () {
		batch.dispose();
		sRenderer.dispose();
		for (Texture t : imgCars) {
			t.dispose();
		}
		imgLine.dispose();
		imgPauseGo.dispose();
		for (Texture t : imgLives) {
			t.dispose();
		}
		fontTimer.dispose();
		fontCars.dispose();
		generator.dispose();
	}

	@Override
	public boolean keyDown(int keycode){
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
		System.out.println("\nX: " + screenX + " // Y: " + screenY);
        System.out.println("\nOWN-> X: " + ownCar.x + " // Y: " + ownCar.y);
        System.out.println("\nRED-> X:" + tEnemy1.getRecCar().x + " // Y: " + tEnemy1.getRecCar().y);
		System.out.println("\nOWN-> W: " + ownCar.width + " // H: " + ownCar.height);
		System.out.println("\nRED-> W:" + tEnemy1.getRecCar().width + " // H: " + tEnemy1.getRecCar().height);

		if (!paused) {
			// Distancia (en px) de movimiento
			int mov = 45;

			// Presionamos el control para mover hacia izq
			if (screenX < (directionBarWidth * 1.025) && SCREEN_HEIGHT - screenY < directionBarHeight){
				if (ownCar.x - mov > laneWidth*1.1) {
					ownCar.x -= mov;
				}
			}

			// Se pulsa el control para mover hacia der
			if (screenX > (directionBarWidth*1.050) && SCREEN_HEIGHT - screenY < directionBarHeight){
				if (ownCar.x + mov < (SCREEN_WIDTH - (laneWidth*1.75))) {
					ownCar.x += mov;
				}
			}
		}

		// Se toca el botón de Go/Pause
		if ((screenX > laneWidth * 0.1 && screenX < laneWidth * 0.75) &&
				(screenY < SCREEN_HEIGHT / 2 && screenY > SCREEN_HEIGHT / 2 - imgPauseGo.getWidth())) {
			if (paused) {
				paused = false;
			} else {
				paused = true;
			}
			for (EnemyCar ec : listEnemyCars) {
				if (ec.isGo())
					ec.setGo(false);
				else
					ec.setGo(true);
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
			// Distancia (en px) de movimiento
			float mov = 7.5f;

			// Comprueba el lado hacia donde se está arrastrando el coche
			// y en caso de poder moverlo sin salirse de la calzada, lo hace

			// izq
			if (screenX < ownCar.x + (ownCar.width / 2)) {
				if (ownCar.x - mov > laneWidth*1.1) {
					ownCar.x -= mov;
				}
			// der
			} else if (screenX > ownCar.x) {
				if (ownCar.x + mov < (SCREEN_WIDTH - (laneWidth*1.75))) {
					ownCar.x += mov;
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

	@Override
	public void run() {
//		for (linesY = SCREEN_HEIGHT; linesY > (0 - imgLine.getHeight()); linesY-= 3) {
//			try {
//				Thread.sleep(10);
//				// Reiniciamos el recorrido cuando la linea esté a la mitad de cruzar la "meta"
//				if (linesY <= 0 - (imgLine.getHeight() / 2)) {
//					linesY = SCREEN_HEIGHT;
//				}
//			} catch (InterruptedException e) {}
//		}
	}


	private void crearTextures() {
		imgCars = new ArrayList<>();
		imgCars.add(new Texture("cotxe1_final.png"));
		imgCars.add(new Texture("cotxe2_final.png"));
		imgCars.add(new Texture("cotxe3_final.png"));
		imgCars.add(new Texture("cotxe4_final.png"));
		imgCars.add(new Texture("cotxe5_final.png"));
		imgLine = new Texture("separ_carril.png");
		imgLives = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			imgLives.add(new Texture("cotxe_vides.png"));
		}
		imgPauseGo = new Texture("gopause.png");
	}

	private String timeFormatter() {
		return new SimpleDateFormat("mm:ss:SS").format(currentTime);
	}

	private boolean hayColision(Rectangle player, Rectangle other) {
		boolean hayColision = false;
//		if (player.x == other.x && player.y == (other.y + other.height)) {
//			hayColision = true;
//		} //else if (ownCar.x == recCar.x && (ownCar.y - ownCar.height) == recCar.y) {

		if (player.x < other.x + other.width &&
			player.x + player.width > other.x &&
			player.y < other.y + other.height &&
			player.y + player.height > other.y) {
			hayColision = true;
		}
		return hayColision;
//		return (ownCar.x == recCar.getX()) || (ownCar.x == recCar.x + recCar.width);
	}
}
