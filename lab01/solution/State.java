package hr.fer.www.ai.lab1;

public class State implements Comparable<State> {

	private State parent;	
	private String name;
	private int cost;
	private int heuristicCost = 0;
	
	public State(State parent, String name, int cost) {
		this.parent = parent;
		this.name = name;
		this.cost = cost;
	}
	public State getParent() {
		return parent;
	}
	public void setParent(State parent) {
		this.parent = parent;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	@Override
	public int compareTo(State o) {
		if(this.cost == o.cost)
			return 0;
		else if (this.cost < o.cost)
			return -1;
		else return 1;
	}	
	public int getHeuristicCost() {
		return heuristicCost;
	}
	public void setHeuristicCost(int heuristicCost) {
		this.heuristicCost = heuristicCost;
	}

}
