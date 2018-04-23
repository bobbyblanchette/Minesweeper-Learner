import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;



public class MainFrame extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;
	private Integer sizeX = 8;
	private Integer sizeY = 8;
	private Board board;
	private Generation gen;

	public MainFrame() {
		super();
		board = new Board(sizeX, sizeY, 8);
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
		try {
			Net net = readNetFromFile();
			if (net != null)
				gen = new Generation(net);
			else
				gen = new Generation();
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			gen = new Generation();
		}
		while (true) {
			List<Net> nets = gen.getPopulation();
			float totalFitness = 0;
			for (Net net : nets) {
				float fitness = 0;
				while (true) {
					float[][] danger = new float[sizeY][sizeX];
					for (int r = 0; r < danger.length; r++) {
						for (int c = 0; c < danger[r].length; c++) {
							if (!board.getElement(c, r).getHidden()) {
								danger[r][c] = Integer.MAX_VALUE;
							} else {
								float[] input = new float[8];
								input[0] = board.eval(c - 1, r - 1);
								input[1] = board.eval(c - 1, r);
								input[2] = board.eval(c - 1, r + 1);
								input[3] = board.eval(c, r - 1);
								input[4] = board.eval(c, r + 1);
								input[5] = board.eval(c + 1, r - 1);
								input[6] = board.eval(c + 1, r);
								input[7] = board.eval(c + 1, r + 1);
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
					if (board.select(lowC, lowR)) {
						fitness += 1;
					} else {
						net.fitness = fitness;
						totalFitness += fitness;
						break;
					}
					if (board.check()) {
						net.wins++;
						System.out.println("Winner! Number of games won by this net: " + net.wins);
						writeNetToFile(net);
						//showOff(net);
						gen = new Generation(net);
						board.genMap();
					}
				}
				board.reset();
			}
			System.out.printf("%.2f%n", totalFitness / nets.size());
			gen.train();
		}
	}
	
	public void writeNetToFile(Net net) {
		try (Writer writer = new FileWriter("net.json")) {
			Gson gson = new GsonBuilder().create();
			gson.toJson(net, writer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Net readNetFromFile() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		Gson gson = new GsonBuilder().create();
		return gson.fromJson(new JsonReader(new FileReader("net.json")), Net.class);
	}
	
	public void showOff(Net net) {
		while (true) {
			while (true) {
				float[][] danger = new float[sizeY][sizeX];
				for (int r = 0; r < danger.length; r++) {
					for (int c = 0; c < danger[r].length; c++) {
						if (!board.getElement(c, r).getHidden()) {
							danger[r][c] = Integer.MAX_VALUE;
						} else {
							float[] input = new float[8];
							input[0] = board.eval(c - 1, r - 1);
							input[3] = board.eval(c - 1, r);
							input[5] = board.eval(c - 1, r + 1);
							input[1] = board.eval(c, r - 1);
							input[6] = board.eval(c, r + 1);
							input[2] = board.eval(c + 1, r - 1);
							input[4] = board.eval(c + 1, r);
							input[7] = board.eval(c + 1, r + 1);
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
				board.select(lowC, lowR);
				if (board.check()) {
					break;
				}
				try {
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			board.reset();
		}
	}
}
