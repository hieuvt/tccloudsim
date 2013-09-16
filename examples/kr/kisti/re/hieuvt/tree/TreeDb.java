package kr.kisti.re.hieuvt.tree;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kr.re.kisti.hieuvt.core.RandomConstantsTp;

import org.hsqldb.Server;

public class TreeDb<T> implements Tree<T> {

	private Connection connection;
	private String tableName;
	private String itemName;
	private String rootContent;

	public TreeDb(String tableName,
			String itemName, String rootContent) {

		setTableName(tableName);
		setItemName(itemName);
		setRootContent(rootContent);

//		getServer().setDatabaseName(0, getDbName());
//		getServer().setDatabasePath(0, "file: trafficGraph/" + getDbName());
//		getServer().start();
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			setConnection(DriverManager.getConnection(
					"jdbc:hsqldb:hsql://localhost/" + RandomConstantsTp.dbName, "sa", ""));
			getConnection().prepareStatement(
					"drop table " + getTableName() + " if exists;").execute();
			getConnection()
					.prepareStatement(
							"create table "
									+ getTableName()
									+ " (id integer generated always as identity primary key, " 
									+ getItemName()
									+ " varchar(1200) unique not null, parentId integer default null, visited boolean default false);")
					.execute();
			getConnection().prepareStatement(
					"insert into " + getTableName() + " (" + getItemName()
							+ ") values ('" + getRootContent() + "');")
					.execute();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void removeNode(Node<T> node) {
		// TODO Auto-generated method stub

	}

	@Override
	public Node<T> getRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Node<T>> getAllNode() {
		// TODO Auto-generated method stub
		return null;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public void addNode(String tableName, String itemName, int parentId,
			String content) {
		// TODO Auto-generated method stub
		String sql = "insert into " + tableName + " (" + itemName
				+ ", parentId) values ('" + content + "', " + parentId + ");";
		try {
			getConnection().prepareStatement(sql).execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getNodeByContent(String tableName, String itemName,
			String content) {
		int contentId = -1;
		String sql = "select id from " + tableName + " where " + itemName
				+ " = '" + content + "';";
		try {
			ResultSet rs = getConnection().prepareStatement(sql).executeQuery();
			if (rs.next()) {
				contentId = rs.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return contentId;
	}

	public List<String> getAllLeafNodes(String tableName, String itemName) {
		List<String> leafNodes = new ArrayList<String>();
		String sql = "select t1." + itemName + " from " + tableName
				+ " as t1 left join " + tableName
				+ " as t2 on t1.id = t2.parentId where t2.id is null;";
		try {
			ResultSet rs = getConnection().prepareStatement(sql).executeQuery();
			while (rs.next()) {
				leafNodes.add(rs.getString(itemName));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return leafNodes;
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
