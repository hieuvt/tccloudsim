package kr.kisti.re.hieuvt.tree;

import java.util.ArrayList;

public class HelperTree<T> {
	public TreeNoDb<T> addNode(TreeNoDb<T> origTree, Node<T> parent, Node<T> child) {
		parent.getChildren().add(child);
		child.setParent(parent);
		child.setChildren(new ArrayList<Node<T>>());
		child.getHierrachicalId().addAll(parent.getHierrachicalId());
		child.getHierrachicalId().add(parent.getChildren().size() - 1);
		return origTree;
	}
	
	public TreeNoDb<T> removeNode(TreeNoDb<T> origTree, Node<T> node) {
		Node<T> parent = node.getParent();
		parent.getChildren().remove(node);
		return origTree;
	} 
}
