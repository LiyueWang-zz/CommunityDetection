
import java.io.*;
import java.util.*;

public class MyGraph extends AbstractGraph
{
	public static int MAX_GRAPH_SIZE = Integer.MAX_VALUE;
	public static boolean ALLOW_SELF_LOOPS = false;
	public Node[] nodes;

	private MyGraph()
	{
	}

	public static MyGraph create_blank()
	{
		MyGraph g = new MyGraph();
		g.nodes = null;
		return g;
	}

	public static MyGraph create_from_matrix(int[][] matrix) throws Exception
	{
		int n = matrix.length;
		MyGraph graph = new MyGraph();

		graph.nodes = new Node[n];
		for(int i = 0; i < n; i++)
		{
			graph.nodes[i] = new Node(i);
			graph.nodes[i].index = i;
		}

		for(int row = 0; row < n; row++)
			for(int col = 0; col < n; col++)
				if(matrix[row][col] == 1)
				{
					graph.nodes[row].outgoing_neighbors.add( graph.nodes[col] );
					graph.nodes[col].incoming_neighbors.add( graph.nodes[row] );
					graph.nodes[row].outdegree++;
					graph.nodes[col].indegree++;
				}

		return graph;
	}
	/**
	 * @author LiyueWang
	 * @time 2014/11/18
	 * 
	 * @param matrix
	 * @return
	 * @throws Exception
	 */
	public static MyGraph create_from_file(String file_path) throws Exception
	{
		MyGraph graph = new MyGraph();
		BufferedReader reader = new BufferedReader( new FileReader(file_path) );
		String line="";
		//read the # of nodes from the first line: Nodes: xxx
		String firstline=reader.readLine();
		int n=Integer.parseInt(firstline.split(" ")[1]);

		//Update n according to MAX_GRAPH_SIZE:
		if(MAX_GRAPH_SIZE > 0 && n > MAX_GRAPH_SIZE)
			n = MAX_GRAPH_SIZE;

		//Initialize n empty nodes:
		graph.nodes = new Node[n];
		for(int i = 0; i < n; i++)
		{
			graph.nodes[i] = new Node(i);
			graph.nodes[i].index = i;
		}

		while ((line = reader.readLine()) != null) {
			int row=Integer.parseInt(line.split(" ")[0])-1;   
			int col=Integer.parseInt(line.split(" ")[1])-1;
			
			graph.nodes[row].outgoing_neighbors.add( graph.nodes[col] );
			graph.nodes[col].incoming_neighbors.add( graph.nodes[row] );
			graph.nodes[row].outdegree++;
			graph.nodes[col].indegree++;
			
		}

		return graph;
	}

	public static MyGraph create_from_webspam2006_dataset(String file_path) throws Exception
	{
		MyGraph graph = new MyGraph();
		BufferedReader reader = new BufferedReader( new FileReader(file_path) );

		//Obtain num nodes:
		String s;
		int orig_n, n = -1;
		do {
			s = reader.readLine();
			n++;
		} while(s != null);

		//Update n according to MAX_GRAPH_SIZE:
		orig_n = n;
		if(MAX_GRAPH_SIZE > 0 && n > MAX_GRAPH_SIZE)
			n = MAX_GRAPH_SIZE;

		//Initialize n empty nodes:
		graph.nodes = new Node[n];
		for(int i = 0; i < n; i++)
		{
			graph.nodes[i] = new Node(i);
			graph.nodes[i].index = i;
		}

		//Go back to the beginning of the file:
		reader.close();
		reader = new BufferedReader( new FileReader(file_path) );

		for(int curr_node_index = 0; curr_node_index < n; curr_node_index++)
		{
			s = reader.readLine();
			if(s == null)		//end of stream
			{
				if(curr_node_index != orig_n)
					throw new Exception("Invalid number of lines");
				break;
			}

			StringTokenizer tok = new StringTokenizer(s, " \t");
			int temp_index = Integer.parseInt( tok.nextToken() );
			if(temp_index != graph.nodes[curr_node_index].index)
				throw new Exception("Invalid line: " + s);
			else
				graph.nodes[curr_node_index]._id = graph.nodes[curr_node_index].index;

			String arrow = tok.nextToken();
			if( !arrow.equals("->") )
				throw new Exception("Invalid line: " + s);

			while(tok.hasMoreTokens())
			{	
				String str = tok.nextToken();
				int i = str.indexOf(':');
				String neigh_str  = str.substring(0, i);
				//String weight_str = str.substring(i);
				int curr_neighbor_index = Integer.parseInt(neigh_str);
				//int weight              = Integer.parseInt(weight_str);

				if(MAX_GRAPH_SIZE > 0 && curr_neighbor_index >= MAX_GRAPH_SIZE)
					continue;

				graph.nodes[curr_node_index].outgoing_neighbors.add(graph.nodes[curr_neighbor_index]);
				graph.nodes[curr_neighbor_index].incoming_neighbors.add(graph.nodes[curr_node_index]);
				graph.nodes[curr_node_index].outdegree++;
				graph.nodes[curr_neighbor_index].indegree++;
			}
		}//	end for

		return graph;
	}

	public static MyGraph create_from_webspam2007_dataset(String file_path) throws Exception
	{
		MyGraph graph = new MyGraph();
		BufferedReader reader = new BufferedReader( new FileReader(file_path) );

		String s = reader.readLine();	//number of nodes
		if (s == null)
			return null;	//end of stream

		int n = Integer.parseInt(s);
		if(MAX_GRAPH_SIZE > 0 && n > MAX_GRAPH_SIZE)
			n = MAX_GRAPH_SIZE;
		graph.nodes = new Node[n];
		for(int i = 0; i < n; i++)
		{
			graph.nodes[i] = new Node(i);
			graph.nodes[i].index = i;
		}

		for(int curr_node_index = 0; curr_node_index < n; curr_node_index++)
		{
			s = reader.readLine();
			if (s == null)
				break;	//end of stream

			StringTokenizer tok = new StringTokenizer(s, " \t");
			while(tok.hasMoreTokens())
			{	
				String str = tok.nextToken();
				int i = str.indexOf(':');
				String neigh_str  = str.substring(0, i);
				//String weight_str = str.substring(i);
				int curr_neighbor_index = Integer.parseInt(neigh_str);
				//int weight              = Integer.parseInt(weight_str);

				if(MAX_GRAPH_SIZE > 0 && curr_neighbor_index >= MAX_GRAPH_SIZE)
					continue;

				graph.nodes[curr_node_index].outgoing_neighbors.add(graph.nodes[curr_neighbor_index]);
				graph.nodes[curr_neighbor_index].incoming_neighbors.add(graph.nodes[curr_node_index]);
				graph.nodes[curr_node_index].outdegree++;
				graph.nodes[curr_neighbor_index].indegree++;
			}
		}//	end for

		return graph;
	}

	public static MyGraph create_from_links_file(String links_file_path) throws Exception
	{
		MyGraph graph = new MyGraph();
		BufferedReader reader = new BufferedReader( new FileReader(links_file_path) );

		TreeMap <Integer, Node> id2node_map = new TreeMap<Integer, Node>();
        while (true)
        {
	        String s = reader.readLine();
	        if (s == null)
	        	break;	//end of stream

	        StringTokenizer tokenizer = new StringTokenizer(s, " \t");	
	        String str1 = tokenizer.nextToken();
	        String str2 = tokenizer.nextToken();
	        if( str2.equals("->") )	//there may or may not be a "->" between the two nodes
				str2 = tokenizer.nextToken();

			//expect to have no more tokens
			if(tokenizer.hasMoreTokens())
				throw new Exception("Invalid line: " + s);

	        int intstr1 = Integer.parseInt(str1);
	        int intstr2 = Integer.parseInt(str2);

			if(!ALLOW_SELF_LOOPS && intstr1 == intstr2)
				continue;

			if(MAX_GRAPH_SIZE > 0)
			{
				int n_to_create = (id2node_map.containsKey(intstr1) ? 0 : 1);
				n_to_create    += (id2node_map.containsKey(intstr2) ? 0 : 1);
		        if(n_to_create > 0 && id2node_map.size() + n_to_create > MAX_GRAPH_SIZE)
		        	continue;
			}

	        Node node1 = id2node_map.get(intstr1);
	        if(node1 == null)
	        {
		        node1 = new Node(intstr1);
		        id2node_map.put(intstr1, node1);
	        }

	        Node node2 = id2node_map.get(intstr2);
	        if(node2 == null)
	        {
		        node2 = new Node(intstr2);
		        id2node_map.put(intstr2, node2);
	        }

			//add node1 to incoming neighbors of node2 if not duplicate edge
			boolean flag1 = false;
			for(Iterator<Node> iter = node2.incoming_neighbors.iterator(); iter.hasNext(); )
				if(iter.next()._id == node1._id)
					{ flag1 = true; break; }
			boolean flag2 = false;
			for(Iterator<Node> iter = node1.outgoing_neighbors.iterator(); iter.hasNext(); )
				if(iter.next()._id == node2._id)
					{ flag2 = true; break; }
			if(flag1 != flag2)
				throw new Exception("!!!!");
			if(!flag1 && !flag2)
			{
		        node2.incoming_neighbors.add(node1);
		        node1.outgoing_neighbors.add(node2);
		        //System.out.println("adding " + node1.index + " to " + node2.index);
		        node1.outdegree++;
		        node2.indegree++;
			}
			else
				;//System.out.println("Ignored duplicate edge: " + node1._id + " -> " + node2._id);
        }

		/* Because node IDs may not be starting from 0 and continuously increasing, we
		 * consider a mapping between node IDs and node indexes, where these indexes
		 * constitute a continuous range [0, 1, ..., n-1]. */

		int n = id2node_map.size();
		Node[] index2node_array = new Node[n];
		int index = 0;

		//Assign an index to each node, and keep track of the mapping between node indexes and node IDs
		for( Iterator<Integer> iter = id2node_map.keySet().iterator(); iter.hasNext(); )
		{
			Node node = id2node_map.get( iter.next() );
			index2node_array[index] = node;
			node.index = index;
			index++;
		}

		graph.nodes = index2node_array;
		return graph;
	}

	public void test_analyze_degrees() throws Exception
	{
		int n = this.nodes.length;
		int BIN_SIZE = 100;
		long sum_outdegree = 0, sum_indegree = 0;
		int[] outdegree = new int[n / BIN_SIZE + 1];
		int[] indegree = new int[n / BIN_SIZE + 1];

		for(int i = 0; i < outdegree.length; i++)
			outdegree[i] = indegree[i] = 0;

		for(int i = 0; i < n; i++)
		{
			Node node = this.nodes[i];
			outdegree[node.outdegree / BIN_SIZE]++;
			indegree[node.indegree / BIN_SIZE]++;
			sum_outdegree += node.outdegree;
			sum_indegree += node.indegree;
			if( node.indegree != node.incoming_neighbors.size() ||
				node.outdegree != node.outgoing_neighbors.size() )
				throw new Exception("!!!!");
		}

		System.out.println("Out-degrees:");
		for(int i = 0; i < outdegree.length; i++)
		{
			if(outdegree[i] == 0)
				continue;
			System.out.print(i * BIN_SIZE + " ... " + ((i + 1) * BIN_SIZE - 1) + ": ");
			System.out.println(outdegree[i]);
		}
		System.out.println();

		System.out.println("In-degrees:");
		for(int i = 0; i < outdegree.length; i++)
		{
			if(indegree[i] == 0)
				continue;
			System.out.print(i * BIN_SIZE + " ... " + ((i + 1) * BIN_SIZE - 1) + ": ");
			System.out.println(indegree[i]);
		}
		System.out.println();

		System.out.println("Average out-degree: " + sum_outdegree / n);
		System.out.println("Average in-degree: " + sum_indegree / n);
	}

	public int get_n_nodes()
	{
		return nodes.length;
	}

	public double[] get_pagerank_array(double d) throws Exception
	{
		return PageRank.get_pagerank_array(this, d);
	}

	public int get_outdegree(int node_index)
	{
		return nodes[node_index].outgoing_neighbors.size();
	}

	public int get_indegree(int node_index)
	{
		return nodes[node_index].incoming_neighbors.size();
	}

	private int[] __get_neighs_array(LinkedList<Node> neighs)
	{
		int[] res = new int[neighs.size()];
		int i = 0;
		for(Iterator<Node> iter = neighs.iterator(); iter.hasNext(); )
			res[i++] = iter.next().index;
		return res;
	}

	public int[] get_outgoing_neighbors_array(int node_index)
	{
		return __get_neighs_array( nodes[node_index].outgoing_neighbors );
	}

	public int[] get_incoming_neighbors_array(int node_index)
	{
		return __get_neighs_array( nodes[node_index].incoming_neighbors );
	}

	public LinkedList<Integer> get_outgoing_neighbors_list(int node_index) throws Exception
	{
		throw new Exception("not implemented ..");
	}

	public LinkedList<Integer> get_incoming_neighbors_list(int node_index) throws Exception
	{
		throw new Exception("not implemented ..");
	}
}
