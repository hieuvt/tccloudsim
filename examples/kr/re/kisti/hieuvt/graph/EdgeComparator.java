package kr.re.kisti.hieuvt.graph;

import java.util.Comparator;

public class EdgeComparator<T> implements Comparator<Edge<T>> {

	@Override
	public int compare(Edge<T> o1, Edge<T> o2) {
		// TODO Auto-generated method stub
		return (o1.getWeight() > o2.getWeight() ? 1 : (o1.getWeight() == o2
				.getWeight() ? 0 : -1));
	}

}
