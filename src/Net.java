import java.util.ArrayList;
import java.util.Random;

public class Net implements Comparable<Net> {
	
	private int inputSize = 8;
	private int hiddenWidth = 6;
	private int hiddenDepth = 2;
	private int outputWidth = 1;
	private Random rand = new Random();
	private Float fitness = 0f;
	private ArrayList<ArrayList<Neuron>> net;
	private Integer wins = 0;

	public Net() {
		createNetwork();
	}

	public Net(Net p) {
		net = p.getNet();
		for (ArrayList<Neuron> nn : net) {
			for (Neuron n : nn) {
				n = new Neuron(n);
			}
		}
		wins = p.wins;
	}
	
	public Net(Net p1, Net p2) {
		net = new ArrayList<ArrayList<Neuron>>();
		wins = Math.max(p1.getWins(), p2.getWins());
	}

	private void createNetwork() {
		net = new ArrayList<ArrayList<Neuron>>();
		
		// input layer
		net.add(new ArrayList<Neuron>());
		for (int i = 0; i < inputSize; i++) {
			net.get(0).add(new Neuron(hiddenWidth));
		}
		
		// hidden layers
		for (int d = 0; d < hiddenDepth; d++) {
			net.add(new ArrayList<Neuron>());
			for (int w = 0; w < hiddenWidth; w++) {
				net.get(d).add(new Neuron(hiddenWidth));
			}
		}
		
		// output layer
		net.add(new ArrayList<Neuron>());
		for (int o = 0; o < outputWidth; o++) {
			net.get(net.size() - 1).add(new Neuron(outputWidth));
		}
	}

	public Net mutate(float mutationRate) {
		if (mutationRate < rand.nextFloat()) {
			int l = rand.nextInt(hiddenDepth + 1);
			int n = rand.nextInt(net.get(l).size());
			net.get(l).get(n).mutate();
			
		}
		return this;
	}

	public float[] getOutput(float[] inputValues) {
		// input value
		for (int i = 0; i < inputValues.length; i++) {
			net.get(0).get(i).val = inputValues[i];
		}
		// process data
		for (int l = 0; l < net.size() - 1; l++) {
			for (int n = 0; n < net.get(l).size(); n++) {
				for (int nln = 0; nln < net.get(l + 1).size(); nln++) {
					net.get(l + 1).get(nln).val += net.get(l).get(n).getValue(nln);
				}
			}
		}
		// create output
		float[] output = new float[outputWidth];
		for (int n = 0; n < net.get(net.size() - 1).size(); n++) {
			for (int o = 0; o < outputWidth; o++) {
				output[o] += net.get(net.size() - 1).get(n).getValue(o);
			}
		}
		// reset neurons
		for (ArrayList<Neuron> nn : net)
			for (Neuron n : nn) 
				n.val = 0f;
		
		return output;
	}
	
	public Neuron getNeuron(int l, int n) {
		return net.get(l).get(n);
	}
	
	public int compareTo(Net other) {
		return Float.compare(fitness, other.fitness);
	}
	
	public String toString() {
		return Float.toString(fitness);
	}

	public Float getFitness() {
		return fitness;
	}

	public void setFitness(Float fitness) {
		this.fitness = fitness;
	}

	public ArrayList<ArrayList<Neuron>> getNet() {
		return net;
	}

	public void setNet(ArrayList<ArrayList<Neuron>> net) {
		this.net = net;
	}

	public Integer getWins() {
		return wins;
	}

	public void setWins(Integer wins) {
		this.wins = wins;
	}
}
