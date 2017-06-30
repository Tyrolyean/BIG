package server_outer_part;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;

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

				// At the very first remove all the old permissions:
				for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
					attachment.unsetPermission(perm.getPermission());
				}

				// Then set the default denys:

				attachment.setPermission("bukkit.command.plugins", false);
				attachment.setPermission("bukkit.command.reload", false);
				attachment.setPermission("bukkit.command.debug", false);
				attachment.setPermission("minecraft.command.debug", false);
				attachment.setPermission("bukkit.command.defaultgamemode", false);
				attachment.setPermission("minecraft.command.defaultgamemode", false);
				attachment.setPermission("bukkit.command.difficulty", false);
				attachment.setPermission("bukkit.command.help", false);
				attachment.setPermission("minecraft.command.help", false);
				attachment.setPermission("minecraft.command.difficulty", false);
				attachment.setPermission("bukkit.command.difficulty", false);
				String sql ="SELECT * FROM server_permissions WHERE world_id=" + world_id(player.getWorld()) + " AND uuid='"
						+ player.getUniqueId().toString().replace('-', ' ').replaceAll("\\s", "") + "'";
				if(Person_splitter.debug){
					Person_splitter.logger.log(Level.INFO, "Getting perms with "+sql);
					
				}
				ResultSet rs = stmt.executeQuery(sql);
				while (rs.next()) {
					add_Perm(attachment, rs.getString("permission"), rs.getBoolean("value"));
					if(Person_splitter.debug){
						Person_splitter.logger.log(Level.INFO,"Ading raw_perm "+rs.getString("permission")+" with Value "+rs.getBoolean("value"));
					}
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
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM worlds");
			while (rs.next()) {
				if(Person_splitter.debug){
					Person_splitter.logger.log(Level.INFO, "IF "+(rs.getInt("server_internal_number") + rs.getString("world_name"))+" =="+world.getName()+" world_id="+rs.getInt("world_id"));
				}
				if ((rs.getInt("server_internal_number") + rs.getString("world_name")).equalsIgnoreCase( world.getName())) {
					
					world_id = rs.getInt("world_id");
					if(Person_splitter.debug){
						Person_splitter.logger.log(Level.INFO, "Got world_id "+rs.getInt("world_id"));
					}
				}
			}
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return world_id;
	}

	private static void add_Perm(PermissionAttachment pa, String permission_raw, Boolean value) {

		switch (permission_raw) {

		case "gamemode":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "achievement":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "version":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "clear":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "effect":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "enchant":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "gamerule":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "give":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "kick":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "kill":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "me":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "playsound":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "say":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "scoreboard":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "seed":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "setblock":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "summon":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "tell":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "tellraw":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "testfor":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "testforblock":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "time":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "toggledownfall":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "teleport":
			pa.setPermission("bukkit.command.teleport", value);
			pa.setPermission("minecraft.command.tp", value);
			break;
		case "weather":
			pa.setPermission("bukkit.command." + permission_raw, value);
			pa.setPermission("minecraft.command." + permission_raw, value);
			break;
		case "*":
			for (String perm : all_perms)
				pa.setPermission(perm, value);
			break;

		}

	}

	private static String[] all_perms = { "minecraft.command.gamemode", "minecraft.command.archievment",
			"minecraft.command.version", "minecraft.command.clear", "minecraft.command.effect",
			"minecraft.command.enchant", "minecraft.command.gamerule", "minecraft.command.give",
			"minecraft.command.kick", "minecraft.command.kill", "minecraft.command.me", "minecraft.command.playsound",
			"minecraft.command.say", "minecraft.command.scoreboard", "minecraft.command.seed",
			"minecraft.command.setblock", "minecraft.command.summon", "minecraft.command.tell",
			"minecraft.command.tellraw", "minecraft.command.testfor", "minecraft.command.testforblock",
			"minecraft.command.time", "minecraft.command.toggledownfall", "minecraft.command.tp",
			"minecraft.command.weather", "bukkit.command.gamemode", "bukkit.command.archievment",
			"bukkit.command.version", "bukkit.command.clear", "bukkit.command.effect", "bukkit.command.enchant",
			"bukkit.command.gamerule", "bukkit.command.give", "bukkit.command.kick", "bukkit.command.kill",
			"bukkit.command.me", "bukkit.command.playsound", "bukkit.command.say", "bukkit.command.scoreboard",
			"bukkit.command.seed", "bukkit.command.setblock", "bukkit.command.summon", "bukkit.command.tell",
			"bukkit.command.tellraw", "bukkit.command.testfor", "bukkit.command.testforblock", "bukkit.command.time",
			"bukkit.command.toggledownfall", "bukkit.command.teleport", "bukkit.command.weather" };

}
