package edu.emory.clir.clearnlp.clusterExperiment.gaussian;

import java.io.BufferedReader;
import java.io.File;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import edu.emory.clir.clearnlp.collection.list.IntArrayList;
import edu.emory.clir.clearnlp.collection.map.IntIntHashMap;
import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.util.IOUtils;

public class SerializeClusters {

	private IntObjectHashMap<IntArrayList> clusters;
	private IntObjectHashMap<double[]> dense_vectors;
	private IntIntHashMap h_structure;


	public SerializeClusters(boolean h){
		dense_vectors = new IntObjectHashMap<double[]>();
		h_structure = new IntIntHashMap();
		clusters = new IntObjectHashMap<IntArrayList>();
	}

	public void process(File vectors, File clusters, boolean h) throws Exception{
		readVectors(vectors);
		readCluster(clusters,h);
	}


	public void readVectors(File vectors) throws Exception {
		BufferedReader br = IOUtils.createBufferedReader(vectors);
		String line;
		int word_id = 0;
		double[] dense_vector;
		int begin_index,space_index,vector_index;
		while((line=br.readLine())!=null){
			vector_index = 0;
			dense_vector = new double[300];
			space_index = line.indexOf(' ');
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
			dense_vectors.put(word_id++, dense_vector);
		}
	}



	public void readCluster(File cluster,boolean h) throws Exception{
		BufferedReader br = IOUtils.createBufferedReader(cluster);
		String line;
		IntArrayList temp_list;
		int firstHyphen,secondHyphen,cluster_index,vector_index,h_index,hierarchy = 0;;
		while((line=br.readLine())!=null){
			firstHyphen = line.indexOf('-', 0);
			cluster_index = Integer.parseInt(line.substring(0, firstHyphen));
			secondHyphen = line.indexOf('-', firstHyphen+1);
			if((secondHyphen-firstHyphen)>1) hierarchy = Integer.parseInt(line.substring(firstHyphen+1, secondHyphen));
			else hierarchy = -1;
			vector_index = Integer.parseInt(line.substring(secondHyphen+1, line.length()));

			if(!h){
				temp_list = clusters.get(cluster_index);
				if(temp_list!=null) temp_list.add(vector_index); 
				else{
					temp_list = new IntArrayList();
					temp_list.add(vector_index);
					clusters.put(cluster_index, temp_list);
				}
			}
			else{
				if(hierarchy ==-1) hierarchy = 0;
				System.out.println(cluster_index +" " + hierarchy);
				h_index = (int) cantorPair(cluster_index,hierarchy);
				System.out.println(h_index);
				temp_list = clusters.get(h_index);
				if(temp_list!=null) temp_list.add(vector_index);
				else{
					temp_list = new IntArrayList();
					temp_list.add(vector_index);
					clusters.put(h_index, temp_list);
				}
			}

			if(hierarchy!=-1) h_structure.put(vector_index, hierarchy);
		}
	}


	private double cantorPair(int cluster_index, int hierarchy) {
		double score =  ((.5)*(cluster_index+hierarchy)*(cluster_index+hierarchy+1)+hierarchy);
		return score;
	}


	public void serializeMaps(boolean h) throws Exception{
		ObjectOutput op = new ObjectOutputStream(IOUtils.createXZBufferedOutputStream("dense_vectors"));
		op.writeObject(dense_vectors);
		op.close();

		op = new ObjectOutputStream(IOUtils.createXZBufferedOutputStream("h_structure"));
		op.writeObject(h_structure);
		op.close();
		
		op =  new ObjectOutputStream(IOUtils.createXZBufferedOutputStream("clusters"));
		op.writeObject(clusters);
		op.close();
	}
	
	
	static public void main(String[] args) throws Exception{
		
		SerializeClusters sc = new SerializeClusters(true);
		sc.process(new File(args[0]), new File(args[1]),true);
		sc.serializeMaps(true);
	}

}
