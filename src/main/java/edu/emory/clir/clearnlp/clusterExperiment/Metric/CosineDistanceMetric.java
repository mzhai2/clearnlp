package edu.emory.clir.clearnlp.clusterExperiment.Metric;

import edu.emory.clir.clearnlp.clusterExperiment.ClusterPoint.AbstractClusterPoint;
import edu.emory.clir.clearnlp.clusterExperiment.Vector.DenseVector;

/**
 * Created by mike on 5/21/15.
 */
public class CosineDistanceMetric implements DistanceMetric {

    @Override
    public double distance(AbstractClusterPoint a, AbstractClusterPoint b, boolean saveNorms) {
        double similarity = 0;
        if (a.getVector().getClass() == edu.emory.clir.clearnlp.clusterExperiment.Vector.SparseVector.class) {

        }
        else {
            double numerator = 0;
            DenseVector v1 = (DenseVector)a.getVector();
            float[] f1 = v1.getFloatArray();
            DenseVector v2 = (DenseVector)b.getVector();
            float[] f2 = v2.getFloatArray();
            for (int i=0; i<f1.length; i++)
                numerator+=f1[i]*f2[i];
            double denominator = a.getNorm(saveNorms)*b.getNorm(saveNorms);
            similarity = numerator/denominator;
        }
        return 1-similarity;
    }
}
