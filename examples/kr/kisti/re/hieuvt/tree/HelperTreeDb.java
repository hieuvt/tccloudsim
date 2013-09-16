package kr.kisti.re.hieuvt.tree;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hsqldb.Server;

public class HelperTreeDb {
	
	private Server server;
	private String dbName;
	private Connection connection;
	
	public HelperTreeDb(Server server, String dbName){
//		setServer(server);
//		setDbName(dbName);
//		
//		getServer().setDatabaseName(0, getDbName());
//		getServer().setDatabasePath(0, "file:" + getDbName());
//		getServer().start();
		
		try {
			setConnection(DriverManager.getConnection(
					"jdbc:hsqldb:hsql://localhost/" + getDbName(), "sa", ""));
			System.out.println("Successfully connected");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public HelperTreeDb(String dbName){
		setDbName(dbName);
		try {
			setConnection(DriverManager.getConnection(
					"jdbc:hsqldb:hsql://localhost/" + getDbName(), "sa", ""));
			System.out.println("Successfully connected");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void close(){
		try {
			getConnection().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void shutdown(){
		getServer().stop();
	}
	
	public List<String> findSiblingLeaves(String tableName, String itemName,
			String content) {
		
		List<String> siblingLeaves = new ArrayList<String>();
		 String sql1 = "select parentId from " + tableName
		 + " where " + itemName + " = '" + content + "';";
		 try {
			ResultSet rs1 = getConnection().prepareStatement(sql1).executeQuery();
			while (rs1.next()){
				System.out.println(rs1.getInt(1));
				String sql2 = "select * from " + tableName
						+ " where parentId = " + rs1.getInt(1) +";";
				ResultSet rs2 = getConnection().prepareStatement(sql2).executeQuery();
				while (rs2.next()){
					String str = rs2.getString(itemName);
					int spaceCount = 0;
					for (char c: str.toCharArray()){
						if (c == ' '){
							spaceCount++;
						}
					}
					if (spaceCount == 1){
						if (!str.equals(content)){
							siblingLeaves.add(str);
						}
						
					}
				}
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return siblingLeaves;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

}
