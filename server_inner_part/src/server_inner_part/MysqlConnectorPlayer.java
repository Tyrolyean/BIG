package server_inner_part;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.bukkit.entity.Player;

public class MysqlConnectorPlayer {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://" + JoinLeave.mysql + "/acounts" + "?useSSL=true";

	// Database credentials
	static final String USER = "minecraft";
	static final String PASS = "minecraft";

	//
	public static int getRank(Player p) {
		int rank = 2;
		
		Connection conn = null;
		Statement stmt = null;
		try {
			// JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			// Executing query
			stmt = conn.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT * FROM acounts WHERE uuid='"+p.getUniqueId().toString().replace('-', ' ').replaceAll("\\s", "")+"'");
			while(rs.next()){
				rank=rs.getInt("rank");
			}
			rs.close();
			stmt.close();
			conn.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return rank;

	}

}
