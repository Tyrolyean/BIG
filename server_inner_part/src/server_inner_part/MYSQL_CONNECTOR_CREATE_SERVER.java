package server_inner_part;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import com.mysql.jdbc.Statement;

import org.apache.commons.net.ftp.*;

public class MYSQL_CONNECTOR_CREATE_SERVER {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://" + JoinLeave.mysql() + "/server_parts" + "?useSSL=true";

	// Database credentials
	static final String USER = "minecraft";
	static final String PASS = "minecraft";

	public static int get_new(String name, String type, String uuid) {
		// JDBC driver name and database URL

		Connection conn = null;
		Statement stmt = null;
		try {
			// JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			// Executing query
			stmt = (Statement) conn.createStatement();
			String sql;
			sql = "SELECT MAX(id) FROM server";
			ResultSet rs = stmt.executeQuery(sql);
			sql = null;
			int id = 0;
			// Extract data from result set
			while (rs.next()) {
				// Retrieve by column name
				id = rs.getInt(1);

			}
			rs = null;
			Random rand = new Random();
			int randie = rand.nextInt(id);// get a new Server-id, where the
											// Spigot should be created
			randie++;
			sql = "SELECT * FROM server WHERE id=" + randie;
			rs = stmt.executeQuery(sql);
			sql = null;
			String adress = null;
			String password = null;
			String username = null;
			String direction = null;
			System.out.println("Connecting to Server with the id " + randie);
			while (rs.next()) {// retrieve necessary information for the
								// FTP-CONNECTION
				// Retrieve by column name
				adress = rs.getString("ip");
				password = rs.getString("password");
				username = rs.getString("username");
				direction = rs.getString("direction");

			}
			// Connect on base of the reserved
			FTPClient server = new FTPClient();
			if (JoinLeave.debug()) {
				System.out.println("Created a new FTP_Client (YESSSS)");
			}
			try {
				server.connect(adress);

				server.setFileType(FTP.BINARY_FILE_TYPE);
				server.setFileTransferMode(FTP.BINARY_FILE_TYPE);
				if (JoinLeave.debug()) {// print Server state
					System.out.println(server.getStatus());
				}
				server.login(username, password);
				server.setFileType(FTP.BINARY_FILE_TYPE);
				server.setFileTransferMode(FTP.BINARY_FILE_TYPE);

				if (JoinLeave.debug()) {// Print server-state
					System.out.println(server.getStatus());
				}

				// Get information for creating a new Server
				int port = 0;
				int sid = 0;

				rs = null;
				sql = "SELECT MAX(id) FROM server_location";
				rs = stmt.executeQuery(sql);
				sql = null;
				while (rs.next()) {
					sid = rs.getInt(1);

				}
				rs = null;
				sql = "SELECT MAX(port) FROM server_location";
				rs = stmt.executeQuery(sql);
				sql = null;
				while (rs.next()) {
					port = rs.getInt(1);

				}
				port++;
				sid++;
				long unixTime = System.currentTimeMillis() / 1000L;
				unixTime += 5184000;
				rs = null;
				rs = stmt.executeQuery("SELECT id FROM server_location WHERE owner ='" + uuid + "'");
				int server_id = 0;
				while (rs.next()) {
					id = rs.getInt(1);
				}
				if (server_id == 0) {//If no Server of the player exists then create a new one
					Create_Server.main(port, sid, username, type, direction, server, username, password);
					direction = direction + sid + "/";
					stmt.execute("INSERT INTO server_location (port,adress,owner,location,name) VALUES ('"
							+ port + "','" + adress + "','" + uuid + "','" + direction + "','" + name
							+ "') ");
					stmt.execute("INSERT INTO worlds (world_name,server_id,owner,expires,location,world_type,server_internal_number) VALUES('"+name+"',"+server_id+",'"+uuid+"',"+unixTime+",'"+adress+"','"+type+"',"+1+")");
					if (JoinLeave.debug()) {
						System.out.println("Created the Server and the 1st spawn world in the MYSQL-Database");
					}
				} else {//The player has already a Server. Adding a new world to the Server
					//GET the maximal Server internal number
					direction = direction + sid + "/";
					rs=null;
					int internal_number=1;
							rs=stmt.executeQuery("SELECT MAX(server_internal_number) FROM worlds WHERE server_id ="+server_id);
					while(rs.next()){
						internal_number=rs.getInt(1);
					}
					stmt.execute("INSERT INTO worlds (world_name,server_id,owner,expires,location,world_type,server_internal_number) VALUES('"+name+"',"+server_id+",'"+uuid+"',"+unixTime+",'"+adress+"','"+type+"',"+internal_number+")");
					if(JoinLeave.debug()){
						System.out.println("");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
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

		return 0;

	}
}