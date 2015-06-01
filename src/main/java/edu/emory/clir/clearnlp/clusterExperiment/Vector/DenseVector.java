package edu.emory.clir.clearnlp.clusterExperiment.Vector;

/**
 * Created by mike on 5/21/15.
 */
public class DenseVector extends Vector {
    private float[] vector;

    public DenseVector(float[] vector) {
        this.vector = vector;
    }

    @Override
    public double euclideanNorm() {
        double sum = 0;
        for (float value : vector) {
            sum += value*value;
        }
        return Math.sqrt(sum);
    }

    public float[] getFloatArray() {
        return vector;
    }
    
    public double[] getDoubleArray(){
    	double[] d = new double[vector.length];
    	for(int i = 0; i<vector.length; i++){
    		d[i] = vector[i];
    	}
    	return d;
    }

}
