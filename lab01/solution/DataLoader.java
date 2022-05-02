package hr.fer.www.ai.lab1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataLoader {
	private Set<String> allStates = new HashSet<>();
	private StateModel stateModel;
	private Map<String, Integer> heuristics = new HashMap<>();
	
	
	public DataLoader(String states, String heuristics) {
		loadStates(states);
		loadHeuristics(heuristics);
		System.out.println("Start state: " + stateModel.initState);
		System.out.println("End state(s): " + stateModel.goalStates);
		System.out.println("State space size: " + stateModel.transitions.size());
		System.out.println("Total transitions: " + stateModel.transitionNum);
		System.out.print("\n");
	}

	private void loadHeuristics(String heuristics) {
		Map<String, Integer> he = new HashMap<>();
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(heuristics), "UTF-8"));
			String currentLine = reader.readLine();
			
			while(currentLine != null) {
				if(currentLine.startsWith("#")) {
					currentLine = reader.readLine();
					continue;
				}
				
				String[] tmp = currentLine.split(": ");
				he.put(tmp[0], Integer.parseInt(tmp[1]));
				currentLine = reader.readLine();
			}
			
			reader.close();
			this.heuristics = he;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadStates(String states) {
		StateModel sm = new StateModel();
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(states), "UTF-8"));
			String currentLine = reader.readLine();
			int nonCommentLineNum = 1;
			
			while(currentLine != null) {
				if(currentLine.startsWith("#")) {
					currentLine = reader.readLine();
					continue;
				}
				if(nonCommentLineNum == 1) {
					sm.putInit(currentLine);
					currentLine = reader.readLine();
					nonCommentLineNum++;
					continue;
				}
				if(nonCommentLineNum == 2) {
					sm.putGoal(currentLine);
					currentLine = reader.readLine();
					nonCommentLineNum++;
					continue;
				}
				
				String[] tmp = currentLine.split(": ");
				if(tmp.length == 1)
					sm.addTrans(tmp[0], " ");
				else {
					sm.addTrans(tmp[0], tmp[1]);
				}
				currentLine = reader.readLine();
			}
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		stateModel = sm;
	}


	public Set<String> getAllStates() {
		return allStates;
	}

	public StateModel getStateModel() {
		return stateModel;
	}

	public Map<String, Integer> getHeuristics() {
		return heuristics;
	}
}
