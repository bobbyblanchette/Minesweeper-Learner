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
		board = new Board(sizeX, sizeY, 50);
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
	
	public int getMapVal(int r, int c) {
		if (r < 0 || c < 0 || r >= sizeX || c >= sizeY) {
			return -2;
		}
		if (board.getElement(r, c).getHidden()) {
			return -1;
		}
		return board.getElement(r, c).getNum();
	}
	
	@Override
	public void run() {
		gen = new Generation(8, 4, 1, 1);
		while (true) {
			List<Net> nets = gen.pop;
			float totalFitness = 0;
			for (Net net : nets) {
				float fitness = 0;
				boolean playing = true;
				while (playing) {
					float[][] danger = new float[16][16];
					for (int r = 0; r < danger.length; r++) {
						for (int c = 0; c < danger[r].length; c++) {
							if (!board.getMap().get(r).get(c).getHidden()) {
								danger[r][c] = Integer.MAX_VALUE;
							} else {
								float[] input = new float[8];
								input[0] = getMapVal(r - 1, c - 1);
								input[1] = getMapVal(r - 1, c);
								input[2] = getMapVal(r - 1, c + 1);
								input[3] = getMapVal(r, c - 1);
								input[4] = getMapVal(r, c + 1);
								input[5] = getMapVal(r + 1, c - 1);
								input[6] = getMapVal(r + 1, c);
								input[7] = getMapVal(r + 1, c + 1);
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
						playing = false;
						net.fitness = fitness;
						totalFitness += fitness;
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
