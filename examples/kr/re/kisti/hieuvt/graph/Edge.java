package kr.re.kisti.hieuvt.graph;

public class Edge<T> {

	private int id;
	private Vertex<T> vertex1;
	private Vertex<T> vertex2;
	private double weight;
	private boolean visited;

	public Edge(int id, Vertex<T> vertex1, Vertex<T> vertex2, double weight) {
		setId(id);
		setVertex1(vertex1);
		setVertex2(vertex2);
		setWeight(weight);
		setVisited(false);
	}

	public String toString() {
		return (getVertex1().getId() + " -- "
				+ getVertex2().getId() + " : " + getWeight());

	}

	public Vertex<T> getVertex1() {
		return vertex1;
	}

	public void setVertex1(Vertex<T> vertex1) {
		this.vertex1 = vertex1;
	}

	public Vertex<T> getVertex2() {
		return vertex2;
	}

	public void setVertex2(Vertex<T> vertex2) {
		this.vertex2 = vertex2;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
}
