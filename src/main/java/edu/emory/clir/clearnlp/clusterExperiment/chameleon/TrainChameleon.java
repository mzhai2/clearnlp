package edu.emory.clir.clearnlp.clusterExperiment.chameleon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.PrintStream;

import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.util.IOUtils;

public class TrainChameleon {

	static public void main(String[] args) throws Exception{
		int K = Integer.parseInt(args[0]);
		double minSize = Double.parseDouble(args[1]);
		int alpha = Integer.parseInt(args[2]);
		double threshold = Double.parseDouble(args[3]);
		Chameleon c = new Chameleon("DenseVector", K, minSize, alpha, threshold);
		long start = System.currentTimeMillis();
		c.createSubClusters();
		c.mergeSubCluster();
		long end = System.currentTimeMillis()-start;
		c.printWordClusters("WordMap");
		System.out.println(end/1000+ " Seconds");
	}
	
	
}
