package server_outer_part;

import java.net.InetAddress;
import java.net.Socket;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
			MYSQL_CONNECTOR_TRANSMISSION mysqltarget = new MYSQL_CONNECTOR_TRANSMISSION();
			String target_world;
			target_world = mysqltarget.select(event.getPlayer());
			if(debug){
				System.out.println("Player "+event.getPlayer().getDisplayName()+" joined world "+target_world);
			}
			if (target_world == null) {
				event.getPlayer().kickPlayer(
						"The redirection which took you to this server is not registered! Please try again if you believe this is an error!");
			} else if (this.getServer().getWorld(target_world) == null) {
				event.getPlayer().kickPlayer(
						"The world you were redirected to does no longer exists or never exists!Contact an Server-Admin if you believe this is an error!");
			} else if(this.getServer().getWorld(target_world).getPlayers().size()>5){
				event.getPlayer().kickPlayer(
						"There are too much players in this world!");
			}
			else{
				Location templocation = new Location(this.getServer().getWorld(target_world),
						this.getServer().getWorld( target_world).getSpawnLocation().getX(),
						this.getServer().getWorld( target_world).getSpawnLocation().getY(),
						this.getServer().getWorld( target_world).getSpawnLocation().getZ());
				event.getPlayer().teleport(templocation);
			}

		} else {
			event.getPlayer()
					.kickPlayer("Du musst dich auf www.tyrolyean.tk registrieren bevor du  Server betreten kannst!");
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void PlayerQuit(PlayerQuitEvent event) {

	}

}
