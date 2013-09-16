package kr.re.kisti.hieuvt.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Graph<T> {

	private List<Edge<T>> edgeList;
	private List<Vertex<T>> vertexList;

	public Graph(List<Vertex<T>> vertexList, List<Edge<T>> edgeList) {
		setEdgeList(edgeList);
		setVertexList(vertexList);
	}

	public Graph(Vertex<T> vertex){
		setEdgeList(null);
		List<Vertex<T>> vertexList = new ArrayList<Vertex<T>>();
		vertexList.add(vertex);
		setVertexList(vertexList);
		vertex.setVisited(false);
		System.out.println("num Vertex: " + getVertexList().size());
	}
	
	public void refreshGraph() {
		for (Vertex<T> vertex : getVertexList()) {
			vertex.setVisited(false);
		}
		for (Edge<T> edge: getEdgeList()) {
			edge.setVisited(false);
		}
	}

	public void visitGraph() {
		for (Vertex<T> vertex : getVertexList()) {
			vertex.setVisited(true);
		}
		if (getEdgeList() != null){
			for (Edge<T> edge: getEdgeList()){
				edge.setVisited(true);
			}
		}	
	}

	public void addVertex(Vertex<T> vertex) {
		List<Vertex<T>> newVertexList = getVertexList();
		newVertexList.add(vertex);
		setVertexList(newVertexList);
	}
	
	public void addEdge(Edge<T> edge) {
		List<Edge<T>> newEdgeList = getEdgeList();
		newEdgeList.add(edge);
		setEdgeList(newEdgeList);
		boolean foundEdgeVertex1 = false;
		boolean foundEdgeVertex2 = false;
		for (Vertex<T> vertex: getVertexList()){
			if (vertex.getId() == edge.getVertex1().getId()){
				foundEdgeVertex1 = true;
			}
			if (vertex.getId() == edge.getVertex2().getId()){
				foundEdgeVertex2 = true;
			}
		}
		if (!foundEdgeVertex1){
			addVertex(edge.getVertex1());
		}
		if (!foundEdgeVertex2){
			addVertex(edge.getVertex2());
		}
		
	}
	public List<Edge<T>> getEdgeList() {
		return edgeList;
	}

	public void setEdgeList(List<Edge<T>> edgeList) {
		this.edgeList = edgeList;
	}

	public List<Vertex<T>> getVertexList() {
		return vertexList;
	}

	public void setVertexList(List<Vertex<T>> vertexList) {
		this.vertexList = vertexList;
	}

	public boolean isFresh() {
		for (Vertex<T> vertex : getVertexList()) {
			if (vertex.isVisited()) {
				return false;
			}
		}
		return true;
	}
	
 @SuppressWarnings("null")
public String toString(){
	 String str = new String();
	 List<Integer> sortedList = new ArrayList<Integer>();
	 for (Vertex<T> vertex: getVertexList()){
		 sortedList.add(vertex.getId());
	 }
	 Collections.sort(sortedList);
	 for (Integer i: sortedList){
		 str = str + i.toString() + " ";
	 }
	 return str;
 }
}
