package structures;


import java.util.ArrayList;

/**
 * Encapsulates an interval tree.
 * 
 * @author runb-cs112
 */
public class IntervalTree {
	/**
	 * The root of the interval tree
	 */
	IntervalTreeNode root;

	/**
	 * Constructs entire interval tree from set of input intervals. Constructing the tree
	 * means building the interval tree structure and mapping the intervals to the nodes.
	 *
	 * @param intervals Array list of intervals for which the tree is constructed
	 */
	public IntervalTree(ArrayList<Interval> intervals) {

		// make a copy of intervals to use for right sorting
		ArrayList<Interval> intervalsRight = new ArrayList<Interval>(intervals.size());
		for (Interval iv : intervals) {
			intervalsRight.add(iv);
		}

		// rename input intervals for left sorting
		ArrayList<Interval> intervalsLeft = intervals;

		// sort intervals on left and right end points
		sortIntervals(intervalsLeft, 'l');
		sortIntervals(intervalsRight, 'r');

		// get sorted list of end points without duplicates
		ArrayList<Integer> sortedEndPoints =
				getSortedEndPoints(intervalsLeft, intervalsRight);

		// build the tree nodes
		root = buildTreeNodes(sortedEndPoints);

		// map intervals to the tree nodes
		mapIntervalsToTree(intervalsLeft, intervalsRight);
	}

	/**
	 * Returns the root of this interval tree.
	 *
	 * @return Root of interval tree.
	 */
	public IntervalTreeNode getRoot() {
		return root;
	}

	/**
	 * Sorts a set of intervals in place, according to left or right endpoints.
	 * At the end of the method, the parameter array list is a sorted list.
	 *
	 * @param intervals Array list of intervals to be sorted.
	 * @param lr        If 'l', then sort is on left endpoints; if 'r', sort is on right endpoints
	 */
	public static void sortIntervals(ArrayList<Interval> intervals, char lr) {
		// COMPLETE THIS METHOD
		//Using bubblesort to sort both endpoints
		if (lr == 'l') {
			boolean move = true;
			while (move) {
				move = false;
				for (int i = 1; i < intervals.size(); i++) {
					Interval temp;
					if (intervals.get(i - 1).leftEndPoint > intervals.get(i).leftEndPoint) {
						temp = intervals.get(i - 1);
						intervals.set(i - 1, intervals.get(i));
						intervals.set(i, temp);
						move = true;
					}
				}
			}
		}
		if (lr == 'r') {
			boolean move = true;
			while (move) {
				move = false;
				for (int i = 1; i < intervals.size(); i++) {
					Interval temp;
					if (intervals.get(i - 1).rightEndPoint > intervals.get(i).rightEndPoint) {
						temp = intervals.get(i - 1);
						intervals.set(i - 1, intervals.get(i));
						intervals.set(i, temp);
						move = true;
					}
				}
			}
		}
	}


	/**
	 * Given a set of intervals (left sorted and right sorted), extracts the left and right end points,
	 * and returns a sorted list of the combined end points without duplicates.
	 *
	 * @param leftSortedIntervals  Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 * @return Sorted array list of all endpoints without duplicates
	 */
	public static ArrayList<Integer> getSortedEndPoints(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE PROGRAM COMPILE
		ArrayList<Integer> points = new ArrayList<Integer>();
		ArrayList<Integer> Rtemp = new ArrayList<Integer>();
		for (int i = 0; i < leftSortedIntervals.size(); i++) {
			if (i > 0) {
				if (leftSortedIntervals.get(i).leftEndPoint != leftSortedIntervals.get(i - 1).leftEndPoint) {
					points.add(leftSortedIntervals.get(i).leftEndPoint);
				}
			} else {
				points.add(leftSortedIntervals.get(i).leftEndPoint);
			}
		}
		for (int i = 0; i < rightSortedIntervals.size(); i++) {
			if (i > 0) {
				if (rightSortedIntervals.get(i).rightEndPoint != rightSortedIntervals.get(i - 1).rightEndPoint) {
					Rtemp.add(rightSortedIntervals.get(i).rightEndPoint);
				}
			} else {
				Rtemp.add(rightSortedIntervals.get(i).rightEndPoint);
			}
		}
		for (Integer x : points) {
			if (Rtemp.contains(x)) {
				Rtemp.remove(x);
			}
		}
		for (int i = 0; i < Rtemp.size(); i++) {
			for (int j = 0; j < points.size(); j++) {
				if (Rtemp.get(i) < points.get(j)) {
					points.add(j, Rtemp.get(i));
					Rtemp.remove(i);
				}
			}
		}
		for (Integer z : Rtemp) {
			points.add(z);
		}
		return points;
	}

	/**
	 * Builds the interval tree structure given a sorted array list of end points
	 * without duplicates.
	 *
	 * @param endPoints Sorted array list of end points
	 * @return Root of the tree structure
	 */
	public static IntervalTreeNode buildTreeNodes(ArrayList<Integer> endPoints) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE PROGRAM COMPILE
		Queue<IntervalTreeNode> tree = new Queue<IntervalTreeNode>();
		for (Integer p : endPoints) {
			IntervalTreeNode a = new IntervalTreeNode(p, p, p);
			tree.enqueue(a);
		}
		IntervalTreeNode T;
		int Tsize = tree.size();
		while (Tsize > 0) {
			if (Tsize == 1) {
				T = tree.dequeue();
				return T;
			} else {
				int temps = Tsize;
				while (temps > 1) {
					IntervalTreeNode T1 = tree.dequeue();
					IntervalTreeNode T2 = tree.dequeue();
					float v1 = T1.maxSplitValue;
					float v2 = T2.minSplitValue;
					IntervalTreeNode N = new IntervalTreeNode((v1 + v2) / 2, T1.minSplitValue, T2.maxSplitValue);
					N.leftChild = T1;
					N.rightChild = T2;
					tree.enqueue(N);
					temps -= 2;
				}
				if (temps == 1) {
					tree.enqueue(tree.dequeue());
				}
				Tsize = tree.size();
			}
		}
		//T = tree.dequeue();
		return null;
	}

	/**
	 * Maps a set of intervals to the nodes of this interval tree.
	 *
	 * @param leftSortedIntervals  Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 */
	public void mapIntervalsToTree(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		// COMPLETE THIS METHOD
		boolean left;
		for (Interval p : leftSortedIntervals) {
			left = true;
			Traversematch(root, p, left).leftIntervals.add(p);
			//System.out.println(p.toString());
		}
		for (Interval x : rightSortedIntervals) {
			left = false;
			Traversematch(root, x, left).rightIntervals.add(x);
			//System.out.println(x.toString());
		}
	}

	private IntervalTreeNode Traversematch(IntervalTreeNode Root, Interval sorted, boolean left) {
		Queue<IntervalTreeNode> T = new Queue<IntervalTreeNode>();
		if (Root == null) {
			return null;
		}//IntervalTreeNode temp = Root;
		T.enqueue(Root);
		while (!T.isEmpty()) {
			IntervalTreeNode s = T.dequeue();
			if (sorted.contains(s.splitValue)) {
				if (left && s.leftIntervals == null) {
					s.leftIntervals = new ArrayList<Interval>();
				}
				if (!left && s.rightIntervals == null) {
					s.rightIntervals = new ArrayList<Interval>();
				}
				return s;

			}
			if (s.leftChild != null) {
				T.enqueue(s.leftChild);
			}
			if (s.rightChild != null) {
				T.enqueue(s.rightChild);
			}
		}
		return null;
	}

	/*private void Traverseintersect(Interval q, ArrayList<Interval> intersect) {
		Queue<IntervalTreeNode> T = new Queue<>();
		if (root == null) {
			return;
		}
		//IntervalTreeNode temp = Root;
		T.enqueue(root);
		while (!T.isEmpty()) {
			IntervalTreeNode a = T.dequeue();
			a.matchLeft(q, intersect);
			a.matchRight(q, intersect);
			//s.leftIntervals.add(sorted);
			//return;*
			if (a.leftChild != null) {
				T.enqueue(a.leftChild);
			}
			if (a.rightChild != null) {
				T.enqueue(a.rightChild);
			}
		}
	}*/
	private ArrayList<Interval> intersecting(Interval q, IntervalTreeNode root, ArrayList<Interval> t) {
		if (root == null) {
			return t;
		}
		if (q.contains(root.splitValue)) {
			root.matchLeft(q, t);
			//System.out.println(root.toString());
			intersecting(q, root.rightChild, t);
			intersecting(q, root.leftChild, t);

		} else if (root.splitValue < q.leftEndPoint && root.rightIntervals != null) {
			int i = root.rightIntervals.size() - 1;
			while (i >= 0 && root.rightIntervals.get(i).intersects(q)) {
				t.add(root.rightIntervals.get(i));
				i--;
			}
			//if(root.rightChild.rightIntervals != null) {
			intersecting(q, root.rightChild, t);
			//}
		} else if (root.splitValue > q.rightEndPoint && root.leftIntervals != null) {
			int i = 0;
			while (i < root.leftIntervals.size() && root.leftIntervals.get(i).intersects(q)) {
				t.add(root.leftIntervals.get(i));
				i++;
			}
			//if(root.leftChild.leftIntervals != null) {
			intersecting(q, root.leftChild, t);
			//}
		}
		return t;
	}
	//private ArrayList<Interval> ()

	/**
	 * Gets all intervals in this interval tree that intersect with a given interval.
	 *
	 * @param q The query interval for which intersections are to be found
	 * @return Array list of all intersecting intervals; size is 0 if there are no intersections
	 */
	public ArrayList<Interval> findIntersectingIntervals(Interval q) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE PROGRAM COMPILE
		ArrayList<Interval> intersections = new ArrayList<Interval>();
		/*Traverseintersect(q,intersections);
		int size = intersections.size();
		for (int i = 0; i < size; i++)//removes duplicate intervals from the left and right side
		{
			for (int j = i + 1; j < size; j++)
			{
				if (intersections.get(i).equals(intersections.get(j)))
				{
					intersections.remove(j--);
					size--;
				}
			}
		}*/
		intersecting(q, root, intersections);
		return intersections;
	}

}
