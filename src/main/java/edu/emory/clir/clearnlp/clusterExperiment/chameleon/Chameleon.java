package edu.emory.clir.clearnlp.clusterExperiment.chameleon;

import java.io.PrintStream;

import com.carrotsearch.hppc.ObjectArrayList;

import edu.emory.clir.clearnlp.clusterExperiment.Vector.DenseVector;
import edu.emory.clir.clearnlp.clusterExperiment.chameleon.graph.Graph;
import edu.emory.clir.clearnlp.clusterExperiment.chameleon.graph.KNNGraph;
import edu.emory.clir.clearnlp.util.IOUtils;

//To do write the print method to print a hmetis graph to be process by the the c++ hmetis library
// Write a method to read the partitioned graphs and then form sub clusters

public class Chameleon {

	private DenseVector[] tupleList;
	private KNNGraph sparseGraph;
	private ObjectArrayList<Graph> clusters;



	public Chameleon(int k, int min, DenseVector[] tupleList){
		this.tupleList = tupleList;
		sparseGraph = new KNNGraph(tupleList,k);
		clusters = new ObjectArrayList<Graph>();
	}

	
	

	//Edge , Vertices 1 (1 = weighted edges)
	//file:///Users/johnnytan/Downloads/manual.pdf hmetis manual
	private void printHyperGraph(){
		PrintStream pw = IOUtils.createBufferedPrintStream("HyperGraph.txt");
		int vertices = tupleList.length;
		pw.print(sparseGraph.getEdgeCount());
		pw.print(' ');
		pw.print(vertices);
		pw.print(' ');
		pw.print(1);

				
	}


}
