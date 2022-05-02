package hr.fer.www.ai.lab1;

import java.util.HashMap;
import java.util.Map.Entry;

public class Lab1 {
	private static String statesPath = "C:\\Users\\Daniel\\Desktop\\inputs\\istra.txt";
	private static String heuristicsPath = "C:\\Users\\Daniel\\Desktop\\inputs\\istra_heuristic.txt";
	
	public static void main(String[] args) {
		DataLoader dl = new DataLoader(statesPath, heuristicsPath);
		Bfs bfs = new Bfs(dl);
		System.out.println("\nRunning ucs:");
		Ucs ucs = new Ucs(dl);
		ucs.run();
		System.out.println("\nRunning astar:");
		Astar astar = new Astar(dl);
		astar.run();
		System.out.println("\nChecking heuristic");
		checkHeuristicOptimist(dl);
		checkHeuristicConsistent(dl);
	}

	private static void checkHeuristicOptimist(DataLoader dl) {
		System.out.println("Checking if heuristic is optimistic.");
		boolean bool = false;
		Ucs ucs2 = new Ucs(dl);
		for (Entry<String, HashMap<String, Integer>> entry : dl.getStateModel().transitions.entrySet()) {
			if(entry.getValue() == null)
				continue;
			if(entry.getKey().equals(dl.getStateModel().initState))
				continue;
			int trueCost = ucs2.fromState(entry.getKey());
			ucs2.clearStructures();
			if(dl.getHeuristics().get(entry.getKey()) > trueCost) {
				bool = true;
				System.out.println("  [ERR] h(" + entry.getKey() + ") > h*: " 
				+ dl.getHeuristics().get(entry.getKey()) + " > " + trueCost);
			}
		}
		if(bool)
			System.out.println("Heuristic is not optimistic");
		else System.out.println("Heuristic is optimistic");
	}

	private static void checkHeuristicConsistent(DataLoader dl) {
		System.out.println("Checking if heuristic is consistent.");
		boolean bool = false;
		for (Entry<String, HashMap<String, Integer>> entry : dl.getStateModel().transitions.entrySet()) {
			if(entry.getValue() == null)
				continue;
			for (Entry<String, Integer> entry2 : entry.getValue().entrySet()) {
				if((dl.getHeuristics().get(entry.getKey()) > 
				(dl.getHeuristics().get(entry2.getKey()) + entry2.getValue()))) {
					bool = true;
					System.out.println("  [ERR] h(" + entry.getKey() + ") > h(" + entry2.getKey() + ") + c: " +
								   dl.getHeuristics().get(entry.getKey()) + " > " + 
								   dl.getHeuristics().get(entry2.getKey()) + " + " + 
								   entry2.getValue());
				}
			}
		}
		if(bool)
			System.out.println("Heuristic is not consistent");
		else System.out.println("Heuristic is consistent");
	}
}
