import java.util.*;

public class Node
{
	public int outdegree = 0, indegree = 0;
	public LinkedList<Node> incoming_neighbors; //Neighbors that having links to me!
	public LinkedList<Node> outgoing_neighbors;
	public int _id, index;

	boolean visited = false;
	double pagerank_score;

	static final int LABEL_SPAM      = 1;
	static final int LABEL_NOTSPAM   = 2;
	static final int LABEL_UNDECIDED = 3;
	static final int LABEL_UNLABELED = 4;
	int spam_label = LABEL_UNLABELED;

	double path_contribution;	//Used for finding page farms
	//boolean disabled = false;
	boolean is_aux_for_danglings = false;

	int distance_from_targetpage = 0;
	boolean is_in_current_pagefarm = false;

	public Node(int id)
	{
		incoming_neighbors = new LinkedList<Node>();
		outgoing_neighbors = new LinkedList<Node>();
		this._id = id;
		this.index = -1;
		this.pagerank_score = 0;
		this.path_contribution = 0;
	}

	public static void add_selfloop_to_dangling_node(Node node) throws Exception
	{
		if(node.outdegree > 0)
			throw new Exception("!!!!");

		node.outgoing_neighbors.add(node);
		node.outdegree = 1;

		//Add to incoming neighbor list in the appropriate location
		Iterator<Node> iter = node.incoming_neighbors.iterator();
		int pos = 0;
		while(iter.hasNext())
		{
			Node neigh = iter.next();
			if(neigh.index > node.index)
				break;
			pos++;
		}
		node.incoming_neighbors.add(pos, node);
		node.indegree++;
	}
}
