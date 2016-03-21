package server_outer_part;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Server;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.io.Files;
import com.mysql.jdbc.Statement;

public class LOOP {

	public void main(Server server, Person_splitter person_splitter) {
		// Called first when the Programm starts!
		// Explanation:
		// Run Task Later is used in order to Run the world-Creation
		// Syncroniously because else the Server isn't creaing the world
		// Creating every world hapens on the Server-Start. Every 30 Seconds the
		// worlds are updated with an mysql-Databse
		// In an earlyer Version of this Plugin MultiVerse Core was used so if
		// you find something from Multiverse please delete it
		//
		//
		//
		//
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// All things put in here are checked every 30 Seconds!
				System.out.println("Checking for new Worlds and deleting old ones ");

				String worlds[] = MYSQL_CONNECTOR_CHECK_WORLDS.main(server);
				if (Person_splitter.debug) {
					System.out.println("Got information from Database about worlds!");
				}
				try {
					// Create Worlds
					if (worlds[1] != null && server.getWorld(1 + worlds[9]) == null) {
						// Create internal world 1
						File world = new File(System.getProperty("user.dir") + "/1" + worlds[9]);
						if (world.exists()) {
							server.createWorld(new WorldCreator(1 + worlds[9]));
						} else {
							WorldCreator c = new WorldCreator(1 + worlds[9]);
							if (worlds[5].equals("normal"))
								c.type(WorldType.NORMAL);
							else if (worlds[5].equals("flat")) {
								c.type(WorldType.FLAT);
							}
							// Create the world async
							server.getScheduler().runTaskLater(person_splitter, new Runnable() {
								@Override
								public void run() {
									if (Person_splitter.debug) {
										System.out.println("Creating world " + c.toString());
										System.out.println(server.createWorld(c));
										System.out.println("Done!");
									} else {
										server.createWorld(c);
									}
								}
							}, 9);

							// End of creation
						}
					}
					if (worlds[2] != null && server.getWorld(2 + worlds[10]) == null) {
						// Create internal world 2
						File world = new File(System.getProperty("user.dir") + "/2" + worlds[10]);
						if (world.exists()) {
							server.createWorld(new WorldCreator(2 + worlds[10]));
						} else {
							WorldCreator c = new WorldCreator(2 + worlds[10]);
							// Setting the world type of the world
							if (worlds[5].equals("normal"))
								c.type(WorldType.NORMAL);
							else if (worlds[5].equals("flat")) {
								c.type(WorldType.FLAT);
							}
							// Create the world async
							server.getScheduler().runTaskLater(person_splitter, new Runnable() {
								@Override
								public void run() {
									if (Person_splitter.debug) {
										System.out.println("Creating world " + c.toString());
										System.out.println(server.createWorld(c));
										System.out.println("Done!");
									} else {
										server.createWorld(c);
									}
								}

							}, 9);
						}

						// End of creation
					}
					if (worlds[3] != null && server.getWorld(3 + worlds[11]) == null) {
						// Create internal world 1
						File world = new File(System.getProperty("user.dir") + "/3" + worlds[11]);
						if (world.exists()) {
							server.createWorld(new WorldCreator(3 + worlds[11]));
						} else {
							WorldCreator c = new WorldCreator(3 + worlds[11]);
							if (worlds[5].equals("normal"))
								c.type(WorldType.NORMAL);
							else if (worlds[5].equals("flat")) {
								c.type(WorldType.FLAT);
							}
							// Create the world async
							server.getScheduler().runTaskLater(person_splitter, new Runnable() {
								@Override
								public void run() {
									if (Person_splitter.debug) {
										System.out.println("Creating world " + c.toString());
										System.out.println(server.createWorld(c));
										System.out.println("Done!");
									} else {
										server.createWorld(c);
									}
								}
							}, 9);
						}
						// End of creation
					}
					if (worlds[4] != null && server.getWorld(4 + worlds[12]) == null) {
						// Create internal world 1
						File world = new File(System.getProperty("user.dir") + "/4" + worlds[12]);
						if (world.exists()) {
							server.createWorld(new WorldCreator(4 + worlds[12]));
						} else {
							WorldCreator c = new WorldCreator(4 + worlds[12]);
							if (worlds[5].equals("normal"))
								c.type(WorldType.NORMAL);
							else if (worlds[5].equals("flat")) {
								c.type(WorldType.FLAT);
							}
							// Create the world async
							server.getScheduler().runTaskLater(person_splitter, new Runnable() {
								@Override
								public void run() {
									if (Person_splitter.debug) {
										System.out.println("Creating world " + c.toString());
										System.out.println(server.createWorld(c));
										System.out.println("Done!");
									} else {
										server.createWorld(c);
									}
								}
							}, 9);
						}
						// End of creation
					}
					// Delete Worlds in MYSQL_COnnector for checking worlds

				} catch (Exception e) {
					System.out.println("An error occured on creating or importing a world!");
					System.out.println(e.getMessage());
				}

				if (worlds[1] == null && server.getWorld(1 + worlds[9]) != null) {
					server.getWorld(1 + worlds[9]).save();
					List<Player> player = server.getWorld(1 + worlds[9]).getPlayers();
					for (int i = 0; i < player.size(); i++) {
						player.get(i).kickPlayer(
								"Your world has been removed from the Server! You can download it from our homepage!");
						;
					}
					server.unloadWorld(1 + worlds[10], true);
					// move World to folder for download
					File world = new File(System.getProperty("user.dir") + "/" + 1 + worlds[9]);
					File destination = new File(
							new File(System.getProperty("user.dir")).getParent().toString() + "/worlds/" + worlds[0]);
					if (!destination.exists()) {
						destination.mkdir();
					}
					try {
						Files.copy(world, new File(destination.toString() + 1 + worlds[9]));
					} catch (IOException e) {
						e.printStackTrace();
					}
					world.delete();
					update_directory(worlds, 1, destination.toString() + "/" + 1 + worlds[9]);

				} else if (worlds[2] == null && server.getWorld(2 + worlds[10]) != null) {
					server.getWorld(2 + worlds[10]).save();
					List<Player> player = server.getWorld(2 + worlds[10]).getPlayers();
					for (int i = 0; i < player.size(); i++) {
						player.get(i).kickPlayer(
								"Your world has been removed from the Server! You can download it from our homepage!");
						;
					}
					server.unloadWorld(2 + worlds[10], true);
					// move World to folder for download
					File world = new File(System.getProperty("user.dir") + "/" + 2 + worlds[10]);
					File destination = new File(
							new File(System.getProperty("user.dir")).getParent().toString() + "/worlds/" + worlds[0]);
					if (!destination.exists()) {
						destination.mkdir();
					}
					try {
						Files.copy(world, new File(destination.toString() + "/" + 2 + worlds[10]));
					} catch (IOException e) {
						e.printStackTrace();
					}
					world.delete();
					update_directory(worlds, 2, destination.toString() + "/" + 2 + worlds[10]);

				} else if (worlds[3] == null && server.getWorld(3 + worlds[11]) != null) {
					server.getWorld(3 + worlds[11]).save();
					List<Player> player = server.getWorld(1 + worlds[11]).getPlayers();
					for (int i = 0; i < player.size(); i++) {
						player.get(i).kickPlayer(
								"Your world has been removed from the Server! You can download it from our homepage!");
						;
					}
					server.unloadWorld(3 + worlds[11], true);

					// move World to folder for download
					File world = new File(System.getProperty("user.dir") + "/" + 3 + worlds[11]);
					File destination = new File(
							new File(System.getProperty("user.dir")).getParent().toString() + "/worlds/" + worlds[0]);
					if (!destination.exists()) {
						destination.mkdir();
					}
					try {
						Files.copy(world, new File(destination.toString() + 3 + worlds[11]));
					} catch (IOException e) {
						e.printStackTrace();
					}
					world.delete();
					update_directory(worlds, 3, destination.toString() + "/" + 3 + worlds[11]);

				} else if (worlds[4] == null && server.getWorld(4 + worlds[12]) != null) {
					server.getWorld(4 + worlds[12]).save();
					List<Player> player = server.getWorld(4 + worlds[12]).getPlayers();
					for (int i = 0; i < player.size(); i++) {
						player.get(i).kickPlayer(
								"Your world has been removed from the Server! You can download it from our homepage!");
						;
					}
					server.unloadWorld(4 + worlds[12], true);

					// move World to folder for download
					File world = new File(System.getProperty("user.dir") + "/" + 4 + worlds[12]);
					File destination = new File(
							new File(System.getProperty("user.dir")).getParent().toString() + "/worlds/" + worlds[0]);
					if (!destination.exists()) {
						destination.mkdir();
					}
					try {
						Files.copy(world, new File(destination.toString() + 4 + worlds[12]));
					} catch (IOException e) {
						e.printStackTrace();
					}
					world.delete();
					update_directory(worlds, 4, destination.toString() + "/" + 4 + worlds[12]);
				}

				System.out.println("Did it!");
				// Here it stops to do this
			}
		}, 30000, 30000);

	}

	public BukkitRunnable createWorld(WorldCreator c, Server server) {
		try {
			server.createWorld(c);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Function to update the directory in the Server-List where the world is at
	// the moment
	private boolean update_directory(String worlds[], int internal, String directory) {
		Connection conn = null;
		Statement stmt = null;
		final String DB_URL = "jdbc:mysql://" + Person_splitter.database() + "/server_parts";

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
			sql = "UPDATE worlds SET loacation '" + directory + "', server_id =0 WHERE server_id =" + worlds[0]
					+ "AND server_internal_number =" + internal;
			stmt.execute(sql);
			sql = null;
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
				se2.printStackTrace();
				return false;
			} // fatal...
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
				return false;
			} // end finally try
		} // end try
		return true;
	}
}