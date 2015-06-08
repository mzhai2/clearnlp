package edu.emory.clir.clearnlp.clusterExperiment.chameleon.graph;

import java.util.List;

import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.cursors.ObjectCursor;

import edu.emory.clir.clearnlp.collection.set.IntHashSet;

public class Subgraph {
	
	private ObjectArrayList<Edge> l_edges;
	private double weightedSum = 0;
	private int id;

	
	public Subgraph(int id){
		l_edges = new ObjectArrayList<>();
		this.id = id;
	}
	
	public void setID(int id){
		this.id = id;
	}
	public double calculateEC(){
		for(ObjectCursor<Edge> e: l_edges){
			weightedSum+= e.value.getWeight();
		}
		return weightedSum;
	}
	
	public double getWeightedSum(){
		return weightedSum;
	}
	public void addEdge(Edge edge){
		l_edges.add(edge);
	}
	
	public ObjectArrayList<Edge> getEdges(){
		return l_edges;
	}
	
	public int getID(){
		return this.id;
	}

	public void addAll(ObjectArrayList<Edge> edges) {
		l_edges.addAll(edges);
		
	}

	public void addAll(List<Edge> closestEdges) {
		for(Edge e : closestEdges){
			addEdge(e);
		}
	}

	public IntHashSet getVertices(){
		IntHashSet vertices = new IntHashSet();
		for(ObjectCursor<Edge> e : l_edges){
			vertices.add(e.value.getSource());
			vertices.add(e.value.getTarget());
		}
		return vertices;
	}

	public int getSize() {
		return l_edges.size();
	}
	
	




}
