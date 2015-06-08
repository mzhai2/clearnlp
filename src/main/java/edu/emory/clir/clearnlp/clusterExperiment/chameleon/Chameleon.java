package edu.emory.clir.clearnlp.clusterExperiment.chameleon;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;

import edu.emory.clir.clearnlp.clusterExperiment.chameleon.graph.Edge;
import edu.emory.clir.clearnlp.clusterExperiment.chameleon.graph.KNNGraph;
import edu.emory.clir.clearnlp.clusterExperiment.chameleon.graph.Subgraph;
import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.collection.set.IntHashSet;
import edu.emory.clir.clearnlp.util.IOUtils;


public class Chameleon {

	private IntObjectHashMap<double[]> vectors;
	private KNNGraph sparseGraph;
	private ObjectArrayList<Subgraph> init_clusters;
	private ObjectArrayList<Subgraph> result_clusters;
	private boolean[] visited;
	private double min_size;
	private int alpha;
	private double threshold;



	public Chameleon(String vector_file, int K, double min_size,int alpha, double threshold ) throws ClassNotFoundException, IOException{
		ObjectInput oi = IOUtils.createObjectXZBufferedInputStream(vector_file);
		this.vectors = (IntObjectHashMap<double[]>) oi.readObject();
		sparseGraph = new KNNGraph(this.vectors,K);
		visited = new boolean[vectors.getMaxKey()];
		this.min_size = min_size*vectors.size();
		this.alpha = alpha;
		this.threshold = (alpha==1) ? threshold : 0.0;
	}




	public void createSubClusters(){
		Subgraph tempGraph;
		init_clusters = new ObjectArrayList<>();
		int currentID = 0;
		for(int i = 0; i<vectors.getMaxKey(); i++){
			if(visited[i]) continue;
			tempGraph = new Subgraph(currentID++);
			recursiveDfsSearch(tempGraph, i, -1);
			if(tempGraph.getSize()>min_size){
				init_clusters.add(tempGraph);
			}
		}
	}


	public void recursiveDfsSearch(Subgraph cluster, int index, int parentIndex){

		if(visited[index]) return;
		visited[index] = true;
		for(Edge e : sparseGraph.getGraph().getIncomingEdges(index)){
			if(e.getSource()!= parentIndex){
				if(e.getSource()==index) System.out.println("Source = Target Check Graph");
				cluster.addEdge(e);
				recursiveDfsSearch(cluster, e.getTarget(), index);
			}
		}

	}


	public void mergeSubCluster(){
		Subgraph cluster = null;
		result_clusters = new ObjectArrayList<Subgraph>();
		while (init_clusters.size() > 1) {
			cluster = init_clusters.get(0);
			combineAndRemove(cluster, init_clusters);
		}
	}


	public void printInitCluster(){
		StringBuilder sb = new StringBuilder();
		PrintStream ps = IOUtils.createBufferedPrintStream("InitClusters.txt");
		for(ObjectCursor<Subgraph> cluster : init_clusters){
			sb.setLength(0);
			for(ObjectCursor<Edge> e : cluster.value.getEdges()){
				sb.append(e.value.getSource());
				sb.append(" ");
				sb.append(e.value.getTarget());
				sb.append(" ");
			}
			ps.println(sb.toString());
		}
		ps.flush();
		ps.close();
	}



	private ObjectArrayList<Subgraph> combineAndRemove(Subgraph cluster, ObjectArrayList<Subgraph> clusterList) {
		ObjectArrayList<Subgraph> remainingClusters;
		double metric = 0;
		double maxMetric = -Integer.MAX_VALUE;
		Subgraph temp_cluster1 = null;
		Subgraph temp_cluster2 = null;

		for(ObjectCursor<Subgraph> otherCluster: clusterList){
			if(cluster.getID()==otherCluster.value.getID()) continue;
			metric = calculateMetricFunction(cluster, otherCluster.value, alpha);
			if (metric > maxMetric) {
				maxMetric = metric;
				temp_cluster1 = cluster;
				temp_cluster2 = otherCluster.value;
			}
		}
		if (maxMetric > threshold) {
			removeCluster(temp_cluster2,clusterList);
			connectClusterToCluster(temp_cluster1, temp_cluster2);
			temp_cluster1.addAll(temp_cluster2.getEdges());
			remainingClusters = combineAndRemove(temp_cluster1, clusterList);
		} else {
			removeCluster(cluster, clusterList);;
			remainingClusters = clusterList;
			result_clusters.add(cluster);
		}
		return remainingClusters;
	}




	private void removeCluster(Subgraph cluster, ObjectArrayList<Subgraph> clusterList) {
		int index = -1;
		for(ObjectCursor<Subgraph> otherCluster: clusterList){
			if(otherCluster.value.getID()==cluster.getID()) index = otherCluster.index;
		}
		if(index == -1) System.out.println("Cluster not found in cluster list");
		else clusterList.remove(index);
	}



	//Find closest pair of edges and form a new edge
	private void connectClusterToCluster(Subgraph c1, Subgraph c2) {
		List<Edge> closestEdges = calculateNearestEdges(c1,c2,2);
		c1.addAll(closestEdges);
	}




	private List<Edge> calculateNearestEdges(Subgraph c1, Subgraph c2, int k) {
		List<Edge> closestEdges = new ArrayList<>(k);
		for(IntCursor i : c1.getVertices()){
			for(IntCursor j : c2.getVertices()){
				if(i == j) System.out.println("Graph share a vertex. Check Partitioning");
				add(closestEdges,i.value, j.value,k);
			}
		}
		return closestEdges;
	}




	private void add(List<Edge> closestEdges, int source, int target, int k) {
		Comparator<Edge> c = new Comparator<Edge>(){
			public int compare(Edge p1, Edge p2){
				return new Double(p1.getWeight()).compareTo(p2.getWeight());
			}
		};
		Edge temp = new Edge(source, target, sparseGraph.getCosineSimilarity(vectors.get(source), vectors.get(target)));
		int i = Collections.binarySearch(closestEdges,temp,c);
		if (i < 0) i = -(i + 1);
		if (i < k)
		{
			closestEdges.add(i, temp);
			if (closestEdges.size() > k) closestEdges.remove(closestEdges.size()-1);
		}
	}




	private double calculateMetricFunction(Subgraph cluster, Subgraph otherCluster, int alpha) {
		double metricValue = 0;
		double RI = 0;
		double RC = 0;

		RI = calculateRI(cluster,otherCluster);
		RC = calculateRC(cluster,otherCluster);
		metricValue = RI * Math.pow(RC, alpha);

		return metricValue;
	}




	private double calculateRC(Subgraph cluster, Subgraph otherCluster) {
		double RC = 0;
		double EC1 = 0;
		double EC2 = 0;
		double EC1To2 = 0;
		int pNum1 = cluster.getSize();
		int pNum2 = cluster.getSize();

		EC1 = cluster.calculateEC();
		EC2 = otherCluster.calculateEC();
		EC1To2 = calculateEC(cluster, otherCluster);

		RC = EC1To2 * (pNum1 + pNum2) / (pNum2 * EC1 + pNum1 * EC2);

		return RC;
	}


	private double calculateRI(Subgraph cluster, Subgraph otherCluster) {
		double RI = 0;
		double EC1 = 0;
		double EC2 = 0;
		double EC1To2 = 0;

		EC1 = cluster.calculateEC();
		EC2 = otherCluster.calculateEC();
		EC1To2 = calculateEC(cluster, otherCluster);
		RI = 2 * EC1To2 / (EC1 + EC2);
		return RI;
	}

	public void printFinalClusters(){
		StringBuilder sb = new StringBuilder();
		PrintStream ps = IOUtils.createBufferedPrintStream("Chameleon.txt");
		for(ObjectCursor<Subgraph> cluster : result_clusters){
			sb.setLength(0);
			for(ObjectCursor<Edge> e : cluster.value.getEdges()){
				sb.append(e.value.getSource()).append(" ").append(e.value.getTarget()).append(" ");
			}
			ps.println(sb.toString());
		}
		ps.flush();
	}

	public void printKNNGraph(){
		sparseGraph.printGraph();
	}
	private double calculateEC(Subgraph cluster, Subgraph otherCluster) {
		double resultEC = 0;
		List<Edge> closestEdges = calculateNearestEdges(cluster, otherCluster, 2);
		for(Edge e : closestEdges){
			resultEC += e.getWeight();
		}
		return resultEC;
	}

	public void printWordClusters(String WordMap) throws Exception {
		PrintStream ps = IOUtils.createBufferedPrintStream("WordClusters.txt");
		ObjectInput oi = IOUtils.createObjectXZBufferedInputStream(WordMap);
		IntObjectHashMap<String> word_map = (IntObjectHashMap<String>) oi.readObject();
		StringBuilder sb = new StringBuilder();
		IntHashSet vertices;
		int source;
		int target;
		for(ObjectCursor<Subgraph> cluster : result_clusters){
			sb.setLength(0);
			vertices = new IntHashSet();
			for(ObjectCursor<Edge> e : cluster.value.getEdges()){
				source = e.value.getSource();
				target = e.value.getTarget();
				if(!vertices.contains(source)){
					sb.append(word_map.get(source));
					sb.append(" ");
					vertices.add(source);
				}
				if(!vertices.contains(target)){
					sb.append(word_map.get(target));
					sb.append(" ");
					vertices.add(target);
				}
			}
			ps.println(sb.toString());
		}
		ps.flush();
	} 




	//
	//	public void printMetisGraph(){
	//		PrintStream ps = IOUtils.createBufferedPrintStream("HmetisGraph.txt");
	//		StringBuilder sb = new StringBuilder();
	//		sb.append(vectors.size()-1).append(" ").append(sparseGraph.getEdgeCount()).append(" ").append("001");
	//		ps.println(sb.toString());
	//		List<Edge> edges;
	//		for(int i = 1; i<vectors.size()-1; i++){
	//			edges = sparseGraph.getGraph().getIncomingEdges(i);
	//			sb.setLength(0);
	//			sb.append("  ");
	//		for(int j = 0; j<edges.size(); j++){
	//			if(j!=0) sb.append(" ");
	//			sb.append(edges.get(j).getSource());
	//			sb.append(" ");
	//			sb.append(String.format("%f", edges.get(j).getWeight()));
	//		}
	//		ps.println(sb.toString());
	//		}
	//		
	//		ps.flush();
	//		ps.close();
	//		}
	//
	//
	//
	//
	//	public void readMetisSubGraph(String initialPartition){
	//		BufferedReader br = IOUtils.createBufferedReader(initialPartition);
	//		
	//	}








}
