package server_outer_part;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;



import com.mysql.jdbc.Statement;

public class MYSQL_CONNECTOR_REGISTER_SERVER{
	String main(String DATA[]){
		//If statements
		boolean exists = check(DATA);
		if (exists==true){
			return "Server was set to online-state: " + set_online(DATA);
		}
		else{
			return "Server was created in list: " + create(DATA);
		}
	}
	
	static Boolean check(String input[]){
		int id =0;
		Connection conn = null;
		Statement stmt = null;
		final String DB_URL = "jdbc:mysql://"+Person_splitter.database()+"/server_parts";
		System.out.println(DB_URL);
		// Database credentials
		final String USER = "server_parts";
		final String PASS = "server_parts";
		try {
			// JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connection created!");
			// Executing query
			stmt = (Statement) conn.createStatement();
			String sql;
			sql = "SELECT id FROM server_location WHERE adress = '"+input[0]+"' AND port = '"+input[1]+"'";
			System.out.print(sql+" bei location ");
			ResultSet rs = stmt.executeQuery(sql);
			sql =null;

			// Extract data from result set
			while (rs.next()) {
				// Retrieve by column name
				id = rs.getInt("id");
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
		if (id ==0){
			return false;
		}
		else{
			return true;
		}
		
	}
	static Boolean set_online(String input[]){
		java.sql.Connection con = null;
		String url = "jdbc:mysql://"+Person_splitter.database()+"/server_parts";
		String user = "server_parts";
		String password = "server_parts";

		try {
		     con = DriverManager.getConnection(url, user, password);
		     Statement st = (Statement) con.createStatement(); 
		     st.executeUpdate("UPDATE server_location SET online = 1 WHERE adress ='"+input[0]+"' AND port = '"+input[1]+"'");

		     con.close();
		     return true; 
		}

		catch (SQLException ex) {
			System.out.println("Exception occured!: \n"+ex);
			return false;
		 } 
	}
	static Boolean create(String input[]){
		
		java.sql.Connection con = null;
		String url = "jdbc:mysql://"+Person_splitter.database()+"/server_parts";
		String user = "server_parts";
		String password = "server_parts";

		try {
		     con = DriverManager.getConnection(url, user, password);
		     Statement st = (Statement) con.createStatement(); 
		     st.executeUpdate("INSERT INTO server_location (adress,port)" + "VALUES ('"+input[0]+"','"+input[1]+"')");

		     con.close();
		     return true; 
		}

		catch (SQLException ex) {
			System.out.println("Exception occured!: \n"+ex);
			return false;
		 } 
		
		
	}
	
}
