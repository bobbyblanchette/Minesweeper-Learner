import java.awt.Dimension;
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



@SuppressWarnings("serial")
public class MainFrame extends JFrame implements Runnable {

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
					for (int y = 0; y < danger.length; y++) {
						for (int x = 0; x < danger[y].length; x++) {
							if (!board.getElement(x, y).getHidden()) {
								danger[y][x] = Integer.MAX_VALUE;
							} else {
								float[] input = new float[8];
								input[0] = board.eval(x - 1, y - 1);
								input[1] = board.eval(x - 1, y);
								input[2] = board.eval(x - 1, y + 1);
								input[3] = board.eval(x, y - 1);
								input[4] = board.eval(x, y + 1);
								input[5] = board.eval(x + 1, y - 1);
								input[6] = board.eval(x + 1, y);
								input[7] = board.eval(x + 1, y + 1);
								danger[y][x] = net.getOutput(input)[0];
							}
						}
					}
					float lowestDanger = Integer.MAX_VALUE;
					int lowestY = 0;
					int lowestX = 0;
					for (int y = 0; y < danger.length; y++) {
						for (int x = 0; x < danger[y].length; x++) {
							if (danger[y][x] < lowestDanger) {
								lowestY = y;
								lowestX = x;
								lowestDanger = danger[y][x];
							}
						}
					}
					if (board.select(lowestX, lowestY)) {
						fitness += 1;
					} else {
						net.setFitness(fitness);
						totalFitness += fitness;
						break;
					}
					if (board.check()) {
						net.setWins(net.getWins() + 1);
						System.out.println("Winner! Number of games won by this net: " + net.getWins());
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
				for (int y = 0; y < danger.length; y++) {
					for (int x = 0; x < danger[y].length; x++) {
						if (!board.getElement(x, y).getHidden()) {
							danger[y][x] = Integer.MAX_VALUE;
						} else {
							float[] input = new float[8];
							input[0] = board.eval(x - 1, y - 1);
							input[1] = board.eval(x - 1, y);
							input[2] = board.eval(x - 1, y + 1);
							input[3] = board.eval(x, y - 1);
							input[4] = board.eval(x, y + 1);
							input[5] = board.eval(x + 1, y - 1);
							input[6] = board.eval(x + 1, y);
							input[7] = board.eval(x + 1, y + 1);
							danger[y][x] = net.getOutput(input)[0];
						}
					}
				}
				float lowestDanger = Integer.MAX_VALUE;
				int lowestY = 0;
				int lowestX = 0;
				for (int y = 0; y < danger.length; y++) {
					for (int x = 0; x < danger[y].length; x++) {
						if (danger[y][x] < lowestDanger) {
							lowestY = y;
							lowestX = x;
							lowestDanger = danger[y][x];
						}
					}
				}
				board.select(lowestX, lowestY);
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
