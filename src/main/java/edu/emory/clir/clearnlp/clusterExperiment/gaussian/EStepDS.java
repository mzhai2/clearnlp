package edu.emory.clir.clearnlp.clusterExperiment.gaussian;

import java.io.ObjectInput;
import java.util.ArrayList;
import java.util.List;

import edu.emory.clir.clearnlp.collection.list.IntArrayList;
import edu.emory.clir.clearnlp.collection.map.IntIntHashMap;
import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.util.IOUtils;


public class EStepDS{

	private IntObjectHashMap<IntArrayList> clusters;
	private IntObjectHashMap<double[]> dense_vectors;
	private IntIntHashMap h_structure;
	private int[] cluster_indices;
	public EStepDS(){
		clusters = new IntObjectHashMap<>();
		dense_vectors = new IntObjectHashMap<>();
		h_structure = new IntIntHashMap();
	}

	@SuppressWarnings("unchecked")
	public EStepDS(String clusters, String dense_vectors, String h_structure) throws Exception{
		ObjectInput oi = IOUtils.createObjectXZBufferedInputStream(clusters);
		this.clusters = (IntObjectHashMap<IntArrayList>) oi.readObject();
		oi = IOUtils.createObjectXZBufferedInputStream(dense_vectors);
		this.dense_vectors = (IntObjectHashMap<double[]>) oi.readObject();
		oi = IOUtils.createObjectXZBufferedInputStream(h_structure);
		this.h_structure = (IntIntHashMap) oi.readObject();
		initClusterIndices();
	}
	
	
	
	private void initClusterIndices(){
		List<ObjectIntPair<IntArrayList>> T = clusters.toList();
		cluster_indices = new int[T.size()];
		for(int i = 0; i<T.size(); i++){
			cluster_indices[i] = (T.get(i).i);
		}
	}
	public int[] getClusterIndicies(){
		return cluster_indices;
	}

	public IntArrayList getCluster(int key){
		return clusters.get(key);
	}

	public double[] getVector(int key){
		return dense_vectors.get(key);
	}
	
	public int getH(int key){
		return h_structure.get(key);
	}
	public IntObjectHashMap<IntArrayList> getClusters(){
		return this.clusters;
	}

	public IntObjectHashMap<double[]> getVectors(){
		return this.dense_vectors;
	}

	public IntIntHashMap getHStructure(){
		return this.h_structure;
	}

	public int getInstanceSize(){
		return dense_vectors.values().size();
	}
	public int getK(){
		return clusters.size();
	}

}