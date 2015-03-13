
import java.util.*;

public class PageRank
{
	static final double CONVERGENCE_THRESH = 0.00001;
	static final int MAX_ROUND_FOR_WEBGRAPH = 20;	//Integer.MAX_VALUE;
	public static final int DANGLING_AUX_NODE  = 1;
	public static final int DANGLING_SELF_LOOP = 2;
	public static final int DANGLING_NONE      = 3;
	public static final int DANGLING_NEW_WAY   = 4;
	static int DANGLING_METHOD = DANGLING_NEW_WAY;

	public static double[] compute(MyGraph graph, double d) throws Exception
	{
		return compute(graph, d, null);
	}

	public static double[] compute( MyGraph graph, double d,
									boolean[] node_is_disabled ) throws Exception
	{
		//Make sure dangling nodes are handled already:
		verify_graph_for_pagerank(graph);

		int n = graph.nodes.length;
		int round = 0;

		double[] score = new double[n];
		double[] old_score = new double[n];
		for(int i = 0; i < n; i++)
		{
			if(graph.nodes[i].index != i)
				throw new Exception("!!!!");
			old_score[i] = score[i] = 1. / n;
		}

		for(round = 0; ; round++)
		{
			for(int i = 0; i < n; i++)
			{
				Node node = graph.nodes[i];

				double sum = 0;
				for(Iterator<Node> iter = node.incoming_neighbors.iterator(); iter.hasNext(); )
				{
					Node neigh = iter.next();
					if( node_is_disabled == null || node_is_disabled[neigh.index] == false )
						sum += d * score[neigh.index] / (double) neigh.outdegree;
				}

				/* If the node is one of the voided nodes, assume a self loop on the node;
				 * no matter whether in the original graph there is already a self loop
				 * on this node or not (it is not counted in the above loop anyway). */
				if( node_is_disabled != null && node_is_disabled[node.index] )
					sum += d * score[node.index] / 1.;

				score[i] = sum + (1. - d) / n;
			}

			if(check_for_convergence(score, old_score) == true)
				break;

			for(int i = 0; i < n; i++)
				old_score[i] = score[i];
			//System.out.println("Round " + round + " of PageRank");
		}

		for(int i = 0; i < n; i++)
			graph.nodes[i].pagerank_score = score[i];

		System.out.println("PageRank took " + (round + 1) + " rounds");
		return score;
	}

/*liyue
	public static double[] compute_for_webgraph(WebGraph graph, double d) throws Exception
	{
		return compute_for_webgraph(graph, d, -1);
	}
*/
	
	/* disabled_node can be -1, which means no node is disabled (as in
	 * the typical case). */

//liyue
//	public static double[] compute_for_webgraph( WebGraph graph, double d,
//												 int disabled_node ) throws Exception
//	{
//		/* Assume an aux node with a self loop, which has
//		 * incoming link from all dangling nodes. */
//		Vector<Integer> dangling_nodes = new Vector<Integer>();
//		for(int i = 0; i < graph.n; i++)
//			if(graph.get_outdegree(i) == 0)
//				dangling_nodes.add(i);
//
//		int round = 0;
//		boolean has_dangling = (dangling_nodes.size() > 0);
//		int new_n = graph.n + (has_dangling ? 1 : 0);
//		int[] inneighs_of_aux;
//		if(has_dangling)
//		{
//			inneighs_of_aux = new int[ dangling_nodes.size() + 1 ];
//			for(int i = 0; i < dangling_nodes.size(); i++)
//				inneighs_of_aux[i] = dangling_nodes.get(i);
//			inneighs_of_aux[ dangling_nodes.size() ] = graph.n;
//			System.out.println("Fixed " + dangling_nodes.size() +
//								" dangling nodes of the graph");
//		}
//		else
//		{
//			inneighs_of_aux = null;
//			System.out.println("Graph has no dangling node to fix");
//		}
//
//		double[] score = new double[new_n];
//		double[] old_score = new double[new_n];
//		for(int i = 0; i < new_n; i++)
//			old_score[i] = score[i] = 1. / new_n;
//
//		for(round = 0; round < MAX_ROUND_FOR_WEBGRAPH; round++)
//		{
//			for(int node = 0; node < new_n; node++)
//			{
//				double sum = 0;
//				int[] incoming_neighs;
//				int n_incoming;
//				if(has_dangling && node == new_n - 1)	//the aux node
//				{
//					incoming_neighs = inneighs_of_aux;
//					n_incoming = inneighs_of_aux.length;
//				}
//				else
//				{
//					incoming_neighs = graph.get_incoming_neighbors_array(node);
//					n_incoming = graph.get_indegree(node);
//				}
//
//				for(int i = 0; i < n_incoming; i++)
//				{
//					int neigh = incoming_neighs[i], neigh_outdegree;
//					if(neigh == disabled_node)
//						continue;
//					else if(neigh == graph.n)
//						neigh_outdegree = 1;	//aux node
//					else if(graph.get_outdegree(neigh) == 0)
//						neigh_outdegree = 1;	//dangling node
//					else
//						neigh_outdegree = graph.get_outdegree(neigh);
//
//					sum += old_score[neigh] * d / (double) neigh_outdegree;
//				}
//
//				/* If the node is the voided node, assume a self loop on the node;
//				 * no matter whether in the original graph there is already a self loop
//				 * on this node or not (it is not counted in the above loop anyway). */
//				if(node == disabled_node)
//					sum += old_score[node] * d / 1.;
//
//				score[node] = sum + (1. - d) / new_n;
//
//				if(node % 1000000 == 0)
//					System.out.println("\t" + Math.round((node + 1) * 100. / (double) new_n) + "% of round " + (round + 1));
//			}
//
//			if(check_for_convergence(score, old_score) == true)
//				break;
//
//			//swap old_score and score
//			double[] temp = old_score;
//			old_score = score;
//			score = temp;
//
//			System.out.println("Finished round " + (round + 1) + " of PageRank");
//		}
//
//		System.out.println("PageRank took " + (round + 1) + " rounds");
//		return score;
//	}

	public static double compute_from_sparsematrix(SparseMatrix m, int target_page, double d, int graph_n)
	{
		int round = 0;
		int subgraph_n = m.rows.length;

		double[] score = new double[subgraph_n];
		double[] old_score = new double[subgraph_n];
		for(int i = 0; i < subgraph_n; i++)
			old_score[i] = score[i] = 1. / graph_n;

		for(round = 0; ; round++)
		{
			for(int node = 0; node < subgraph_n; node++)
			{
				double sum = 0;
				for(Iterator<SparseMatrixEntry> iter = m.rows[node].iterator(); iter.hasNext(); )
				{
					SparseMatrixEntry entry = iter.next();
					int neigh = entry.index;
					double w = entry.value;
					sum += old_score[neigh] * w;
				}

				/* If node is a dangling node, assume a self-loop on it */
				if(m.cols[node].size() == 0)
					sum += old_score[node] * d / 1.;

				score[node] = sum + (1 - d) / graph_n;
			}

			if(check_for_convergence(score, old_score) == true)
				break;

			//swap old_score and score
			double[] temp = old_score;
			old_score = score;
			score = temp;
		}

		System.out.println("PageRank took " + (round + 1) + " rounds");
		return score[target_page];
	}

	static boolean check_for_convergence(double[] score, double[] old_score)
	{
		double sum_diff = 0, max_diff = 0;
		boolean converged = true;

		for(int i = 0; i < score.length; i++)
		{
			double diff = Math.abs(score[i] - old_score[i]) / Math.min(score[i], old_score[i]);
			sum_diff += diff;
			if(diff > max_diff)
				max_diff = diff;
			if(diff >= CONVERGENCE_THRESH)
			{
				converged = false;
				break;
			}
		}

		//double avg_diff = sum_diff / score.length;
		//System.out.println("PageRank convergence: avg_diff = " +
		//					avg_diff + ",\t max_diff = " + max_diff);

		return converged;
	}

	public static boolean check_for_convergence( HashMap<Integer, Double> pagerank,
												 HashMap<Integer, Double> new_pagerank )
												 throws Exception
	{
		double CUSTOM_CONVERGENCE_THRESH = 0.001;
		if(pagerank.size() != new_pagerank.size())
			throw new Exception("The two maps do not contain the same set of pages");

		for( Map.Entry<Integer, Double> entry : pagerank.entrySet() )
		{
			int page = entry.getKey();
			double pr1 = entry.getValue();
			Double __pr2 = new_pagerank.get(page);
			if(__pr2 == null)
				throw new Exception("The two maps do not contain the same set of pages");
			double pr2 = __pr2.doubleValue();

			double diff = Math.abs(pr2 - pr1) / ((pr1 + pr2) / 2.);;
			if(diff >= CUSTOM_CONVERGENCE_THRESH)
				return false;
		}

		return true;
	}

	//Get an array of pagerank scores; the i-th entry is the pagerank of node i.
	public static double[] get_pagerank_array(MyGraph graph, double d) throws Exception
	{
		//Remove dangling nodes:
		fix_graph_for_pagerank(graph);

		int n = graph.nodes.length;
		double[] res = new double[n];

		compute(graph, d);
		for(int i = 0; i < n; i++)
			res[i] = graph.nodes[i].pagerank_score;

		return res;
	}

	//Make sure no dangling nodes
	public static void verify_graph_for_pagerank(MyGraph graph) throws Exception
	{
		if(DANGLING_METHOD == DANGLING_NONE)
			return;
		for(int i = 0; i < graph.nodes.length; i++)
			if(graph.nodes[i].outdegree == 0)
				throw new Exception("Cannot compute PageRank: dangling node " + i);
	}

	public static void __fix_graph_for_pagerank_by_adding_aux(
						MyGraph graph, boolean just_self_looped )
	{
		//First see if there is anything to do in the first place:
		int n = graph.nodes.length;
		boolean has_dangling = false;
		for(int i = 0; i < n; i++)
			if(graph.nodes[i].outdegree == 0)
			{
				has_dangling = true;
				break;
			}
		if(!has_dangling)
		{
			System.out.println("Graph doesn't have any dangling nodes to fix.");
			return;
		}

		//Add a node X which has link to either all nodes of the graph or just itself
		Node node = new Node(n/*id*/);
		node.index = n;
		node.is_aux_for_danglings = true;
		graph.has_aux_node = true;
		if(just_self_looped)
		{
			node.outgoing_neighbors.add(node);
			node.incoming_neighbors.add(node);
			node.indegree = 1;
			node.outdegree = 1;
		}
		else
		{
			for(int i = 0; i < n; i++)
			{
				node.outgoing_neighbors.add(graph.nodes[i]);
				graph.nodes[i].incoming_neighbors.add(node);
				node.outdegree++;
				graph.nodes[i].indegree++;
			}
		}

		//Now from any dangling node, add a link to the new node:
		int n_fixed = 0;
		for(int i = 0; i < n; i++)
			if(graph.nodes[i].outdegree == 0)
			{
				graph.nodes[i].outgoing_neighbors.add(node);
				node.incoming_neighbors.add(graph.nodes[i]);
				graph.nodes[i].outdegree++;
				node.indegree++;
				n_fixed++;
			}

		//Finally, add the new node to graph.nodes array:
		Node[] old_array = graph.nodes;
		graph.nodes = new Node[n + 1];
		for(int i = 0; i < n; i++)
			graph.nodes[i] = old_array[i];
		graph.nodes[n] = node;

		System.out.println("Fixed all " + n_fixed +
				" dangling nodes of the graph by adding aux node (method = " +
				(just_self_looped ? "new" : "old") + ").");
	}

	public static void __fix_graph_for_pagerank_by_adding_selfloops(MyGraph graph) throws Exception
	{
		//First see if there is anything to do in the first place:
		int n = graph.nodes.length;
		int n_fixed = 0;

		for(int i = 0; i < n; i++)
		{
			Node node = graph.nodes[i];
			if(node.outdegree == 0)
			{
				Node.add_selfloop_to_dangling_node(node);
				n_fixed++;
			}
		}

		if(n_fixed > 0)
			System.out.println("Fixed all " + n_fixed + 
					" dangling nodes of the graph by adding self loops.");
		else
			System.out.println("Graph doesn't have any dangling nodes to fix.");
	}

	//Solve the problem of "dangling" nodes (those without outgoing neighbors):
	public static void fix_graph_for_pagerank(MyGraph graph) throws Exception
	{
		if(DANGLING_METHOD == DANGLING_AUX_NODE)
			__fix_graph_for_pagerank_by_adding_aux(graph, false);
		else if(DANGLING_METHOD == DANGLING_SELF_LOOP)
			__fix_graph_for_pagerank_by_adding_selfloops(graph);
		else if(DANGLING_METHOD == DANGLING_NEW_WAY)
			__fix_graph_for_pagerank_by_adding_aux(graph, true);
		else
			; //do nothing
	}

	/* Returns an array of nodes sorted according to their PageRank score.
	 * The top-k pages are returned where k is res_len. */
	public static Node[] run_pagerank_sort(MyGraph graph, double damping_factor, int res_len) throws Exception
	{
		PageRank.fix_graph_for_pagerank(graph);
		double[] pagerank = PageRank.compute(graph, damping_factor);

		int n = graph.nodes.length;
		boolean[] picked = new boolean[n];
		for(int i = 0; i < n; i++)
			picked[i] = false;

		if(res_len > n)
			res_len = n;
		Node[] sorted_nodes = new Node[res_len];
		for(int i = 0; i < res_len; i++)
		{
			int max_j = -1;
			for(int j = 0; j < n; j++)
				if(!picked[j])
					if(max_j == -1 || pagerank[j] > pagerank[max_j])
						max_j = j;

			sorted_nodes[i] = graph.nodes[max_j];
			picked[max_j] = true;
//			System.out.println("Rank " + i + ": Node " + sorted_nodes[i].index +
//								" (" + MCL.round(sorted_nodes[i].pagerank_score, 4) + ")");
		}

		return sorted_nodes;
	}
}
