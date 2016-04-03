package server_outer_part;

import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Server;

import com.mysql.jdbc.Statement;

public class MYSQL_CONNECTOR_CHECK_WORLDS {

	// String Array with information abouth the Worlds
	//0= Server id
	// 1= Expire time of world 1
	// 2= Expire time of world 2
	// 3= Expire time of world 3
	// 4= Expire time of world 4
	// 5= type of world 1
	// 6= type of world 2
	// 7= type of world 3
	// 8= type of world 4
	//Types are at the moment only under constructing the world for the first time!
	// 9= name of world 1
	// 10= name of world 2
	// 11= name of world 3
	// 12= name of world 4
	// 13= future gamemode of world 1
	// 14=future gamemode of world 2
    // 15=future gamemode of world 3
	// 16=future gamemode of world 4
	
	
	
	public static String[] main(Server server) {
		int id = get_id(server);
		long unixTime = System.currentTimeMillis() / 1000L;
		String worlds[] = get_worlds(id);
		// Checking if Worlds are too old
		if(Person_splitter.debug){
			System.out.println("Delivered World information:");
		for(int i =0;i<16;i++){
			System.out.println(worlds[i]);
		}
		System.out.println("Information end!");
		}
		if (worlds[1] != null) {
			if (unixTime > Long.parseLong(worlds[1])) {
				if(Person_splitter.debug){
					System.out.println("Actual unix timestamp "+unixTime+" is bigger than the time from the world 1 : "+Long.parseLong(worlds[1]));
				}
				worlds[1] = delete_server(1, id);
			}
		}
		if (worlds[2] != null) {
			if (unixTime > Long.parseLong(worlds[2])) {
				if(Person_splitter.debug){
					System.out.println("Actual unix timestamp "+unixTime+" is bigger than the time from the world 2 : "+Long.parseLong(worlds[2]));
				}
				worlds[2] = delete_server(2, id);
			}
		}
		if (worlds[3] != null) {
			if (unixTime > Long.parseLong(worlds[3])) {
				if(Person_splitter.debug){
					System.out.println("Actual unix timestamp "+unixTime+" is bigger than the time from the world 3 : "+Long.parseLong(worlds[3]));
				}
				worlds[3] = delete_server(3, id);
			}
		}
		if (worlds[4] != null) {
			if (unixTime > Long.parseLong(worlds[4])) {
				if(Person_splitter.debug){
					System.out.println("Actual unix timestamp "+unixTime+" is bigger than the time from the world 1 : "+Long.parseLong(worlds[4]));
				}
				worlds[4] = delete_server(4, id);
			}
		}
		worlds[0]=Integer.toString(id);
		return worlds;

	}

	public static String delete_server(int internal, int id) {

		Connection conn = null;
		Statement stmt = null;
		final String DB_URL = "jdbc:mysql://" + Person_splitter.database + "/server_parts";

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
			sql = "UPDATE worlds SET  WHERE server_id = " + id + " AND server_internal_number = " + internal;
			if(Person_splitter.debug){
				System.out.println("Should delete world! : "+sql);
			}else{
			stmt.execute(sql);
			}
			sql = null;
			// Extract data from result set
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
		return null;
	}

	public static int get_id(Server server) {
		int id = 0;
		String i = null;
		   try {
	    	   Socket s = new Socket("192.168.0.1",80);
	    	   i=s.getLocalAddress().getHostAddress();
	    	   s.close();   
	       } catch (Exception e) {
			e.printStackTrace();
		}
		String ip = i;
		Connection conn = null;
		Statement stmt = null;
		final String DB_URL = "jdbc:mysql://" + Person_splitter.database + "/server_parts";

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
			sql = "SELECT id FROM server_location WHERE adress = '" + ip + "' AND port = '" + server.getPort() + "'";
			ResultSet rs = stmt.executeQuery(sql);
			sql = null;

			// Extract data from result set
			while (rs.next()) {
				// Retrieve by column name
				id = rs.getInt("id");
				if(Person_splitter.debug){
					System.out.println("Got world_id:"+id);
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

	}

	public static String[] get_worlds(int id) {
		String worlds[] = new String[16];
		Connection conn = null;
		Statement stmt = null;
		final String DB_URL = "jdbc:mysql://" + Person_splitter.database + "/server_parts";

		// Database credentials
		final String USER = "server_parts";
		final String PASS = "server_parts";
		try {
			// JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			// Executing query 1
			stmt = (Statement) conn.createStatement();
			String sql;
			sql = "SELECT * FROM worlds WHERE server_id = " + id + " AND server_internal_number = 1";
			ResultSet rs = stmt.executeQuery(sql);
			sql = null;

			// Extract data from result set
			while (rs.next()) {
				// Retrieve by column name
				try {
					if (Person_splitter.debug) {
						System.out.println("Looking for information for the world 1");
					}
					worlds[1] = rs.getString("expires");
					worlds[5] = rs.getString("world_type");
					worlds[9] = rs.getString("world_name");
					if (Person_splitter.debug) {
						System.out.println("Got info abouth  World 1: ");
						System.out.println(worlds[1]);
						System.out.println(worlds[5]);
						System.out.println(worlds[9]);
						System.out.println("End of information");
					}
				} catch (Exception e) {
					if (Person_splitter.debug) {
						System.out.println("An Error occured !" + e.getMessage());
					}
					worlds[1] = null;
					worlds[5] = null;
					worlds[9] = null;
				}

			}
			String sql1;
			sql1 = "SELECT * FROM worlds WHERE server_id = " + id + " AND server_internal_number =2";
			ResultSet rs1 = stmt.executeQuery(sql1);
			sql1 = null;

			// Extract data from result set
			while (rs1.next()) {
				try {
					if (Person_splitter.debug) {
						System.out.println("Looking for information for the world 2");
					}
					// Retrieve by column name
					worlds[2] = rs1.getString("expires");
					worlds[6] = rs1.getString("world_type");
					worlds[10] = rs1.getString("world_name");
					if (Person_splitter.debug) {
						if (Person_splitter.debug) {
							System.out.println("Got info abouth  World 2: ");
							System.out.println(worlds[2]);
							System.out.println(worlds[6]);
							System.out.println(worlds[10]);
							System.out.println("End of information");
						}
					}
				} catch (Exception e) {
					if (Person_splitter.debug) {
						System.out.println("An Error occured !" + e.getMessage());
					}
					worlds[2] = null;
					worlds[6] = null;
					worlds[10] = null;
				}
			}
			String sql2;
			sql2 = "SELECT * FROM worlds WHERE server_id = " + id + " AND server_internal_number =3";
			ResultSet rs2 = stmt.executeQuery(sql2);
			sql2 = null;

			// Extract data from result set
			while (rs2.next()) {
				try {
					if (Person_splitter.debug) {
						System.out.println("Looking for information for the world 3");
					}
					// Retrieve by column name
					worlds[3] = rs2.getString("expires");
					worlds[7] = rs2.getString("world_type");
					worlds[11] = rs2.getString("world_name");
					if (Person_splitter.debug) {
						System.out.println("Got info abouth  World 3: ");
						System.out.println(worlds[3]);
						System.out.println(worlds[7]);
						System.out.println(worlds[11]);
						System.out.println("End of information");
					}
				} catch (Exception e) {
					if (Person_splitter.debug) {
						System.out.println("An Error occured !" + e.getMessage());
					}
					worlds[3] = null;
					worlds[7] = null;
					worlds[11] = null;
				}

			}
			String sql3;
			sql3 = "SELECT * FROM worlds WHERE server_id = " + id + " AND server_internal_number = 4";
			if (Person_splitter.debug) {
				System.out.println(sql3);
			}
			ResultSet rs3 = stmt.executeQuery(sql3);
			sql3 = null;

			// Extract data from result set
			while (rs3.next()) {
				try {
					// Retrieve by column name
					if (Person_splitter.debug) {
						System.out.println("Looking for information for the world 4");
					}
					worlds[4] = rs3.getString("expires");
					worlds[8] = rs3.getString("world_type");
					worlds[12] = rs3.getString("world_name");
					if (Person_splitter.debug) {
						System.out.println("Got info abouth  World 4: ");
						System.out.println(worlds[4]);
						System.out.println(worlds[8]);
						System.out.println(worlds[12]);
						System.out.println("End of information");
					}
				} catch (Exception e) {
					if (Person_splitter.debug) {
						System.out.println("An Error occured !" + e.getMessage());
					}
					worlds[4] = null;
					worlds[8] = null;
					worlds[12] = null;
				}
			}
			// Close Connection

			rs3.close();
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

		return worlds;

	}

}
