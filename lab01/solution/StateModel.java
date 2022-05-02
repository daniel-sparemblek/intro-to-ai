package hr.fer.www.ai.lab1;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StateModel {
	public String initState;
	public List<String> goalStates = new LinkedList<>();
	public Map<String, HashMap<String, Integer>> transitions = new HashMap<>();
	public int transitionNum = 0;
	
	public StateModel() {
		
	}
	
	public void putInit(String state) {
		this.initState = state;
	}
	
	public void putGoal(String goals) {
		String[] tmp = goals.split(" ");
		for(int i = 0; i < tmp.length; i++)
			this.goalStates.add(tmp[i]);
	}
	
	public void addTrans(String start, String ends) {
		if(ends.equals(" "))
			this.transitions.put(start, null);
		else {
			String[] tmp = ends.split(" ");
			for(int i = 0; i < tmp.length; i++) {
				String[] tmp2 = tmp[i].split(",");
				if(!this.transitions.containsKey(start)) {
					HashMap<String, Integer> tmpMap = new HashMap<>();
					tmpMap.put(tmp2[0], Integer.parseInt(tmp2[1]));
					this.transitions.put(start, tmpMap);
				} else
					this.transitions.get(start).put(tmp2[0], Integer.parseInt(tmp2[1]));
				this.transitionNum++;
			}
		}		
	}
}
