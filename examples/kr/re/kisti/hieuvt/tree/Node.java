package kr.re.kisti.hieuvt.tree;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {
	private List<Integer> hierrachicalId;
	private T data;
	private Node<T> parent;
	private List<Node<T>> children;
	private boolean isVisited;

	public Node() {

	}

	public Node(T data) {
		this.data = data;
		setHierrachicalId(new ArrayList<Integer>());
		setChildren(new ArrayList<Node<T>>());
		setVisited(false);
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Node<T> getParent() {
		return parent;
	}

	public void setParent(Node<T> parent) {
		this.parent = parent;
	}

	public List<Node<T>> getChildren() {
		return children;
	}

	public void setChildren(List<Node<T>> children) {
		this.children = children;
	}

	public List<Integer> getHierrachicalId() {
		return hierrachicalId;
	}

	public void setHierrachicalId(List<Integer> hierrachicalId) {
		this.hierrachicalId = hierrachicalId;
	}

	public boolean isVisited() {
		return isVisited;
	}

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}

}
