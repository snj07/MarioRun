import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class ImageFollowingMouseTest
{
	/*public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				createAndShowGUI();
			}
		});
	}
*/
	private static void createAndShowGUI()
	{
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new ImageFollowingPanel());
		f.setSize(480, 640);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}

}

class ImageFollowingPanel extends JPanel
{
	private final BufferedImage image;
	private Point imagePosition = new Point(150, 150);
	public Point redPoint = new Point(0, 0);
	private double imageAngleRad = 0;
	public static int x, y;
	public ImageFollowingPanel()
	{
		BufferedImage i = null;
		try
		{
			i = ImageIO.read(new File("forward.png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		image = i;

		ActionListener taskPerformer = new ActionListener()
		{

			public void actionPerformed(ActionEvent evt)
			{
				if (redPoint != null)
				{

					int centerX = imagePosition.x + (image.getWidth() / 2);
					int centerY = imagePosition.y + (image.getHeight() / 2);

					if (redPoint.x != centerX)
					{
						imagePosition.x += redPoint.x < centerX ? -1 : 1;
					}
					if (redPoint.y != centerY)
					{
						imagePosition.y += redPoint.y < centerY ? -1 : 1;
					}
					// System.out.println("mouse:::  x : " + x + "y :" + y);
					redPoint.x = x;
					redPoint.y = y;
					double dx = x - imagePosition.getX();
					double dy = y - imagePosition.getY();
					imageAngleRad = Math.atan2(dy, dx);
					repaint();
				}
			}
		};
		Timer timer = new Timer(10, taskPerformer);
		timer.start();
	}

	protected void paintComponent(Graphics gr)
	{
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		int cx = image.getWidth() / 2;
		int cy = image.getHeight() / 2;
		AffineTransform oldAT = g.getTransform();
		g.translate(cx + imagePosition.x, cy + imagePosition.y);
		g.rotate(imageAngleRad);
		g.translate(-cx, -cy);
		g.drawImage(image, 0, 0, null);
		g.setTransform(oldAT);

	}
}
