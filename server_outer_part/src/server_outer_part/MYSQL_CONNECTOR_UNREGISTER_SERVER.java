package server_outer_part;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

public class MYSQL_CONNECTOR_UNREGISTER_SERVER {
	public static String main(){
		
		java.sql.Connection con = null;
		String url = "jdbc:mysql://"+Person_splitter.database+"/server_parts";
		String user = "server_parts";
		String password = "server_parts";

		try {
		     con = DriverManager.getConnection(url, user, password);
		     Statement st = (Statement) con.createStatement(); 
		     
		     
		     st.execute("UPDATE server_location SET online = 0 WHERE id='"+Person_splitter.server_id+"'");
		     
		     
		     st.close();;
		     con.close();
		     return "No error occured on Disabling Big plugin!"; 
		}

		catch (SQLException ex) {
			return "Couldn't disable BIG! \n"+ex.toString();
		 } 
		
	}
}
