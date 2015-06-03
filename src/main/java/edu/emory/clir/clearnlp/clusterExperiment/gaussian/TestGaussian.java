package edu.emory.clir.clearnlp.clusterExperiment.gaussian;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.apache.commons.math3.distribution.fitting.MultivariateNormalMixtureExpectationMaximization;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

import com.carrotsearch.hppc.cursors.IntCursor;

import edu.emory.clir.clearnlp.collection.list.IntArrayList;
import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.collection.set.IntHashSet;
import edu.emory.clir.clearnlp.util.IOUtils;

public class TestGaussian {
	
	static public void main(String[] args) throws Exception{
		//EStepDS ds = new EStepDS(args[0],args[1],args[2]);
		//System.out.println(ds.getK());
		
		double[][] test_matrix = new double[3][3];
		test_matrix[0] = new double[]{4.0,2.0,6.0};
		
	    RealMatrix matrix = MatrixUtils.createRealMatrix(test_matrix);
	    RealMatrix covarianceMatrix = new Covariance(matrix).getCovarianceMatrix();
		
		
	}

}
