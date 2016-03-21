package bungee_plugin;

import java.sql.Connection;
import java.sql.DriverManager;

import com.mysql.jdbc.Statement;


public class MYSQL_CONNECTOR_PLAYER_TRANSMISSION {
	public static boolean main(String playername, String world_name){
	try {
		// create the mysql database connection
		String myDriver = "org.gjt.mm.mysql.Driver";
		String mysqlUrl = "jdbc:mysql://"+big.mysql+"/server_parts";
		Class.forName(myDriver);
		Connection conn = DriverManager.getConnection(mysqlUrl, "server_parts", "server_parts");
		// create the mysql delete statement.
		String query = "INSERT INTO player_transmission (username,target_world) VALUES ('"+playername+"','"+world_name+"')";
		Statement stmt = (Statement) conn.createStatement();
		return stmt.execute(query);

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
