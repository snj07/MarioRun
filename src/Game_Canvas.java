import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

public class Game_Canvas extends GameDriver {
	private static final long serialVersionUID = 1L;
	private double bgXPos, wfXPos, score, highScore;
	private int waterfallCount;
	private int pressEnterCount;
	private double loading;
	private BufferedImage background, waterfall, endImg, title;
	static public Mario joon;
	static public Foreground fg;
	private boolean startGame;
	public static boolean endGame;
	private Font defaultFont;
	static public SoundDriver sd;
	private FileDriver fd;
	private int spawnSpace;
	private int timer;
	private double accelRate;
	private int musicTimer;
	private boolean isEnter;
	private boolean isEnterTwo;
	private int endCounter;
	public static boolean ENDED = false;

	// private ArrayList<Fireball> fire;

	public Game_Canvas() {
		String[] stuff = { "audio/Jump.wav", "audio/Shooting.wav", "audio/BulletDying.wav", "audio/Dead.wav",
				"audio/Opening.wav", "audio/BuildUp.wav", "audio/BackgroundSong.wav" };
		sd = new SoundDriver(stuff);
		fd = new FileDriver();
		background = addImage("background.png");
		waterfall = addImage("waterfall.png");
		title = addImage("Title.png");
		timer = 0;
		new Rectangle(0, 0, 800, 600);
		joon = new Mario(120, 416, 60, 75, 0, 0, Color.RED, addImage("MarioSprites.png"));
		loading = 1.1;
		accelRate = -0.001;
		spawnSpace = 250;
		fg = new Foreground(-6, addImage("LeftEdgeTile.png"), addImage("Tile.png"), addImage("RightEdgeTile.png"));
		fg.init();
		musicTimer = 0;
		startGame = false;
		endGame = false;
		isEnterTwo = false;
		isEnter = false;
		endImg = addImage("GameEnd.png");
		highScore = Integer.parseInt(fd.getStringArray("highScores.txt")[0]);
		Font ttfBase = null;
		sd.loop(4);
		try {
			InputStream myStream = new BufferedInputStream(new FileInputStream("BorisBlackBloxx.ttf"));
			ttfBase = Font.createFont(Font.TRUETYPE_FONT, myStream);
			defaultFont = ttfBase.deriveFont(Font.BOLD, 36);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("CODE2000 not loaded.");
		}
	}

	public void draw(Graphics2D win) {
		win.setColor(Color.WHITE);
		win.setFont(defaultFont);
		if (!startGame) {

			drawAndMoveBG(win);
			win.drawImage(title, 800 / 2 - title.getWidth() / 2 - 12, 100, null);
			Font f1 = defaultFont.deriveFont(Font.BOLD, 20);
			int pos = 470;
			win.setFont(f1);
			win.drawString("By:SS", 396, 550);
			win.setColor(Color.white);
			// win.drawString("Here's How to Play:", 10, pos);
			win.drawString("To jump, move RED OBJECT upwards!!", 20, pos + 20);
			// win.drawString("To shoot, press Spacebar", 20, pos + 40);
			win.drawString("Good luck!", 20, pos + 80);
			// win.drawString("Avoid large jump", 20, pos+60);
			win.setColor(new Color(245, 49, 28));
			f1 = defaultFont.deriveFont(Font.BOLD, 36);
			win.setFont(f1);

			if (loading >= 100) {
				if (pressEnterCount % 60 <= 29) {
					win.drawString("Press ENTER to Play", 170, 340);
				}
				pressEnterCount++;
				if (pressEnterCount >= 120) {
					pressEnterCount = 0;
				}
				if (keys[10]) {
					startGame = true;
					isEnter = true;
					pressEnterCount = 0;
					sd.stop(4);
					sd.play(5);
				}
				if (isEnter = true) {
					isEnter = false;
					isEnterTwo = true;
				}
				if (isEnterTwo = true) {
					if (musicTimer >= 723) {
						startGame = true;
						sd.stop(4);
						sd.stop(5);
						sd.loop(6);
					} else {
						musicTimer++;
					}
				}
			} else {
				win.drawString("Loading: " + (int) loading, 275, 340);
				// loading = loading * 1.01;
				loading += 100;
			}
		} else if (!endGame) {
			drawAndMoveBG(win);
			drawAndMoveWaterfall(win, endGame);
			win.drawString("Score: " + (int) score, 8, 36);
			win.drawString("High Score: " + (int) highScore, 8, 80);
			setScore();
			fg.move();
			fg.draw(win);
			fg.accel(accelRate);
			// accelRate += -0.0000001;
			// if(!Mario.MOVED)
			joon.jump(keys, fg.checkCollision(joon), sd);
			joon.move(keys);
			joon.drawActor(win);
			death();
			timer++;
		} else {
			win.drawImage(background, (int) bgXPos, 0, null);
			win.drawImage(background, (int) bgXPos + background.getWidth(), 0, null);
			drawAndMoveWaterfall(win, endGame);
			win.drawString("Score: " + (int) score, 8, 36);
			win.drawString("High Score: " + (int) highScore, 8, 80);
			fg.draw(win);
			win.setColor(new Color(245, 49, 28));
			if (pressEnterCount % 60 <= 29 && endCounter > 100) {
				win.drawString("Game Over", 280, 260);
				win.drawString("Press ENTER to Continue", 120, 300);
			}
			pressEnterCount++;
			if (pressEnterCount >= 120) {
				pressEnterCount = 0;
			}
			// win.drawImage(endImg, 30, 200, null);
			if (highScore < score) {
				highScore = score;
				String[] scores = fd.getStringArray("highScores.txt");
				scores[0] = "" + (int) score;
				fd.addToFile("highScores.txt", scores);
			}

			if ((keys[10] && endCounter > 100) || ENDED) {

				ENDED = false;
				reset();
				pressEnterCount = 0;
				sd.stop(3);
				sd.loop(6);
			}
			endCounter++;
		}
	}

	public void drawAndMoveBG(Graphics2D win) {
		bgXPos -= 0.5;
		if (bgXPos <= -background.getWidth()) {
			bgXPos = 0;
		}
		win.drawImage(background, (int) bgXPos, 0, null);
		win.drawImage(background, (int) bgXPos + background.getWidth(), 0, null);
	}

	public void drawAndMoveWaterfall(Graphics2D win, boolean end) {
		wfXPos += fg.getSpeed();
		if (end)
			wfXPos -= fg.getSpeed();
		if (wfXPos <= -waterfall.getWidth()) {
			wfXPos = 0;
		}
		int pos = waterfallCount / 15;
		BufferedImage img = waterfall.getSubimage(0, 0 + 92 * pos, 896, 92);
		win.drawImage(img, (int) wfXPos, 520, null);
		win.drawImage(img, (int) wfXPos + waterfall.getWidth(), 520, null);
		waterfallCount++;
		if (waterfallCount == 45) {
			waterfallCount = 0;
		}
	}

	public void death() {
		if (joon.getYPos() > 500) {
			sd.stop(4);
			sd.stop(5);
			sd.stop(6);
			sd.play(3);
			endGame = true;
		}

	}

	public void setScore() {
		score += 0.1;
	}

	public void reset() {
		endGame = false;
		endCounter = 0;
		joon.getHitBox().setLocation(120, 416);
		joon.reset();
		fg.clear();
		// launcher.clear();
		// fire.clear();
		fg.init();
		score = 0;
		timer = 0;
		accelRate = -0.001;
	}
}
