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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class JoinLeave extends JavaPlugin implements Listener {
	// variables
	public Player player[] = new Player[this.getServer().getMaxPlayers() + 1];
	public Location Lobby = new Location(this.getServer().getWorld("world"), 0, 0, 0);
	public static ItemStack world_connector;
	public static Server server;
	public static Logger logger;
	// Global used Mysql-Database
	public static String mysql = "192.168.0.13";

	// Global Debug mode
	public static Boolean debug() {
		return true;
	}

	public static Plugin plugin;

	// onEnable
	@Override
	public void onEnable() {
		server = this.getServer();
		logger = this.getLogger();
		this.getLogger().info("Loading BIG-Plugin. �2016|Tyrolyean. Al Rights Reserved. ");
		// importing Javaprograms :
		// jsch:
		// At the Moment Version 0.1.53
		// Needed to start new Servers over the ssh Protocol
		// apache.commons.net
		// Needet for sending mails and transfering files over ftp
		// In future I will transfer Data over a Socket
		// To prevent the usage of the apache commons

		try {
			final File[] libs = new File[] { new File(getDataFolder(), "commons-net-3.5.jar"),
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
		world_connector = new ItemStack(Material.COMPASS);
		ItemMeta metas = world_connector.getItemMeta();
		metas.setDisplayName("Weltenswitch");
		world_connector.setItemMeta(metas);
		Loop.main();
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
		MysqlConnectorLogin mysql = new MysqlConnectorLogin();
		int id = 0;
		id = mysql.main(event.getPlayer().getUniqueId().toString().replace('-', ' ').replaceAll("\\s", ""));
		if (id != 0) {
			String code = MysqlConnectorLogin
					.get_Activation(event.getPlayer().getUniqueId().toString().replace('-', ' ').replaceAll("\\s", ""));
			if (code != null) {
				if (debug()) {
					System.out.println("Der acount von Spieler " + event.getPlayer().getDisplayName()
							+ " wurde noch nicht Aktiviert.");
				}
				// Player hasn't activated his acount

				this.getServer().getScheduler().runTaskLater(this, new Runnable() {

					public void run() {
						event.getPlayer().sendMessage("Dein Aktivierungscode: " + code);
					}
				}, 20L);
			}

			event.getPlayer().sendMessage("Wilkommen auf dem Server " + event.getPlayer().getName() + "!");
		} else {
			event.getPlayer()
					.sendMessage("Wir empfehlen dir dich zu reistrieren! Sonst kannst du keine Server betreten!");
		}
		// set position to Lobby
		// event.getPlayer().teleport(Lobby);
	}

	@EventHandler
	public static void onPlayerJoin(PlayerJoinEvent event) {

		event.getPlayer().getInventory().clear();
		event.getPlayer().getInventory().setItem(1, world_connector);
		Tools.updatePlayerRanks();

	}

	// Leaving
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		try {
			if (event.getCurrentItem() == null) {
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (event.getInventory().getTitle() == world_connector.getItemMeta().getDisplayName()) {
			int world_id = event.getCurrentItem().getAmount();
			TcpClient.transfer_player(this.getServer().getPlayer(event.getWhoClicked().getName()), world_id);
			event.setCancelled(true);
		} else {

			if (ranks.get(event.getWhoClicked()) != 0) {
				event.setCancelled(true);

			} else {
				event.setCancelled(false);
			}
		}

	}

	@EventHandler
	public void onPlayerUse(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		try {
			if (debug()) {
				if (event.getHand() == null) {
					this.getLogger().info("Player " + event.getPlayer().getDisplayName() + " Interacted with "
							+ event.getItem() + " to " + event.getMaterial() + ". Throwing away...");

					@SuppressWarnings("deprecation")
					FallingBlock f = event.getPlayer().getWorld().spawnFallingBlock(event.getPlayer().getLocation(),
							Material.PISTON_MOVING_PIECE, (byte) 0);
					f.setVelocity(new Vector(1.0, 1.0, 0.0));
					f.setPassenger(event.getPlayer());
				} else {

					this.getLogger().info("Player " + event.getPlayer().getDisplayName() + " Interacted over Hand:"
							+ event.getHand().name() + " with " + event.getItem() + " to " + event.getMaterial());
				}
			}

			if (event.getHand() == null) {// If he didin't interacted with his
											// Hand
				if (ranks.get(p) != 0) {
					event.setCancelled(true);

				} else {
					event.setCancelled(false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (ranks.get(p) != 0) {
				event.setCancelled(true);

			} else {
				event.setCancelled(false);
			}

		}
		try {

			if (event.getItem().getType() == world_connector.getType()) {
				HashMap<Integer, String[]> info = MysqlConnectorServer.get_servers_allowed(p);
				int temp = info.keySet().size() / 9;
				temp *= 9;
				temp += 9;

				if (debug()) {
					this.getLogger().log(Level.INFO,
							"Player clicked with Switcher! Opening Inventory with length " + temp + " !");
				}

				Inventory world_viewer = this.getServer().createInventory(null, temp,
						world_connector.getItemMeta().getDisplayName());
				int i = 0;
				for (Integer world_id : info.keySet()) {
					ItemStack item = new ItemStack(Material.COMPASS);
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(info.get(world_id)[0] + " von " + info.get(world_id)[1]);
					item.setItemMeta(meta);
					item.setAmount(world_id);
					world_viewer.setItem(i, item);
					i++;
				}
				p.openInventory(world_viewer);
				return;
			} else {

			}
		} catch (Exception e) {

		}
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
					MysqlConnectorServer.upgrade_RAM(RAM, uuid);

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
					MysqlConnectorServer.get_new(args[0], args[1], uuid);// MYSQL_CONNECTOR
																			// WITH
																			// FTP
																			// and
																			// SSH
				} catch (Exception e) {
					sender.sendMessage("Too few Arguments!");

				}
			} else {
				sender.sendMessage("Du darfst diesen Command unter keinen Umst�nden ausf�hren!");
			}
		}
		return false;
	}

	public void activationCode(final String code, final Player player) { // Just
																			// a
																			// Joke
																			// :D
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(500);
					player.sendMessage("Dein Aktivierungscode ist " + code);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static HashMap<Player, Integer> ranks = new HashMap<Player, Integer>();

}