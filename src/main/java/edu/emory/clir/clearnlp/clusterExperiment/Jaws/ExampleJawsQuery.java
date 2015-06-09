package edu.emory.clir.clearnlp.clusterExperiment.Jaws;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class ExampleJawsQuery {

	static public void main(String[] args){
		
		NounSynset nounSynset; 
		NounSynset[] hyponyms; 

		WordNetDatabase database = WordNetDatabase.getFileInstance(); 
		Synset[] synsets = database.getSynsets("fly", SynsetType.NOUN); 
		for (int i = 0; i < synsets.length; i++) { 
		    nounSynset = (NounSynset)(synsets[i]); 
		    System.out.println(synsets[i].getDefinition());
		    hyponyms = nounSynset.getHyponyms(); 
		    //System.err.println(nounSynset.getWordForms()[0] + ": " + nounSynset.getDefinition() + ") has " + hyponyms.length + " hyponyms"); 
		}

	}
}
