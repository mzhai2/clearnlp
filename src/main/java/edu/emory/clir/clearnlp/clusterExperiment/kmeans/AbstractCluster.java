package edu.emory.clir.clearnlp.clusterExperiment.kmeans;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCluster {
	
	protected List<double[]> s_points;
	
	public AbstractCluster(){
		s_points = new ArrayList<double[]>();
	}

	public void addPoint(double[] point){
		s_points.add(point);
	}
	
	public void setPoints(List<double[]> points){
		s_points = points;
	}
	
	
	public double[] getPoint(int index){
		return s_points.get(index);
	}
	
	
	public List<double[]> getPoints(){
		return s_points;
	}
	
	
	public abstract List<Cluster> cluster();
	

}
