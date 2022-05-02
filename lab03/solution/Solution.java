package ui;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class Solution {
	private static Dataset datasetTrain = new Dataset();
	private static Dataset datasetTest = new Dataset();
	
	
	public static void main(String ... args) {
		//String path_to_config;
		String path_to_test;
		String path_to_train;
		
		if(args.length != 3) {
			System.out.println("Treba 3 filea");
			System.exit(1);
		}
		
		//path_to_config = args[2];
		path_to_test = args[1];
		path_to_train = args[0];
		
		try {
			readFile(path_to_train, true);
			readFile(path_to_test, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ID3 model = new ID3();
		model.fit(datasetTrain);
		model.predict(datasetTest);
	}
	
	public static void readFile(String path, boolean isTrain) throws IOException {
		Dataset datasetTemp = new Dataset();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
		LinkedList<String[]> data = new LinkedList<>();
		String line = bufferedReader.readLine();
		while(line != null) {
			String splitter[] = line.split(",");
			data.add(splitter);
			line = bufferedReader.readLine();
		}
		bufferedReader.close();
		if(isTrain) {
			datasetTemp.addData(data, isTrain);
			datasetTrain = datasetTemp;
		} else {
			datasetTemp.addData(data, isTrain);
			datasetTest = datasetTemp;
		}
		
	}
	
	
}
