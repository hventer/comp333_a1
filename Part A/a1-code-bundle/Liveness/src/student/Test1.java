package student;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.TreeMap;

import org.junit.Test;


public class Test1 {


	String dataDir = "C:/Users/Hannes/Documents/Uni Work/2018 S2/COMP333/Assignment 1/Part A/Data files for Assignment 1-20180826/";
	String dataFileName = "ex1";
	String fInName = dataDir + dataFileName + ".dat";
	String solnInName = dataDir + dataFileName + ".out";

	String dataFileName2 = "ex2mod";
	String fInName2 = dataDir + dataFileName2 + ".dat";
	String solnInName2 = dataDir + dataFileName2 + ".out";

	String dataFileName3 = "ex3mod";
	String fInName3 = dataDir + dataFileName3 + ".dat";
	String solnInName3 = dataDir + dataFileName3 + ".out";

	String dataFileName4 = "ex4mod";
	String fInName4 = dataDir + dataFileName4 + ".dat";
	String solnInName4 = dataDir + dataFileName4 + ".out";


	@Test
	public void testReadWrite() {
		TreeMap<String, Integer> soln;
		Liveness a = new Liveness();
		soln = a.generateSolution(fInName);
		a.writeSolutionToFile(soln, solnInName);

//		fail("Not yet implemented");
	}

	@Test
	public void lineReader() {
		TreeMap<String, Integer> soln;
		Liveness a = new Liveness();
		soln = a.generateSolution(fInName);

		ArrayList<String> fileLines = new ArrayList<String>();
		fileLines = a.getProgramLines();

		assertEquals("live-in k0 j1", fileLines.get(0));
		assertEquals("gA := k0 - 1", fileLines.get(1));
		assertEquals("h5 := mem[j1+12]", fileLines.get(2));
		assertEquals("fX := gA * h5", fileLines.get(3));
		assertEquals("live-out fX", fileLines.get(4));


		TreeMap<String, Integer> soln4;
		Liveness d = new Liveness();
		soln4 = d.generateSolution(fInName4);

		ArrayList<String> fileLines4 = new ArrayList<String>();
		fileLines4 = d.getProgramLines();

		assertEquals("live-in a0 b0 d0", fileLines4.get(0));
		assertEquals("c0 := a0 + 8", fileLines4.get(1));
		assertEquals("e0 := mem[c0] + 12 * b0", fileLines4.get(2));
		assertEquals("z0 := c0 + 4", fileLines4.get(3));
		assertEquals("g0 := a0 + e0", fileLines4.get(4));
		assertEquals("z0 := g0 * a0", fileLines4.get(5));
		assertEquals("k0 := 10 + g0 * 2 +z0", fileLines4.get(6));
		assertEquals("h0 := mem[e0] + mem[e0+k0]", fileLines4.get(7));
		assertEquals("j0 := d0 - 12", fileLines4.get(8));
		assertEquals("f0 := j0 * k0", fileLines4.get(9));
		assertEquals("z0 := h0 / 2", fileLines4.get(10));
		assertEquals("e0 := mem[j0] + k0", fileLines4.get(11));
		assertEquals("i0 := j0 * 5 + 2", fileLines4.get(12));
		assertEquals("live-out f0 i0 k0 z0", fileLines4.get(13));
	}

	@Test
	public void lineSplitter() {
		TreeMap<String, Integer> soln;
		Liveness a = new Liveness();
		soln = a.generateSolution(fInName);

		String[] lhs = a.getLhs();
		String[] rhs = a.getRhs();

		assertEquals("k0 j1", lhs[0]);
		assertEquals("gA", lhs[1]);
		assertEquals("h5", lhs[2]);
		assertEquals("fX", lhs[3]);
		assertEquals(null, lhs[4]);

		assertEquals(null, rhs[0]);
		assertEquals("k0", rhs[1]);
		assertEquals("j1", rhs[2]);
		assertEquals("gA h5", rhs[3]);
		assertEquals("fX", rhs[4]);


		TreeMap<String, Integer> soln4;
		Liveness d = new Liveness();
		soln4 = d.generateSolution(fInName4);

		String[] lhs4 = d.getLhs();
		String[] rhs4 = d.getRhs();

		assertEquals("a0 b0 d0", lhs4[0]);
		assertEquals("c0", lhs4[1]);
		assertEquals("e0", lhs4[2]);
		assertEquals("z0", lhs4[3]);
		assertEquals("g0", lhs4[4]);
		assertEquals("z0", lhs4[5]);
		assertEquals("k0", lhs4[6]);
		assertEquals("h0", lhs4[7]);
		assertEquals("j0", lhs4[8]);
		assertEquals("f0", lhs4[9]);
		assertEquals("z0", lhs4[10]);
		assertEquals("e0", lhs4[11]);
		assertEquals("i0", lhs4[12]);
		assertEquals(null, lhs4[13]);

		assertEquals(null, rhs4[0]);
		assertEquals("a0", rhs4[1]);
		assertEquals("c0 b0", rhs4[2]);
		assertEquals("c0", rhs4[3]);
		assertEquals("a0 e0", rhs4[4]);
		assertEquals("g0 a0", rhs4[5]);
		assertEquals("g0 z0", rhs4[6]);
		assertEquals("e0 e0 k0", rhs4[7]);
		assertEquals("d0", rhs4[8]);
		assertEquals("j0 k0", rhs4[9]);
		assertEquals("h0", rhs4[10]);
		assertEquals("j0 k0", rhs4[11]);
		assertEquals("j0", rhs4[12]);
		assertEquals("f0 i0 k0 z0", rhs4[13]);
	}

	@Test
	public void livesnessAnalysis() {
		TreeMap<String, Integer> soln;
		Liveness a = new Liveness();
		soln = a.generateSolution(fInName);

		assertEquals(4, a.getLiveness().get("fX").get(0)[0]);
		assertEquals(5, a.getLiveness().get("fX").get(0)[1]);

		assertEquals(2, a.getLiveness().get("gA").get(0)[0]);
		assertEquals(4, a.getLiveness().get("gA").get(0)[1]);

		assertEquals(3, a.getLiveness().get("h5").get(0)[0]);
		assertEquals(4, a.getLiveness().get("h5").get(0)[1]);

		assertEquals(1, a.getLiveness().get("j1").get(0)[0]);
		assertEquals(3, a.getLiveness().get("j1").get(0)[1]);

		assertEquals(1, a.getLiveness().get("k0").get(0)[0]);
		assertEquals(2, a.getLiveness().get("k0").get(0)[1]);



		TreeMap<String, Integer> soln2;
		Liveness b = new Liveness();
		soln2 = b.generateSolution(fInName2);

		assertEquals(7, b.getLiveness().get("c1").get(0)[0]);
		assertEquals(9, b.getLiveness().get("c1").get(0)[1]);

		assertEquals(1, b.getLiveness().get("k0").get(0)[0]);
		assertEquals(3, b.getLiveness().get("k0").get(0)[1]);
		assertEquals(8, b.getLiveness().get("k0").get(1)[0]);
		assertEquals(9, b.getLiveness().get("k0").get(1)[1]);

		assertEquals(1, b.getLiveness().get("j1").get(0)[0]);
		assertEquals(9, b.getLiveness().get("j1").get(0)[1]);

		assertEquals(6, b.getLiveness().get("m6").get(0)[0]);
		assertEquals(8, b.getLiveness().get("m6").get(0)[1]);

		assertEquals(5, b.getLiveness().get("eE").get(0)[0]);
		assertEquals(7, b.getLiveness().get("eE").get(0)[1]);

		assertEquals(4, b.getLiveness().get("fX").get(0)[0]);
		assertEquals(6, b.getLiveness().get("fX").get(0)[1]);

		assertEquals(2, b.getLiveness().get("gA").get(0)[0]);
		assertEquals(4, b.getLiveness().get("gA").get(0)[1]);

		assertEquals(3, b.getLiveness().get("h5").get(0)[0]);
		assertEquals(4, b.getLiveness().get("h5").get(0)[1]);
	}

}
