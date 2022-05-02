package ui;

import java.util.HashMap;
import java.util.LinkedList;

public class Dataset {
	private HashMap<Integer, HashMap<Integer, String>> dataset = new HashMap<>();
	
	public HashMap<Integer, HashMap<Integer, String>> addData(LinkedList<String[]> file, boolean isTrain) {
		
		if(isTrain) { //TRAIN
			for(int i = 0; i < file.get(0).length; i++) {	
				HashMap<Integer, String> dataMap = new HashMap<>();
				for(int j = 0; j < file.size(); j++) {
					dataMap.put(j, file.get(j)[i]);
				}
				dataset.put(i, dataMap);
			}
			return dataset;
		}
		else { //TEST
			
			for(int i = 0; i < file.size(); i++) {	
				HashMap<Integer, String> dataMap = new HashMap<>();
				for(int j = 0; j < file.get(0).length; j++) {
					dataMap.put(j, file.get(i)[j]);
				}
				dataset.put(i, dataMap);
			}
			return dataset;
		}
	}

	public HashMap<Integer, HashMap<Integer, String>> getDataset() {
		return dataset;
	}
}
