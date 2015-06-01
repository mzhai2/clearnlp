package edu.emory.clir.clearnlp.clusterExperiment.gaussian;

import java.io.BufferedReader;
import java.io.File;

import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.collection.set.IntHashSet;
import edu.emory.clir.clearnlp.util.IOUtils;

public class SerializeCluster {

	private IntHashSet clusters;
	private IntObjectHashMap<double[]> dense_vectors;
	
	
	public SerializeCluster(){
		clusters = new IntHashSet();
		dense_vectors = new IntObjectHashMap<double[]>();
	}
	
	
	public void readVectors(File vectors) throws Exception {
		BufferedReader br = IOUtils.createBufferedReader(vectors);
		String line;
		double[] dense_vector;
		int word_id, begin_index,space_index,vector_index;
		while((line=br.readLine())!=null){
			vector_index = 0;
			dense_vector = new double[300];
			space_index = line.indexOf(' ');
			word_id = Integer.parseInt(line.substring(0, space_index));
			System.out.println(word_id);
			while(true){
				begin_index = space_index+1;
				space_index = line.indexOf(' ', begin_index);
				if(space_index == -1) {
					space_index = line.length();
					dense_vector[vector_index++] = Double.parseDouble(line.substring(begin_index, space_index));
					break;
				}
				dense_vector[vector_index++] = Double.parseDouble(line.substring(begin_index, space_index));
			}
			dense_vectors.put(word_id, dense_vector);
		}
	}
	
	
	public void readCluster(File cluster) throws Exception{
		BufferedReader br = IOUtils.createBufferedReader(cluster);
		
	}
	
	public void serializeMaps(){
		
	}
	
}
