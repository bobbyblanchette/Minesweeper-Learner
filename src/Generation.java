import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class Generation {
	public int popSize = 125;
	public int purgeRate = 50;
	public List<Net> pop = new ArrayList<>();

	public Generation(int inputWidth, int hiddenWidth, int hiddenDepth, int output) {
		for (int i = 0; i < popSize; i++) {
			Net network = new Net(inputWidth, hiddenWidth, hiddenDepth, output);
			pop.add(network);
		}
	}

	public void train() {
		purge();
		breed();
	}

	private void breed() {
		int numChildren = popSize - pop.size();
		Collections.sort(pop);
		for (int i = pop.size() - 1; numChildren >= 0; i--) {
			pop.add(new Net(pop.get(i)));
			numChildren--;
		}
		for (int i = 0; i < pop.size(); i++) {
			pop.get(i).mutate();
		}
	}

	private void purge() {
		PriorityQueue<Net> deathsRow = new PriorityQueue(purgeRate, Collections.reverseOrder());
		for (int i = 0; i < pop.size(); i++) {
			Net defendent = pop.get(i);
			if (deathsRow.size() < purgeRate) {
				deathsRow.add(defendent);
			} else if (defendent.compareTo(deathsRow.peek()) == -1) {
				deathsRow.poll();
				deathsRow.add(defendent);
			}
		}
		while (deathsRow.size() > 0) {
			pop.remove(deathsRow.poll());
		}
	}
}
