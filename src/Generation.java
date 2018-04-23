import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Generation {
	
	private int populationSize = 100;
	private float crossoverRate = 0.9f;
	private float mutationRate = 0.1f;
	private List<Net> population = new ArrayList<>();
	private Random rand = new Random();

	public Generation() {
		population = Stream.generate(() -> new Net()).limit(populationSize).collect(Collectors.toCollection(ArrayList::new));
	}
	
	public Generation(Net net) {
		population = Stream.generate(() -> new Net(net)).limit(populationSize).collect(Collectors.toCollection(ArrayList::new));
	}

	public void train() {
		breed();
	}

	private void breed() {
		List<Net> newGen = new ArrayList<Net>();
		while (newGen.size() < populationSize) {
			if (rand.nextFloat() < crossoverRate)
				for (Net child : crossover(tournament(4), tournament(4)))
					newGen.add(child.mutate(mutationRate));
			else
				newGen.add(tournament(10).mutate(mutationRate));
		}
		population = newGen;
	}
	
	private Net tournament(int size) {
		Net best = null;
		for (int i = 0; i < size; i++) {
			Net contestant = population.get(rand.nextInt(population.size()));
			if (best == null || contestant.getFitness() > best.getFitness()) {
				best = contestant;
			}
		}
		return best;
	}
	
	private ArrayList<Net> crossover(Net p1, Net p2) {
		ArrayList<Net> children = Stream.generate(() -> new Net(p1, p2)).limit(2).collect(Collectors.toCollection(ArrayList::new));
		for (int l = 0; l < p1.getNet().size(); l++) {
			children.get(0).getNet().add(new ArrayList<Neuron>());
			children.get(1).getNet().add(new ArrayList<Neuron>());
			for (int n = 0; n < p1.getNet().get(l).size(); n++) {
				if (0.5 > rand.nextFloat()){
					children.get(0).getNet().get(l).add(new Neuron(p1.getNet().get(l).get(n)));
					children.get(1).getNet().get(l).add(new Neuron(p2.getNet().get(l).get(n)));
				} else {
					children.get(0).getNet().get(l).add(new Neuron(p2.getNet().get(l).get(n)));
					children.get(1).getNet().get(l).add(new Neuron(p1.getNet().get(l).get(n)));
				}
			}
		}
		return children;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	public List<Net> getPopulation() {
		return population;
	}

	public void setPopulation(List<Net> population) {
		this.population = population;
	}
}
