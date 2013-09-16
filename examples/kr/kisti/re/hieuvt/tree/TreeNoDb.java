package kr.kisti.re.hieuvt.tree;

import java.util.ArrayList;
import java.util.List;

// tree deployment
public class TreeNoDb<T> {
	private Node<T> root;
	private List<Node<T>> traversedNodeList;

	public TreeNoDb(Node<T> root) {
		this.root = root;
		root.getHierrachicalId().add(0);
	}

	public Node<T> getRoot() {
		return root;
	}

	public void removeNode(Node<T> node) {
		Node<T> parent = node.getParent();
		parent.getChildren().remove(node);
	}

	public Node<T> getNode(List<Integer> hierrachicalId) {
		Node<T> tmpNode = new Node<T>();
		tmpNode = getRoot();
		if (hierrachicalId.size() > 1) {
			for (int i = 1; i < hierrachicalId.size(); i++) {
				tmpNode = tmpNode.getChildren().get(hierrachicalId.get(i));
			}
		}
		return tmpNode;
	}

	public int calculateDistance(Node<T> node1, Node<T> node2) {
		List<Integer> shorter;
		List<Integer> longer;
		int distance = 0;
		if (node1.getHierrachicalId().size() <= node2.getHierrachicalId()
				.size()) {
			shorter = new ArrayList<Integer>(node1.getHierrachicalId());
			longer = new ArrayList<Integer>(node2.getHierrachicalId());
		} else {
			shorter = new ArrayList<Integer>(node2.getHierrachicalId());
			longer = new ArrayList<Integer>(node1.getHierrachicalId());
		}
		for (int i = 0; i < shorter.size(); i++) {
			if (shorter.get(i) != longer.get(i)) {
				distance = (shorter.size() - 1 - i) + (longer.size() - 1 - i)
						+ 1;
				return distance;
			} else {
				distance = longer.size() - shorter.size();
			}
		}
		return distance;
	}

	private void traverse(Node<T> fromNode) {
		Node<T> tmpNode = fromNode;
		if (tmpNode.getChildren() != null) {
			doVisit(tmpNode);
			for (int i = 0; i < tmpNode.getChildren().size(); i++){
				traverse(tmpNode.getChildren().get(i));
			}
		}
	}

	protected void doVisit(Node<T> node) {
		node.setVisited(true);
		getTraversedNodeList().add(node);
	}

	public List<Node<T>> getTraversedNodeList() {
		return traversedNodeList;
	}

	public void setTraversedNodeList(List<Node<T>> traversedNodeList) {
		this.traversedNodeList = traversedNodeList;
	}
		
}