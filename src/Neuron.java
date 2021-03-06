import java.util.Random;

public class Neuron {
	private Random rand = new Random();
	private final float initialWeightMax = 5;
	private final float delta = 2f;
	private float[] weight;
	private float val;
	private int width = 0;

	public Neuron() {
		this(0);
	}

	public Neuron(int width) {
		this.width = width;
		weight = new float[this.width];
		for (int i = 0; i < weight.length; i++) {
			weight[i] = (float) (initialWeightMax * rand.nextFloat() * (rand.nextBoolean() ? 1 : -1));
		}
		val = 0;
	}

	public Neuron(Neuron p) {
		width = p.width;
		weight = new float[p.width];
		for (int i = 0; i < weight.length; i++) {
			weight[i] = p.weight[i];
		}
	}

	public void mutate() {
		for (int w = 0; w < weight.length; w++)
			weight[w] += delta * (rand.nextBoolean() ? 1 : -1);
	}

	private float actFunc(float v) {
		float actiVal = (float) ((Math.pow(Math.E, v) - Math.pow(Math.E, -v))
				/ (Math.pow(Math.E, v) + Math.pow(Math.E, -v)));
		return actiVal;
	}

	public float getValue(int nc) {
		return weight[nc] * actFunc(val);
	}

	public void newRand() {
		this.rand = new Random();
	}

	public float[] getWeight() {
		return weight;
	}

	public void setWeight(float[] weight) {
		this.weight = weight;
	}

	public float getVal() {
		return val;
	}

	public void setVal(float val) {
		this.val = val;
	}
	
	public void addToVal(float add) {
		this.val += add;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
}
