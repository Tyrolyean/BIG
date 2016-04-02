package server_outer_part;

import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Person_splitter extends JavaPlugin implements Listener {

	//To anyone who can read this. If you are allowed 
	//to do this, hello, else FUCK YOU , this is my code ! leave it alone.
	//If you have any Questions please contact me at Tyrolyean@gmx.at
	//Copyright Tyrolyean 2016
	//if you copy my code i will sue you into hell!!!!
	//It was very Hard to write this code so please let me my rights.
	//Maybe I should rewrite half of it, but this comnes later :D
	//Good Luck you programmer And Goodbye

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
		System.out.println("©Tyrolyean 2016 Made for Ownworld");
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
		//Register all the worlds that exists at the beginning to do the startup faster
		List<String> list =MYSQL_CONNECTOR_OPTIONS.get_worlds();
		for(int i1=1;i1<list.size()-1;i1+=6){
		WorldCreator c = new WorldCreator(list.get(i1+1)+list.get(i1));
		if (list.get(i1+5).equals("normal"))
			c.type(WorldType.NORMAL);
		else if (list.get(i1+5).equals("flat")) {
			c.type(WorldType.FLAT);
		}else if(list.get(i1+5).equals("nether")){
			c.environment(Environment.NETHER);
		}else if(list.get(i1+5).equals("end")){
			c.environment(Environment.THE_END);
		}
		// Create the world async
		server.getScheduler().runTaskLater(this, new Runnable() {
			@Override
			public void run() {
				if (Person_splitter.debug) {
						System.out.println("Creating world " + c.toString());
					System.out.println(server.createWorld(c));
					System.out.println("Done!");
				} else {
					server.createWorld(c);
				}
			}
		}, 0L);// End of creation
		}//End of for
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
			event.getPlayer().sendMessage("Wilkommen auf dem Server " + event.getPlayer().getName());
			// set and get target world
			if(debug){
				System.out.println("Player "+event.getPlayer().getDisplayName()+" joined Server!");
			}
			if(this.getServer().getOnlinePlayers().size()>5){
				event.getPlayer().kickPlayer(
						"Es befinden sich momentan zu viele Spieler in dieser Welt!");
			}else{
				this.getServer().broadcastMessage(event.getPlayer().getDisplayName()+" hat den Server betreten!");
				String target=MYSQL_CONNECTOR_OPTIONS.get_default_world();
				Location templocation = new Location(this.getServer().getWorld(target),
						this.getServer().getWorld( target).getSpawnLocation().getX(),
						this.getServer().getWorld( target).getSpawnLocation().getY(),
						this.getServer().getWorld( target).getSpawnLocation().getZ());
				event.getPlayer().teleport(templocation);
			}

		} else {
			event.getPlayer()
					.kickPlayer("Du musst dich auf www.ownworld.de registrieren bevor du  Server betreten kannst!");
		}
	}
	//Variables that are globally needed and defined first in the onEnable
public static Server server;
public static int server_id;
public static List<String> worlds;

}