package hr.fer.www.ai.lab1;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public class Bfs {
	private static StateModel stateModel;
	private static Set<String> visited = new HashSet<>();
	private static String path;
	private static State current;
	private static List<State> open = new LinkedList<>();
	private static List<State> stateList = new LinkedList<>();
	
	public Bfs(DataLoader dl) {
		System.out.println("Running bfs:");
		Bfs.stateModel = dl.getStateModel();
		current = new State(null, stateModel.initState, 0);
		run();
	}

	private void run() {
		open.add(current);
		while(!open.isEmpty()) {
			current = open.remove(0);
			visited.add(current.getName());
			if(stateModel.goalStates.contains(current.getName())) {
				found(current);
				break;
			}
			for (Entry<String, Integer> entry : stateModel.transitions.get(current.getName()).entrySet()) {
				open.add(new State(current, entry.getKey(), entry.getValue()));
			}
		}
	}

	private void found(State current) {
		System.out.println("States visited = " + visited.size());
		while(current.getParent() != null) {
			stateList.add(current);
			current = current.getParent();
		}
		System.out.println("Found path of length " + (stateList.size()+1) + ": ");
		path = current.getName() + " =>\n";
		
		for(int i = stateList.size() - 1; i >= 0; i--) {
			path += stateList.get(i).getName() + " =>\n";
		}
		
		path = path.substring(0, path.length() - 3);
		System.out.println(path);
	}
}