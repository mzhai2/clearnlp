package edu.emory.clir.clearnlp.clusterExperiment.gaussian;

import java.util.List;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

import com.carrotsearch.hppc.cursors.IntCursor;

import edu.emory.clir.clearnlp.collection.map.IntDoubleHashMap;
import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.collection.set.IntHashSet;

public class EStep {
	
	private IntObjectHashMap<double[]> means;
	private IntObjectHashMap<RealMatrix> sigmas;
	private IntDoubleHashMap priors;
	private int dimension;
	private int k;

	
	
	
	public EStep(){
		means = new IntObjectHashMap<double[]>();
		sigmas = new IntObjectHashMap<>();
		priors = new IntDoubleHashMap();
	}
	
	
	public void process(EStepDS clusters){
		this.k = clusters.getK();
		this.dimension = 300;
		calculateMeans(clusters);
		calculatePriors(clusters);
		calculateSigmas(clusters);
	}


	//Variance of a cluster with a single point??
	private void calculateSigmas(EStepDS clusters) {
		double[][] init_matrix;
		Covariance cv_matrix;
		int size,index;
		int[] indicies = clusters.getClusterIndicies();
		for(int i = 0; i<indicies.length; i++){
			size = clusters.getCluster(indicies[i]).size();
			init_matrix = new double[dimension][size];
			index = 0;
			for(IntCursor j : clusters.getCluster(indicies[i])){
				addToColumn(init_matrix,clusters.getVector(j.value),index);
				index++;
			}
			cv_matrix = new Covariance(init_matrix);
			sigmas.put(i, cv_matrix.getCovarianceMatrix());
		}
	}


	
	private void addToColumn(double[][] init_matrix, double[] vector, int index) {
		for(int i = 0; i<vector.length; i++){
			init_matrix[i][index] = vector[i];
		}
		
	}


	private void calculateMeans(EStepDS clusters) {
		double[] mean;
		int[] indicies = clusters.getClusterIndicies();
		
		for(int i = 0; i<indicies.length; i++){
			mean = new double[dimension];
			for(IntCursor j : clusters.getCluster(indicies[i])){
				add(mean,clusters.getVector(j.value));
			}
			for(int j = 0; j<mean.length; j++){
				mean[j] = mean[j] / clusters.getCluster(indicies[i]).size();
			}
			means.put(i, mean);
		}
		
	}
	
	//Prior = cluster.get(i).size / Total Instances
	private void calculatePriors(EStepDS clusters) {
		int i, t_instances = 0;
		int[] indicies = clusters.getClusterIndicies();
		for(i= 0; i<indicies.length; i++){
			t_instances +=clusters.getCluster(indicies[i]).size();
		}
		for(i = 0; i<indicies.length; i++){
			priors.put(indicies[i], clusters.getCluster(indicies[i]).size()/t_instances);
		}
	}
	
	
	public int getVectorIndex(int z){
		int w =  (int) (Math.sqrt(8*z+1)-1)/2;
		int t =  (int) ((Math.pow(w, 2)+ w)/2);
		int y = z - t;
		int x = w - y;
		return x;
	}
	
	public int getHIndex(int z){
		int w =  (int) (Math.sqrt(8*z+1)-1)/2;
		int t =  (int) ((Math.pow(w, 2)+ w)/2);
		int y = z - t;
		int x = w - y;
		return y;
		
	}
	
	public IntObjectHashMap<IntHashSet> findAllMemberships(EStepDS clusters,double threshold){
		IntObjectHashMap<IntHashSet> membership_vectors = new IntObjectHashMap<IntHashSet>();
		double score;
		IntHashSet set;
		for(ObjectIntPair<double[]> v_pair : clusters.getVectors()){
			for(int i =  0; i<k; i++){
				score = calculateMembership(v_pair.o, i);
				if(score>threshold) {
					set = membership_vectors.get(v_pair.i);
					if(set!= null) set.add(i);
					else {
						set = new IntHashSet(); 
						set.add(i);
						membership_vectors.put(v_pair.i, set);
					}
				}
			}
		}
		set = new IntHashSet();
		for(ObjectIntPair<IntHashSet> m_pair : membership_vectors){
			if(m_pair.o.size()<2){
				set.add(m_pair.i);
			}
		}
		for(IntCursor i : set){
			membership_vectors.remove(i.value);
		}
		return membership_vectors;
	}
	
	
	
	
	
	public double calculateMembership(double[] vector, int c_index){
		LUDecomposition d_matrix = new LUDecomposition(sigmas.get(c_index));
		RealMatrix inverse = d_matrix.getSolver().getInverse();
		double[] x_mean = subtract(vector,means.get(c_index));
		RealMatrix xm_matrix = MatrixUtils.createRowRealMatrix(x_mean);
		double determinant = d_matrix.getDeterminant();
		// 1/ Sqrt of 2pi^dimension * determinant
		double score = 1/(Math.sqrt(Math.pow(2*Math.PI, dimension)*determinant));
		RealMatrix xm_t = xm_matrix.transpose().scalarMultiply(-1/2);
		RealMatrix maha_distance = xm_t.multiply(inverse);
		maha_distance = maha_distance.multiply(xm_matrix);
		//Assuming maha dist returns a 1x1 matrix
		System.out.println(maha_distance.getRowDimension() + " x " +maha_distance.getColumnDimension());
		score = priors.get(c_index)* score * Math.exp(maha_distance.getTrace());
		return score;
	}

	
	public void add(double[] v1, double[] v2) {
		for(int i = 0; i<v2.length; i++){
			v1[i] += v2[i];
		}
	}
	
	public double[] subtract(double[] v1, double[] v2) {
		for(int i = 0; i<v2.length; i++){
			v1[i] -= v2[i];
		}
		return v1;
	}
	
}
