package edu.emory.clir.clearnlp.clusterExperiment.ClusterPoint;

import edu.emory.clir.clearnlp.clusterExperiment.Vector.Vector;

/**
 * Created by mike on 5/21/15.
 */
public class DensityPoint extends AbstractClusterPoint {
    private boolean corePoint;

    public DensityPoint(Vector vector) {
        super(vector);
    }

    public boolean isCorePoint() {
        return corePoint;
    }




}
