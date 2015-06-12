package edu.emory.clir.clearnlp.clusterExperiment.kmeans;

import edu.emory.clir.clearnlp.collection.set.IntHashSet;

public class Cluster {

	private IntHashSet point_set;
	
	public Cluster(){
		point_set = new IntHashSet();
	}
	
	public void addPoint(int point){
		point_set.add(point);
	}
	
	
	public IntHashSet getPointSet(){
		return point_set;
	}
	
	public int size(){
		return point_set.size();
	}
	
	
	public void merge(Cluster cluster){
		point_set.addAll(cluster.getPointSet());
	}
	
	
	
	

}
