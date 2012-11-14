package g2.testing.hierarchy;

import g2.model.Hierarchical;

import java.util.Collection;
import java.util.Set;

public class TestPrerequisiteDepth {
	
	/* Label the nodes of a hierarchical collection with their depth.
	 * Note that depth if a course has prerequisites at depth 2, 5, 5,
	 * the courses's depth is 6, as all prerequisites must be fulfilled.
	 * 
	 * Important: all possible prerequisites should be contained within
	 * the set that's passed in; otherwise it might loop forever.
	 * 
	 * Note: nodes will be labelled in order of their depth; e.g. all nodes of
	 * depth 1 will be labelled, then all nodes of depth 2, ...
	 * 
	 * Node with no prereqs has depth 0.
	 * Node with prereqs has depth min (depth of labelled prereqs) if any prereqs have labels.
	 * 
	 * If there is a cycle, this will loop infinitely. I think it'll just be
	 * better to use hasPrereq.
	 */
	public static void label(Collection<? extends Hierarchical> set) {
		final int size = set.size();
		int nLabelled = 0;
		
		while (nLabelled != size) {
			for (Hierarchical h : set) {
				/* Case already labelled */
				if (h.getDepth() != Hierarchical.DEPTH_NOT_SET)
					continue;
				
				/* Case this is a root */
				if (h.prereqs().size() == 0) {
					h.setDepth(0);
					continue;
				}
				
				/* Find the depth of prerequisite with maximal depth */
				int maxDepth = Hierarchical.DEPTH_NOT_SET;
				Set<Hierarchical> hPrereqs = h.prereqs();
				for (Hierarchical hPrereq : hPrereqs) {
					final int curPrereqDepth = hPrereq.getDepth();
					
					if(curPrereqDepth == Hierarchical.DEPTH_NOT_SET)
						continue;
					
					if (maxDepth == Hierarchical.DEPTH_NOT_SET) {
						/* Maximum has not been set yet */
						maxDepth = curPrereqDepth;
					} else {
						maxDepth = Math.max(maxDepth, curPrereqDepth);
					}
				}
				
				/* There were prerequisites, but none of them were labeled it.
				 * Note that this can indicate a cycle is present, if an iteration
				 * of the while loop goes by without labelling a single node.
				 */
				if (maxDepth == Hierarchical.DEPTH_NOT_SET)
					continue;
				
				/* If we've gotten here, at least one prereq of H has a labelled depth,
				 * and therefore we can label H.
				 */
				int nodeDepth = maxDepth + 1;
				h.setDepth(nodeDepth);
				nLabelled++;
			}
			System.out.println(nLabelled + "/" + size);
		}
	}
}
