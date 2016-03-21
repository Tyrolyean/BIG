package server_inner_part;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.mysql.jdbc.Statement;

public class set_inventory_server {

	public static void main(Player player, Server server){
		Connection conn = null;
		Statement stmt = null;
		final String DB_URL = "jdbc:mysql://" + JoinLeave.mysql() + "/server_parts";

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
			sql = "SELECT * FROM permissions WHERE uuid='"+player.getUniqueId().toString().replace('-', ' ').replaceAll("\\s", "")+"'";
			ResultSet rs = stmt.executeQuery(sql);
			sql = null;
			// Extract data from result set
			InventoryHolder owner = null;
			Inventory chest =server.createInventory(owner,64 , "Server-Switch");
			while (rs.next()) {
				ResultSet information=stmt.executeQuery("SELECT * FROM worlds WHERE world_id="+rs.getInt("world_id"));
				ItemStack servers=new ItemStack(Material.IRON_BLOCK, information.getInt("world_id"));				
				chest.setItem(rs.getRow(),servers);
				
				
				
				
				
				
				}
			player.openInventory(chest);
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
		
		
		
		
		
	}
	
	
	
}
