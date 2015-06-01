package edu.emory.clir.clearnlp.clusterExperiment.gaussian;

import java.io.File;

public class TestGaussian {
	
	static public void main(String[] args) throws Exception{
		SerializeCluster gp = new SerializeCluster();
		gp.readVectors(new File(args[0]));
	}

}
