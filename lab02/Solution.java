package ui;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class Solution {
	
	public static void main(String ... args) {
		//final long startTime = System.currentTimeMillis();
		boolean resolution = false;
		boolean cooking_test = false;
		boolean cooking_interactive = false;
		boolean verbose = false;
		String path_clauses = "";
		String path_user_commands = "";
		String goalClause = null;
		String goalClauseIndex = null;
		int indexSoS = 0;
		
		//Structures
		Map<Integer, String> clauses = new LinkedHashMap<Integer, String>();
		LinkedList<String> inputs = new LinkedList<String>();
		
		for(String arg : args) {
			if(arg.equals("resolution"))
				resolution = true;
			if(arg.equals("cooking_test"))
				cooking_test = true;
			if(arg.equals("cooking_interactive"))
				cooking_interactive = true;
		}
		if(args.length == 4) {
			if(args[3].equals("verbose"))
				verbose = true;
			path_user_commands = args[2];
		}
		else if(args.length == 3) {
			if(args[2].equals("verbose"))
				verbose = true;
			else path_user_commands = args[2];
		}
		
		path_clauses = args[1];
		
		try {
			if(resolution) {
				goalClauseIndex = readFile(path_clauses, clauses, indexSoS);
				String tempArray[] = goalClauseIndex.split(" ii ");
				goalClause = tempArray[0].strip();
				indexSoS = Integer.parseInt(tempArray[1].strip());
			}
			if(cooking_test) {
				readFileCooking(path_clauses, clauses);
				readInputFile(path_user_commands, inputs);
			}
			if(cooking_interactive)
				readFileCooking(path_clauses, clauses);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		int removed = removeSubStrings(clauses);
		indexSoS = indexSoS - removed;
		if(resolution)
			refResAlgorithm(clauses, goalClause, verbose, indexSoS);
		if(cooking_test)
			cookingrefRes(clauses, inputs, false, verbose, indexSoS);
		if(cooking_interactive)
			cookingInteractive(clauses, verbose, indexSoS);
		//final long endTime = System.currentTimeMillis();
		//System.out.println("Total execution time: " + (endTime - startTime)/1000. + "s");
	}

	private static void cookingInteractive(Map<Integer, String> clauses, boolean verbose, int indexSoS) {
		System.out.println("Testing cooking assistant with standard resolution\nConstructed with knowledge:");
		for(Entry<Integer, String> e : clauses.entrySet())
			System.out.println("> " + e.getValue());
		Scanner scanner = new Scanner(System.in);
		LinkedList<String> inputs = new LinkedList<String>();
		while(true) {
			System.out.println("\n>>> Please enter your query\n>>> ");
			String line = scanner.nextLine();
			if(line.equals("exit")) break; 
			inputs.add(line.toLowerCase());
			cookingrefRes(clauses, inputs, true, verbose, indexSoS);
			inputs.clear();
		}
		scanner.close();
	}

	private static void readFileCooking(String path_clauses, Map<Integer, String> clauses) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path_clauses), "UTF-8"));
		String line = bufferedReader.readLine().toLowerCase();
		int index = 1;
		while(line != null) {
			if(!line.startsWith("#")) {
				clauses.put(index, line.toLowerCase());
				index++;
			}
		    line = bufferedReader.readLine();
		}
		bufferedReader.close();		
	}

	private static void cookingrefRes(Map<Integer, String> clauses, LinkedList<String> inputs, boolean interactive, boolean verbose, int indexSoS) {
		for(String input : inputs) {
			String clause = input.substring(0, input.length()-2).strip();
			String command = input.substring(input.length()-1, input.length());
			Map<Integer, String> clausesTests = new LinkedHashMap<Integer, String>(clauses);
			if(command.equals("?")) {
				clausesTests.put(clausesTests.size()+1, clause);
				invertClause(clausesTests);
				refResAlgorithm(clausesTests, clause, verbose, indexSoS);
			} else if(command.equals("+")) {
				clauses.put(clauses.size()+1, clause);
				if(interactive)
					System.out.println("added " + clause);
			} else if(command.equals("-")) {
				Map<Integer, String> tempClauses = new LinkedHashMap<Integer, String>(clauses);
				clauses.clear();
				int index = 1;
				for(Entry<Integer, String> e : tempClauses.entrySet()) {
					if(e.getValue().equals(clause)) continue;
					clauses.put(index++, e.getValue());
				}
				if(interactive)
					System.out.println("removed " + clause);
			}
		}		
	}

	private static void invertClause(Map<Integer, String> clauses) {
		int index = clauses.size();
		String goalClauseInverted = clauses.get(index);
		clauses.remove(index);
		if(goalClauseInverted.contains(" v ")) {
			String splitArray[] = goalClauseInverted.split(" v ");
			for(String st : splitArray) {				
				if(st.startsWith("~")) {
					clauses.put(clauses.size()+1, st.replace("~", ""));
				} else {
					clauses.put(clauses.size()+1, "~"+st);
				}
			}
		} else if (goalClauseInverted.startsWith("~")) {			
			goalClauseInverted = goalClauseInverted.replace("~", "");
			clauses.put(index, goalClauseInverted);
		} else
			clauses.put(index, "~" + goalClauseInverted);
	}

	private static void readInputFile(String path_user_commands, LinkedList<String> inputs) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path_user_commands), "UTF-8"));
		String line = bufferedReader.readLine();
		while(line != null) {
			if(!line.startsWith("#")) 
				inputs.add(line.toLowerCase());		
		    line = bufferedReader.readLine();
		}
		bufferedReader.close();
	}

	private static void refResAlgorithm(Map<Integer, String> clauses, String goalClause, boolean verbose, int indexSoS) {
		StringBuilder resultVerbose = new StringBuilder();
		int size = clauses.size();
		for(int i = 1; i <= size; i++) {
			if (i == indexSoS)
				resultVerbose.append("=============\n");
			resultVerbose.append(i+". "+clauses.get(i)+"\n");
		}
		resultVerbose.append("=============\n");
		Set<String> visitedClauses = new LinkedHashSet<String>();
		Set<String> newClauses = new LinkedHashSet<String>();
		String testClause;
		String newClause;
		int spot;
		boolean nil = false;
		Set<String> written = new HashSet<String>();
		while(true) {
			spot = clauses.size(); //used for verbose writing of indices
			for(int i = indexSoS; i <= clauses.size(); i++) {
				if(nil == true) break;
				for(int j = 1; j <= clauses.size(); j++) {
					if(nil == true) break;
					if(i == j) continue;
					if(visitedClauses.contains(i + "," + j) || visitedClauses.contains(j + "," + i)) continue;
					visitedClauses.add(i + "," + j);
					visitedClauses.add(j + "," + i);
					testClause = clauses.get(i) + " v " + clauses.get(j);
					newClause = occurence(testClause);
					if(!testClause.equals(newClause)) {
						if(newClause.isBlank()) {
							newClause = "NIL";
							nil = true;
						}
						newClauses.add(newClause);
						if(!written.contains(newClause)) {
							if(i < j)
								resultVerbose.append(++spot + ". " + newClause + " (" + i + "," + j + ")" +"\n");
							else
								resultVerbose.append(++spot + ". " + newClause + " (" + j + "," + i + ")" +"\n");
							written.add(newClause);
						}
					}
				}
			}
			if(newClauses.isEmpty())
				break;
				
			for(String clause : newClauses) {
				clauses.put(clauses.size()+1, clause);
			}
			newClauses.clear();
			if (clauses.containsValue("NIL")) {								
				break;
			}
		}
		resultVerbose.append("=============\n");
		if(verbose)
			System.out.print(resultVerbose);
		if(clauses.containsValue("NIL"))
			System.out.println(goalClause + " is true");
		else System.out.println(goalClause + " is unknown");
	}

	private static String occurence(String testClause) {
		boolean changed = false;
		String testClauseChanged = removeEqual(testClause);
		String temp = testClauseChanged.replace("~", "");
		String tempArray[] = temp.split(" v ");
		Set<String> tempSet = new HashSet<String>();
		ArrayList<String> remove = new ArrayList<String>();
		boolean added = true;
		for(String s : tempArray) {
			added = tempSet.add(s);
			if(added == false)
				remove.add(s);
		}
		
		if (!remove.isEmpty()) changed = true;
		String tempArray2[] = testClauseChanged.split(" v ");
		for(String rem : remove) {
			for(int i = 0; i < tempArray2.length; i++) {
				if(tempArray2[i].equals(rem)) {
					tempArray2[i] = tempArray2[i].replace(rem, "");
				} else if (tempArray2[i].equals("~"+rem))
					tempArray2[i] = tempArray2[i].replace("~"+rem, "");
			}
		}
		if (!changed)
			return testClause;
			
		else {
			StringBuilder sb = new StringBuilder();
			for(String s : tempArray2) {
				if(!s.isEmpty())
					sb.append(s + " v ");
			}
			if(!sb.toString().isEmpty())
				sb.replace(sb.length()-3, sb.length(), "");
			return sb.toString().strip();
		}
	}

	private static String removeEqual(String clause) {
		Set<String> tempSet = new HashSet<String>();
		ArrayList<String> remove = new ArrayList<String>();
		boolean added = true;
		if(clause.length() > 1) {
			String clauseArray[] = clause.split(" v ");
			for(String s : clauseArray) {
				added = tempSet.add(s);
				if(added == false)
					remove.add(s);
			}
			for(String rem : remove) {
				while(true) {
					if(clause.indexOf(rem) == clause.lastIndexOf(rem))
						break;
					String tempClause = clause.replace(" v "+rem, "");
					clause = tempClause;
				}
			}
		}
		return clause;
	}

	private static String readFile(String path_clauses, Map<Integer, String> clauses, int indexSoS) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path_clauses), "UTF-8"));
		String line = bufferedReader.readLine().toLowerCase();
		String delStrat;
		int index = 1;
		while(line != null) {
			if(!line.startsWith("#")) {
				delStrat = removeEqual(line.toLowerCase());
				clauses.put(index, delStrat);
				index++;
			}
		    line = bufferedReader.readLine();
		}
		bufferedReader.close();
		index--;
		String goalClause = clauses.get(index);
		indexSoS = index;
		invertClause(clauses);
	return goalClause +  " ii " + indexSoS;
	}

	private static int removeSubStrings(Map<Integer, String> clauses) {
		Set<Integer> remove = new HashSet<Integer>();
		for(int i = 1; i <= clauses.size(); i++) {
			for(int j = i+1; j <= clauses.size(); j++) {
				if(i == j) continue;
				String firstComp = " v " + clauses.get(i) + " v ";
				String secondComp = " v " + clauses.get(j) + " v ";
				if(firstComp.contains(secondComp))
					remove.add(i);
				if(secondComp.contains(firstComp))
					remove.add(j);
			}
		}
		//remove clause and fix numbering
		Map<Integer, String> tempClauses = new LinkedHashMap<Integer, String>(clauses);
		clauses.clear();
		int index = 1;
		for(Entry<Integer, String> e : tempClauses.entrySet()) {
			if(remove.contains(e.getKey())) continue;
			clauses.put(index++, e.getValue());
		}
		return remove.size();
	}
}
