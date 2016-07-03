package server_outer_part;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class OTHER_THINGS {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://" + Person_splitter.database + "/server_parts";

	// Database credentials
	static final String USER = "server_parts";
	static final String PASS = "server_parts";

	

	/*
	 * public static boolean get_command_permissions(int world_id, Player
	 * player) { Connection conn = null; Statement stmt = null; Boolean
	 * permission = false; try { Class.forName("com.mysql.jdbc.Driver"); conn =
	 * DriverManager.getConnection(DB_URL, USER, PASS); stmt =
	 * conn.createStatement(); ResultSet rs = stmt.executeQuery("");
	 * 
	 * return permission; } catch (Exception e) { e.printStackTrace(); return
	 * false; }
	 * 
	 * }
	 */

	// In Order to set Permission for Commands you have to Set the
	// Minecraft AND the bukkit internal Permissions Like for the
	// /gamemode command you have to give the Player
	// bukit.command.gamemode
	// minecraft.command.gamemode
	//
	// I don't really know why that is how it is, but it is like it is :D

	public static void update_player_permissions() {
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();// Connection created
			// Now will get All permissons hich are defined as
			// default_permissions
			for (Player player : Person_splitter.server.getOnlinePlayers()) {
				
				Person_splitter.logger.info("Updating permissions for Player " + player.getDisplayName());

				PermissionAttachment attachment = Person_splitter.permissions.get(player.getUniqueId());
				
				//At the very first remove all the old permissions:
				for(PermissionAttachmentInfo perm:player.getEffectivePermissions()){
					attachment.unsetPermission(perm.getPermission());
				}
				
				//Then set the default denys:
				
				attachment.setPermission(Person_splitter.server.getPluginManager().getPermission("bukkit.command.plugins"), false);
				attachment.setPermission(Person_splitter.server.getPluginManager().getPermission("bukkit.command.reload "), false);
				attachment.setPermission(Person_splitter.server.getPluginManager().getPermission("bukkit.command.debug "), false);
				attachment.setPermission(Person_splitter.server.getPluginManager().getPermission("minecraft.command.debug "), false);
				attachment.setPermission(Person_splitter.server.getPluginManager().getPermission("bukkit.command.defaultgamemode"), false);
				attachment.setPermission(Person_splitter.server.getPluginManager().getPermission("minecraft.command.defaultgamemode"), false);
				attachment.setPermission(Person_splitter.server.getPluginManager().getPermission("bukkit.command.difficulty "), false);
				attachment.setPermission(Person_splitter.server.getPluginManager().getPermission("bukkit.command.help"), false);
				attachment.setPermission(Person_splitter.server.getPluginManager().getPermission("minecraft.command.help"), false);
				attachment.setPermission(Person_splitter.server.getPluginManager().getPermission("minecraft.command.difficulty "), false);
				attachment.setPermission(Person_splitter.server.getPluginManager().getPermission("minecraft.command.difficulty "), false);
				attachment.setPermission(Person_splitter.server.getPluginManager().getPermission("minecraft.command.difficulty "), false);
				attachment.setPermission(Person_splitter.server.getPluginManager().getPermission("minecraft.command.difficulty "), false);
				attachment.setPermission(Person_splitter.server.getPluginManager().getPermission("minecraft.command.difficulty "), false);
				attachment.setPermission(Person_splitter.server.getPluginManager().getPermission("minecraft.command.difficulty "), false);
				attachment.setPermission(Person_splitter.server.getPluginManager().getPermission("minecraft.command.difficulty "), false);

				
				
				
				
				ResultSet rs = stmt.executeQuery(
						"SELECT * FROM server_permissions WHERE world_id=" + world_id(player.getWorld()) + " AND uuid='"
								+ player.getUniqueId().toString().replace('-', ' ').replaceAll("\\s", "") + "'");
				while (rs.next()) {
					attachment.setPermission(rs.getString("permission"), rs.getBoolean("value"));
				}
				rs.close();
				if (Person_splitter.debug) {
					for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
						Person_splitter.logger.info("Permissions from Player " + player.getDisplayName() + " "
								+ permission.getPermission() + " " + permission.getValue());
					}

				}
				player.recalculatePermissions();
			}
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// And there the Objectivity begins XD.
	// Sorry that this is one of the first patrts that are Object oriented... :(
	public static int world_id(World world) {
		// returns Null if the world is not registered!
		int world_id = 0;
		Connection conn = null;
		Statement stmt = null;
		// Database credentials
		try {
			// JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			// Executing query 1
			stmt = (Statement) conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM worlds WHERE server_id=" + Person_splitter.server_id);
			while (rs.next()) {
				if ((rs.getInt("server_internal_number") + rs.getString("world_name")) == world.getName()) {
					world_id = rs.getInt("worrld_id");
				}
			}
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return world_id;
	}

}
