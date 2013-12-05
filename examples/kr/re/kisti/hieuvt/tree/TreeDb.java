package kr.re.kisti.hieuvt.tree;

import java.sql.Connection;
import java.util.List;

public abstract class TreeDb<T> {

	private Connection connection;
	private String tableName;
	private String itemName;
	private String rootContent;
	
	public TreeDb(String tableName,
			String itemName, String rootContent){
		setTableName(tableName);
		setItemName(itemName);
		setRootContent(rootContent);
		initDb();
	}

	protected void initDb() {
		// TODO Auto-generated method stub
		
	}

	public void addNode(String tableName, String itemName, int parentId,
			String content){
	}
	
	public int getNodeByContent(String tableName, String itemName,
			String content){
		return -1;
	}
	
	public List<String> getAllLeafNodes(String tableName, String itemName){
		return null;
	}
	
	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getRootContent() {
		return rootContent;
	}

	public void setRootContent(String rootContent) {
		this.rootContent = rootContent;
	}
}
