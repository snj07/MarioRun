
/*
 *  opencv to make a colored object tracking,
 */

import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvFlip;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvInRangeS;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import static org.bytedeco.javacpp.opencv_highgui.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_MEDIAN;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvEqualizeHist;
import static org.bytedeco.javacpp.opencv_imgproc.cvGetCentralMoment;
import static org.bytedeco.javacpp.opencv_imgproc.cvGetSpatialMoment;
import static org.bytedeco.javacpp.opencv_imgproc.cvMoments;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;

import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_imgproc.CvMoments;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

public class ColoredObjectTrack1 implements Runnable {

	/*
	 * public static void main(String[] args) { ColoredObjectTrack1 cot = new
	 * ColoredObjectTrack1(); Thread th = new Thread(cot); th.start(); }
	 */

	final int INTERVAL = 0;// 0sec 10=1sec
	final int CAMERA_NUM = 0; // Default camera for this time

	/**
	 * Correct the color range- it depends upon the object, camera quality,
	 * environment.
	 */
	static CvScalar rgba_min = cvScalar(0, 0, 130, 0);// RED wide
	static CvScalar rgba_max = cvScalar(80, 80, 255, 0);

	IplImage image;
	CanvasFrame canvas = new CanvasFrame("Web Cam Live");
	static JFrame path = new JFrame("Detection");
	int ii = 0;
	static ImageFollowingPanel jp = new ImageFollowingPanel();

	public ColoredObjectTrack1() {
		canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		path.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});

	}

	private static void createAndShowGUI() {
		path.getContentPane().add(jp);
		path.setSize(400, 400);
		path.setLocationRelativeTo(null);
		path.setVisible(true);
		path.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
	}

	public void run() {
		try {
			FrameGrabber grabber = FrameGrabber.createDefault(CAMERA_NUM);
			OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
			grabber.start();
			IplImage img;
			int posX = 0;
			int posY = 0;
			int lastposX = 0;
			int lastposY = 0;
			while (true) {
				img = converter.convert(grabber.grab());

				if (img != null) {

					cvFlip(img, img, 1);
					IplImage detectThrs = getThresholdImage(img);
					CvMoments moments = new CvMoments();
					cvMoments(detectThrs, moments, 1);
					double mom10 = cvGetSpatialMoment(moments, 1, 0);
					double mom01 = cvGetSpatialMoment(moments, 0, 1);
					double area = cvGetCentralMoment(moments, 0, 0);
					posX = (int) (mom10 / area);
					posY = (int) (mom01 / area);

					if ((posY >= lastposY + 20) && !Game_Canvas.endGame) {
						Mario.MOVED = true;
						// Game_Canvas.fg.move();
						// Game_Canvas.joon.jump(GameDriver.keys,Game_Canvas.fg.checkCollision(Game_Canvas.joon),Game_Canvas.sd);
						// Game_Canvas.joon.shoot(keys, fireTimer,
						// timeBetweenFire);
						// Game_Canvas.joon.move(GameDriver.keys);
						// Game_Canvas.joon.drawActor(win);

						System.out.println("Jump!!!_-----!!!!");

					}

					if (Game_Canvas.endGame && (posY - lastposY >= 50)) {

						Game_Canvas.ENDED = true;
					}

					lastposX = posX;
					lastposY = posY;

					// only if its a valid position
					if (posX > 0 && posY > 0) {
						paint(img, posX, posY);
					}

					// show image on window
					// l-r =
					canvas.showImage(converter.convert(img));
					// 90_degrees_steps_anti_clockwise
				}
				// Thread.sleep(INTERVAL);
			}
		} catch (Exception e) {
		}
	}

	private void paint(IplImage img, int posX, int posY) {
		ImageFollowingPanel.x = posX;
		ImageFollowingPanel.y = posY;

		/*
		 * Graphics g = jp.getGraphics(); path.setSize(img.width(),
		 * img.height()); // g.clearRect(0, 0, img.width(), img.height());
		 * g.setColor(Color.RED); // g.fillOval(posX, posY, 20, 20);
		 * g.drawOval(posX, posY, 20, 20); // g.drawLine(posX, posY, posX + 1,
		 * posX + 1);
		 */System.out.println(posX + " , " + posY);

	}

	private IplImage getThresholdImage(IplImage orgImg) {
		IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);
		//
		cvInRangeS(orgImg, rgba_min, rgba_max, imgThreshold);// red

		cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 15, 0, 0, 0);
		cvSaveImage(++ii + System.getProperty("user.dir") + "/src/dsmthreshold.jpg", imgThreshold);
		return imgThreshold;
	}

	public IplImage Equalize(BufferedImage bufferedimg) {
		Java2DFrameConverter converter1 = new Java2DFrameConverter();
		OpenCVFrameConverter.ToIplImage converter2 = new OpenCVFrameConverter.ToIplImage();
		IplImage iploriginal = converter2.convert(converter1.convert(bufferedimg));
		IplImage srcimg = IplImage.create(iploriginal.width(), iploriginal.height(), IPL_DEPTH_8U, 1);
		IplImage destimg = IplImage.create(iploriginal.width(), iploriginal.height(), IPL_DEPTH_8U, 1);
		cvCvtColor(iploriginal, srcimg, CV_BGR2GRAY);
		cvEqualizeHist(srcimg, destimg);
		return destimg;
	}
}
