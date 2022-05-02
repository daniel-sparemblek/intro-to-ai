package ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

public class Branch {
	private String name;
	private Leaf leaf;
	private int depth;
	private String parentName;
	private Node nextNode;
	private Dataset dataset;
	private ID3 originalID3;
	private ID3 realid3;

	public Branch(String parentName, String name, int depth, Dataset dataset, ID3 id3, ID3 realid3) {
		this.parentName = parentName;
		this.name = name;
		this.depth = depth;
		this.dataset = dataset;
		this.originalID3 = id3;
		this.realid3 = realid3;
	}

	public void makeBranch() {
		// find out column in which parent node is
		int column = 354;
		loop:
		for (Integer feature : dataset.getDataset().keySet()) {
			HashMap<Integer, String> frequency = dataset.getDataset().get(feature);
			for (Entry<Integer, String> key2 : frequency.entrySet())
				if (key2.getValue().equals(parentName)) {
					column = feature;
					break loop;
				}
		}
		alterDataset(column);
	}

	private void alterDataset(int column) {
		boolean res = testForLeaf(originalID3.getFeaturesMain(), parentName, name);
		if(res) return;
		Set<Integer> rowsToCopy = new HashSet<>();
		for (Integer feature : dataset.getDataset().keySet()) {
			if (column == feature) {
				HashMap<Integer, String> frequency = dataset.getDataset().get(feature);
				for (Entry<Integer, String> key2 : frequency.entrySet()) {
					if (key2.getValue().equals(name)) {
						rowsToCopy.add(key2.getKey());
					}
				}
			}
		}
		
		rowsToCopy.add(0); // Always copy names
		LinkedList<String[]> data = new LinkedList<>();
		LinkedList<LinkedList<String>> data2 = new LinkedList<>();
		for (Integer row : rowsToCopy) {
			LinkedList<String> inner = new LinkedList<>();
			for (int columnIndex = 0; columnIndex < dataset.getDataset().size(); columnIndex++) {
				if (columnIndex == column)
					continue;
				inner.add(dataset.getDataset().get(columnIndex).get(row));
			}
			data2.add(inner);
		}

		for (LinkedList<String> innerList : data2) {
			String[] stringArray = innerList.toArray(new String[0]);
			data.add(stringArray);
		}

		Dataset altered = new Dataset();
		altered.addData(data, true);
		ID3 test = new ID3();
		String parent = test.train(altered);
		nextNode = new Node(parent, depth + 1, test.getFeatureAttributes().get(parent), altered, test, realid3); // CHANGE NULL
		nextNode.expandNode();
	}

	private boolean testForLeaf(HashMap<String, HashMap<String, HashMap<String, Integer>>> featuresMain, String outer,
			String inner) {

		HashMap<String, Integer> tempMap = featuresMain.get(outer).get(inner);
		
		String possibleLeafOutcome = null;
		int size = 0;
		for (Entry<String, Integer> key2 : tempMap.entrySet()) {
			size += key2.getValue();
			if(key2.getValue() != 0.0) {
				possibleLeafOutcome = key2.getKey();
			}
		}

		double attributeEntropy = 0.0;
		for (Entry<String, Integer> key2 : tempMap.entrySet()) {
			double part = (double) key2.getValue() / size;
			if (part == 0.0)
				continue;
			attributeEntropy += -1.0 * part * log2(part);
		}
		
		//FOUND A LEAF NODE
		if(attributeEntropy == 0.0) {
			leaf = new Leaf(possibleLeafOutcome);
			return true;
		}
		return false;
	}
	
	public static double log2(double x)
	{
		return (double) (Math.log(x) / Math.log(2));
	}
	
	public Leaf getLeaf() {
		return leaf;
	}

	public Node getNextNode() {
		return nextNode;
	}

	public String getName() {
		return name;
	}

	public void printBranchNodes() {
		if(nextNode != null) {
			System.out.print(", ");
			nextNode.printNodes();
		}		
	}

	public void predict(LinkedList<String> predictRow, int index, LinkedList<String> featureRow) {
		if(nextNode != null)
			nextNode.predict(featureRow, predictRow, index);	
		else {
			System.out.print(leaf.getOutcome()+" ");
			realid3.getPrediction(leaf.getOutcome()+" ");
		}
	}
}
