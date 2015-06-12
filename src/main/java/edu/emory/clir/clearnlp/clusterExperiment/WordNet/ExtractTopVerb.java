package edu.emory.clir.clearnlp.clusterExperiment.WordNet;

import java.io.BufferedReader;
import java.io.PrintStream;

import edu.emory.clir.clearnlp.util.IOUtils;

public class ExtractTopVerb {

	static public void main(String[] args) throws Exception{
		BufferedReader br = IOUtils.createBufferedReader(args[0]);
//		PrintStream ps = IOUtils.createBufferedPrintStream("v_all.txt");
//		PrintStream ps_100 = IOUtils.createBufferedPrintStream("v_top100.txt");
		PrintStream ps_1000 = IOUtils.createBufferedPrintStream("v_top_1000.txt");

		String line,word;
		int index;
		int count = 1;
		while((line = br.readLine())!=null){
			index = line.indexOf(" ");
			if(index==-1) index = line.indexOf("	");
			word = line.substring(0, index);
			System.out.println(word+ " " +count);
			if(count>74941){
				ps_1000.println(word);
			}
			//ps.println(word);
			count++;
		}
//		ps.flush();
//		ps_100.flush();
		ps_1000.flush();
		
	}
}
