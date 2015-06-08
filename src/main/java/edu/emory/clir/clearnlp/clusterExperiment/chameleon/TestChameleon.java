package edu.emory.clir.clearnlp.clusterExperiment.chameleon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.PrintStream;

import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.util.IOUtils;

public class TestChameleon {

	static public void main(String[] args) throws Exception{
		Chameleon c = new Chameleon(args[0], 10, 0, 2, .0001);
		long start = System.currentTimeMillis();
		c.createSubClusters();
		c.printInitCluster();
		c.mergeSubCluster();
		c.printFinalClusters();
		long end = System.currentTimeMillis()-start;
		c.printWordClusters(args[1]);
		System.out.println(end/1000+ " Seconds");

		
	}
	
	
}
