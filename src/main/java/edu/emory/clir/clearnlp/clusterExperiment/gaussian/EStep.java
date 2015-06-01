package edu.emory.clir.clearnlp.clusterExperiment.gaussian;

import java.io.File;
import java.util.List;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

import edu.emory.clir.clearnlp.clusterExperiment.Vector.DenseVector;
import edu.emory.clir.clearnlp.collection.map.IntDoubleHashMap;
import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;

public class EStep {

	private IntObjectHashMap<double[]> mean;
	private IntObjectHashMap<RealMatrix> sigma;
	private IntDoubleHashMap prior;
	private int dimension;
	private int k;


	public EStep(){
		mean = new IntObjectHashMap<double[]>();
		sigma = new IntObjectHashMap<RealMatrix>();
		prior = new IntDoubleHashMap();
	}


	public void process(List<List<DenseVector>> clusters){
		k = clusters.size();
		getDimension(clusters);
		calculateMean(clusters);
		calculateVar(clusters);
		calculatePrior(clusters);
	}


	private void calculatePrior(List<List<DenseVector>> clusters) {
		int i,t_instances = 0;
		for(i = 0; i<k; i++){
			t_instances+= clusters.get(i).size();
		}
		for(i = 0; i<k; i++){
			prior.put(i, clusters.get(i).size()/t_instances);
		}
	}


	private void calculateVar(List<List<DenseVector>> clusters) {
		double[][] matrix;
		List<DenseVector> cluster;
		DenseVector dv;
		int index,size;
		Covariance cv_matrix;
		for(int i = 0; i<k; i++){
			cluster = clusters.get(i);
			size = cluster.size();
			index = 0;
			matrix = new double[size][dimension];
			for(int j =0; j<size; j++){
				dv = cluster.get(j);
				setColumn(matrix,index++,dv.getDoubleArray());
			}
			cv_matrix = new Covariance(matrix);
			sigma.put(i, cv_matrix.getCovarianceMatrix());
		}
	}


	private void setColumn(double[][] matrix, int index, double[] doubleArray) {
		for(int i = 0; i<doubleArray.length; i++){
			matrix[index][i] = doubleArray[i];
		}
	}


	private void getDimension(List<List<DenseVector>> clusters) {
		int max = Integer.MIN_VALUE;
		for(List<DenseVector> cluster:clusters){
			for(DenseVector vector : cluster){
				if(vector.getFloatArray().length>max) 
					max = vector.getFloatArray().length;
			}
		}
		dimension = max;
	}


	private void calculateMean(List<List<DenseVector>> clusters) {
		double[] meanVector;
		int i,size;
		for(i= 0; i<k; i++){
			meanVector = new double[dimension];
			size = clusters.get(i).size();
			for(DenseVector vector : clusters.get(i)){
				add(meanVector,vector.getDoubleArray());
			}
			for(i = 0; i<meanVector.length; i++){
				meanVector[i] = meanVector[i] / (size);
			}
			mean.put(i, meanVector);
		}

	}


	private void add(double[] v1, double[] v2) {
		for(int i = 0; i<v2.length; i++){
			v1[i] += v2[i];
		}
	}



	private double calculateMembershipProb(DenseVector vector, int c_index){
		LUDecomposition d_matrix = new LUDecomposition(sigma.get(c_index));
		RealMatrix inverse = d_matrix.getSolver().getInverse();
		double[] x_mean = subtract(vector.getDoubleArray(),mean.get(c_index));
		RealMatrix xm_matrix = MatrixUtils.createColumnRealMatrix(x_mean);
		double determinant = d_matrix.getDeterminant();
		// 1/ Sqrt of 2pi^dimension * determinant
		double score = 1/(Math.sqrt(Math.pow(2*Math.PI, dimension)*determinant));
		RealMatrix xm_t = xm_matrix.transpose();
		RealMatrix maha_distance = xm_matrix.transpose().scalarMultiply(-1/2).multiply(inverse).multiply(xm_matrix);
		System.out.println(maha_distance.toString());
		//Assuming maha dist returns a 1x1 matrix
		score = prior.get(c_index)* score * Math.exp(maha_distance.getEntry(1, 1));
		return score;
	}
	
	
	private double[][] getProbabilityArray(List<List<DenseVector>> vectors){
		double[][] p_matrix = new double[vectors.size()][];
		double[] p_vector;
		for(int i = 0; i<k; i++){
			for(int j = 0; j<vectors.get(i).size(); j++){
				p_vector = new double[k];
				for(int l = 0; l<k; l++){
					p_vector[l] = calculateMembershipProb(vectors.get(i).get(j), l);
				}
				p_matrix[j] = p_vector;
			}
		}
		return p_matrix;
	}
	
	private double[] subtract(double[] v1, double[] v2) {
		for(int i = 0; i<v2.length; i++){
			v1[i] -= v2[i];
		}
		return v1;
	}

}
