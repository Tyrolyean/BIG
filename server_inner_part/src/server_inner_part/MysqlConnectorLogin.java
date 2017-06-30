package server_inner_part;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.*;

public class MysqlConnectorLogin {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://" + JoinLeave.mysql + "/acounts" + "?useSSL=true";

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
			sql = "SELECT id FROM acounts WHERE uuid = '" + playername + "'";
			System.out.print(sql);
			ResultSet rs = stmt.executeQuery(sql);
			sql = null;

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
		System.out.println("End of stream!");
		return id;
	}// end main

	public static String get_Activation(String uuid){//will be NULL if activated
		String code=null;
		Connection conn = null;
		Statement stmt = null;
		try {
			// JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt=conn.createStatement();
			
			ResultSet rs=stmt.executeQuery("SELECT * FROM acounts WHERE uuid='"+uuid+"'");
			Boolean activated = null;
			while(rs.next()){
				activated=rs.getBoolean("activated");
			}
			if(activated==null){
				activated=true;
			}
			if(activated){
				stmt.close();
				conn.close();
				return null;
			}
			
			
			rs=null;
			rs=stmt.executeQuery("SELECT * FROM activation WHERE uuid='"+uuid+"'");
			while(rs.next()){
				code=rs.getString("code");
			}
			if(code==null){
				code=nextSessionId().substring(0, 11);
				stmt.execute("INSERT INTO activation (uuid,code) VALUES('"+uuid+"','"+code+"')");
			}
			stmt.close();
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		if(JoinLeave.debug()){
			System.out.println("Code of player with uuid "+uuid+" is "+code);
		}
		return code;
	}
	//generate random Strings
	 private static SecureRandom random = new SecureRandom();

	  public static String nextSessionId() {
	    return new BigInteger(130, random).toString(130);
	  }
}
