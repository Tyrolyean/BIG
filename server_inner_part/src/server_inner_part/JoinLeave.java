package server_inner_part;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
//import java.nio.charset.Charset;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class JoinLeave extends JavaPlugin implements Listener {
	// variables
	public Player player[] = new Player[this.getServer().getMaxPlayers() + 1];
	public Location Lobby = new Location(this.getServer().getWorld("world"), 0, 0, 0);
	//Global used Mysql-Database
	public static String mysql(){
		return "192.168.0.2";
	}
	//Global Debug mode
	public static Boolean debug(){
		return true;
	}

	// onEnable
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		this.getServer().broadcastMessage(System.getProperty("user.dir"));
		File f = new File(System.getProperty("user.dir") + "/plugins/big/config.yml");
		if (f.exists() && !f.isDirectory()) {
			this.getServer().broadcastMessage("File config.yml exists! Getting information from there!");
			// Reading data from file
			//Path filePath = new File(System.getProperty("user.dir") + "/plugins/big/config.yml").toPath();
			//Charset charset = Charset.defaultCharset();
			//List<String> stringList;
			//try {
				//stringList = Files.readAllLines(filePath, charset);
				//String[] caught = stringList.toArray(new String[] {});
				/*double lobbyy = Double.parseDouble(caught[1].replace("lobby_x: ", ""));
				double lobbyx = Double.parseDouble(caught[2].replace("lobby_y: ", ""));
				double lobbyz = Double.parseDouble(caught[3].replace("lobby_z: ", ""));
				Lobby = new Location(this.getServer().getWorld(caught[4].replace("lobby_world: ", "")), lobbyy, lobbyx,
						lobbyz);*/
			//} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			//}
		} else {
			f.getParentFile().mkdirs();
			try {
				// Creating file
				f.createNewFile();
				// Putting standards to file
				PrintWriter writer = new PrintWriter(System.getProperty("user.dir") + "/plugins/big/config.yml",
						"UTF-8");
				writer.println("#Plugin made by Tyrolyean\n");
				writer.println("lobby_x: 10\n");
				writer.println("lobby_y: 100\n");
				writer.println("lobby_z: 10\n");
				writer.println("lobby_world: world\n");
				writer.close();
				// Put standards into file
			} catch (IOException e) {
				this.getServer().broadcastMessage("Fatal error at creating File!: " + e);
			}
		}
	}

	// Joining
	@EventHandler(priority = EventPriority.LOW)
	public void PlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		MYSQL_CONNECTOR_LOGIN mysql = new MYSQL_CONNECTOR_LOGIN();
		int id = 0;
		id = mysql.main(event.getPlayer().getUniqueId().toString().replace('-', ' ').replaceAll("\\s", ""));
		if (id != 0) {
			event.getPlayer().sendMessage("Wilkommen auf dem Server " + event.getPlayer().getName()
					+ " dir wurde die id " + id + " zugeteilt!");
		} else {
			event.getPlayer().sendMessage("Wir empfehlen dir dich zu reistrieren! Sonst kannst du keine Server betreten!");
		}
		// set position to Lobby
		//event.getPlayer().teleport(Lobby);
		
		
		
		
	}

	// Leaving
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {

		// Commands:
		// "Ping"
		if (cmd.getName().equalsIgnoreCase("ping")) {
			try {
				this.getServer().broadcastMessage(args[1]);
			} catch (Exception e) {
				this.getServer().broadcastMessage(e.getMessage());
			}
			sender.sendMessage("pong");
		}
		// "Time"
		else if (cmd.getName().equalsIgnoreCase("time")) {
			sender.sendMessage("Time is relative!");
		}
		// Set lobby
		else if (cmd.getName().equalsIgnoreCase("lobby") && sender.hasPermission("big.setlobby")) {
			sender.sendMessage("Setting the Lobby-Spawn to the Place you are standing on!");
			// getting player position
			Player temp = Bukkit.getPlayer(sender.getName());
			Lobby = temp.getLocation();
			// Writing to file
			File writelobby = new File(System.getProperty("user.dir") + "/plugins/big/config.yml");
			FileWriter fw;
			try {
				fw = new FileWriter(writelobby.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write("#Plugin made by Tyrolyean\n");
				bw.write("lobby_x: " + Lobby.getX());
				bw.write("\nlobby_y: " + Lobby.getY());
				bw.write("\nlobby_z: " + Lobby.getZ());
				bw.write("\nlobby_world: " + Lobby.getWorld());
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// written
		} else if (cmd.getName().equalsIgnoreCase("createserver")) {
			if (sender == this.getServer().getConsoleSender()) {
				try {
					MYSQL_CONNECTOR_CREATE_SERVER.get_new(args[0], args[1], args[2]);
				} catch (Exception e) {
					sender.sendMessage("Too few Arguments!");

				}
			} else {
				sender.sendMessage("Du darfst diesen Command unter keinen Umständen ausführen!");
			}
		}
		return false;
	}
}