import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

public class Generation {
	private int populationSize = 10;
	private int purgeRate = 3;
	private float crossoverRate = 0.8f;
	private float mutationRate = 0.3f;
	private List<Net> population = new ArrayList<>();
	private Random rand = new Random();

	public Generation() {
		for (int i = 0; i < populationSize; i++) {
			Net network = new Net();
			population.add(network);
		}
	}
	
	public Generation(Net net) {
		for (int i = 0; i < populationSize; i++) {
			net.newRand();
			population.add(net);
		}
	}

	public void train() {
		purge();
		breed();
	}

	private void breed() {
		int numChildren = populationSize - population.size();
		Collections.sort(population);
		
		for (int i = population.size() - 1; numChildren >= 0; i--) {
			if (rand.nextFloat() < crossoverRate)
				population.add(new Net(population.get(i), population.get(i - 1)));
			else
				population.add(new Net(population.get(i)));
			numChildren--;
		}
		for (Net net : population)
			if (rand.nextFloat() < mutationRate)
					net.mutate();
	}

	private void purge() {
		PriorityQueue<Net> deathsRow = new PriorityQueue(purgeRate, Collections.reverseOrder());
		for (Net net : population) {
			if (deathsRow.size() < purgeRate) {
				deathsRow.add(net);
			} else if (net.compareTo(deathsRow.peek()) == -1) {
				deathsRow.poll();
				deathsRow.add(net);
			}
		}
		while (deathsRow.size() > 0) {
			population.remove(deathsRow.poll());
		}
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	public int getPurgeRate() {
		return purgeRate;
	}

	public void setPurgeRate(int purgeRate) {
		this.purgeRate = purgeRate;
	}

	public List<Net> getPopulation() {
		return population;
	}

	public void setPopulation(List<Net> population) {
		this.population = population;
	}
}
