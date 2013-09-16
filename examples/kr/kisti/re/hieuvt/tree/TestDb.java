package kr.kisti.re.hieuvt.tree;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import kr.re.kisti.hieuvt.graph.Graph;

import org.cloudbus.cloudsim.Vm;
import org.hsqldb.Server;

public class TestDb {

	public static void main(String[] args) {
		// stub to get in/out of embedded db
//		Server hsqlServer = null;
//		Connection connection = null;
//		ResultSet rs = null;
//
//		hsqlServer = new Server();
//		hsqlServer.setLogWriter(null);
//		hsqlServer.setSilent(true);
//		hsqlServer.setDatabaseName(0, "iva");
//		hsqlServer.setDatabasePath(0, "file:ivadb");
//
//		hsqlServer.start();
//
//		// making a connection
//		try {
//			Class.forName("org.hsqldb.jdbcDriver");
//			connection = DriverManager.getConnection(
//					"jdbc:hsqldb:hsql://localhost/iva", "sa", ""); // can
//																	// through
//																	// sql
//																	// exception
//			DatabaseMetaData metadata = connection.getMetaData();
//			ResultSet result = metadata.getTables(null, null, "BARCODES", null);
//			boolean exists = result.next();
////			System.out.println(result.getInt(2));
//			result.close();
//			// connection.prepareStatement("drop table barcodes if exists;").execute();
//			if (!exists){
//				connection
//				.prepareStatement(
//						"create table barcodes (id integer, barcode varchar(20) not null);")
//				.execute();
//			}
//			
//			connection.prepareStatement(
//					"insert into barcodes (id, barcode)"
//							+ "values (1, '12345566');").execute();
//
//			// query from the db
//			rs = connection.prepareStatement(
//					"select id, barcode  from barcodes;").executeQuery();
//			rs.next();
//			System.out.println(String.format("ID: %1d, Name: %1s",
//					rs.getInt(1), rs.getString(2)));
//
//		} catch (SQLException e2) {
//			e2.printStackTrace();
//		} catch (ClassNotFoundException e2) {
//			e2.printStackTrace();
//		}
//
//		hsqlServer.stop();
//		hsqlServer = null;

		// end of stub code for in/out stub
//		Server server = new Server();
//		TreeDb<Graph<Vm>> graphTree = new TreeDb<>(server, "TestDb", "TestTable", "TestItem", "hahaha");
//		graphTree.addNode("TestTable", "TestItem", 0, "hohoho");
//		graphTree.addNode("TestTable", "TestItem", 0, "hihoho");
//		graphTree.shutdown();
//		HelperTreeDb helperTreeDb = new HelperTreeDb("graphDb");
		TreeDbServer.start();
		TreeDbServer.stop();
	}

}
