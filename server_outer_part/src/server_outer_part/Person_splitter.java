package server_outer_part;

import java.net.InetAddress;
import java.net.Socket;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import org.bukkit.plugin.java.JavaPlugin;

public class Person_splitter extends JavaPlugin implements Listener {

	// Global variables
	//Database Location
	public static String database() {
		return "192.168.0.2";
	}
	//Debug
	public static boolean debug =true;
	// End of variables

	@Override
	public void onEnable() {
		server=this.getServer();
		server_id=MYSQL_CONNECTOR_OPTIONS.get_id();
		String DATA[] = new String[2];
		String i = null;
	       try {
	    	   Socket s = new Socket("192.168.0.1",80);
	    	   i=s.getLocalAddress().getHostAddress();
	    	   s.close();

	    	   
	       }
	       catch(Exception e){e.printStackTrace();}
	       if(debug){
	    	   System.out.println("Retrievt ip ="+i);
	       }
		DATA[0] = i;
		DATA[1] = Integer.toString(this.getServer().getPort());
		getServer().getPluginManager().registerEvents(this, this);
		this.getServer().broadcastMessage("The Server has been reloaded!");
		LOOP checker = new LOOP();
		checker.main(this.getServer(),this);
		MYSQL_CONNECTOR_REGISTER_SERVER register = new MYSQL_CONNECTOR_REGISTER_SERVER();
		this.getLogger().info(register.main(DATA));
	}

	@Override
	public void onDisable() {
		String DATA[] = new String[2];
		String i = null;
	       try {
	    	   Socket s = new Socket("192.168.0.1",80);
	    	   s.getLocalAddress();
			i=InetAddress.getLocalHost().getHostAddress();
	    	   s.close();

	    	   
	       }
	       catch(Exception e){e.printStackTrace();}
	       if(debug){
	    	   System.out.println("Retrievt ip ="+i);
	       }
		DATA[0] = i;
		DATA[1] = Integer.toString(this.getServer().getPort());
		this.getLogger().info(MYSQL_CONNECTOR_UNREGISTER_SERVER.main(DATA));

	}

	@EventHandler(priority = EventPriority.LOW)
	public void PlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		MYSQL_CONNECTOR_LOGIN mysqllogin = new MYSQL_CONNECTOR_LOGIN();
		int id = 0;
		id = mysqllogin.main(event.getPlayer().getUniqueId().toString().replace('-', ' ').replaceAll("\\s", ""));
		if (id != 0) {
			event.getPlayer().sendMessage("Wilkommen auf deiner Welt " + event.getPlayer().getName());
			// set and get target world
			if(debug){
				System.out.println("Player "+event.getPlayer().getDisplayName()+" joined Server!");
			}
			if(this.getServer().getOnlinePlayers().size()>5){
				event.getPlayer().kickPlayer(
						"There are too much players in this world!");
			}else{
				String target=MYSQL_CONNECTOR_OPTIONS.get_default_world();
				Location templocation = new Location(this.getServer().getWorld(target),
						this.getServer().getWorld( target).getSpawnLocation().getX(),
						this.getServer().getWorld( target).getSpawnLocation().getY(),
						this.getServer().getWorld( target).getSpawnLocation().getZ());
				event.getPlayer().teleport(templocation);
			}

		} else {
			event.getPlayer()
					.kickPlayer("Du musst dich auf www.tyrolyean.tk registrieren bevor du  Server betreten kannst!");
		}
	}
	//Variables that are globally needed and defined first in the onEnable
public static Server server;
public static int server_id;

}