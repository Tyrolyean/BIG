package server_outer_part;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

public class MYSQL_CONNECTOR_TRANSMISSION {
	int target_world = 0;

	public String delete(Player player) {
		try {
			// create the mysql database connection
			String myDriver = "org.gjt.mm.mysql.Driver";
			String mysqlUrl = "jdbc:mysql://"+Person_splitter.database()+"/server_parts";
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(mysqlUrl, "server_parts", "server_parts");
			// create the mysql delete statement.
			ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM player_transmission WHERE username='"+player.getName()+"'");
			
			while(rs.next()){
				try{
					String world_name=rs.getString("world_name");
					
					if(world_name==null){
						return null;
					}else{
						return world_name;
					}
					
					
					
				}
				catch(Exception e){
					return null;
				}
			}
			
			
			
			
			
			String query_delete = "delete from player_transmission where username = '" + player.getDisplayName() + "'";
			conn.createStatement().execute(query_delete);

			conn.close();
		} catch (

		Exception e)

		{
			System.out.println("Got an exception! ");
			System.out.println(e.getMessage());
		}
		return null;
	}
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://"+Person_splitter.database()+"/server_parts";

	// Database credentials
	static final String USER = "server_parts";
	static final String PASS = "server_parts";

	public String select(Player player) {
		String target_world = null;
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
			sql = "SELECT target_world FROM player_transmission WHERE username = '"+player.getDisplayName()+"'";
			System.out.print(sql);
			ResultSet rs = stmt.executeQuery(sql);
			sql =null;

			// Extract data from result set
			while (rs.next()) {
				// Retrieve by column name
				target_world = rs.getString("target_world");
			}
			// Close Connection
			if (target_world != null){
				delete(player);
			}
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
		System.out.println("Player "+player+"joined world "+target_world);
		return target_world;
	}// end main
}
