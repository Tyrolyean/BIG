package server_inner_part;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
//import java.nio.charset.Charset;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.List;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class JoinLeave extends JavaPlugin implements Listener {
	// variables
	public Player player[] = new Player[this.getServer().getMaxPlayers() + 1];
	public Location Lobby = new Location(this.getServer().getWorld("world"), 0, 0, 0);

	// Global used Mysql-Database
	public static String mysql = "192.168.0.102";
	public static Server server;

	// Global Debug mode
	public static Boolean debug() {
		return true;
	}

	public static Plugin plugin;

	// onEnable
	@Override
	public void onEnable() {
		server = this.getServer();
		this.getLogger().info("Loading BIG-Plugin. ©2016|Tyrolyean. Al Rights Reserved. ");
		// importing Javaprograms :
		// jsch:
		// At the Moment Version 0.1.53
		// Needed to start new Servers over the ssh Protocol
		// apache.commons.net
		// Needet for sending mails and transfering files over ftp
		// In future I will transfer Data over a Socket
		// To prevent the usage of the apache commons

		try {
			final File[] libs = new File[] { new File(getDataFolder(), "commons-net-3.4.jar"),
					new File(getDataFolder(), "jsch-0.1.53.jar") };
			for (final File lib : libs) {
				if (!lib.exists()) {
					JarUtils.extractFromJar(lib.getName(), lib.getAbsolutePath());
				}
			}
			for (final File lib : libs) {
				if (!lib.exists()) {
					this.getLogger()
							.warning("There was a critical error loading BIG! Could not find lib: " + lib.getName());
					Bukkit.getServer().getPluginManager().disablePlugin(this);
					return;
				}
				addClassPath(JarUtils.getJarUrl(lib));
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

		getServer().getPluginManager().registerEvents(this, this);
		File f = new File(System.getProperty("user.dir") + "/plugins/big/config.yml");
		if (f.exists() && !f.isDirectory()) {
			this.getServer().broadcastMessage("File config.yml exists! Getting information from there!");
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

	private void addClassPath(final URL url) throws IOException {
		final URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		final Class<URLClassLoader> sysclass = URLClassLoader.class;
		try {
			final Method method = sysclass.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { url });
		} catch (final Throwable t) {
			t.printStackTrace();
			throw new IOException("Error adding " + url + " to system classloader");
		}
	}

	// Using Playr Login event to keep Datastream low
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerLoginEvent(PlayerLoginEvent event) {
		MYSQL_CONNECTOR_LOGIN mysql = new MYSQL_CONNECTOR_LOGIN();
		int id = 0;
		id = mysql.main(event.getPlayer().getUniqueId().toString().replace('-', ' ').replaceAll("\\s", ""));
		if (id != 0) {
			String code = MYSQL_CONNECTOR_LOGIN
					.get_Activation(event.getPlayer().getUniqueId().toString().replace('-', ' ').replaceAll("\\s", ""));
			if (code != null) {
				if (debug()) {
					System.out.println("Der acount von Spieler " + event.getPlayer().getDisplayName()
							+ " wurde noch nicht Aktiviert.");
				}
				// Player hasn't activated his acount

				this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

					public void run() {
						event.getPlayer().sendMessage("Dein Aktivierungscode: "+code);
					}
				}, 120L);
			}

			event.getPlayer().sendMessage("Wilkommen auf dem Server " + event.getPlayer().getName() + "!");
		} else {
			event.getPlayer()
					.sendMessage("Wir empfehlen dir dich zu reistrieren! Sonst kannst du keine Server betreten!");
		}
		// set position to Lobby
		// event.getPlayer().teleport(Lobby);

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
		// RAM-UPGRADE
		else if (cmd.getName().equalsIgnoreCase("ramupgrade")) {
			if (sender == this.getServer().getConsoleSender()) {
				URL uuidresolver;
				try {
					uuidresolver = new URL("https://api.mojang.com/users/profiles/minecraft/" + args[0]);

					BufferedReader input = new BufferedReader(
							new InputStreamReader(uuidresolver.openConnection().getInputStream()));
					String rawuuid = input.readLine();
					String uuid = rawuuid.substring(7, 39);
					int RAM = 0;
					try {
						RAM = Integer.parseInt(args[1]);
					} catch (Exception e) {
						this.getLogger().warning(e.getMessage());
					}
					MYSQL_CONNECTOR_SERVER.upgrade_RAM(RAM, uuid);

				} catch (IOException e) {
					e.printStackTrace();
				}

			} else {
				sender.sendMessage("Das ist dir nicht gestattet!");
			}
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
					// Retrieve the uuid out of the Playername
					URL uuidresolver = new URL("https://api.mojang.com/users/profiles/minecraft/" + args[2]);
					BufferedReader input = new BufferedReader(
							new InputStreamReader(uuidresolver.openConnection().getInputStream()));
					String rawuuid = input.readLine();
					String uuid = rawuuid.substring(7, 39);
					plugin = this;
					MYSQL_CONNECTOR_SERVER.get_new(args[0], args[1], uuid);// MYSQL_CONNECTOR
																			// WITH
																			// FTP
																			// and
																			// SSH
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