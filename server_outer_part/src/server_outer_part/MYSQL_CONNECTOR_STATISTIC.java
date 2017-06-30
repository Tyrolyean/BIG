package server_outer_part;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MYSQL_CONNECTOR_STATISTIC  {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://" + Person_splitter.database + "/statistics";

	// Database credentials
	static final String USER = "minecraft";
	static final String PASS = "minecraft";
	static Connection conn ;
	static Statement stmt = null;
	
	public static void send(){
		try {
			// JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// Open connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			
			stmt=conn.createStatement();
			
			ResultSet rs=null;
			rs=stmt.executeQuery("SHOW TABLES LIKE 's"+Person_splitter.server_id+"'");
			Boolean exists=false;
			while(rs.next()){
				exists=true;
			}
			
			if(!exists){
				stmt.execute("CREATE TABLE s"+Person_splitter.server_id+" (timestamp bigint,deaths int,removed int,placed int, joins int, PRIMARY KEY (timestamp))");
			}
			stmt.execute("INSERT INTO s"+Person_splitter.server_id+" (timestamp,deaths,joins,placed,removed) VALUES ("+System.currentTimeMillis() / 1000L+","+Person_splitter.deaths+" , "+Person_splitter.joins+" , "+Person_splitter.places+" , "+Person_splitter.destroys+" )");
			Person_splitter.joins=0;
			Person_splitter.places=0;
			Person_splitter.destroys=0;
			Person_splitter.deaths=0;
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
}
