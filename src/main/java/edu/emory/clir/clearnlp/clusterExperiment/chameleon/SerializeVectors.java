package edu.emory.clir.clearnlp.clusterExperiment.chameleon;

import java.io.BufferedReader;
import java.io.File;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import edu.emory.clir.clearnlp.clusterExperiment.gaussian.SerializeClusters;
import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.util.IOUtils;

public class SerializeVectors {
	
	private  IntObjectHashMap<double[]> dense_vectors;
	private  IntObjectHashMap<String> word_map;
	
	public SerializeVectors(){
		dense_vectors = new IntObjectHashMap();
		word_map = new IntObjectHashMap();
	}
	
	public void readVectors(File vectors) throws Exception{
		BufferedReader br = IOUtils.createBufferedReader(vectors);
		String line;
		int word_id = 1;
		String word;
		double[] dense_vector;
		int begin_index,space_index,vector_index;
		while((line=br.readLine())!=null){
			vector_index = 0;
			dense_vector = new double[300];
			space_index = line.indexOf(' ');
			word = line.substring(0, space_index+1);
			//word_id = Integer.parseInt(line.substring(0, space_index));
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
			word_map.put(word_id, word);
			dense_vectors.put(word_id++, dense_vector);
		}
	}
	
	public void serializeMaps() throws Exception{
		ObjectOutput op = new ObjectOutputStream(IOUtils.createXZBufferedOutputStream("DenseVectors"));
		op.writeObject(dense_vectors);
		op.close();

		op =  new ObjectOutputStream(IOUtils.createXZBufferedOutputStream("WordMap"));
		op.writeObject(word_map);
		op.close();
		
	}
	
	static public void main(String[] args) throws Exception{
		SerializeVectors sv = new SerializeVectors();
		sv.readVectors(new File(args[0]));
		sv.serializeMaps();
	}

}
