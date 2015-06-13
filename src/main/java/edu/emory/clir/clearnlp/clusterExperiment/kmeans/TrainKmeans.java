package edu.emory.clir.clearnlp.clusterExperiment.kmeans;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.BasicConfigurator;

import com.carrotsearch.hppc.cursors.IntCursor;

import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.util.BinUtils;
import edu.emory.clir.clearnlp.util.IOUtils;

public class TrainKmeans {

	
	static public void main(String[] args) throws Exception{
		BasicConfigurator.configure();
		String trainFile = args[0];
		int K = Integer.parseInt(args[1]);
		double RSS = Double.parseDouble(args[2]);
		int THREADS = Runtime.getRuntime().availableProcessors();
		int DIMENSIONS = Integer.parseInt(args[4]);
		String DISTANCEMEASURE = args[5];
		String INITMETHOD = args[6];
		String OUTPUTFILE = args[7];
		
		// DISTANCE MEASURE  : cosine or euclidean
		// INIT : RandomCluster, RandomCentroid, Regular (Jinhos)
		KmeansClustering kmeans = new KmeansClustering(K, RSS, THREADS,DIMENSIONS, DISTANCEMEASURE, INITMETHOD);
		
		
		IntObjectHashMap<String> word_map = new IntObjectHashMap<String>();
		BufferedReader br = IOUtils.createBufferedReader(trainFile);
		int delimiter_index,previous_index = 0;
		String line,word;
		double[] vector;
		int v_position, word_id = 0;
		while((line=br.readLine())!=null){
			line = line.trim();
			v_position = 0;
			previous_index = line.indexOf(" ")+1;
			vector = new double[DIMENSIONS];
			word = line.substring(0, line.indexOf(" ")+1).trim();
			word_map.put(word_id++, word);
			while(v_position<DIMENSIONS){
				delimiter_index = line.indexOf(" ",previous_index);
				if(delimiter_index == -1) {
					vector[v_position++] = Double.parseDouble(line.substring(previous_index, line.length()).trim());
					break;
					
				}
				vector[v_position++] = Double.parseDouble(line.substring(previous_index, delimiter_index+1).trim());
				previous_index = delimiter_index+1;
			}
			kmeans.addPoint(vector);
		}
		br.close();
		
		List<Cluster> clusters = kmeans.cluster();

		PrintStream ps = IOUtils.createBufferedPrintStream(OUTPUTFILE);
		StringBuilder sb = new StringBuilder();
		for(Cluster cluster : clusters){
			sb.setLength(0);
			for(IntCursor i : cluster.getPointSet()){
				sb.append(word_map.get(i.value)).append(" ");
			}
			ps.println(sb.toString());
			ps.println();
		}
		ps.flush();
		BinUtils.LOG.info("Done QED");
		System.exit(0);
	}
}
