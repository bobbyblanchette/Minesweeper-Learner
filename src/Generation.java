import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class Generation {
	private int populationSize = 125;
	private int purgeRate = 50;
	private List<Net> population = new ArrayList<>();

	public Generation(int inputWidth, int hiddenWidth, int hiddenDepth, int output) {
		for (int i = 0; i < populationSize; i++) {
			Net network = new Net(inputWidth, hiddenWidth, hiddenDepth, output);
			population.add(network);
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
			population.add(new Net(population.get(i)));
			numChildren--;
		}
		for (int i = 0; i < population.size(); i++) {
			population.get(i).mutate();
		}
	}

	private void purge() {
		PriorityQueue<Net> deathsRow = new PriorityQueue(purgeRate, Collections.reverseOrder());
		for (int i = 0; i < population.size(); i++) {
			Net defendent = population.get(i);
			if (deathsRow.size() < purgeRate) {
				deathsRow.add(defendent);
			} else if (defendent.compareTo(deathsRow.peek()) == -1) {
				deathsRow.poll();
				deathsRow.add(defendent);
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
