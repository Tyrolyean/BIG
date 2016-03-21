package bungee_plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

public class MYSQL_CONNECTOR_GET_WORLD_NAME {
	public static String[] main(int world_id) {
		String returner[] = new String[3];
		Connection conn = null;
		Statement stmt = null;
		final String DB_URL = "jdbc:mysql://" + big.mysql+ "/server_parts";

		// Database credentials
		final String USER = "server_parts";
		final String PASS = "server_parts";
		try {
			// JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			// Executing query
			stmt = (Statement) conn.createStatement();
			String sql;
			sql = "SELECT server_id, world_name, server_internal_number FROM worlds WHERE world_id = "+world_id;
			ResultSet rs = stmt.executeQuery(sql);
			sql = null;

			// Extract data from result set
			while (rs.next()) {
				// Retrieve by column name
				try{
				 returner[0] = rs.getString("server_internal_number");
				returner[1]= rs.getString("server_id");
				returner[2] = rs.getString("world_name");
				}
				catch (Exception e){
					if(big.debug){
						System.out.println("Could not find player's world!");
					}
					returner[0] = null;
					returner[1]= null;
					returner[2] = null;
				}
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
	}
}
