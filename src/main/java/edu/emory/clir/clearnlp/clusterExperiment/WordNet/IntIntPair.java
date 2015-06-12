package edu.emory.clir.clearnlp.clusterExperiment.WordNet;

public class IntIntPair implements Comparable<IntIntPair> {

	private int index;
	private int frequency;

	public IntIntPair(int index, int frequency){
		setIndex(index);
		setFreq(frequency);
	}

	public int getIndex(){
		return this.index;
	}

	public int getFreq(){
		return this.frequency;
	}


	public void setIndex(int index){
		this.index= index;
	}

	public void setFreq(int frequency){
		this.frequency=frequency;
	}

	@Override
	public int compareTo(IntIntPair o) {
		if(this.getFreq()>o.getFreq())
			return 1;
		else if(this.getFreq()<o.getFreq())
			return -1;
		else return 0;
	}


}
