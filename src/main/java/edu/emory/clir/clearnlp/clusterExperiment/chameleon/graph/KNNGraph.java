package edu.emory.clir.clearnlp.clusterExperiment.chameleon.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.emory.clir.clearnlp.clusterExperiment.Vector.DenseVector;
import edu.emory.clir.clearnlp.clusterExperiment.chameleon.IntDoublePair;

public class KNNGraph {

	private Graph sparseGraph;
	private int K;
	
	public KNNGraph(DenseVector[] tupleList, int K){
		sparseGraph = new Graph(tupleList.length);
		this.K = K;
		initSparseGraph(tupleList);
	}
	
	private void initSparseGraph(DenseVector[] tupleList) {
		List<IntDoublePair> neighbors;
		for(int i = 0; i<tupleList.length; i++){
			neighbors = new ArrayList<IntDoublePair>();
			for(int j = 1; j<tupleList.length; j++){
				add(neighbors,getCosineSimilarity(tupleList[i],tupleList[j]),j);
			}
			addToSparseGraph(i,neighbors);
		}
	}


	private double getCosineSimilarity(DenseVector d1, DenseVector d2) {
		double similarity = 0;
		double numerator = 0;
		float[] f1 = d1.getFloatArray();
		float[] f2 = d2.getFloatArray();
		for (int i=0; i<f1.length; i++)
			numerator+=f1[i]*f2[i];
		double denominator = d1.euclideanNorm()* d2.euclideanNorm();
		similarity = numerator/denominator;
		return 1-similarity;
	}
	
	
	private void addToSparseGraph(int i, List<IntDoublePair> neighbors) {
		for(IntDoublePair pair : neighbors){
			sparseGraph.setUndirectedEdge(i, pair.getIndex(), pair.getValue());
		}
	}


	
	private void add(List<IntDoublePair> kneighbors, double distance, int neighbor) {
		Comparator<IntDoublePair> c = new Comparator<IntDoublePair>(){
			public int compare(IntDoublePair p1, IntDoublePair p2){
				return new Double(p2.getValue()).compareTo(p1.getValue());
			}
		};
		IntDoublePair temp = new IntDoublePair(neighbor,distance);
		int i = Collections.binarySearch(kneighbors,temp,c);
		if (i < 0) i = -(i + 1);
		if (i < K)
		{
			kneighbors.add(i, temp);
			if (kneighbors.size() > K) kneighbors.remove(kneighbors.size()-1);
		}
	}
	
	public Graph getGraph(){
		return this.sparseGraph;
	}

	public int getEdgeCount() {
		return 0;
	}
}
