
package disease.Algorithms;

import org.tweetsmining.model.matrices.GuavaMatrix;
import org.tweetsmining.model.matrices.IMatrix;
import disease.Algorithms.fwelement.DirectedEdge;


/*************************************************************************
 * Compilation:  javac FloydWarshall.java
 * Execution:  java FloydWarshall V E
 * Dependencies: AdjMatrixEdgeWeightedDigraph.java
 *
 * Floyd-Warshall all-pairs shortest path algorithm.
 *
 * % java FloydWarshall 100 500
 *
 * Should check for negative cycles during triple loop; otherwise
 * intermediate numbers can get exponentially large.
 * Reference: "The Floyd-Warshall algorithm on graphs with negative cycles"
 * by Stefan Hougardy
 *
 *************************************************************************/


/**
 * The <tt>FloydWarshall</tt> class represents a data type for solving the
 * all-pairs shortest paths problem in edge-weighted digraphs with
 * no negative cycles.
 * The edge weights can be positive, negative, or zero.
 * This class finds either a shortest path between every pair of vertices
 * or a negative cycle.
 * <p>
 * This implementation uses the Floyd-Warshall algorithm.
 * The constructor takes time proportional to <em>V</em><sup>3</sup> in the
 * worst case, where <em>V</em> is the number of vertices.
 * Afterwards, the <tt>dist()</tt>, <tt>hasPath()</tt>, and <tt>hasNegativeCycle()</tt>
 * methods take constant time; the <tt>path()</tt> and <tt>negativeCycle()</tt>
 * method takes time proportional to the number of edges returned.
 * <p>
 * For additional documentation, see <a href="/algs4/44sp">Section 4.4</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */ public class FloydWarshall {
    //private boolean hasNegativeCycle;  // is there a negative cycle?
    /**
     * Computes a shortest paths tree from each vertex to to every other vertex in
     * the edge-weighted digraph <tt>G</tt>. If no such shortest path exists for
     * some pair of vertices, it computes a negative cycle.
     * @param G the edge-weighted digraph
     */
    private FloydWarshall() {
    }
    private static FloydWarshall self = null;
    public static FloydWarshall getInstance() {
        if (self==null)
            self = new FloydWarshall();
        return self;
    }
    
    private int V;
    private int m;
    //double distTo[][];
    
    public int get_minIndex() {
        return m;
    }
    public int get_MaxIndex() {
        return V;
    }
    
    public double[][] executealgorithm(IMatrix G) {
        //GuavaMatrix distTo;  // distTo[v][w] = length of shortest v->w path
          // edgeTo[v][w] = last edge on shortest v->w path
        
        V = (int)G.getMaxKey();
        m = (int)G.getMinKey();
        DirectedEdge[][] edgeTo;
        double distTo[][] = new double[V+1][V+1];
        edgeTo = new DirectedEdge[V+1][V+1];

        // initialize distances to infinity
        System.out.println("Initializting the graph - max distances");
        for (int v = m; v <= V; v++) {
            if (v%1000==0)
                System.out.println(v + " for \"v\" over "+ V);
            for (int w = m; w <= V; w++) {
                distTo[v][w] = Double.POSITIVE_INFINITY;
                //distTo.set(v, w, Double.POSITIVE_INFINITY);
            }
        }

        // initialize distances using edge-weighted digraph's
        System.out.println("Setting the directed edges");
        for (int src = m; src <= V; src++) {
            if (src%1000==0)
                System.out.println(src + "'s OutSet over "+ V);
            for (Long tmp : G.getOut(src)) {
                if (tmp==null) continue; //XXX
                int dst = (int)tmp.intValue();
                double toset = G.get(src,tmp);
                distTo[src][dst] = toset;
                edgeTo[src][dst] = new DirectedEdge(src,dst,toset);
            }
            // in case of self-loops
            if (distTo[src][src] >= 0.0) {
                distTo[src][src] = 0;
                edgeTo[src][src] = null;
            }
        }

        // Floyd-Warshall updates
        System.out.println("Updating with weights");
        for (int i = m; i <= V; i++) {
            if (i%1000==0)
                System.out.println(i + " for \"i\" over "+ V);
            // compute shortest paths using only 0, 1, ..., i as intermediate vertices
            for (int v = m; v <= V; v++) {
                if (edgeTo[v][i] == null) continue;  // optimization
                for (int w = m; w <= V; w++) {
                    if (distTo[v][w] > distTo[v][i] + distTo[i][w]) {
                        distTo[v][w] =  distTo[v][i] + distTo[i][w];
                        edgeTo[v][w] = edgeTo[i][w];
                    }
                }
                // check for negative cycle
                if (distTo[v][v] < 0.0) {
                    //hasNegativeCycle = true;
                    return distTo; //null;
                }
            }
        }
        
        return distTo;
        //return distTo;
    }


    /**
     * Returns a negative cycle, or <tt>null</tt> if there is no such cycle.
     * @return a negative cycle as an iterable of edges,
     * or <tt>null</tt> if there is no such cycle
     */
    /*public Iterable<DirectedEdge> negativeCycle() {
        for (int v = 0; v < distTo.length; v++) {
            // negative cycle in v's predecessor graph
            if (distTo[v][v] < 0.0) {
                int V = edgeTo.length;
                EdgeWeightedDigraph spt = new EdgeWeightedDigraph(V);
                for (int w = 0; w < V; w++)
                    if (edgeTo[v][w] != null)
                        spt.addEdge(edgeTo[v][w]);
                EdgeWeightedDirectedCycle finder = new EdgeWeightedDirectedCycle(spt);
                assert finder.hasCycle();
                return finder.cycle();
            }
        }
        return null;
    }*/

    /**
     * Is there a path from the vertex <tt>s</tt> to vertex <tt>t</tt>?
     * @param distTo
     * @param s the source vertex
     * @param t the destination vertex
     * @return <tt>true</tt> if there is a path from vertex <tt>s</tt>
     * to vertex <tt>t</tt>, and <tt>false</tt> otherwise
     */
    public boolean hasPath(double[][] distTo, int s, int t) {
        return distTo[s][t] < Double.POSITIVE_INFINITY;
    }

    /**
     * Returns the length of a shortest path from vertex <tt>s</tt> to vertex <tt>t</tt>.
     * @param distTo
     * @param s the source vertex
     * @param t the destination vertex
     * @return the length of a shortest path from vertex <tt>s</tt> to vertex <tt>t</tt>;
     * <tt>Double.POSITIVE_INFINITY</tt> if no such path
     * @throws UnsupportedOperationException if there is a negative cost cycle
     */
    public double dist(double distTo[][], int s, int t) {
        /*if (hasNegativeCycle())
            throw new UnsupportedOperationException("Negative cost cycle exists");*/
        return distTo[s][t];
    }
    

    /**
     * Returns a shortest path from vertex <tt>s</tt> to vertex <tt>t</tt>.
     * @param distTo
     * @param s the source vertex
     * @param t the destination vertex
     * @return a shortest path from vertex <tt>s</tt> to vertex <tt>t</tt>
     * as an iterable of edges, and <tt>null</tt> if no such path
     * @throws UnsupportedOperationException if there is a negative cost cycle
     */
    /*public Iterable<DirectedEdge> path(GuavaMatrix distTo, int s, int t) {
        //if (hasNegativeCycle())
        //  throw new UnsupportedOperationException("Negative cost cycle exists");
        if (!hasPath(distTo,s, t)) return null;
        Stack<DirectedEdge> path = new Stack<>();
        for (DirectedEdge e = edgeTo[s][t]; e != null; e = edgeTo[s][e.from()]) {
            path.push(e);
        }
        return path;
    }*/

    // check optimality conditions
    /*private boolean check(GuavaMatrix G, int s) {
        int V = (int)G.getMaxKey();
        int m = (int)G.getMinKey();
        // no negative cycle
        if (!hasNegativeCycle()) {
            for (int v = m; v < V; v++) {
                for (DirectedEdge e : G.getOutDS(v)) {
                    int w = e.to();
                    for (int i = m; i < V; i++) {
                        if (distTo[i][w] > distTo[i][v] + e.weight()) {
                            System.err.println("edge " + e + " is eligible");
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }*/


    /**
     * Unit tests the <tt>FloydWarshall</tt> data type.
     * @param args
     */
    public static void main(String[] args) {

        // random graph with V vertices and E edges, parallel edges allowed
        /*
        int V = Integer.parseInt(args[0]);
        int E = Integer.parseInt(args[1]);
        AdjMatrixEdgeWeightedDigraph G = new AdjMatrixEdgeWeightedDigraph(V);
        for (int i = 0; i < E; i++) {
            int v = (int) (V * Math.random());
            int w = (int) (V * Math.random());
            double weight = Math.round(100 * (Math.random() - 0.15)) / 100.0;
            if (v == w) G.addEdge(new DirectedEdge(v, w, Math.abs(weight)));
            else G.addEdge(new DirectedEdge(v, w, weight));
        }
        System.out.println(G);
        */

        // run Floyd-Warshall algorithm
        GuavaMatrix  G = null;
        int V = (int)G.getMaxKey();
        int m = (int)G.getMinKey();
        FloydWarshall mindist = FloydWarshall.getInstance();
        double[][] result = mindist.executealgorithm(G);

        // print all-pairs shortest path distances
        System.out.printf("  ");
        for (int v = 0; v <V; v++) {
            System.out.printf("%6d ", v);
        }
        System.out.println();
        for (int v = m; v < V; v++) {
            System.out.printf("%3d: ", v);
            for (int w = m; w < V; w++) {
                if (mindist.hasPath(result,v, w)) System.out.printf("%6.2f ", mindist.dist(result,v, w));
                else System.out.printf("  Inf ");
            }
            System.out.println();
        }

        // print negative cycle - assumes that there are no negative cycles
        /*if (spt.hasNegativeCycle()) {
            System.out.println("Negative cost cycle:");
            for (DirectedEdge e : spt.negativeCycle())
                System.out.println(e);
            System.out.println();
        }*/

        // print all-pairs shortest paths
        {
            for (int v = m; v <V; v++) {
                for (int w = m; w < V; w++) {
                    //if (mindist.hasPath(v, w)) {
                        System.out.printf("%d to %d (%5.2f)  ", v, w, mindist.dist(result,v, w));
                        /*for (DirectedEdge e : mindist.path(result,v, w))
                            System.out.print(e + "  ");*/
                        System.out.println();
                    /*}
                    else {
                        System.out.printf("%d to %d no path\n", v, w);
                    }*/
                }
            }
        }

    }

}

