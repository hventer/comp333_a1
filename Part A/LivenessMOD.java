package student;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.*;
import java.util.Map.Entry;


class Variable {
	private String name;
	private Integer register;
	private ArrayList<int[]> allLinesLive = new ArrayList<int[]>();
	List<Integer> linesLiveList;
	private VariableCollection neighbour; // the collection of neighbours

	public Variable(String n) {
		name = n;
		neighbour = new VariableCollection();
	}

	public void addLiveLines(int[] lines) {
		allLinesLive.add(0, lines);
	}

	public List<Integer> getLinesLiveList() {
		// PRE: -
		// POST: Gives a list of every line that this variable is live
		linesLiveList = new ArrayList<Integer>();

		for(int[] lines : allLinesLive)
			for(int i=lines[0]; i<=lines[1]; i++)
				linesLiveList.add(i);

		return linesLiveList;
	}


	public void addNeighbour(String name) {
		// PRE: -
		// POST: Adds a neighbour to the current variable as part of graph structure
		neighbour.push(name);
	}

	public Integer numNeighbours() {
		// PRE: -
		// POST: Returns the number of neighbours of this variable
		return getNeighbourNames().size();
		/*
		Integer count = 0;
		for(Variable entry : neighbour.values())
			count++;
		return count;
		*/
	}

	public void deleteAllNeighbours() {
		// PRE: -
		// POST: Deletes all neighbours of this variable
		while(!neighbour.isEmpty())
			neighbour.pop();
	}

	public Vector<String> getNeighbourNames() {
		// PRE: -
		// POST: Returns names of neighbouring variables as vector of strings
		Vector<String> v = new Vector<String>();

		for(String neighName : neighbour.keySet())
			v.add(neighName);

		return v;
	}

	public Boolean isNeighbour(String name) {
		// PRE: -
		// POST: Returns true if name is neighbour of the current variable, false otherwise
		if(neighbour.get(name) == null)
	        	return false;
		else
			return true;
	}

	public String getName() {
		return name;
	}

	public ArrayList<int[]> getAllLinesLive() {
		return allLinesLive;
	}

	public void setRegister(Integer r) {
		register = r;
	}

	public Integer getRegister() {
		return register;
	}


	public static void main(String[] args) {

	}
}

/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////

class VariableCollection extends TreeMap<String, Variable> {
	private TreeMap<Integer, Vector<String>> variablesInEachLine;

	public VariableCollection() {
		super();
	}

	public VariableCollection(String fInName) {
		super();


		Worker.fileReader(fInName);

		Worker.assignmentStatementSplitter();

		livenessAnalysis();
		constructSharedLinesGraph();
	}


	public void push(String name) {
		// inserts first element of list, treating as a queue
		this.put(name, getVariableByName(name));
	}

	public void pop() {
		// deletes first element of list, treating as a queue
		this.remove(this.firstKey());
	}

	public Variable getVariableByName(String name) {
		// PRE: -
		// POST: Returns the variable object that with variable name
		return this.get(name);
	}

	public void variablesLiveAtEachLine() {
		variablesInEachLine = new TreeMap<Integer, Vector<String>>();

		for(int i=0; i<Worker.getProgramLines().size(); i++) {
			//run through the lines of the program

			Vector<String> v = new Vector<String>();

			for(Variable variable : this.values())
				//each line of the program, run through all the variable in this treemap

				if(variable.getLinesLiveList().contains(i+1))
					//if this variable is live during this line (i+1)

					v.add(variable.getName()); //add it to the vector of variables

			variablesInEachLine.put(i+1, v);
		}
	}

	public void constructSharedLinesGraph() {
		// PRE:
		// POST: Graph constructed, with variables as vertices,
		//         and edges between them if they liveness

		for(Variable variable : this.values())
			//all the variables in this treemap

			for(Vector<String> line : getVariablesInEachLine().values())
				//each line of variablesInEachLine

				if(line.contains(variable.getName()))
					//the current variable is in this line

					for(String v : line)
						//all the variables in this line

						if(!v.equals(variable.getName()) && !variable.isNeighbour(v))
							//not the current variable && not already neighbours
							variable.addNeighbour(v);
	}


	public void livenessAnalysis() {
		String[] rhsVariables; //all the variables on that line
		ArrayList<int[]> allLinesLive; //all the lines the variable is live ({[1][2]}{[3][4]})


		for(int i=Worker.getRhs().length-1; i>0; i--) { //rhs doesn't go to 0 coz it's null

			rhsVariables = Worker.getRhs()[i].split("\\s");

			for(String item : rhsVariables) {

				if(!item.equals("")) { //check that there are variables on this line

					Variable v = new Variable(item);
					allLinesLive = new ArrayList<int[]>();
					int[] linesLive = {1, i+1}; //current live lines (ie. 3-5)

					for(int k=i-1; k>0; k--) { //note lhs doesn't go to 0, don't check 0 yet
						//run from i-1 through the lhs

						if(item.equals(Worker.getLhs()[k])) { //if rhs==lhs

							linesLive[0] = k+1; //where liveness starts

							break; //don't need to keep searching
						}
					}

					if(this.get(item) == null) { //the variable isn't in treemap
						allLinesLive.add(linesLive);
						v.addLiveLines(linesLive);
						this.put(item, v);
					}
					else { //variable already in treemap
						if(this.get(item).getAllLinesLive().get(0)[0] > i) { //this is a new liveness
							this.get(item).addLiveLines(linesLive); //add this to the front
						}
					}
				}
			}
		}
	}


	/*
	 *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * THIS IS MY ACTUAL ALGORITHM * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 *
	 *///TODO

	public TreeMap<String, Integer> registerAllocation() {
		TreeMap<String, Integer> minVariableRegister = new TreeMap<String, Integer>();
		TreeMap<String, Integer> tempVariableRegister = new TreeMap<String, Integer>();
		Integer rTemp = -1;
		Integer rMin = this.size();


		ArrayList<Variable> variableList = new ArrayList<Variable>();
		for(Variable v : this.values())
			variableList.add(v);


		Integer register;
		for(int i=0; i<this.size(); i++) {
			//run through this treemap starting at 0, then at 1, then at 2 etc.

			for(int k=i; k<this.size()+i; k++) {//size()+0, size()+1, size()+2...
				//all the variables in this treemap starting at i
				int counter = k;

				if(counter >= this.size()) {//k has gone past size, start from 0 up to i;
					counter-= this.size(); //subtract size which (13-11=2)
				}

				Variable variable = variableList.get(counter);

				register = 1;

				for(int m=0; m<variable.getNeighbourNames().size(); m++) {
					//all the neighbours of this register

					if(getVariableByName(variable.getNeighbourNames().get(m)).getRegister() == register) {
						//this neighbour variable already uses this register

						register++; //increase the register
						m=-1; //re-check neighbour's registers with new register
					}
				}
				tempVariableRegister.put(variable.getName(), register);
				variable.setRegister(register);
			}

			for(Integer v : tempVariableRegister.values())
				rTemp = Math.max(rTemp, v);
			//get the number of registers used in temp

			for(Integer v : minVariableRegister.values())
				rMin = Math.max(rMin, v);
			//get the number of registers used in min

			if(rTemp < rMin) { //if temp uses less register than min
				rMin = rTemp;
				for(String v : tempVariableRegister.keySet()) {
					minVariableRegister.put(v, getVariableByName(v).getRegister()); //deep copy
				}
			}
		}
		return minVariableRegister;
	}

	/*
	 *
	 * ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^
	 * ^ ^ ^ ^ ^ ^ THIS IS MY ACTUAL ALGORITHM ^ ^ ^ ^ ^ ^
	 * ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^ ^
	 *
	 */



	public TreeMap<Integer, Vector<String>> getVariablesInEachLine() {
		variablesLiveAtEachLine();
		return variablesInEachLine;
	}
}

/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////

class Worker {
	private static ArrayList<String> programLines;
	private static String[] lhs;
	private static String[] rhs;

	public static void fileReader(String fInName) {
		// PRE: fInName is a valid input file
		// POST: Read in and store each program line as index in String ArrayList

		//Found how to import here: https://docs.oracle.com/javase/tutorial/essential/io/index.html
		//Also used techniques from Mark's 2018_S1_COMP225_Assignment2
		programLines = new ArrayList<String>();

		try (BufferedReader reader = Files.newBufferedReader(Paths.get(fInName));) {

			String line = reader.readLine();

			while (line != null) {
				programLines.add(line);
				line = reader.readLine(); //next line
			}
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
	}

	public static void assignmentStatementSplitter() {
		//DONE
		lhs = new String[programLines.size()];
		rhs = new String[programLines.size()];
		String tmod;

		tmod = programLines.get(0);
		lhs[0] = tmod.replace("live-in ", "");

		tmod = programLines.get(programLines.size()-1);
		rhs[programLines.size()-1] = tmod.replace("live-out ", "");


		for(int i=1; i<programLines.size()-1; i++) { //from second line to second-last line
			tmod = programLines.get(i); //get line
			lhs[i] = tmod.replaceAll(" :=(.*)", ""); //remove all after the ":="
			rhs[i] = tmod.replaceAll("(.*):= ", "") ; //remove all before the ":="
		}

		for(int i=1; i<rhs.length; i++) {
			tmod = rhs[i];
			tmod = tmod.replace("mem[", "");
			tmod = tmod.replace("]", "");
			tmod = tmod.replaceAll("[\\W&&]+", " "); //remove any symbols
			tmod = tmod.replaceAll("[0-9][0-9] ", ""); //remove two digit numbers (ie. 12)
			tmod = tmod.replaceAll(" [0-9][0-9]", ""); //remove two digit numbers (ie. 12)
			tmod = tmod.replaceAll("[^A-Za-z][0-9]", ""); //remove digits without letter in front
			tmod = tmod.replaceFirst("^[0-9] ", ""); //remove first character if its a digit
			tmod = tmod.replaceFirst("^[0-9]", ""); //remove first character if its a digit
			rhs[i] = tmod;
		}
	}


	public static String[] getLhs() {
		return lhs;
	}

	public static String[] getRhs() {
		return rhs;
	}

	public static ArrayList<String> getProgramLines() {
		return programLines;
	}
}

/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////

public class Liveness {
	private VariableCollection variables;
	private TreeMap<String, Integer> variableRegister;

	public TreeMap<String, Integer> generateSolution(String fInName) {
		// PRE: fInName is a valid input file
		// POST: returns a TreeMap mapping variables (String) to registers (Integer)
		variables = new VariableCollection(fInName);
		variableRegister = variables.registerAllocation();

		return variableRegister;
	}

	public void writeSolutionToFile(TreeMap<String, Integer> t, String solnName) {
		// PRE: t represents a valid register allocation
		// POST: the register allocation in t is written to file solnName
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(solnName, true));) {

			Integer r = -1;
			for(Integer v : t.values())
				r = Math.max(r, v);
			writer.write(r.toString());

			for(Entry<String, Integer> v : t.entrySet()) {
				writer.newLine();
				writer.append(v.getKey());
				writer.append(' ');
				writer.append(v.getValue().toString());
			}
		    writer.close();
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
	}


	public VariableCollection getVariables() {
		return variables;
	}

	public static void main(String[] args) {
	}
}
