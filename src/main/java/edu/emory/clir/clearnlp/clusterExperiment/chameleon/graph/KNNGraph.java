package edu.emory.clir.clearnlp.clusterExperiment.chameleon.graph;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.carrotsearch.hppc.cursors.ObjectCursor;

import edu.emory.clir.clearnlp.clusterExperiment.Vector.DenseVector;
import edu.emory.clir.clearnlp.clusterExperiment.chameleon.IntDoublePair;
import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.util.IOUtils;

public class KNNGraph {

	private Graph sparseGraph;
	private int K;
	private int e_count;
	
	public KNNGraph(IntObjectHashMap<double[]> vectors, int K){
		sparseGraph = new Graph(vectors.values().size());
		this.K = K;
		e_count = 0;
		initSparseGraph(vectors);
	}
	
	public void printGraph(){
		StringBuilder sb = new StringBuilder();
		PrintStream ps = IOUtils.createBufferedPrintStream("KNNGraph.txt");
		for(Edge e : sparseGraph.getAllEdges()){
			ps.println(e.getSource() +"  -> " +e.getTarget() +"  " +e.getWeight());
		}
		ps.flush();
	}
	private void initSparseGraph(IntObjectHashMap<double[]> vectors) {
		List<IntDoublePair> neighbors;
		int key_size = vectors.getMaxKey();
		
		for(int i = 1; i<key_size; i++){
			neighbors = new ArrayList<IntDoublePair>();
			for(int j = i+1; j<key_size; j++){
				add(neighbors,getCosineSimilarity(vectors.get(i), vectors.get(j)),j);
			}
			addToSparseGraph(i,neighbors);
		}
	}


	public double getCosineSimilarity(double[] d1, double[] d2){
		double similarity = 0;
		double numerator = 0;
		for(int i = 0; i<d1.length; i++){
			numerator+=d1[i]*d2[i];
		}
		double denominator = getEuclideanNorm(d1)*getEuclideanNorm(d2);
		similarity = numerator/denominator;
		return 1-similarity;
		
		
	}
	
	private double getEuclideanNorm(double[] vector){
		double sum = 0;
        for (double value : vector) {
            sum += value*value;
        }
        return Math.sqrt(sum);
    }

	private void addToSparseGraph(int i, List<IntDoublePair> neighbors) {
		for(IntDoublePair pair : neighbors){
			sparseGraph.setUndirectedEdge(i, pair.getIndex(), pair.getValue());
			e_count++;
		}
	}


	
	private void add(List<IntDoublePair> kneighbors, double distance, int neighbor) {
		Comparator<IntDoublePair> c = new Comparator<IntDoublePair>(){
			public int compare(IntDoublePair p1, IntDoublePair p2){
				return new Double(p1.getValue()).compareTo(p2.getValue());
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
		return e_count;
	}
}

