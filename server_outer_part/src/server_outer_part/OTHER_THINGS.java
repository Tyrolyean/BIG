package server_outer_part;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

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

	public static int get_world_id_by_player(Player player) {
		Connection conn = null;
		Statement stmt = null;
		int world_id = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			String world_name = player.getWorld().getName();
			ResultSet rs = stmt.executeQuery("SELECT * FROM worlds WHERE server_id=" + Person_splitter.server_id);
			while (rs.next()) {
				if (rs.getInt("server_internal_number") + rs.getString("world_id") == world_name) {
					world_id = rs.getInt("world_id");
				}
			}
			return world_id;
		} catch (Exception e) {
			e.printStackTrace(System.out);
			return 0;
		}
	}

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

				ResultSet rs = stmt.executeQuery("SELECT * FROM permissions WHERE id=1 AND uuid='"
						+ player.getUniqueId().toString().replace('-', ' ').replaceAll("\\s", "") + "'");
				while (rs.next()) {
					attachment.setPermission(rs.getString("string"), true);
				}
				if (Person_splitter.debug) {
					for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
						Person_splitter.logger.info("Permissions from Player " + player.getDisplayName() + " "
								+ permission.getPermission() + " " + permission.getValue());
					}

					attachment.getPermissible().recalculatePermissions();
					player.recalculatePermissions();					
				}
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
