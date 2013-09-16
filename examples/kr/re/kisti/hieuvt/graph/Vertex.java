package kr.re.kisti.hieuvt.graph;

public class Vertex<T> {

	private Integer id;
	private T data;
	private boolean visited;

	public Vertex(int id, T data){
		setId(id);
		setData(data);
	}
	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public boolean isVisited() {
		return visited;
	}
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
}
