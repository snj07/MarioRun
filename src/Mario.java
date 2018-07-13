import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class Mario extends Actor {

	private BufferedImage img;
	private int spriteCount;
	private int spriteDir;
	private int jumpCount;
	private int spriteSpeed;
	private int soundCount;
	private boolean isJumping;
	public static boolean MOVED = false;
	private final double gravityConst = 1;
	private final double jumpConst = 18;

	public Mario(int x, int y, int w, int h, int xv, int yv, Color c, BufferedImage i) {
		super(x, y, w, h, xv, yv, c);
		img = i;
		spriteDir = 1;
		isJumping = false;
		spriteSpeed = 9;
		spriteCount = 2 * spriteSpeed;
	}

	public void move(boolean[] keys) {
		getHitBox().translate(0, (int) (getYVel() * -1));
	}

	public void jump(boolean[] keys, boolean collision, SoundDriver sd) {

		if ((collision && getYVel() == 0 && keys[0] && jumpCount > 3) || (MOVED && getYVel() == 0 && collision)) {

			System.out.println("------Jumpsss");
			setYVel(jumpConst);
			jumpCount = 0;
			isJumping = true;
			if (soundCount > 30)
				sd.play(0);
			soundCount = 1;

		} else if (!collision) {

			if ((keys[0] && getYVel() > 0) || MOVED) {
				if (getYVel() == 0)
					MOVED = false;
				setYVel(getYVel() - gravityConst * 0.5);

			} else {
				setYVel(getYVel() - gravityConst);
			}
		} else {
			// System.out.println("in else");
			setYVel(0);
			isJumping = false;
			jumpCount++;
		}

		soundCount++;
	}

	public void drawActor(Graphics2D win) {
		if (isJumping) {
			BufferedImage spriteImg = null;
			// if(this.getYPos()<420)
			spriteImg = img.getSubimage(0, 0 + 84 * 5, 85, 84);

			win.drawImage(spriteImg, this.getXPos(), this.getYPos(), null);
		} else {
			int pos = spriteCount / spriteSpeed;
			BufferedImage spriteImg = img.getSubimage(0, 0 + 84 * pos, 85, 84);
			win.drawImage(spriteImg, this.getXPos(), this.getYPos(), null);
			spriteCount += spriteDir;
			if (spriteCount >= spriteSpeed * 4 + spriteSpeed / 2 || spriteCount <= spriteSpeed / 2)
				spriteDir *= -1;
		}
	}

	public void reset() {
		spriteCount = 2 * spriteSpeed;
		spriteDir = 1;
		isJumping = false;
	}
}
