package server_outer_part;

import java.sql.*;

public class MYSQL_CONNECTOR_LOGIN {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://"+Person_splitter.database+"/acounts";

	// Database credentials
	static final String USER = "minecraft";
	static final String PASS = "minecraft";

	public static int main(String uuid) {
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
			sql = "SELECT * FROM acounts WHERE uuid = '"+uuid+"'";
			ResultSet rs = stmt.executeQuery(sql);
			if(Person_splitter.debug){
				Person_splitter.logger.info("Getting Server_id for Player with uuid "+uuid+" got "+rs.getFetchSize());
			}
			sql =null;

			// Extract data from result set
			while (rs.next()) {
				// Retrieve by column name
				id = rs.getInt("id");
				if(Person_splitter.debug){
					Person_splitter.logger.info("Got id "+id);
				}
				if(!rs.getBoolean("activated")){
					id=-1;
				}
				if(Person_splitter.debug){
					Person_splitter.logger.info("Got id "+id);
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
		return id;
	}// end main
	
	public static int get_max_players(String world_name){
		Connection conn = null;
		Statement stmt = null;
		int maxplayers=0;
		try {
			// JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open connection
			conn = DriverManager.getConnection("jdbc:mysql://"+Person_splitter.database+"/server_parts", USER, PASS);

			// Executing query
			stmt = conn.createStatement();
		ResultSet rs =stmt.executeQuery("SELECT * FROM worlds WHERE server_id="+Person_splitter.server_id);
		
		while(rs.next()){
			if(world_name==rs.getInt("server_internal_number")+rs.getString("world_name")){
				maxplayers=rs.getInt("maxplayers");
			}
			
		}
		stmt.close();
		conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return maxplayers;
	}
}
