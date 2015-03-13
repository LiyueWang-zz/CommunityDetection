
import java.util.LinkedList;

public abstract class AbstractGraph
{
	public boolean has_aux_node = false;
	public abstract double[] get_pagerank_array(double d) throws Exception;
	public abstract int get_n_nodes();
	public abstract int get_outdegree(int node_index);
	public abstract int get_indegree(int node_index);
	public abstract int[] get_outgoing_neighbors_array(int node_index);
	public abstract int[] get_incoming_neighbors_array(int node_index);
	public abstract LinkedList<Integer> get_outgoing_neighbors_list(int node_index) throws Exception;
	public abstract LinkedList<Integer> get_incoming_neighbors_list(int node_index) throws Exception;
}
