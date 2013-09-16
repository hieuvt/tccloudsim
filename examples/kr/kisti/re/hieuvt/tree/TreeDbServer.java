package kr.kisti.re.hieuvt.tree;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import kr.re.kisti.hieuvt.core.RandomConstantsTp;

import org.hsqldb.Server;

public class TreeDbServer {

	private static Server hsqlServer;
	
	public static void start(){
		setHsqlServer(new Server());
		getHsqlServer().setDatabaseName(0, RandomConstantsTp.dbName);
		getHsqlServer().setDatabasePath(0, "file: trafficGraph/" + RandomConstantsTp.dbName);
		getHsqlServer().start();
	}
	
	public static void stop(){
		getHsqlServer().stop();
	}

	private static Server getHsqlServer() {
		return hsqlServer;
	}

	private static void setHsqlServer(Server server) {
		hsqlServer = server;
	}
}
