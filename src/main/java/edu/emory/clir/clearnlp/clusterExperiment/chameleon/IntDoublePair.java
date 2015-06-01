package edu.emory.clir.clearnlp.clusterExperiment.chameleon;

public class IntDoublePair {

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
}