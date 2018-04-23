import java.util.Random;

public class Net implements Comparable<Net> {
	private int inputSize = 8;
	private int hiddenWidth = 6;
	private int hiddenDepth = 2;
	private int outputWidth = 1;
	Float fitness = 0f;
	Neuron[][] net;
	Random rand = new Random();
	int wins = 0;

	public Net() {
		createNetwork();
	}

	public Net(Net p) {
		net = new Neuron[p.net.length][0];
		for (int r = 0; r < net.length; r++) {
			net[r] = new Neuron[p.net[r].length];
			for (int c = 0; c < net[r].length; c++) {
				net[r][c] = new Neuron(p.net[r][c]);
			}
		}
		wins = p.wins;
	}
	
	public Net(Net p1, Net p2) {
		net = new Neuron[hiddenDepth + 1][0];
		for (int r = 0; r < net.length; r++) {
			net[r] = new Neuron[p1.net[r].length];
			for (int c = 0; c < net[r].length; c++) {
				net[r][c] = new Neuron();
			}
		}
		wins = Math.max(p1.wins, p2.wins);
	}

	private void createNetwork() {
		net = new Neuron[hiddenDepth + 1][0];
		net[0] = new Neuron[inputSize];
		for (int i = 0; i < net[0].length; i++) {
			net[0][i] = new Neuron(hiddenWidth);
		}
		for (int r = 1; r < net.length - 1; r++) {
			net[r] = new Neuron[hiddenWidth];
			for (int c = 0; c < net[r].length; c++) {
				net[r][c] = new Neuron(hiddenWidth);
			}
		}
		net[net.length - 1] = new Neuron[outputWidth];
		for (int i = 0; i < net[net.length - 1].length; i++) {
			net[net.length - 1][i] = new Neuron(outputWidth);
		}
	}

	public Net mutate(float mutationRate) {
		if (mutationRate < rand.nextFloat()) {
			int r = rand.nextInt(hiddenDepth + 1);
			int c = rand.nextInt(net[r].length);
			net[r][c].mutate();
			
		}
		return this;
	}

	public float[] getOutput(float[] inputValues) {
		for (int i = 0; i < inputValues.length; i++) {
			net[0][i].val = inputValues[i];
		}
		for (int r = 0; r < net.length - 1; r++) {
			for (int c = 0; c < net[r].length; c++) {
				for (int nc = 0; nc < net[r + 1].length; nc++) {
					net[r + 1][nc].val += net[r][c].getValue(nc);
				}
			}
		}
		float[] output = new float[outputWidth];
		for (int i = 0; i < net[net.length - 1].length; i++) {
			for (int nc = 0; nc < outputWidth; nc++) {
				output[nc] += net[net.length - 1][i].getValue(nc);
			}
		}

		for (int r = 0; r < net.length; r++) {
			for (int c = 0; c < net[r].length; c++) {
				net[r][c].val = 0;
			}
		}
		return output;
	}
	
	public Net newRand() {
		this.rand = new Random();
		for (Neuron[] nn : net) {
			for (Neuron n : nn) {
				n.newRand();
			}
		}
		return this;
	}

	public int compareTo(Net other) {
		return Float.compare(fitness, other.fitness);
	}
	
	public String toString() {
		return Float.toString(fitness);
	}
}
