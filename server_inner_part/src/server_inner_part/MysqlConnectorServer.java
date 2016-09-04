package server_inner_part;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import com.mysql.jdbc.Statement;

import org.apache.commons.net.ftp.*;
import org.bukkit.entity.Player;

public class MysqlConnectorServer {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://" + JoinLeave.mysql + "/server_parts" + "?useSSL=true";

	// Database credentials
	static final String USER = "minecraft";
	static final String PASS = "minecraft";

	@SuppressWarnings("resource")
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
					server_id = rs.getInt(1);
				}
				if (JoinLeave.debug()) {
					System.out.println("Retrievet Server_id from Player with: " + server_id);
				}
				if (server_id == 0) {// If no Server of the player exists then
										// create a new one
					if (JoinLeave.debug()) {
						System.out.println("Creating a new Server for player with uuid " + uuid);
					}
					CreateServer.main(port, sid, username, type, direction, server, username, password);
					direction = direction + sid + "/";
					stmt.execute("INSERT INTO server_location (RAM,port,adress,owner,location,name) VALUES (512,'"
							+ port + "','" + adress + "','" + uuid + "','" + direction + "','" + name + "') ");
					stmt.execute(
							"INSERT INTO worlds (server_ip,world_name,server_id,owner,expires,location,world_type,server_internal_number) VALUES('"
									+ adress + "','" + name + "'," + server_id + ",'" + uuid + "'," + unixTime + ",'"
									+ direction + 1 + name + "','" + type + "'," + 1 + ")");
					rs=stmt.executeQuery("SELECT * FROM worlds WHERE server_internal_number=1 AND server_id="+server_id);
					int world_id=0;
					while(rs.next()){
					world_id=rs.getInt("world_id");	
					}
					stmt.execute("INSERT INTO options (world_id) VALUES("+world_id+")");
					if(JoinLeave.debug()){
						System.out.println("Created options for world "+world_id);
					}
					
					if (JoinLeave.debug()) {
						System.out.println("Created the Server and the 1st spawn world in the MYSQL-Database");
					}
					
					
					
					
					
				} else {// The player has already a Server. Adding a new world
						// to the Server
					// GET the maximal Server internal number
					direction = direction + sid + "/";
					rs = null;
					int internal_number = 1;
					String ip = null;
					stmt.execute("UPDATE server_location SET RAM=RAM+256 WHERE id=" + server_id);
					rs = stmt.executeQuery(
							"SELECT MAX(server_internal_number) FROM worlds WHERE server_id =" + server_id);
					while (rs.next()) {
						internal_number = rs.getInt(1);
					}
					rs = null;
					rs = stmt.executeQuery("SELECT adress FROM server_location WHERE id =" + server_id);
					while (rs.next()) {
						ip = rs.getString(1);
					}
					stmt.execute(
							"INSERT INTO worlds (server_ip,world_name,server_id,owner,expires,location,world_type,server_internal_number) VALUES('"
									+ ip + "','" + name + "'," + server_id + ",'" + uuid + "'," + unixTime + ",'"
									+ direction + internal_number + name + "','" + type + "'," + (internal_number+1) + ")");
					rs=null;
					rs=stmt.executeQuery("SELECT * FROM worlds WHERE server_internal_number="+(internal_number+1)+" AND server_id="+server_id);
					int world_id=0;
					while(rs.next()){
					world_id=rs.getInt("world_id");	
					}
					stmt.execute("INSERT INTO options (world_id) VALUES("+world_id+")");
					if(JoinLeave.debug()){
						System.out.println("Created options for world "+world_id);
					}
					
					
					
					if (JoinLeave.debug()) {
						System.out.println("Added world to existing Server with Server_id " + server_id
								+ " and Server internal number " + internal_number);
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

	public static void upgrade_RAM(int RAM,String uuid) {
		Connection conn = null;
		java.sql.Statement stmt = null;
		try {

			// JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			// Executing query
			stmt = conn.createStatement();
			stmt.execute("UPDATE server_location SET RAM=RAM+"+RAM+" WHERE owner ='"+uuid+"'");

			// Close Connection
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
	}
	
	
	public static HashMap<Integer, String[]> get_servers_allowed(Player player){
		
		HashMap<Integer, String[]> worlds=new HashMap<Integer, String[]>();
		
		//okay to explain  what is returned:
		
		//1. the integer stands for the World Id
		//2.the String Array contains Information about the Server:
		//	1. The erver Name
		//	2. The Serverowner
		
		//More Information will come in the Future. For example an  MOTD.
		
		Connection conn = null;
		java.sql.Statement stmt = null;
		try {

			// JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			// Executing query
			stmt = conn.createStatement();
			ResultSet rs =stmt.executeQuery("SELECT * FROM permissions WHERE uuid='"+player.getUniqueId().toString().replace('-', ' ').replaceAll("\\s", "")+"'");
			ArrayList<Integer> world_ids=new ArrayList<Integer>();
			while (rs.next()){
				
				world_ids.add(rs.getInt("world_id"));
				
			}
			rs.close();
			
			
			for(int world_id:world_ids){
				
				rs=stmt.executeQuery("SELECT * FROM worlds WHERE world_id="+world_id);
				while(rs.next()){
					
					String[] data ={rs.getString("world_name"),rs.getString("owner")};
					worlds.put(world_id, data);
				}
				rs.close();
			}
			rs.close();
			stmt.close();
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return worlds;
	}
	
	
	
}