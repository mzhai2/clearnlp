package edu.emory.clir.clearnlp.clusterExperiment.gaussian;

import java.util.List;

import org.apache.commons.math3.linear.RealMatrix;

import edu.emory.clir.clearnlp.clusterExperiment.Vector.DenseVector;
import edu.emory.clir.clearnlp.collection.map.IntDoubleHashMap;
import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;

public class EStep2 {
	
	private IntObjectHashMap<double[]> mean;
	private IntObjectHashMap<RealMatrix> sigma;
	private IntDoubleHashMap prior;
	private int dimension;
	private int k;


	public EStep2(){
		mean = new IntObjectHashMap<double[]>();
		sigma = new IntObjectHashMap<RealMatrix>();
		prior = new IntDoubleHashMap();
	}


	
	private void calculatePrior(List<List<DenseVector>> clusters) {
		int i,t_instances = 0,c_instances;
		for(i = 0; i<k; i++){
			t_instances+= clusters.get(i).size();
		}
		for(i = 0; i<k; i++){
			prior.put(i, clusters.get(i).size()/t_instances);
		}
	}

}
