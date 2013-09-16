package kr.kisti.re.hieuvt.tree;

import java.util.List;

public interface Tree<T> {

	public void addNode(String tableName, String itemName, int parentId, String content);
	public void removeNode(Node<T> node);
	public Node<T> getRoot();
	public List<Node<T>> getAllNode();
}
