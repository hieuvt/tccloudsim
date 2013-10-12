package kr.kisti.re.hieuvt.tree;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kr.re.kisti.hieuvt.core.RandomConstantsTp;

public class TreeMySql<T> extends TreeDb<T> {

	public TreeMySql(String tableName, String itemName, String rootContent) {
		super(tableName, itemName, rootContent);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initDb() {
		// TODO Auto-generated method stub
		try {
			Class.forName("com.mysql.jdbc.Driver");
			setConnection(DriverManager.getConnection("jdbc:mysql://localhost/"
					+ RandomConstantsTp.dbName + "?"
					+ "user=tpUser&password=123456"));
			getConnection().prepareStatement("drop table if exists "
			// + RandomConstantsTp.dbName + "."
					+ getTableName() + ";").execute();
			System.out.println("Droped table");
			getConnection()
					.prepareStatement(
							"create table "	+ 
							getTableName() + 
							" (id int not null auto_increment," + 
							" primary key (id), " +   
							getItemName() + 
							" varchar(1600) not null," +
							" parentId int default null" +
							" );")
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
	
	@Override
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
	
	@Override
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

}
