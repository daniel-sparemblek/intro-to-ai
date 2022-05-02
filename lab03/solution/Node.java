package ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Map.Entry;

public class Node {
	private String featureName;
	private Set<Branch> children = new HashSet<>();
	private Set<String> atts;
	public int depth;
	public String mostCommonOutcome;
	Dataset dataset;
	ID3 id3;
	ID3 realid3;

	public Node(String featureName, int depth, Set<String> atts, Dataset dataset, ID3 id3, ID3 id32) {
		this.featureName = featureName;
		this.depth = depth;
		this.atts = atts;
		this.dataset = dataset;
		this.id3 = id3;
		this.realid3 = id32;
		setMostCommonOutcome(dataset);
	}

	private void setMostCommonOutcome(Dataset dataset2) {
		HashMap<Integer, String> h = dataset2.getDataset().get(dataset2.getDataset().size() -1);
		
		String skip = h.get(0);
		LinkedList<String> l = new LinkedList<>();
		for(Entry<Integer, String> key2 : h.entrySet()) {
			if(key2.getValue().equals(skip)) continue;
			l.add(key2.getValue());
		}
		int max = 0;
		int curr = 0;
		String currKey = null;
		Set<String> unique = new HashSet<String>(l);

		for (String key : unique) {
			curr = Collections.frequency(l, key);

			if (max < curr) {
				max = curr;
				currKey = key;
			}
		}

		mostCommonOutcome = currKey;
	}

	public String getFeatureName() {
		return featureName;
	}

	public void expandNode() {
		for (String child : atts) {
			Branch b = new Branch(featureName, child, depth, dataset, id3, realid3);
			b.makeBranch();
			children.add(b);
		}
	}

	public void printNodes() {
		System.out.print(depth + ":" + featureName);
		for (Branch b : children) {
			b.printBranchNodes();
		}

	}

	public void predict(LinkedList<String> featureRow, LinkedList<String> predictRow, int index) {
		int indexOf = featureRow.indexOf(featureName);
		boolean entered = false;
		for (Branch b : children) {
			if (predictRow.get(indexOf).equals(b.getName())) {
				entered = true;
				b.predict(predictRow, index + 1, featureRow);
				break;
			}
		}
		if(!entered)
			System.out.println(mostCommonOutcome+" ");
	}
}
