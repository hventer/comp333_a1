package student;

import java.io.IOException;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.*;


public class Liveness {
	ArrayList<String> programLines;
	String[] lhs;
	String[] rhs;

	TreeMap<String, ArrayList<int[]>> liveness = new TreeMap<String, ArrayList<int[]>>();

	TreeMap<String, Integer> variableRegister;



	public TreeMap<String, Integer> generateSolution(String fInName) {
		// PRE: fInName is a valid input file
		// POST: returns a TreeMap mapping variables (String) to registers (Integer)

		// TODO
		try {
			fileReader(fInName);
		} catch (IOException e) {
			System.out.println("in exception: " + e);
			e.printStackTrace();
		}

		livenessAnalysis();

		return variableRegister;
	}

	public void livenessAnalysis() {
		assignmentStatementSplitter();

		String[] rhsVariables; //all the variables on that line
		ArrayList<int[]> allLinesLive; //all the lines the variable is live ({[1][2]}{[3][4]})


		for(int i=rhs.length-1; i>0; i--) { //rhs doesn't go to 0 coz it's null

			rhsVariables = rhs[i].split("\\s");

			for(String item : rhsVariables) {

				allLinesLive = new ArrayList<int[]>();
				int[] linesLive = {1, i+1}; //current live lines (ie. 3-5)

				for(int k=i-1; k>0; k--) { //note lhs doesn't go to 0, don't check 0 yet
					//run from i-1 through the lhs

					if(item.equals(lhs[k])) { //if rhs==lhs

						linesLive[0] = k+1; //where liveness starts

						break; //don't need to keep searching
					}
				}

				if(liveness.get(item) == null) { //the variable isn't in treemap
					allLinesLive.add(linesLive);
					liveness.put(item, allLinesLive);
				}
				else { //variable already in treemap
					if(liveness.get(item).get(0)[0] > i) { //this is a new liveness
						liveness.get(item).add(0, linesLive); //add this to the front
					}
				}
			}
		}
	}


	public void assignmentStatementSplitter () {
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
			rhs[i] = tmod;
		}
	}


	public void fileReader(String fInName) throws IOException {
		// PRE: fInName is a valid input file
		// POST: Read in and store each program line as index in String ArrayList

		//Found how to import here: https://docs.oracle.com/javase/tutorial/essential/io/index.html
		//Also used techniques from Mark's 2018_S1_COMP225_Assignment2
		// DONE
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



	public void writeSolutionToFile(TreeMap<String, Integer> t, String solnName) {
		// PRE: t represents a valid register allocation
		// POST: the register allocation in t is written to file solnName

		// TODO
	}


	public TreeMap<String, ArrayList<int[]>> getLiveness() {
		return liveness;
	}

	public ArrayList<String> getProgramLines() {
		return programLines;
	}

	public String[] getLhs() {
		return lhs;
	}

	public String[] getRhs() {
		return rhs;
	}




	public static void main(String[] args) {

		ArrayList<Integer> test = new ArrayList<Integer>();
		test.add(3);
		test.add(0,2);
		System.out.println(test.get(0));
		System.out.println(test.get(1));

		/*
		String tmod = "10 mem[e0] + mem[e0+k0] * 10 + g0 * 2 / mem[c0] + 12 * b0 - 23";
		System.out.println(tmod);
		tmod = tmod.replace("mem[", "");
		tmod = tmod.replace("]", "");
		tmod = tmod.replaceAll("[\\W&&]+", " "); //remove any symbols
		System.out.println(tmod);

		tmod = tmod.replaceAll("[0-9][0-9] ", ""); //remove two digit numbers (ie. 12)
		System.out.println(tmod);
		tmod = tmod.replaceAll(" [0-9][0-9]", ""); //remove two digit numbers (ie. 12)
		System.out.println(tmod);
		tmod = tmod.replaceAll("[^A-Za-z][0-9]", ""); //remove digits without letter in front
		System.out.println(tmod);

		//tmod = tmod.replaceAll("\\s+", " ");

		//tmod = tmod.replaceAll("[^A-Za-z][0-9]", ""); //remove any two digit numbers (ie. 12)
		//System.out.println(tmod);

//		tmod = tmod.replaceAll(" [0-9]", ""); //remove any single digit numbers (ie. 2)
		System.out.println(tmod);

/*
		System.out.println(
			    "mem[c0+13] + 12 * b0".replaceAll("[\\W&&]+", " ")
			);

		String lhs;
		String rhs;
		String tmod = "h5 := mem[j1+12]";
		tmod = "live-in k0 j1";

		lhs = tmod.replace("live-in ", "");
		lhs = tmod.replaceAll(" :=(.*)", ""); //remove all after the ":="
		rhs = tmod.replaceAll("(.*):= ", "") ; //remove all before the ":="

		System.out.println(tmod);
		System.out.println(lhs);
		System.out.println(rhs);

		String tmod = "j1 + 12 * h4 - i9 / 74";
		if(tmod.startsWith("mem[")) {
			tmod=tmod.replace("mem[", "");
			tmod=tmod.replace("]", "");
			tmod=tmod.replaceAll("[\\W&&[^\\s]]+", " ");
		}
		else {
			tmod=tmod.replaceAll("[\\W&&]+", " ");
		}
		System.out.println(tmod);


		System.out.println(
			    tmod.replaceAll("[\\W&&[^\\s]]+", " ")
			);

		System.out.println(
			    "mem[j1+12]".replace("mem[", "")
			);

		System.out.println(
			    "gA := k0 -1".replaceAll(" :=(.*)", "")
			);

		System.out.println(
			    "gA := k0 -1".replaceAll("(.*):= ", "")
			);

			System.out.println(
			    "left-right".replaceAll("(.*)-(.*)", "$2-$1")
			); // prints "right-left"

			System.out.println(
			    "You want million dollar?!?".replaceAll("(\\w*) dollar", "US\\$ $1")
			); // prints "You want US$ million?!?"
			*/
	}

}
