package edu.emory.clir.clearnlp.clusterExperiment.kmeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.carrotsearch.hppc.cursors.IntCursor;

import edu.emory.clir.clearnlp.collection.pair.DoubleIntPair;
import edu.emory.clir.clearnlp.collection.pair.ObjectDoublePair;
import edu.emory.clir.clearnlp.collection.set.IntHashSet;
import edu.emory.clir.clearnlp.util.BinUtils;
import edu.emory.clir.clearnlp.util.MathUtils;

public class KmeansClustering extends AbstractCluster {

	public boolean user_input;
	final protected int K;
	final protected int NUM_THREADS;
	final protected double RSS_THRESHOLD;
	final protected int DIMENSIONS;
	volatile double[] D2;
	private String distanceMeasure;
	private String initMethod;


	public KmeansClustering(int k, double rssThreshold, int numThreads, int dimensions, String distance, String init)
	{
		super();
		K = k;
		RSS_THRESHOLD  = rssThreshold;
		NUM_THREADS    = numThreads;
		DIMENSIONS = dimensions;
		this.distanceMeasure = distance;
		this.initMethod = init;
		
	}



	@Override
	public List<Cluster> cluster() {
		ObjectDoublePair<List<Cluster>> current = new ObjectDoublePair<>(null, 0);
		List<double[]> centroids = null;
		if(initMethod.equalsIgnoreCase("RandomCluster")) centroids = initRandomClusters();
		if(initMethod.equalsIgnoreCase("RandomCentroid")) centroids = initRandomCentroids();
		if(initMethod.equalsIgnoreCase("Regular")) centroids = initialization();
		if(centroids==null) BinUtils.LOG.info("Init Method is wrong. Please Enter: RandomCluster or RandomCentroid or Regular");
		double previousRSS;
		List<Double> oscillation = new ArrayList<>();
		for(int i = 0; i<Integer.MAX_VALUE; i++){
			BinUtils.LOG.info(String.format("Iteration: %d\n", i));
			previousRSS = current.d;
			current = maximization(centroids);
			centroids = expectation(current.o);
			BinUtils.LOG.info("Current RSS: " +current.d +" PreviousRSS " +previousRSS + " Difference is " +Math.abs(current.d - previousRSS));
			oscillation.add(current.d-previousRSS);
			if(detectOscillation(oscillation)) break;
			if(current.d - previousRSS < RSS_THRESHOLD) break;
		}

		return current.o;
	}

	


	private boolean detectOscillation(List<Double> oscillation) {
		if(oscillation.size()<6) return false;
		if(oscillation.size()>6){
			for(int i = 0; i<oscillation.size()-6; i++){
				oscillation.remove(i);
			}
		}
		int i= 0;
		int prev_pos = -1;
		int prev_neg = -1;
		int osc_count = 0;
		while(i <oscillation.size()){
			if(Math.abs(prev_neg-prev_pos)==1) osc_count++;
			if(Math.signum(oscillation.get(i))==-1) prev_neg = i;
			if(Math.signum(oscillation.get(i))==1) prev_pos = i;
			i++;
		}
		System.out.println(osc_count);
		if(osc_count==5) return true;
		oscillation.remove(0);
		return false;
	}



	private List<double[]> initRandomCentroids(){
		List<double[]> randomCentroids = new ArrayList<>();
		Random rand = new Random(1);
		int N = s_points.size();
		while(randomCentroids.size()<K){
			randomCentroids.add(s_points.get(rand.nextInt(N)));
		}
		
		return randomCentroids;
	}
	
	private List<double[]> initRandomClusters(){
		List<double[]> randomCentroids = new ArrayList<>();
		Map<Integer,Integer> counts = new HashMap();
		int i,curr_count,r_index,N = s_points.size();
		Random rand = new Random(1);
		for(i = 0; i<K; i++){
			randomCentroids.add(new double[DIMENSIONS]);
		}
	
		for(i = 0; i<N; i++){
			r_index = rand.nextInt(K);
			if(counts.get(r_index)==null) counts.put(r_index, 1);
			else {
				curr_count = counts.get(r_index); counts.put(r_index,++curr_count);
			}
			add(randomCentroids.get(r_index),s_points.get(i));
		}
		for(i = 0; i<K; i++){
			divide(randomCentroids.get(i),counts.get(i));
		}
		
		return randomCentroids;
	}

	private List<double[]> initialization() {
		IntHashSet centroidSet = new IntHashSet();
		int newCentroid, i, N = s_points.size();
		Random rand = new Random(1);
		double[] cum;
		double sum;

		BinUtils.LOG.info("Initialization");

		D2 = new double[N];
		Arrays.fill(D2, Double.MAX_VALUE);
		centroidSet.add(newCentroid = rand.nextInt(N));
		D2[newCentroid] = 0;

		while(centroidSet.size()<K){
			sum = computeD2(centroidSet,newCentroid);
			cum = cumulativeD2(N,sum);

			for(i = 0; i<N; i++){
				if(!centroidSet.contains(i) && rand.nextDouble()<cum[i]){
					centroidSet.add(newCentroid = i);
					D2[newCentroid] = 0;
					break;
				}
			}
		}

		D2 = null;
		BinUtils.LOG.info(centroidSet.toString()+"\n");
		List<double[]> centroids = new ArrayList<double[]>();
		for(IntCursor c : centroidSet) centroids.add(s_points.get(c.value));
		return centroids;
	}


	private double computeD2(IntHashSet centroidSet, int newCentroidID) {
		ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
		List<Future<Double>> list = new ArrayList<>(NUM_THREADS);
		double[] newCentroid = s_points.get(newCentroidID);
		double centroidNorm = getEuclideanNorm(newCentroid);
		int i, j, N = s_points.size(), GAP = gap();
		Callable<Double> task;

		for(i= 0; i<N; i+=GAP){
			if((j= i + GAP)>N) j = N;
			task = new InitializationTask(centroidSet, newCentroid, centroidNorm, i, j);
			list.add(pool.submit(task));
		}
		double sum = 0;
		try{
			for(Future<Double> f : list) sum+= f.get();
		}catch(Exception e ){ e.printStackTrace();}

		return sum;
	}

	private double[] cumulativeD2(int N, double sum) {
		double[] c = new double[N];
		c[0] = D2[0] /sum;
		for(int i = 1; i<N; i++){
			c[i] = c[i-1] + D2[i]/sum;
		}
		return c;
	}

	//Initial Task

	private class InitializationTask implements Callable<Double>{

		private IntHashSet centroid_set;
		private double[] new_centroid;
		private int begin_index;
		private int end_index;
		private double centroid_norm;

		public InitializationTask(IntHashSet centroidSet, double[] newCentroid, double centroidNorm, int beginIndex, int endIndex) {
			this.centroid_set = centroidSet;
			this.new_centroid = newCentroid;
			this.centroid_norm = centroidNorm;
			this.begin_index = beginIndex;
			this.end_index = endIndex;
		}

		@Override
		public Double call() throws Exception {
			double[] point;
			double sum = 0;
			for(int i = begin_index; i<end_index; i++){
				if(centroid_set.contains(i)) continue;
				point = s_points.get(i);
				if(distanceMeasure.equalsIgnoreCase("cosine")) D2[i] = Math.min(D2[i], 1-cosineSimilarity(new_centroid,centroid_norm, point, getEuclideanNorm(point)));
				if(distanceMeasure.equalsIgnoreCase("euclidean")) D2[i] = Math.min(D2[i], euclideanDistance(new_centroid,centroid_norm, point, getEuclideanNorm(point)));

				sum+=D2[i];
			}
			return sum;
		}


	}

	//Max Task	

	private ObjectDoublePair<List<Cluster>> maximization(List<double[]> centroids) {
		List<Future<ObjectDoublePair<List<Cluster>>>> list = new ArrayList<>();
		ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
		double[] centroidNorms = getEuclideanNorm(centroids);
		Callable<ObjectDoublePair<List<Cluster>>> task;
		int i,j, N = s_points.size(), GAP = gap();

		BinUtils.LOG.info("- Maximization: ");

		for(i = 0; i<N; i+=GAP){
			if((j=i+GAP)>N) j = N;
			task = new MaximizationTask(centroids,centroidNorms,i,j);
			list.add(pool.submit(task));
		}

		List<Cluster> clusters = centroids.stream().map(c -> new Cluster()).collect(Collectors.toCollection(ArrayList::new));
		ObjectDoublePair<List<Cluster>> p;
		double rss = 0;

		try{
			for(Future<ObjectDoublePair<List<Cluster>>> f: list){
				p = f.get();
				rss+= p.d;
				for(i = 0; i<K; i++) clusters.get(i).merge(p.o.get(i));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		BinUtils.LOG.info((String.format("%f\n", rss)));
		return new ObjectDoublePair<List<Cluster>>(clusters, rss);
	}

	private class MaximizationTask implements Callable<ObjectDoublePair<List<Cluster>>>{

		List<double[]> centroid_list;
		private double[] centroid_norm;
		private int begin_index;
		private int end_index;
		
		
		
		public MaximizationTask(List<double[]> centroidList, double[] centroidNorms, int beginIndex, int endIndex) {
			this.centroid_list = centroidList;
			this.centroid_norm = centroidNorms;
			this.begin_index = beginIndex;
			this.end_index = endIndex;
		}



		@Override
		public ObjectDoublePair<List<Cluster>> call() throws Exception {
			List<Cluster> clusters = centroid_list.stream().map(c -> new Cluster()).collect(Collectors.toCollection(ArrayList::new));
			DoubleIntPair max = new DoubleIntPair(0, 0);
			Cluster temp_set;
			double rss = 0;
			
			for(int i = begin_index; i<end_index; i++){
				max = max(centroid_list, centroid_norm, s_points.get(i));
				temp_set = clusters.get(max.i);
				temp_set.addPoint(i);
				rss +=max.d;
			}
			return new ObjectDoublePair<>(clusters,rss);
		}



		private DoubleIntPair max(List<double[]> centroids, double[] centroidNorms, double[] point) {
			DoubleIntPair max = new DoubleIntPair(-10000d, 0);
			double d = 0,pointNorm = getEuclideanNorm(point);
			
			for(int k = centroidNorms.length-1; k>=0; k--){
				if(distanceMeasure.equalsIgnoreCase("euclidean")) d = euclideanDistance(centroids.get(k), centroidNorms[k], point, pointNorm);
				if(distanceMeasure.equalsIgnoreCase("cosine")) d = cosineSimilarity(centroids.get(k), centroidNorms[k], point, pointNorm);

				if(d>max.d) max.set(d, k);
			}
			return max;
		}
		
	}
	
	
	// Expectation Task
	
	private List<double[]> expectation(List<Cluster> clusters) {
		ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
		List<Future<double[]>> list = new ArrayList<>(K);
		Callable<double[]> task;
		BinUtils.LOG.info("- Expectation\n");
		for(int i = 0; i<K; i++){
			task = new ExpectationTask(clusters.get(i));
			list.add(pool.submit(task));
		}
		
		List<double[]> centroids = new ArrayList<>(K);
		try{
			for(Future<double[]> f: list) centroids.add(f.get());
		}catch(Exception e){
			e.printStackTrace();
		}
	
		return centroids;
	}

	private class ExpectationTask implements Callable<double[]>{
		private Cluster cluster;
		public ExpectationTask(Cluster cluster){
			this.cluster = cluster;
		}
		public double[] call(){
			double[] temp;
			int i;
			double[] centroid = new double[DIMENSIONS];
			for(IntCursor set : cluster.getPointSet()){
				temp = s_points.get(set.value);
				for(i = 0; i<temp.length; i++){
					centroid[i] += temp[i];
				}
			}
			for(i = 0; i<centroid.length; i++){
				centroid[i] /= cluster.size();
			}
			return centroid; 
		}
	}
	
	
	
	private void divide(double[] centroid, int size) {
		for(int i = 0; i<centroid.length; i++){
			centroid[i] /= size;
		}
	}
	private void add(double[] centroid, double[] point) {
		for(int i = 0; i<point.length; i++){
			centroid[i] += point[i];
		}
	}
	private double[] getEuclideanNorm(List<double[]> points) {
		return points.stream().mapToDouble(point-> getEuclideanNorm(point)).toArray();
	}


	private double euclideanDistance(double[] centroid, double centroidNorm, double[] points, double euclideanNorm){
		double sum = 0;
		
		for(int i = 0; i<centroid.length; i++){
			sum += MathUtils.sq(centroid[i]-points[i]);
		}
		
		return Math.sqrt(sum);
	}

	private double cosineSimilarity(double[] centroid, double centroidNorm, double[] point, double euclideanNorm) {
		double numerator = 0;
		for(int i = 0; i<centroid.length; i++){
			numerator += centroid[i] * point[i];
		}
		return numerator/(centroidNorm*euclideanNorm);
	}

	private int gap() {
		return (int)Math.ceil(MathUtils.divide(s_points.size(), NUM_THREADS));
	}



	private double getEuclideanNorm(double[] newCentroid) {
		double d = 0;
		for(double value : newCentroid){
			d += MathUtils.sq(value);
		}
		return Math.sqrt(d);
	}






}
