/* Driver code for PERT algorithm (Project 4)
 * @axb161031
 */

package axb161031;

import axb161031.Graph.Vertex;
import axb161031.Graph.Edge;
import axb161031.Graph.GraphAlgorithm;
import axb161031.Graph.Factory;

import java.io.File;
import java.util.*;

public class PERT extends GraphAlgorithm<PERT.PERTVertex> {
	/**
	 * Arraylist of vertices of sorted order.
	 */
    LinkedList<Vertex> finishList;
	/**
	 * Time taken to reach that vertex
	 */
	int time;
	/**
	 * List of vertex finishing vertex
	 */
	LinkedList<Vertex> V;
	/**
	 * Topological numbering of vertex
	 */
	static int topNum;
	/**
	 * Boolean that stores information about the graph being acyclic or cyclic
	 */
	boolean isDAG;
	/**
	 * Critical Path
	 */
	int CPL;

	public static class PERTVertex implements Factory {
	// Add fields to represent attributes of vertices here
		enum Colors {
			WHITE, GRAY, BLACK;
			}
		/**
		 * Color of vertex
		 */
		Colors color;
		/**
		 * Parent of vertex
		 */
		Vertex parent;
		/**
		 * Vertical distance travelled
		 */
		int dist;
		/**
		 * Finish time to reach the depth
		 */
		int fin;
		/**
		 * Top vertex
		 */
		int top;
		/**
		 * Duration to reach that vertex
		 */
		int duration;
        /**
         * Latest completion time
         */
		int lc;
        /**
         * Earliest completion time
         */
		int ec;
        /**
         * Slack time
         */
		int slack;
		public PERTVertex(Vertex u) {
			this.color = Colors.WHITE;
			parent = null;
			dist = 0;
			fin=0;
			top=0;
	}


        /**
         * Stores properties of vertices
         *
         *
         * @param u
         * @return vertex with properites
         */

	public PERTVertex make(Vertex u) {
			return new PERTVertex(u);
		}
    }


    // Constructor for PERT is private. Create PERT instances with static method pert().
    private PERT(Graph g) {
	super(g, new PERTVertex(null));
	CPL=0;
    }


    /**
     * Sets duration of vertex
     *
     *
     * @param u and d
     */

    public void setDuration(Vertex u, int d) {

	    get(u).duration=d;
    }

    /**
     * Implements Pert Algorithm
     *  Calculates the optimal time to complete the project.
     *  Calculates critical path to complete the path
     *
     *  @See wiki: PERT
     *
     */

    public boolean pert() {
		LinkedList<Vertex> topOrder = topologicalOrder();
		//if DAG implement pert


		if(isDAG){
			V = new LinkedList<>();

			CPL = Integer.MIN_VALUE;
			for(Vertex u: g){
				get(u).ec = get(u).duration;

			}
			for(Vertex u: topOrder){
				if(u.outDegree()==0) {
					V.add(u);

				}

				for(Edge e: g.outEdges(u)){
					if (get(e.toVertex()).ec < (get(u).ec + get(e.toVertex()).duration))
						get(e.toVertex()).ec = get(u).ec + get(e.toVertex()).duration;

				}
			}
			for(Vertex u: V ){

				if(get(u).ec > CPL)
					CPL= get(u).ec;
			}

			for (Vertex u: g){
				get(u).lc = CPL;
			}
			Iterator<Vertex> itr= topOrder.descendingIterator();
			while(itr.hasNext()){
				Vertex u= itr.next();
				for(Edge e: g.outEdges(u)){
					if(get(u).lc > (get(e.toVertex()).lc - get(e.toVertex()).duration))
						get(u).lc = get(e.toVertex()).lc - get(e.toVertex()).duration;

				}
				get(u).slack = get(u).lc - get(u).ec;
			}

			return true;
		}
		return false;
    }

    /**
     * Initialization of DFS
     * Topological sorting of vertices
     *
     *
     *
     * @return list of topologically sorted vertices
     */

    LinkedList<Vertex> topologicalOrder() {
    	//Initialization for DFS
		finishList =new LinkedList<>();
		time=0;
		topNum=g.size();
		for (Vertex u: g){
			get(u).color = PERTVertex.Colors.WHITE;
			get(u).parent = null;
		}
		//Loop
		topNum=g.size();
		isDAG = true;
		for(Vertex u: g){
			if(get(u).color== PERTVertex.Colors.WHITE) {
				dfsVisit(u);
			}

		}
    	return finishList;
    }
    /**
     * Performs depth first traversal of vertex
     * @param u
     * See Wiki: Depth First Search Traversal
     *
     */


    void dfsVisit(Vertex u) {


		if((get(u).color)==PERTVertex.Colors.GRAY){
			isDAG = false;
			return;
		}
    	get(u).color = PERTVertex.Colors.GRAY;
    	get(u).dist= ++time;
    	//Loop
		for (Edge e: g.outEdges(u)){
			Vertex v= e.otherEnd(u);

			if(get(v).color== PERTVertex.Colors.WHITE){
				get(v).parent = u;
				dfsVisit(v);
			}

		}

		get(u).fin = ++time;
		get(u).color = PERTVertex.Colors.BLACK;
		get(u).top = topNum--;

		//If there is cycle pert cannot be executed

		finishList.addFirst(u);

    }

    // The following methods are called after calling pert().

    /**
     * Returns earliest completion time of vertex
     *
     *
     * @param u
     * @return ec property of u
     */

    public int ec(Vertex u) {

		return get(u).ec;
    }

    /**
     * Returns latest completion time of vertex
     *
     *
     * @param u
     * @return lc property of u
     */
    public int lc(Vertex u) {
	return get(u).lc;

    }

    /**
     * Returns slack time of vertex
     *
     *
     * @param u
     * @return slack property of u
     */
    public int slack(Vertex u) {

	return get(u).slack;
    }

    /**
     * Return length of critical time
     *
     *
     * @return CPL
     */
    public int criticalPath() {
		return this.CPL;
    }

    /**
     * Checks if the vertex is in critical path or not
     *
     *
     * @return boolean true or false
     */
    public boolean critical(Vertex u) {
	return get(u).slack ==0;

    }

    /**
     * Calculates the total number of critical vertices in vritical path
     *
     * @return count of critical vertices
     */
    public int numCritical() {
	int count = 0;
	for(Vertex v: g){
		if(critical(v))
			count++;
	}
	return count;
    }

    /* Create a PERT instance on g, runs the algorithm.
     * Returns PERT instance if successful. Returns null if G is not a DAG.
     */
    public static PERT pert(Graph g, int[] duration) {
	PERT p = new PERT(g);
	for(Vertex u: g) {
	    p.setDuration(u, duration[u.getIndex()]);
	}
	// Run PERT algorithm.  Returns false if g is not a DAG
	if(p.pert()) {
	    return p;
	} else {
	    return null;
	}
    }

    public static void main(String[] args) throws Exception {
    String graph = "10 13  1 2 1   2 4 1   2 5 1   3 5 1   3 6 1   4 7 1   5 7 1   5 8 1   6 8 1   6 9 1   7 10 1   8 10 1   9 10 1    0 3 2 3 2 1 3 2 4 1";
	Scanner in;
	// If there is a command line argument, use it as file from which
	// input is read, otherwise use input from string.
	in = args.length > 0 ? new Scanner(new File(args[0])) : new Scanner(graph);
	Graph g = Graph.readDirectedGraph(in);
	g.printGraph(false);

	int[] duration = new int[g.size()];
	for(int i=0; i<g.size(); i++) {
	    duration[i] = in.nextInt();
	}
	PERT p = pert(g, duration);
	if(p == null) {
	    System.out.println("Invalid graph: not a DAG");
	} else {
	    System.out.println("Number of critical vertices: " + p.numCritical());
	    System.out.println("u\tEC\tLC\tSlack\tCritical");
	    for(Vertex u: g) {
		System.out.println(u + "\t" + p.ec(u) + "\t" + p.lc(u) + "\t" + p.slack(u) + "\t" + p.critical(u) );

	    }
	}
    }
}
