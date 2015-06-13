package edu.emory.clir.clearnlp.clusterExperiment.chameleon;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

import edu.emory.clir.clearnlp.util.IOUtils;

public class IrisTest {

	static public void main(String[] args) throws Exception{
		
		Chameleon c = new Chameleon(.02, 2, .1);
		
		
		Map<Integer,String> word_map = new HashMap<Integer,String>();
		String line;
		String[] arr;
		double[] vector;
		int count = 0;
		BufferedReader br = IOUtils.createBufferedReader(args[0]);
		while((line=br.readLine())!=null){
			arr = line.split(",");
			vector = new double[4];
			word_map.put(count, arr[arr.length-1]);
			for(int i = 0; i<arr.length-1; i++){
				vector[i] = Double.parseDouble(arr[i]);
			}
			c.addVectors(vector, count++);
		}
		br.close();
		
		
		c.createGraph(3);
		c.createSubClusters();
		c.printInitCluster();
		c.mergeSubCluster();
		c.printFinalClusters();
		c.printWordClusters(word_map);
		
		
		
	}
}
