package hr.fer.www.ai.lab1;

import java.util.Comparator;

public class SortByHeuristicCost implements Comparator<State> {
	public int compare(State a, State b) { 
		return a.getHeuristicCost() - b.getHeuristicCost(); 
	} 
}