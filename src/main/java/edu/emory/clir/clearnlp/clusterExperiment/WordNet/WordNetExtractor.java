package edu.emory.clir.clearnlp.clusterExperiment.WordNet;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import com.carrotsearch.hppc.ObjectOpenHashSet;
import com.carrotsearch.hppc.ObjectSet;
import com.carrotsearch.hppc.cursors.ObjectCursor;

import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.util.IOUtils;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class WordNetExtractor {

	private WordNetDatabase database;
	private IntObjectHashMap<String> word_map;
	private IntObjectHashMap<ObjectSet<String>> sense_map;
	private IntObjectHashMap<ObjectSet<String>> hypo_map;
	private IntObjectHashMap<ObjectSet<String>> hyper_map;
	private int K;
	private int hypo_level;
	private int hyper_level;
	private SynsetType s_type;

	public WordNetExtractor(int K, int hypo_level, int hyper_level, String type){
		database = WordNetDatabase.getFileInstance(); 
		this.K =K;
		this.hypo_level=hypo_level;
		this.hyper_level=hyper_level;
		word_map = new IntObjectHashMap<String>();
		if(K!=0) sense_map = new IntObjectHashMap<ObjectSet<String>>();
		if(hypo_level!=0) hypo_map = new IntObjectHashMap<ObjectSet<String>>();
		if(hyper_level != 0) hyper_map = new IntObjectHashMap<ObjectSet<String>>();
		s_type = getType(type);

	}

	public void readWords(String fileName)throws Exception{
		BufferedReader br = IOUtils.createBufferedReader(fileName);
		String word;  
		int index = 0;
		while((word = br.readLine())!=null){
			word_map.put(index++, word);
		}
	}


	public void createSenseMap(){
		for(ObjectIntPair<String> p : word_map){
			putSenseMap(p.i,getSense(p.o));
		}
	}


	private void putSenseMap(int i, List<String> senses) {
		ObjectSet<String> t_set = sense_map.get(i);
		if(t_set!=null){
			for(String sense : senses)
				t_set.add(sense);
		}
		else{
			t_set = new ObjectOpenHashSet<String>();
			for(String sense : senses)
				t_set.add(sense);
			sense_map.put(i, t_set);
		}
	}	

	private List<String> getSense(String word) {
		List<String> best = new ArrayList<>(K);
		Synset[] sense_set = database.getSynsets(word, s_type);
		String[] arr;
		for(Synset syn : sense_set){
			arr = syn.getWordForms();
			for(String words : arr){
				best.add(words);
			}
		}
		return best;
	}

	public SynsetType getType(String type){
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

	public void createHypoMap(){
		for(ObjectIntPair<String> pair : word_map){
			putHypo(pair.i,getHypoSet(pair.o));
		}
	}

	private void putHypo(int i, ObjectSet<String> hypoSet) {
		hypo_map.put(i, hypoSet);
	}

	private ObjectSet<String> getHypoSet(String word) {
		ObjectSet<String> best = new ObjectOpenHashSet<String>();
		getHypoAux(best, word, 0);
		return best;
	}
	//Hypo at level 1 is best
	private void getHypoAux(ObjectSet<String> best, String word, int i) {
		if(i==hypo_level) {
			best.add(word); 
			return;
		}
		Synset[] sets = database.getSynsets(word, s_type);
		int length = (sets.length<K) ? sets.length : 2;
		for(int index = 0; index<length; index++){
			if(s_type.equals(SynsetType.NOUN)){
			NounSynset ns = (NounSynset) sets[index];
			for(NounSynset hyponym : ns.getHyponyms()){
				for(String w : hyponym.getWordForms()){
					getHypoAux(best, w , i+1);
				}
			}
			}
			if(s_type.equals(SynsetType.VERB)){
				VerbSynset vs = (VerbSynset) sets[index];
				for(VerbSynset hyponym : vs.getTroponyms()){
					for(String w : hyponym.getWordForms()){
						getHyperAux(best, w , i+1);
					}
				}
			}
		}
	}
	
	public void createHyperMap(){
		for(ObjectIntPair<String> pair : word_map){
			putHyper(pair.i,getHyperSet(pair.o));
		}
	}

	private void putHyper(int i, ObjectSet<String> hypoSet) {
		hyper_map.put(i, hypoSet);
	}

	private ObjectSet<String> getHyperSet(String word) {
		ObjectSet<String> best = new ObjectOpenHashSet<String>();
		getHyperAux(best, word, 0);
		return best;
	}
	//Hypo at level 1 is best
	private void getHyperAux(ObjectSet<String> best, String word, int i) {
		if(i==hyper_level) {
			best.add(word); 
			return;
		}
		Synset[] sets = database.getSynsets(word, s_type);
		int length = (sets.length<K) ? sets.length : K;
		for(int index = 0; index<length; index++){
			if(s_type.equals(SynsetType.NOUN)){
			NounSynset ns = (NounSynset) sets[index];
			for(NounSynset hyponym : ns.getHypernyms()){
				for(String w : hyponym.getWordForms()){
					getHyperAux(best, w , i+1);
				}
			}
			}
			if(s_type.equals(SynsetType.VERB)){
				VerbSynset vs = (VerbSynset) sets[index];
				for(VerbSynset hyponym : vs.getHypernyms()){
					for(String w : hyponym.getWordForms()){
						getHyperAux(best, w , i+1);
					}
				}
			}
			
			
			
			
		}
	}
	
	
	private void printFile(String file){
		PrintStream ps = IOUtils.createBufferedPrintStream(file);
		ObjectSet<String> temp_set;
		StringBuilder sb = new StringBuilder();
		for(ObjectIntPair<String> pair : word_map){
			ps.println(pair.o);
			temp_set = sense_map.get(pair.i);
			for(ObjectCursor<String> s : temp_set){
				sb.setLength(0);
				sb.append("S ").append(s.value);
				ps.println(sb.toString());
			}
			if(hypo_level>0){
				temp_set = hypo_map.get(pair.i);
				sb.setLength(0);
				sb.append("Hypo");
				for(ObjectCursor<String> s : temp_set){
					sb.append(" ").append(s.value);
				}
				ps.println(sb.toString());
			}
			if(hyper_level>0){
				temp_set = hyper_map.get(pair.i);
				sb.setLength(0);
				sb.append("Hyper");
				for(ObjectCursor<String> s : temp_set){
					sb.append(" ").append(s.value);
				}
				ps.println(sb.toString());
			}
			ps.flush();
		}
	}
	
	public String getPath(String fileName){
		int index = fileName.lastIndexOf("/");
		return fileName.substring(index+1, fileName.length());
		}
	
	
	
	public void printSimilarWords(String fileName){
		StringBuilder sb = new StringBuilder();
		PrintStream ps = IOUtils.createBufferedPrintStream("sense_"+getPath(fileName));
		PrintStream ps_hypo = IOUtils.createBufferedPrintStream("hypo_"+getPath(fileName));
		String[] temp_array;
		ObjectSet<String> temp_set;
		for(ObjectIntPair pair:word_map){
			sb.setLength(0);
			temp_set = sense_map.get(pair.i);
			sb.append(pair.o).append("	");
			for(ObjectCursor<String> s: temp_set){
			sb.append(s.value).append(",");
			}
			ps.println(sb.toString());
			temp_set = hypo_map.get(pair.i);
			if(temp_set.size()>0 && hypo_level>0){
				sb.setLength(0);
				sb.append(pair.o).append("	");
				for(ObjectCursor<String> s : temp_set){
					temp_array = s.value.split(" ");
					if(temp_array.length>2) continue;
					sb.append(s.value).append(",");
				}
				ps_hypo.println(sb.toString());
			}
//			temp_set = hyper_map.get(pair.i);
//			if(temp_set.size()>0 && hyper_level>0){
//				for(ObjectCursor<String> s : temp_set){
//					temp_array = s.value.split(" ");
//					if(temp_array.length>2) continue;
//					sb.append(s.value).append(",");
//				}
//			}
		}
		ps.flush();
		ps_hypo.flush();
		
		
	}
	
	
	
	
	
	

	
	static public void main(String[] args) throws Exception{
		WordNetExtractor wne = new WordNetExtractor(3, 2, 1, "n");
		wne.readWords(args[0]);
		wne.createSenseMap();
		wne.createHyperMap();
		wne.createHypoMap();
		//wne.printMatlab();
		//wne.printFile("s_nytverb.txt");
		wne.printSimilarWords(args[0]);
	}
	
	
	

}
