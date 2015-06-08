package edu.emory.clir.clearnlp.clusterExperiment.chameleon.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import edu.emory.clir.clearnlp.util.DSUtils;

public class Graph {

	private List<Edge>[] edges;

	@SuppressWarnings("unchecked")
	public Graph(int size){
		edges = (List<Edge>[]) DSUtils.createEmptyListArray(size);
	}


	public List<Edge> getAllEdges(){
		List<Edge> allEdges = new ArrayList<Edge>();
		for(int i = 0; i<size(); i++){
			allEdges.addAll(edges[i]);
		}
		return allEdges;
	}
	
	public boolean isEmpty(){
		for (int i=0; i<size(); i++)
		{
			if (!getIncomingEdges(i).isEmpty())
				return false;
		}
		return true;
	}
	@SuppressWarnings("unchecked")
	public Deque<Edge>[] getOutgoingEdges()
	{
		Deque<Edge>[] edges = (Deque<Edge>[]) createEmptyDequeArray(size());
		for (int target=0; target<size(); target++)
		{
			for (Edge edge : getIncomingEdges(target))
				edges[edge.getSource()].add(edge);
		}
		return edges;
	}
	
	public void setDirectedEdge(int source, int target, double weight)
	{
		List<Edge> edges = getIncomingEdges(target);
		edges.add(new Edge(source, target, weight));
	}
	public void setUndirectedEdge(int source, int target, double weight)
	{
		setDirectedEdge(source, target, weight);
		setDirectedEdge(target, source, weight);
	}
	
	static public <T>Deque<?>[] createEmptyDequeArray(int size)
	{
		Deque<?>[] deque = new ArrayDeque<?>[size];
		for (int i=0; i<size; i++)
			deque[i] = new ArrayDeque<T>();
		return deque;
	}


	public Deque<Integer> getVerticesWithNoIncomingEdges()
	{
		Deque<Integer> deque = new ArrayDeque<>();
		int i, size = size();
		for (i=0; i<size; i++)
		{
			if (getIncomingEdges(i).isEmpty())
				deque.add(i);
		}
		return deque;
	}
	
	public List<Edge> getIncomingEdges(int i) {
		return edges[i];
	}


	public int size() {
		return edges.length;
	}

	public String toString()
	{
		StringBuilder build = new StringBuilder();
		for (int i=0; i<edges.length; i++)
		{
			build.append(i);
			build.append(" <- ");
			build.append(edges[i].toString());
			build.append("\n");
		}
		return build.toString();
	}


}
