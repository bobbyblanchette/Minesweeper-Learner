import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;

public class MainFrame extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;
	private Integer sizeX = 16;
	private Integer sizeY = 16;
	private Board board;
	private Generation gen;

	public MainFrame() {
		super();
		board = new Board(sizeX, sizeY, 25);
		this.setContentPane(board);
		this.setTitle("Minesweeper Learner");
		this.setSize(new Dimension(518,541));
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		MainFrame mainFrame = new MainFrame();
		Thread thread = new Thread(mainFrame);
		thread.start();
	}
	
	@Override
	public void run() {
		gen = new Generation(8, 4, 1, 1);
		while (true) {
			List<Net> nets = gen.getPopulation();
			float totalFitness = 0;
			for (Net net : nets) {
				float fitness = 0;
				while (true) {
					float[][] danger = new float[16][16];
					for (int r = 0; r < danger.length; r++) {
						for (int c = 0; c < danger[r].length; c++) {
							if (!board.getElement(c, r).getHidden()) {
								danger[r][c] = Integer.MAX_VALUE;
							} else {
								float[] input = new float[8];
								input[0] = board.eval(r - 1, c - 1);
								input[1] = board.eval(r - 1, c);
								input[2] = board.eval(r - 1, c + 1);
								input[3] = board.eval(r, c - 1);
								input[4] = board.eval(r, c + 1);
								input[5] = board.eval(r + 1, c - 1);
								input[6] = board.eval(r + 1, c);
								input[7] = board.eval(r + 1, c + 1);
								float d = net.getOutput(input)[0];
								danger[r][c] = d;
							}
						}
					}
					int lowR = 0;
					float lowVal = Integer.MAX_VALUE;
					int lowC = 0;
					for (int r = 0; r < danger.length; r++) {
						for (int c = 0; c < danger[r].length; c++) {
							if (danger[r][c] < lowVal) {
								lowR = r;
								lowC = c;
								lowVal = danger[r][c];
							}
						}
					}
					if (board.select(lowR, lowC)) {
						fitness += 1;
					} else {
						net.fitness = fitness;
						totalFitness += fitness;
						break;
					}
					if (board.check()) {
						System.exit(1);
					}
				}
				board.reset();
			}
			System.out.printf("%.2f%n", totalFitness / nets.size());
			gen.train();
		}
	}
}
