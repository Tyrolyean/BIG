package server_outer_part;

import java.sql.*;

public class MYSQL_CONNECTOR_LOGIN {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://"+Person_splitter.database+"/acounts";

	// Database credentials
	static final String USER = "minecraft";
	static final String PASS = "minecraft";

	public int main(String playername) {
		int id = 0;
		Connection conn = null;
		Statement stmt = null;
		try {
			// JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			// Executing query
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT id FROM acounts WHERE uuid = '"+playername+"'";
			System.out.print(sql);
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
		return id;
	}// end main
}
