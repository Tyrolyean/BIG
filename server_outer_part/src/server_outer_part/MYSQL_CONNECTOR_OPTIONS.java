package server_outer_part;

import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class MYSQL_CONNECTOR_OPTIONS {
	
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://"+Person_splitter.database()+"/server_parts";

	// Database credentials
	static final String USER = "server_parts";
	static final String PASS = "server_parts";
public static String get_default_world(){
	Connection conn = null;
	Statement stmt = null;
	int internal=0;
	int id=0;
	String name=null;
	try {
		
		// JDBC driver
		Class.forName("com.mysql.jdbc.Driver");

		// Open connection
		conn = DriverManager.getConnection(DB_URL, USER, PASS);

		// Executing query
		stmt = conn.createStatement();
		String i =null;
		try {
	    	   Socket s = new Socket("192.168.0.1",80);
	    	   i=s.getLocalAddress().getHostAddress();
	    	   s.close();

	    	   
	       }
	       catch(Exception e){e.printStackTrace();}
		String sql;
		ResultSet ider =stmt.executeQuery("SELECT default_world,id FROM server_location WHERE adress ='"+i+"' AND port ="+Person_splitter.server.getPort());
		while(ider.next()){
			internal=ider.getInt("default_world");
			id=ider.getInt("id");
		}
		sql = "SELECT * FROM worlds WHERE id="+id+" AND server_internal_number ="+internal;
		System.out.print(sql);
		ResultSet rs = stmt.executeQuery(sql);
		sql =null;

		// Extract data from result set
		while (rs.next()) {
			// Retrieve by column name
			 name=rs.getString("world_name");
		}
		// Close Connection
		rs.close();
		stmt.close();
		conn.close();
	} catch (SQLException se) {
		// Handle errors for JDBC
		se.printStackTrace();
	} catch (Exception e) {
		// Handle errors for Class.forName
		e.printStackTrace();
	} finally {
		// finally block used to close resources
		try {
			if (stmt != null)
				stmt.close();
		} catch (SQLException se2) {
		} // fatal...
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		} // end finally try
	} // end try
	return internal+name;
	
}
public static int get_id(){
	Connection conn = null;
	Statement stmt = null;
	int id=0;
	try {
		
		// JDBC driver
		Class.forName("com.mysql.jdbc.Driver");

		// Open connection
		conn = DriverManager.getConnection(DB_URL, USER, PASS);

		// Executing query
		stmt = conn.createStatement();
		String i =null;
		try {
	    	   Socket s = new Socket("192.168.0.1",80);
	    	   i=s.getLocalAddress().getHostAddress();
	    	   s.close();

	    	   
	       }
	       catch(Exception e){e.printStackTrace();}
		ResultSet ider =stmt.executeQuery("SELECT id FROM server_location WHERE adress ='"+i+"' AND port ="+Person_splitter.server.getPort());
		while(ider.next()){
			id=ider.getInt(1);
		}
		// Close Connection
		ider.close();
		stmt.close();
		conn.close();
	} catch (SQLException se) {
		// Handle errors for JDBC
		se.printStackTrace();
	} catch (Exception e) {
		// Handle errors for Class.forName
		e.printStackTrace();
	} finally {
		// finally block used to close resources
		try {
			if (stmt != null)
				stmt.close();
		} catch (SQLException se2) {
		} // fatal...
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
		} // end finally try
	} // end try
	return id;
}
	public static List<String> worlds() {
		
		//This is a List of all the Data needed for generating a world
		//it contains the information in the following way:
		//0.Boolean if almost one world exists
		//1. name
		//2.internal number
		//3.expiry date(Unix Timestamp)
		//4.owner(Trimmed UUID)
		//5.gamemode(int 0,1,2)
		//6.world-Type(At the moment not aviable, future:normal,flat,nether,end)
		//
		//
		//
		
		List<String> returner = new ArrayList<String>();
		Connection conn = null;
		java.sql.Statement stmt = null;
		try {
			// JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			// Executing query
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT * FROM worlds WHERE server_id="+Person_splitter.server_id+"ORDER BY server_internal_number";
			System.out.print(sql);
			ResultSet rs = stmt.executeQuery(sql);
			sql =null;
			returner.add("false");
			// Extract data from result set
			while (rs.next()) {
				// Retrieve by column name
				returner.set(0, "true");
				returner.add(rs.getString("name"));
				returner.add(rs.getString("expires"));
				returner.add(rs.getString("owner"));
				returner.add(rs.getString("gamemode"));
				returner.add(rs.getString("owner"));
				returner.add(rs.getString("world_type"));
			}
			// Close Connection
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			} // fatal...
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
		return returner;
	}// end main
}
