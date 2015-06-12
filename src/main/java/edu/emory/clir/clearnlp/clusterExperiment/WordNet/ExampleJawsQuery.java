package edu.emory.clir.clearnlp.clusterExperiment.WordNet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.carrotsearch.hppc.ObjectOpenHashSet;
import com.carrotsearch.hppc.ObjectSet;
import com.carrotsearch.hppc.cursors.ObjectCursor;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordSense;

public class ExampleJawsQuery {
	public static WordNetDatabase database = WordNetDatabase.getFileInstance(); 

	public static int K = 2;
	public static SynsetType s_type = SynsetType.NOUN;
	public static List<String> getHypo(String o) {
		List<String> best = new ArrayList<>();
		return best;
	}

	public static ObjectSet<String> getHypoSet(String word) {
		ObjectSet<String> best = new ObjectOpenHashSet<String>();
		getHypoAux(best, word, 0);
		return best;
	}

	public static void getHypoAux(ObjectSet<String> best, String word, int i) {
		System.out.println(word);
		if(i==K) {
			best.add(word);
			return;
		}
		Synset[] sets = database.getSynsets(word, s_type);
		int length = (sets.length<K) ? sets.length : K;
		for(int index = 0; index<length; index++){
			NounSynset ns = (NounSynset) sets[index];
			for(NounSynset hyponym : ns.getHypernyms()){
				for(String w : hyponym.getWordForms()){
					getHypoAux(best, w , i+1);
				}
			}
		}
	}
		




	static public void main(String[] args) throws IOException{
		ObjectSet<String>set = getHypoSet("blue");
		Synset[] sets = database.getSynsets("blue", s_type);
		for(Synset syn : sets){
			String[] arr = syn.getWordForms();
			for(String s : arr){
				System.out.println(s);
			}
		}

		
		for(ObjectCursor<String> s : set){
//			System.out.println(s.value);
		}
		
	}
}
