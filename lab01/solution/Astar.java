package hr.fer.www.ai.lab1;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Astar {
	private static StateModel stateModel;
	private static Set<String> visited = new HashSet<>();
	private static String path;
	private static State current;
	private static List<State> open = new LinkedList<>();
	private static List<State> closed = new LinkedList<>();
	private static Map<String, Integer> heuristics = new HashMap<>();
	private static List<State> stateList = new LinkedList<>();
	private static int pathCost = 0;
	private static boolean removing = false;
	private static List<State> toRemove = new LinkedList<>();
	private static boolean petra = false;
	
	public Astar(DataLoader dl) {
		
		Astar.stateModel = dl.getStateModel();
		Astar.heuristics = dl.getHeuristics();
		current = new State(null, stateModel.initState, 0);
	}

	public void run() {
		open.add(current);
		while(!open.isEmpty()) {
			current = open.remove(0);
			visited.add(current.getName());
			if(stateModel.goalStates.contains(current.getName())) {
				found(current);
				break;
			}
			closed.add(current);
			for (Entry<String, Integer> entry : stateModel.transitions.get(current.getName()).entrySet()) {
				for(State s : open) {
					if(s.getName().equals(entry.getKey())) {
						if(s.getCost() <= entry.getValue()) {
							petra = true;
							continue;
						}
						else {
							removing = true;
							toRemove.add(s);
						}
					}
				}
				for(State s : closed)
					if(s.getName().equals(entry.getKey())) {
						if(s.getCost() <= entry.getValue()) {
							petra = true;
							continue;
						}
						else {
							removing = true;
							toRemove.add(s);
						}
					}
				if(removing)
					remove(toRemove);
				if(!petra) {
					State temp = new State(current, entry.getKey(), entry.getValue() + current.getCost());
					temp.setHeuristicCost(heuristics.get(entry.getKey()) + entry.getValue() + current.getCost());
				//System.out.println("stavljam " + temp.getName() + " u open");
					open.add(temp);
				//System.out.println(temp.getParent().getName()+" "+temp.getName()+" "+temp.getCost()+" "
				//+temp.getHeuristicCost());
				}
				removing = false;
				toRemove.clear();
			}
			Collections.sort(open, new SortByHeuristicCost());
		}
	}

	private void remove(List<State> states) {
		for (State s : states)  {
			int index = open.indexOf(s);
			if (index != -1)
				open.remove(index);
			index = closed.indexOf(s);
			if (index != -1)
				closed.remove(index);
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
}
