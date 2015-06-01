package edu.emory.clir.clearnlp.clusterExperiment.chameleon.graph;

import com.carrotsearch.hppc.ObjectArrayList;

import edu.emory.clir.clearnlp.collection.set.IntHashSet;

public class Subgraph {
	
	private ObjectArrayList<Edge> l_edges;
	private IntHashSet vertices;

	
	public Subgraph(){
		l_edges = new ObjectArrayList<>();
		vertices = new IntHashSet();
	}
	
	public void addEdge(Edge edge){
		l_edges.add(edge);
		vertices.add(edge.getSource());
		vertices.add(edge.getTarget());
	}
	
	public ObjectArrayList<Edge> getEdges(){
		return l_edges;
	}
	
	public IntHashSet getVertices(){
		return vertices;
	}
	
	public boolean contains(int vertex){
		return vertices.contains(vertex);
	}





}
