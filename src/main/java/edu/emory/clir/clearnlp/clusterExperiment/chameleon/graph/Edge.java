package edu.emory.clir.clearnlp.clusterExperiment.chameleon.graph;

public class Edge {

	private int u;
	private int v;
	private double w;
//	private boolean isVisited;
	
	
	public Edge(int u, int v, double w){
		setSource(u);
		setTarget(v);
		setWeight(w);
//		isVisited = false;
	}

	
	public Edge(Edge o){
		setSource(o.getSource());
		setTarget(o.getTarget());
		setWeight(o.getWeight());
	}

	private void setWeight(double w) {
		this.w = w;
		
	}


	private void setTarget(int v) {
		this.v = v;
	}


	private void setSource(int u) {
		this.u = u;
	}
	
	
	public int getSource(){
		return this.u;
	}
	
	public int getTarget(){
		return this.v;
	}
	
	public double getWeight(){
		return this.w;
	}
	
	
}
