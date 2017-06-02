package apps;

import structures.*;
import java.util.ArrayList;

public class MST {
	
	/**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
	
		/* COMPLETE THIS METHOD */
        PartialTreeList trees = new PartialTreeList();
		for(int i = 0;i<graph.vertices.length;i++)
        {
            PartialTree temp = new PartialTree(graph.vertices[i]);
			Vertex.Neighbor t = graph.vertices[i].neighbors;
            while(t!=null) {
				temp.getArcs().insert(new PartialTree.Arc(graph.vertices[i], t.vertex, t.weight));
				t = t.next;
			}
            trees.append(temp);
        }
		return trees;
	}

	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */

	public static ArrayList<PartialTree.Arc> execute(PartialTreeList ptlist) {
		
		/* COMPLETE THIS METHOD */
        ArrayList<PartialTree.Arc> a = new ArrayList<>();
        ArrayList<Vertex> children = new ArrayList<>();
		boolean inside;
		PartialTree.Arc arcRoot;
		while(ptlist.size() > 1) {
            PartialTree temp = ptlist.remove();
            //System.out.print(temp.toString() + " ---> ");
            do {
                arcRoot = temp.getArcs().deleteMin();
                inside = containsV2(arcRoot,temp.getArcs());
            }
            while(inside && !temp.getArcs().isEmpty());
            System.out.println(arcRoot.toString() + " is a component of the MST");
            a.add(arcRoot);
            Vertex u = arcRoot.v2;
            for(Vertex x: children)//checks to see if vertex is a child of another vertex. If it is, we search remove the tree containing its parent instead
            {
                if(u == x) {
                    u = x.getRoot();
                    break;
                }
            }
            PartialTree PTY = ptlist.removeTreeContaining(u);
            temp.merge(PTY);
            children.add(PTY.getRoot());
            ptlist.append(temp);
        }
		return a;
	}

    /**
     *  Helper method
     *  Checks to see if the vertex v2 is already within this tree
     *
     *  @param X Arc which holds the v2 we will be comparing
     *  @param List The list of the other arcs along with X
     */
	private static boolean containsV2(PartialTree.Arc X, MinHeap<PartialTree.Arc> List)
    {
        for(PartialTree.Arc temp: List)
        {
            if(X.v2 == temp.v1)
            {
                return true;
            }
        }
        return false;
    }
}
