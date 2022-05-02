package hr.fer.www.ai.lab1;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public class Ucs {
	private static StateModel stateModel;
	private static Set<String> visited = new HashSet<>();
	private static String path;
	private static State current;
	private static List<State> open = new LinkedList<>();
	private static List<State> stateList = new LinkedList<>();
	private static int pathCost = 0;
	private static boolean fromStart = true;
	
	public Ucs(DataLoader dl) {
		
		Ucs.stateModel = dl.getStateModel();
		current = new State(null, stateModel.initState, 0);
	}

	public void run() {
		open.add(current);
		while(!open.isEmpty()) {
			current = open.remove(0);
			visited.add(current.getName());
			if(stateModel.goalStates.contains(current.getName())) {
				if(fromStart)
					found(current);
				else pathCost = current.getCost();
				break;
			}
			for (Entry<String, Integer> entry : stateModel.transitions.get(current.getName()).entrySet()) {
				open.add(new State(current, entry.getKey(), entry.getValue() + current.getCost()));
			}
			Collections.sort(open, new SortByCost());
		}
	}

	private void found(State current) {
		System.out.println("States visited = " + visited.size());
		pathCost = current.getCost();
		while(current.getParent() != null) {
			stateList.add(current);
			current = current.getParent();
		}
		
		System.out.println("Found path of length " + (stateList.size()+1) + " with total cost of " + pathCost + ":");
		path = current.getName() + " =>\n";
		
		for(int i = stateList.size() - 1; i >= 0; i--) {
			path += stateList.get(i).getName() + " =>\n";
		}
		
		path = path.substring(0, path.length() - 3);
		System.out.println(path);
	}

	public int fromState(String stateName) {
		current = new State(null, stateName, 0);
		fromStart = false;
		run();
		return pathCost;
	}

	public void clearStructures() {
		visited.clear();
		path = " ";
		current = null;
		open.clear();
		stateList.clear();
		pathCost = 0;
		fromStart = true;
	}
}
