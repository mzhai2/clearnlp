package edu.emory.clir.clearnlp.clusterExperiment.WordNet;

import java.io.BufferedReader;
import java.io.PrintStream;

import edu.emory.clir.clearnlp.util.IOUtils;

public class ExtractVocabulary {

	static public void main(String[] args) throws Exception{
		PrintStream ps;
		BufferedReader br;
		String line;
		StringBuilder sb = new StringBuilder();
		for(String s : args){
			sb.setLength(0);
			sb.append(s);
			sb.append(".txt");
			ps = IOUtils.createBufferedPrintStream(sb.toString());
			br = IOUtils.createBufferedReader(s);
			
			while((line = br.readLine())!=null){
			ps.println(line.substring(0, line.indexOf(' ')+1));	
			}
			ps.flush();
			br.close();
		}
	}
}
