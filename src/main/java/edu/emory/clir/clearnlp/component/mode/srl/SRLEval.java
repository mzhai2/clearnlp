/**
 * Copyright 2015, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.emory.clir.clearnlp.component.mode.srl;

import java.util.List;

import edu.emory.clir.clearnlp.component.evaluation.AbstractF1Eval;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.arc.SRLArc;

/**
 * @since 3.1.3
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class SRLEval extends AbstractF1Eval<SRLArc[]>
{
	@Override
	public void countCorrect(DEPTree sTree, SRLArc[][] gSemanticHeads)
	{
		int i, size = sTree.size();
		List<SRLArc> sHeads;
		SRLArc[]     gHeads;
		
		for (i=1; i<size; i++)
		{
			sHeads = sTree.get(i).getSemanticHeadArcList();
			gHeads = gSemanticHeads[i];
			
			p_total += sHeads.size();
			r_total += gHeads.length;
			
			for (SRLArc g : gHeads)
				for (SRLArc s : sHeads)
					if (s.equals(g.getNode(), g.getLabel()))
						n_correct++;
		}
	}
}
