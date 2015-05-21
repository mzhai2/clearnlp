package edu.emory.clir.clearnlp.clusterExperiment.Clusterer;

import edu.emory.clir.clearnlp.cluster.Cluster;
import edu.emory.clir.clearnlp.clusterExperiment.ClusterPoint.DensityPoint;
import edu.emory.clir.clearnlp.clusterExperiment.Metric.DistanceMetric;

import java.util.List;

/**
 * Created by mike on 5/21/15.
 */
public class DeLiCluClustering {
    final protected int NUM_THREADS;
    final protected int MIN_PTS;
    final protected DistanceMetric metric;

    public DeLiCluClustering(int num_threads, int min_pts, DistanceMetric metric) {
        super();
        NUM_THREADS = num_threads;
        MIN_PTS = min_pts;
        this.metric = metric;
        buildReachability();
        cluster();
    }

    public double getCoreDistance(DensityPoint point) {
        if (!point.isCorePoint())
            return -1;
        //DensityPoint closestPoint = get Closest Point
        metric.distance(point, closestPoint);
        return closestPoint;
    }


}
