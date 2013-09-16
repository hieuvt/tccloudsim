package kr.re.kisti.hieuvt.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class HelperGraph<T> {

	private Graph<T> origVertexCloseGraph;

	public HelperGraph() {

	}

	private boolean isAllEdgeNotVisited(List<Edge<T>> edgeList) {
		for (Edge<T> edge : edgeList) {
			if (edge.isVisited()) {
				return false;
			}
		}
		return true;
	}

	private Graph<T> getVertexNeighbors(Graph<T> bigGraph, Vertex<T> vertex) {
		List<Vertex<T>> connectedVertexList = new ArrayList<Vertex<T>>();
		connectedVertexList.add(vertex);
		List<Edge<T>> connectedEdgeList = new ArrayList<Edge<T>>();
		for (Edge<T> edge : bigGraph.getEdgeList()) {
			if (edge.getVertex1().getId() == vertex.getId()) {
				connectedVertexList.add(edge.getVertex2());
				connectedEdgeList.add(edge);
			} else if (edge.getVertex2().getId() == vertex.getId()) {
				connectedVertexList.add(edge.getVertex1());
				connectedEdgeList.add(edge);
			}
		}
		Graph<T> closeGraph = null;
		if ((connectedEdgeList.size() != 0)
				&& (connectedVertexList.size() != 0)) {
			closeGraph = new Graph<>(connectedVertexList, connectedEdgeList);
		} else {
			closeGraph = new Graph<>(vertex);
		}

		// System.out.println("from vertex: " + vertex.getId());
		// for (Vertex<T> closeVertex : closeGraph.getVertexList()) {
		// System.out.println("close vertex: " + closeVertex.getId());
		// }
		// for (Edge<T> closeEdge: closeGraph.getEdgeList()){
		// System.out.println("close edge " + closeEdge.getId() + " : " +
		// closeEdge);
		// }
		// System.out.println("List of close vertex: ");
		// for (Vertex<T> closeVertex : closeGraph.getVertexList()) {
		// System.out.println(closeVertex.getId());
		// }
		return closeGraph;
	}

	public Graph<T> subGraphFromVertex(Graph<T> bigGraph, Vertex<T> origVertex) {
		Vertex<T> tmpVertex = origVertex;
		if (!tmpVertex.isVisited()) {
			if (isAllEdgeNotVisited(getVertexNeighbors(bigGraph, tmpVertex)
					.getEdgeList())) {
				setOrigVertexCloseGraph(getVertexNeighbors(bigGraph, tmpVertex));
				for (Edge<T> edge : getOrigVertexCloseGraph().getEdgeList()) {
					edge.setVisited(true);
				}
				tmpVertex.setVisited(true);
			} else {
				for (Edge<T> edge : getVertexNeighbors(bigGraph, tmpVertex)
						.getEdgeList()) {
					if (!edge.isVisited()) {
						getOrigVertexCloseGraph().addEdge(edge);
						edge.setVisited(true);
					}
				}
				tmpVertex.setVisited(true);
			}

			for (int i = 0; i < getVertexNeighbors(bigGraph, tmpVertex)
					.getVertexList().size(); i++) {
				subGraphFromVertex(bigGraph,
						getVertexNeighbors(bigGraph, tmpVertex).getVertexList()
								.get(i));
			}
		}
		return getOrigVertexCloseGraph();
	}

	public List<Graph<T>> divideToSubGraphs(Graph<T> bigGraph,
			double removeRatio) {
		List<Graph<T>> subGraphList = new ArrayList<Graph<T>>();
		
		
		double numRemovedEdge = bigGraph.getEdgeList().size() * removeRatio;
		if (numRemovedEdge < 1) {
			for (Vertex<T> vertex : bigGraph.getVertexList()) {
				subGraphList.add(new Graph<>(vertex));
			}
		} else {
			Collections.sort(bigGraph.getEdgeList(), new EdgeComparator<T>());
			Iterator<Edge<T>> itr = bigGraph.getEdgeList().iterator();
			for (int count = 0; count < numRemovedEdge; count++) {
				itr.next();
				itr.remove();
			}
			System.out.println("# remain edge: "
					+ bigGraph.getEdgeList().size());

			List<Vertex<T>> blockedVertexList = bigGraph.getVertexList();
			List<Edge<T>> blockedEdgeList = bigGraph.getEdgeList();

			Graph<T> blockedGraph = new Graph<>(blockedVertexList,
					blockedEdgeList);
			blockedGraph.refreshGraph();
			Graph<T> subGraph;
			for (Vertex<T> vertex : blockedGraph.getVertexList()) {
				if (!vertex.isVisited()) {
					System.out.println("subgraph from vertex :"
							+ vertex.getId());
					System.out.println("Vertex List:");
					if (getVertexNeighbors(bigGraph, vertex).getEdgeList() == null) {
						subGraph = getVertexNeighbors(bigGraph, vertex);
						vertex.setVisited(true);
					} else {
						subGraph = subGraphFromVertex(blockedGraph, vertex);
					}
					for (Vertex<T> vertex2 : subGraph.getVertexList()) {
						System.out.println("vertex " + vertex2.getId());
					}
					subGraphList.add(subGraph);
				}

			}
		}
		return subGraphList;
	}

	public Graph<T> getOrigVertexCloseGraph() {
		return origVertexCloseGraph;
	}

	public void setOrigVertexCloseGraph(Graph<T> origVertexCloseGraph) {
		this.origVertexCloseGraph = origVertexCloseGraph;
	}
}
