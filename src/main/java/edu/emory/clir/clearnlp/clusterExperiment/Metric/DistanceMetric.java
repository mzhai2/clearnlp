package edu.emory.clir.clearnlp.clusterExperiment.Metric;

import edu.emory.clir.clearnlp.clusterExperiment.ClusterPoint.AbstractClusterPoint;

/**
 * Created by mike on 5/21/15.
 */
public interface DistanceMetric {
    public double distance(AbstractClusterPoint a, AbstractClusterPoint b, boolean saveNorms);
}
