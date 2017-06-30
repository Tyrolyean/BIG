package bungee_plugin;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.md_5.bungee.api.config.ServerInfo;

import com.mysql.jdbc.Statement;

import net.md_5.bungee.api.ProxyServer;

public class MYSQL_CONNECTOR_CHECK_SERVER {

	public static boolean get_Servers(ProxyServer proxyServer) {
		Connection conn = null;
		Statement stmt = null;
		final String DB_URL = "jdbc:mysql://" + big.mysql + "/server_parts";

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
			sql = "SELECT * FROM server_location";
			if (big.debug) {
				System.out.println(sql);
			}
			ResultSet rs = stmt.executeQuery(sql);
			sql = null;

			// Extract data from result set
			if (big.debug) {
				System.out.println("Checking for new Servers:");
			}	
			while (rs.next()) {
				boolean check = true;
				try {
					if (big.debug) {
						System.out.println("Checking Server " + rs.getString("id"));
					}
					ServerInfo old = proxyServer.getServerInfo("s" + rs.getString("id"));
					if (old.getName() == null) {
						check = true;
					} else {
						check = false;
						if (big.debug) {
							System.out.println("Server already existing!");
						}
					}
				} catch (Exception e) {
				}
				if (check) {
					if (big.debug) {
						System.out.println("Implementing new Server into bungeecord:" + rs.getString("id"));
						System.out.println("Creating Server with adress : " + rs.getString("adress") + " and port: "
								+ rs.getInt("port"));
						System.out.println(proxyServer.getServers().put("s" + rs.getString("id"),
								proxyServer.constructServerInfo("s" + rs.getString("id"),
										new InetSocketAddress(rs.getString("adress"), rs.getInt("port")),
										"AUTO-GENERATED SERVER", false)));
					} else {
						proxyServer.getServers().put("s" + rs.getString("id"),
								proxyServer.constructServerInfo("s" + rs.getString("id"),
										new InetSocketAddress(rs.getString("adress"), rs.getInt("port")),
										"AUTO-GENERATED SERVER", false));
					}
				}
				if (big.debug) {
					System.out.println("Returning true:");
				}
			}
			if (big.debug) {
				System.out.println("Done!");
			}
			
			// Close Connection

			stmt.close();
			conn.close();
			return true;
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
		return false;

	}
}
