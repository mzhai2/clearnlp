package edu.emory.clir.clearnlp.clusterExperiment;

import java.io.BufferedReader;
import java.io.PrintStream;

import edu.emory.clir.clearnlp.util.IOUtils;

public class RemoveFirstWord {

	
	
	static public void main(String[] args) throws Exception{
		BufferedReader br = IOUtils.createBufferedReader(args[0]);
		PrintStream ps = IOUtils.createBufferedPrintStream("fc_"+getFileName(args[0]));
		String line;
		String word;
		int space_index;
		StringBuilder sb = new StringBuilder();
		while((line=br.readLine())!=null){
			sb.setLength(0);
			space_index = line.indexOf(' ');
			word = line.substring(0, space_index+1);
			sb.append(line.substring(space_index+1, line.length()));
			//sb.append(word);
			ps.println(sb.toString());
		}
		ps.flush();
		ps.close();
	}
	
	
	public static String getFileName(String path){
	int index = path.lastIndexOf("/");
	return path.substring(index+1, path.length());
	
	}
	}
