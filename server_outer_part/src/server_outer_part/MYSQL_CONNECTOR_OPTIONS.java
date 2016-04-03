package server_outer_part;

import java.io.File;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;

public class MYSQL_CONNECTOR_OPTIONS {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://" + Person_splitter.database + "/server_parts";

	// Database credentials
	static final String USER = "server_parts";
	static final String PASS = "server_parts";

	public static String get_default_world() {
		Connection conn = null;
		Statement stmt = null;
		int internal = 0;
		int id = 0;
		String name = null;
		try {

			// JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			// Executing query
			stmt = conn.createStatement();
			String i = null;
			try {
				Socket s = new Socket("192.168.0.1", 80);
				i = s.getLocalAddress().getHostAddress();
				s.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
			String sql;
			ResultSet ider = stmt.executeQuery("SELECT default_world,id FROM server_location WHERE adress ='" + i
					+ "' AND port =" + Person_splitter.server.getPort());
			while (ider.next()) {
				internal = ider.getInt("default_world");
				id = ider.getInt("id");
			}
			sql = "SELECT * FROM worlds WHERE id=" + id + " AND server_internal_number =" + internal;
			System.out.print(sql);
			ResultSet rs = stmt.executeQuery(sql);
			sql = null;

			// Extract data from result set
			while (rs.next()) {
				// Retrieve by column name
				name = rs.getString("world_name");
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
		return internal + name;

	}

	public static int get_id() {
		Connection conn = null;
		Statement stmt = null;
		int id = 0;
		try {

			// JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			// Executing query
			stmt = conn.createStatement();
			String i = null;
			try {
				Socket s = new Socket("192.168.0.1", 80);
				i = s.getLocalAddress().getHostAddress();
				s.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
			ResultSet ider = stmt.executeQuery("SELECT id FROM server_location WHERE adress ='" + i + "' AND port ="
					+ Person_splitter.server.getPort());
			while (ider.next()) {
				id = ider.getInt(1);
			}
			// Close Connection
			ider.close();
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

	public static List<String> get_worlds() {
		List<String> returner = null;
		Boolean loop = false;
		do {
			loop = false;
			returner = worlds();
			long actual_unix = System.currentTimeMillis() / 1000L;
			try {
				Connection conn = null;
				java.sql.Statement stmt = null;
				// JDBC driver
				Class.forName("com.mysql.jdbc.Driver");

				// Open connection
				conn = DriverManager.getConnection(DB_URL, USER, PASS);

				// Executing query
				stmt = conn.createStatement();
				for (int i = 2; i < returner.size(); i += 6) {
					if (Long.parseLong(returner.get(i)) < actual_unix) {// World
																		// is
																		// too
																		// old
						loop = true;
						if (Person_splitter.debug) {
							System.out.println("World with the internal_id " + returner.get(i + 4)
									+ " was removed from the Server!");
						}
						Person_splitter.server.getWorld(returner.get(i + 4) + returner.get(i - 1)).save();
						List<Player> players = Person_splitter.server
								.getWorld(returner.get(i + 4) + returner.get(i - 1)).getPlayers();
						for (int ii = 0; i <= players.size(); i++) {
							players.get(ii).kickPlayer(
									"Deine Welt ist abgelaufen, bitte lade sie dir auf www.ownworld.eu herunter.");
						}
						if (Person_splitter.debug) {
							Person_splitter.logger.info("If World is defaultworld change it!");
						}
						String ip = null;
						try {
							Socket s = new Socket("192.168.0.1", 80);
							ip = s.getLocalAddress().getHostAddress();
							s.close();

						} catch (Exception e) {
							e.printStackTrace();
						}

						ResultSet rss = stmt.executeQuery(
								"SELECT default_world FROM server_location WHERE id=" + Person_splitter.server_id);
						int default_world = 0;
						while (rss.next()) {
							default_world = rss.getInt(1);
						}
						if (default_world == 0) {
							if (Person_splitter.debug) {

								Person_splitter.logger.info("This server is configured to contain standalone worlds!");
							}
						} else if (default_world == Integer.parseInt(returner.get(i + 4))) {
							if (Person_splitter.debug) {
								Person_splitter.logger.info(
										"The Deleted world was the default world for this Server. Auto-replacing the world");
							}
							ResultSet rsss = stmt.executeQuery(
									"SELECT MIN(server_internal_number) WHERE server_id=" + Person_splitter.server_id
											+ " AND server_internal_number NOT LIKE " + returner.get(i + 4));
							int new_internal = 0;
							while (rsss.next()) {
								new_internal = rsss.getInt(1);
							}
							stmt.execute("UPDATE server_location SET default_world=" + new_internal
									+ " WHERE server_id=" + Person_splitter.server_id);
						}
						Person_splitter.server.unloadWorld(returner.get(i + 4) + returner.get(i - 1), true);
						File world = new File(new File(System.getProperty("user.dir")).getAbsolutePath() + "/"
								+ returner.get(i + 4) + returner.get(i - 1));
						File world_save = new File(
								new File(System.getProperty("user.dir")).getParentFile().getAbsolutePath() + "/worlds");
						if (!world_save.exists()) {
							world_save.mkdirs();
						}
						if (new File(world_save.getAbsolutePath() + "/" + world.getName()).exists()) {
							FileUtils.deleteDirectory(new File(world_save.getAbsolutePath() + "/" + world.getName()));
						}
						FileUtils.moveDirectory(world, new File(world_save.getAbsolutePath() + "/" + world.getName()));
						stmt.execute("UPDATE worlds SET server_internal_number = 0 , server_id =0, server_ip='" + ip
								+ "', location ='" + world_save.getAbsolutePath() + "/" + world.getName()
								+ "' WHERE server_id=" + Person_splitter.server_id + " AND server_internal_number="
								+ returner.get(i + 4));
					}
				}
			} catch (Exception e) {
				loop = false;
				e.printStackTrace();
			} // End try
		} while (loop);

		return returner;

	}

	private static List<String> worlds() {

		// This is a List of all the Data needed for generating a world
		// it contains the information in the following way:
		// 0.Boolean if almost one world exists
		// 1. name
		// 2.internal number
		// 3.expiry date(Unix Timestamp)
		// 4.owner(Trimmed UUID)
		// 5.gamemode(int 0,1,2)
		// 6.world-Type(At the moment not aviable,
		// future:normal,flat,nether,end)
		//
		//
		//

		List<String> returner = new ArrayList<String>();
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
			sql = "SELECT * FROM worlds WHERE server_id=" + Person_splitter.server_id
					+ " ORDER BY server_internal_number";
			if (Person_splitter.debug) {
				Person_splitter.logger.info(sql);
			}
			ResultSet rs = stmt.executeQuery(sql);
			sql = null;
			returner.add("false");
			// Extract data from result set
			while (rs.next()) {
				// Retrieve by column name
				returner.set(0, "true");
				returner.add(rs.getString("world_name"));
				returner.add(rs.getString("expires"));
				returner.add(rs.getString("owner"));
				returner.add(rs.getString("gamemode"));
				returner.add(rs.getString("world_type"));
				returner.add(rs.getString("server_internal_number"));
			} // Got the returner
			try {// For debug print the list
				if (Person_splitter.debug) {
					for (int i = 0; i < returner.size(); i++) {
						Person_splitter.logger.info(returner.get(i));
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
		return returner;
	}// end main

	public static String get_spawn_world(Player joiner) {
		Connection conn = null;
		java.sql.Statement stmt = null;
		String returner=null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			// Open connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt=conn.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT default_world FROM server_location WHERE id ="+Person_splitter.server_id);
			int default_world=0;
			while(rs.next()){
				default_world=rs.getInt(1);
			}
			if(default_world==0){
				rs=null;
				rs=stmt.executeQuery("SELECT * FROM player_transmission WHERE username='"+joiner.getDisplayName()+"'");
				int internal=0;
				while(rs.next()){
					internal=rs.getInt("target_world");
				}
				String name=null;
				rs=null;
				rs=stmt.executeQuery("SELECT world_name FROM worlds WHERE server_internal_number ="+internal+" AND server_id ="+Person_splitter.server_id);
				while(rs.next()){
					name=rs.getString(1);
				}
				returner=internal+name;
			}else{
				returner=get_default_world();
			}
			conn.close();
			stmt.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returner;
	}
}
