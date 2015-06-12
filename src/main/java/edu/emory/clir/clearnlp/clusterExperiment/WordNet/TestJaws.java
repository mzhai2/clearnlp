package edu.emory.clir.clearnlp.clusterExperiment.WordNet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import edu.emory.clir.clearnlp.clusterExperiment.chameleon.IntDoublePair;

public class TestJaws {

	static public void main(String[] args){
		List<String> s = new ArrayList<>();
		s.add("hey");
		s.add("omg");
		System.out.println(s.toString());
		Comparator<IntDoublePair> c = new Comparator<IntDoublePair>(){
			public int compare(IntDoublePair p1, IntDoublePair p2){
				return p1.compareTo(p2);
			}
		};
		PriorityQueue<IntDoublePair> k_list = new PriorityQueue<>(2,c);
		
		k_list.add(new IntDoublePair(1,2.0));
		k_list.add(new IntDoublePair(3,3.0));
		k_list.add(new IntDoublePair(4,1.0));
		k_list.add(new IntDoublePair(2,.04));

		
		System.out.println(k_list.size());
		IntDoublePair pair;
		while(!k_list.isEmpty()){
			pair = k_list.remove();
			System.out.println(pair.getIndex() +" "+ pair.getValue());
		}
		
	}
}
