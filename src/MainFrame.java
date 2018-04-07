import java.awt.Dimension;

import javax.swing.JFrame;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MainFrame() {
		super();
		this.setContentPane(new Board(16,16,25));
		this.setTitle("Minesweeper Learner");
		this.setSize(new Dimension(518,541));
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		MainFrame mainFrame = new MainFrame();
	}
}
