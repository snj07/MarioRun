import javax.swing.JFrame;
public class Main
{

	public Main()
	{
		
	}

	public static void main(String[] args)
	{

		ColoredObjectTrack1 cot = new ColoredObjectTrack1();
		Thread th = new Thread(cot);
		th.start();
		JFrame j1 = new JFrame();
		j1.setSize(800, 600);
		j1.setTitle("Mario Run");
		j1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Game_Canvas g1 = new Game_Canvas();

		j1.add(g1);
		j1.setVisible(true);
	}
}
