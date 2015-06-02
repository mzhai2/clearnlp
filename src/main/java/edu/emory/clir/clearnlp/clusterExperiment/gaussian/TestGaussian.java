package edu.emory.clir.clearnlp.clusterExperiment.gaussian;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import com.carrotsearch.hppc.cursors.IntCursor;

import edu.emory.clir.clearnlp.collection.list.IntArrayList;
import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.collection.set.IntHashSet;
import edu.emory.clir.clearnlp.util.IOUtils;

public class TestGaussian {
	
	static public void main(String[] args) throws Exception{
		EStepDS ds = new EStepDS(args[0],args[1],args[2]);
		System.out.println(ds.getK());
		
	}

}
