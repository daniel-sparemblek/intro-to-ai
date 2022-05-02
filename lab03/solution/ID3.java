package ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class ID3 {
	private double datasetEntropy;
	//for feature we have gain
	private HashMap<String, Double> gains = new HashMap<>();
	private HashMap<String, Set<String>> featureAttributes = new HashMap<>();
	Dataset dataset;
	HashMap<String, HashMap<String, HashMap<String, Integer>>> featuresMain = new HashMap<>();
	Node rootNode;
	LinkedList<String> predictions = new LinkedList<>();
	
	public void fit(Dataset data) {
		this.dataset = data;		
		String root = train(data);
		rootNode = new Node(root, 0, featureAttributes.get(root), dataset, this, this);
		rootNode.expandNode();
		printNodes();
	}
	
	private String printNodes() {
		rootNode.printNodes();
		return null;
	}

	public String train(Dataset data) {
		Set<String> uniqueOutcomes = computeDatasetEntropy(data.getDataset());
		computeGains(data.getDataset(), uniqueOutcomes);
		//System.out.println("");
		String root = highestGain();
		return root;
	}

	private String highestGain() {
		double highest = 0.0;
		String highestName = null;
		for(Entry<String, Double> entry : gains.entrySet()) {
			if(highest < entry.getValue()) {
				highest = entry.getValue();
				highestName = entry.getKey();
			}
		}
		int count = 0;
		HashMap<String, Double> tempGains = new HashMap<>();
		for(Entry<String, Double> entry : gains.entrySet()) {
			if(highest == entry.getValue()) {
				count++;
				tempGains.put(entry.getKey(), entry.getValue());
			}
		}
		Map<String, Double> map = new TreeMap<>(tempGains);
		if(count > 1) {
			map = new TreeMap<>(tempGains);
			for(Entry<String, Double> entry : map.entrySet()) {
				highestName = entry.getKey();
				break;
			}
		}
		return highestName;
	}

	private void computeGains(HashMap<Integer, HashMap<Integer, String>> data, Set<String> uniqueOutcomes) {
		for(int i = 0; i < data.size()-1; i++) {
			HashMap<String, HashMap<String, Integer>> features = calculateOutcomeFrequency(data, i, uniqueOutcomes);
			String featureName = data.get(i).get(0);
			double featureGain = calculateGain(features, data.get(0).size() - 1);
			//System.out.print("IG(" + featureName + ")=" + ((double)Math.round(featureGain * 10000d) / 10000d) + " ");
			gains.put(featureName, featureGain);
		}
	}

	private double calculateGain(HashMap<String, HashMap<String, Integer>> features, int datasetSize) {
		//[cloudy={no=0, yes=4}, rainy={no=2, yes=3}, sunny={no=3, yes=2}] PRIMJER
		double averageInformationEntropy = 0.0;
		for (String feature : features.keySet()) {
			HashMap<String, Integer> frequency = features.get(feature);
			int size = 0;
			for(Entry<String, Integer> key2 : frequency.entrySet())
				size += key2.getValue();
			
			double attributeEntropy = 0.0;
			for(Entry<String, Integer> key2 : frequency.entrySet()) {
				double part = (double) key2.getValue() / size;
				if (part == 0.0)
					continue;
				attributeEntropy += -1.0*part * log2(part);
			}
			averageInformationEntropy += attributeEntropy * size / datasetSize; 			
		}
		return datasetEntropy - averageInformationEntropy;
	}

	private HashMap<String, HashMap<String, Integer>> calculateOutcomeFrequency(HashMap<Integer, HashMap<Integer, String>> data, int column, Set<String> uniqueOutcomes2) {
		//Find unique attributes in feature
		Set<String> uniqueAttributes = new HashSet<>();
		boolean skip = true;
		for(Entry<Integer, String> columnData : data.get(column).entrySet()) {
			if(skip) {
				skip = false;
				continue;
			}
			uniqueAttributes.add(columnData.getValue());
		}
		
		//Fill "features" with list of outcome frequencies for every attribute
		HashMap<String, HashMap<String, Integer>> features = new HashMap<>();
		for(String attribute : uniqueAttributes) {
			HashMap<String, Integer> numOfOutcomes = new HashMap<>();
			for(String s : uniqueOutcomes2)
				numOfOutcomes.put(s, 0);
			for(Entry<Integer, String> d : data.get(column).entrySet()) {
				if(d.getValue().equals(attribute)) {
					String key = data.get(data.size()-1).get(d.getKey());
					numOfOutcomes.put(key, numOfOutcomes.get(key)+1);
				}
			}
			features.put(attribute, numOfOutcomes);
		}
		
		//Add feature attributs to a seperate Map
		String featureName = data.get(column).get(0);
		featureAttributes.put(featureName, uniqueAttributes);
		
		featuresMain.put(featureName, features);
		return features;
		
	}

	private Set<String> computeDatasetEntropy(HashMap<Integer, HashMap<Integer, String>> data) {
		//Give me last column
		int indexOfLastColumn = data.size() - 1;
		Set<String> unique = new HashSet<>();
		boolean skip = true;
		for(Entry<Integer, String> d : data.get(indexOfLastColumn).entrySet()) {
			if(skip) {
				skip = false;
				continue;
			}
			unique.add(d.getValue());
		}
		
		HashMap<String, Integer> numOfOutcomes = new HashMap<>();
		for(String s : unique)
			numOfOutcomes.put(s, 0);
		skip = true;
		for(Entry<Integer, String> d : data.get(indexOfLastColumn).entrySet()) {
			if(skip) {
				skip = false;
				continue;
			}
			numOfOutcomes.put(d.getValue(), numOfOutcomes.get(d.getValue())+1);
		}
		datasetEntropy = 0.0;
		int datasetSize = data.get(0).size() - 1;
		for(String s : unique) {
			int first = numOfOutcomes.get(s);
			double part = (double) first / datasetSize;
			datasetEntropy += -1.0*part * log2(part); 
		}
		return unique;
	}

	public static double log2(double x)
	{
		return (double) (Math.log(x) / Math.log(2));
	}
	
	public void predict(Dataset testDataset) {
		System.out.println("");
		HashMap<Integer, HashMap<Integer, String>> datasetForTest = testDataset.getDataset();
		LinkedList<String> featureRow = new LinkedList<>();
		for (Integer row : datasetForTest.keySet()) {
			if(row == 0) {
				HashMap<Integer, String> frequency = datasetForTest.get(row);
				for(Entry<Integer, String> key2 : frequency.entrySet()) {
						featureRow.add(key2.getValue());
				}
			}		
		}		
			
		for (Integer row : datasetForTest.keySet()) {
			if(row == 0) continue;
			HashMap<Integer, String> frequency = datasetForTest.get(row);
			LinkedList<String> predictRow = new LinkedList<>();
			for(Entry<Integer, String> key2 : frequency.entrySet()) {
				if(key2.getKey() == frequency.size()-1)
					continue;
				predictRow.add(key2.getValue());
			}	
			rootNode.predict(featureRow, predictRow, 0);
		}
		calcAccuracy(testDataset);
	}
	
	private void calcAccuracy(Dataset testDataset) {
		LinkedList<String> actualValue = new LinkedList<>();
		for (Integer row : testDataset.getDataset().keySet()) {
			if(row == 0) continue;
			HashMap<Integer, String> frequency = testDataset.getDataset().get(row);
			for(Entry<Integer, String> key2 : frequency.entrySet()) {
				if(key2.getKey() == frequency.size()-1)
					actualValue.add(key2.getValue());
				else continue;
			}	
		}
		
		int count = 0;
		for(int i = 0; i < predictions.size(); i++) {
			if(predictions.get(i).equals(actualValue.get(i)))
				count++;
		}
		double accuracy = (double) count / predictions.size();
		System.out.printf("\n%.5f %n", accuracy);
		calcConfusionMatrix(actualValue);
	}

	private void calcConfusionMatrix(LinkedList<String> actualValue) {
		Set<String> actualUnique = new TreeSet<String>(actualValue);
		Set<String> predictedUnique = new TreeSet<String>(predictions);
//		int matrixSize = actualUnique.size();
//		if(predictedUnique.size() > actualUnique.size())
//			matrixSize = predictedUnique.size();
		int count = 0;
		LinkedList<Integer> list = new LinkedList<>();
		for(String act : predictedUnique) {
			for(String pred : actualUnique) {
				for(int i = 0; i < predictions.size(); i++) {
					if(predictions.get(i).equals(pred)) {
						if(actualValue.get(i).equals(act)) {
							count++;
						}
					}
				}
				list.add(count);
				count = 0;
			}
		}
		int i = 0;
		for(int c : list) {
			if(i % 2 == 0)
				if(i != 0)
					System.out.print("\n");
			if(i % 2 != 0)
				System.out.print(" ");
			System.out.print(c);
			i++;
		}
	}

	public HashMap<String, Set<String>> getFeatureAttributes() {
		return featureAttributes;
	}

	public HashMap<String, HashMap<String, HashMap<String, Integer>>> getFeaturesMain() {
		return featuresMain;
	}

	public void getPrediction(String string) {
		predictions.add(string.trim());
	}
}
