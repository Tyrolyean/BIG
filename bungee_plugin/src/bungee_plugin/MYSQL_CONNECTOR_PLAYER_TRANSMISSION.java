package bungee_plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import com.mysql.jdbc.Statement;

public class MYSQL_CONNECTOR_PLAYER_TRANSMISSION {
	public static boolean main(String playername, String world_id) {
		try {
			// create the mysql database connection
			String myDriver = "org.gjt.mm.mysql.Driver";
			String mysqlUrl = "jdbc:mysql://" + big.mysql + "/server_parts";
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(mysqlUrl, "server_parts", "server_parts");
			// create the mysql delete statement.
			Statement stmt = (Statement) conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM worlds WHERE world_id =" + world_id);
			int server_id = 0;
			int internal = 0;
			while (rs.next()) {
				server_id = rs.getInt("server_id");
				internal = rs.getInt("server_internal_number");
			}
			rs = null;
			rs = stmt.executeQuery("SELECT default_world FROM server_location WHERE id=" + server_id);
			int default_world = 0;
			while (rs.next()) {
				default_world = rs.getInt(1);
			}
			if(big.debug){
			System.out.println("Player "+playername+" got internal number "+default_world+" from world_id "+world_id);
			}
			if (default_world == 0) {
				stmt.execute("DELETE FROM player_transmission WHERE username ='"+playername+"'");
				String query = "INSERT INTO player_transmission (username,target_world) VALUES ('" + playername + "','"
						+ internal + "')";
				System.out.println(stmt.execute(query));
			}
			conn.close();
			stmt.close();

			// execute the preparedstatement

		} catch (

		Exception e)

		{
			System.out.println("Got an exception! ");
			System.out.println(e.getMessage());
		}
		return false;
	}
}
