package edu.emory.clir.clearnlp.clusterExperiment.ClusterPoint;

import edu.emory.clir.clearnlp.clusterExperiment.Vector.Vector;

/**
 * Created by mike on 5/21/15.
 */
public abstract class AbstractClusterPoint {

    protected Vector vector;
    private double norm;

    public AbstractClusterPoint(Vector vector) {
        this.vector = vector;
        norm = -1;
    }

    public double getNorm(boolean save) {
        if (norm > 0) {
            double norm = vector.euclideanNorm();
            if (save) {
                this.norm = norm;
            }
            return norm;
        }
        return this.norm;
    }

    public Vector getVector() {
        return vector;
    }
}
