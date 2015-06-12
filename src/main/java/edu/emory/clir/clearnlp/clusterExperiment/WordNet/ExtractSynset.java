package edu.emory.clir.clearnlp.clusterExperiment.WordNet;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import edu.emory.clir.clearnlp.clusterExperiment.chameleon.IntDoublePair;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class ExtractSynset {
	
	
	static public void main(String[] args) throws Exception{
		
		if(args[1].equals("all")) extractBestIgnorePOS(args[0]);
		if(args[1].equals("topk")) extractTopK(args[0], args[2]);
		else extractBest(args[0],args[1]);
	}
	
	
	private static void extractTopK(String fileName, String type) throws Exception {
		BufferedReader br = IOUtils.createBufferedReader(fileName);
		PrintStream ps = IOUtils.createBufferedPrintStream("topk_"+fileName);
		String word;
		SynsetType s_type = getType(type);
		Synset[] set;
		StringBuilder sb = new StringBuilder();
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		List<String> t_list;
		while((word = br.readLine())!=null){
			sb.setLength(0);
			set = database.getSynsets(word.trim(),s_type);
			t_list = getTopK(word, set, 3);
			ps.println(word.trim());
			for(String s : t_list){
				sb.append("Sense ").append(s);
				ps.println(sb.toString());
			}
			sb.setLength(0);
			sb.append("Hyponym ");
			t_list = getHyponyms(word, set, 2);
			for(String s : t_list){
				sb.append(" ");
				sb.append(s);
				ps.println(sb.toString());
			}
			sb.setLength(0);
			sb.append("Hypernym");
			t_list = getHypernyms(word, set, 2);
			for(String s : t_list){
				sb.append(" ");
				sb.append(s);
				ps.println(sb.toString());
			}
			
			
			
		}
		
		
		
	}


	private static List<String> getHypernyms(String word, Synset[] set, int i) {
		// TODO Auto-generated method stub
		return null;
	}


	private static List<String> getHyponyms(String word, Synset[] set, int i) {
		return null;
	}


	private static void extractBest(String fileName, String type) throws Exception {
		BufferedReader br = IOUtils.createBufferedReader(fileName);
		PrintStream ps = IOUtils.createBufferedPrintStream("sense_"+type+"_"+getFileName(fileName));
		String word;
		SynsetType s_type = getType(type);
		Synset[] set;
		StringBuilder sb = new StringBuilder();
		WordNetDatabase database = WordNetDatabase.getFileInstance(); 
		while((word = br.readLine())!=null){
			sb.setLength(0);
			set = database.getSynsets(word.trim(),s_type);
			sb.append(word.trim()).append(" ").append(getBest(word.trim(),set));
			ps.println(sb.toString());
		}
		ps.flush();
		br.close();
	}


	private static String getBest(String word, Synset[] set) {
		int highestFreq = Integer.MIN_VALUE;
		Synset best = null;
		for(int i = 0; i<set.length; i++){
			try{
				if(highestFreq<set[i].getTagCount(word)){
					highestFreq = set[i].getTagCount(word);
					best = set[i];
				}
					
			}catch(edu.smu.tspell.wordnet.WordNetException e){
				if(i+1>=set.length) return "Not Found";
				else continue;
			}
		}
		return best.getDefinition();
	}
	
	private static List<String> getTopK(String word, Synset[] set, int k){
		Comparator<IntIntPair> c = new Comparator<IntIntPair>(){
			public int compare(IntIntPair p1, IntIntPair p2){
				return p1.compareTo(p2);
			}
		};
		PriorityQueue<IntIntPair> k_list = new PriorityQueue<>(k,c);
		for(int i = 0; i<set.length; i++){
			try{
				k_list.add(new IntIntPair(i,set[i].getTagCount(word)));
					
			}catch(edu.smu.tspell.wordnet.WordNetException e){
				if(i+1>=set.length) k_list.add(new IntIntPair(-1,-1));
				else continue;
			}
		}
		List<String> best = new ArrayList<>(k);
		int index;
		for(int i = 0; i<k; i++){
			index = k_list.remove().getIndex();
			if(index == -1) continue;
			best.add(set[index].getDefinition());
		}
		return best;
		
	}

	private static void extractBestIgnorePOS(String fileName) throws Exception{
		BufferedReader br = IOUtils.createBufferedReader(fileName);
		PrintStream ps = IOUtils.createBufferedPrintStream("s_all"+getFileName(fileName));
		String word;
		List<Synset[]> l_syn = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		WordNetDatabase database = WordNetDatabase.getFileInstance(); 
		Synset best;
		while((word = br.readLine())!=null){
			for(SynsetType st : SynsetType.ALL_TYPES){
				l_syn.add(database.getSynsets(word.trim(), st));
			}
			best = getBestFromList(word, l_syn);
			sb.setLength(0);
			sb.append(word).append(" ").append(best.getType()).append(" ").append(best.getDefinition());
			ps.println(sb.toString());
		}
		ps.flush();
		br.close();
	}

	private static Synset getBestFromList(String word, List<Synset[]> l_syn) {
		int highestFreq = Integer.MIN_VALUE;
		Synset best = null;
		for(Synset[] set : l_syn){
		for(Synset syn : set){
			if(highestFreq<syn.getTagCount(word)){
				highestFreq = syn.getTagCount(word);
				best = syn;
			}
		}
			
		}
		return best;
	}


	private static String getFileName(String Path){
		int index = Path.lastIndexOf('/');
		return Path.substring(index+1, Path.length());
	}

	public static SynsetType getType(String type){
		if(type.equals("n")){
			return SynsetType.NOUN;
		}
		if(type.equals("adj"))
			return SynsetType.ADJECTIVE;
		if(type.equals("v"))
			return SynsetType.VERB;
		if(type.equals("adv"))
			return SynsetType.ADVERB;
		return null;
	}

}
