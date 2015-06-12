package edu.emory.clir.clearnlp.clusterExperiment.WordNet;

import java.io.BufferedReader;
import java.io.PrintStream;

import edu.emory.clir.clearnlp.util.IOUtils;

public class ExtractTopNoun {

	
	static public void main(String[] args) throws Exception{
		BufferedReader br = IOUtils.createBufferedReader(args[0]);
		//PrintStream ps_all= IOUtils.createBufferedPrintStream("n_all.txt");
		//PrintStream ps_100 = IOUtils.createBufferedPrintStream("n_top_100.txt");
		PrintStream ps_1000 = IOUtils.createBufferedPrintStream("n_top_1000.txt");

		String line;
		int index;
		String word;
		int count = 1;
		while((line = br.readLine())!=null){
			index = line.indexOf(" ");
			if(index==-1) index = line.indexOf("	");
			word = line.substring(0, index);
			//ps_all.println(word);
			if(count>985963){
				ps_1000.println(word);
			}
			count++;
		}
		//ps_all.flush();
		//ps_100.flush();
		ps_1000.flush();
		br.close();

	}
}
