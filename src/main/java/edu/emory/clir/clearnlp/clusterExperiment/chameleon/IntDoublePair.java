package edu.emory.clir.clearnlp.clusterExperiment.chameleon;

public class IntDoublePair implements Comparable<IntDoublePair> {

	private int Index;
	private double value;
	
	
	public IntDoublePair(){
	}
	
	public IntDoublePair(int index, double value){
		this.Index=index;
		this.value=value;
	}

	public int getIndex() {
		return Index;
	}

	public void setIndex(int index) {
		Index = index;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	

	@Override
	public int compareTo(IntDoublePair o) {
		if(this.getValue()<o.getValue())
			return -1;
		if(this.getValue()>o.getValue())
			return 1;
		else return 0;
	}
}