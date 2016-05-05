package server_outer_part;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

public class Person_splitter extends JavaPlugin implements Listener {

	// To anyone who can read this. If you are allowed
	// to do this, hello, else FUCK YOU , this is my code ! leave it alone.
	// If you have any Questions please contact me at Tyrolyean@gmx.at
	// Copyright Tyrolyean 2016
	// if you copy my code i will sue you into hell!!!!
	// It was very Hard to write this code so please let me my rights.
	// Maybe I should rewrite half of it, but this comes later :D
	// Good Luck you programmer And Goodbye
	// Yours
	// Tyrolyean

	// Global variables
	// Database Location
	public static String database = "192.168.0.102";
	public static Logger logger;
	// Debug
	public static boolean debug = true;

	// End of variables
	// chars[ii]=='0'||chars[ii]=='1'||chars[ii]=='2'||chars[ii]=='3'||chars[ii]=='4'||chars[ii]=='5'||chars[ii]=='6'||chars[ii]=='7'||chars[ii]=='8'||chars[ii]=='9'
	@Override
	public void onEnable() {
		deaths = 0;
		joins = 0;
		places = 0;
		destroys = 0;
		try {
			if (debug) {
				List<String> arguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
				int maxheap = 0;
				List<Character> tempheap = new ArrayList<Character>();
				for (String temporary : arguments) {
					char[] chars = temporary.toLowerCase().toCharArray();
					if (chars.length > 4) {
						if (chars[0] == '-' && chars[1] == 'x' && chars[2] == 'm' && chars[3] == 'x') {
							for (char temp : chars) {
								if (temp == '0' || temp == '1' || temp == '2' || temp == '3' | temp == '4'
										|| temp == '5' || temp == '6' || temp == '7' || temp == '8' || temp == '9') {
									tempheap.add(temp);
								}
							}

						}
					}
				}
				String temp = null;
				for (char temporary : tempheap) {
					if (temp == null) {
						temp = Character.toString(temporary);
					} else {
						temp += Character.toString(temporary);

					}
				}
				maxheap = Integer.parseInt(temp);
				this.getLogger().info("Got as Max. Heap-Size: " + Integer.toString(maxheap));
			}
		} catch (Exception e) {
			this.getLogger().warning(e.toString());
		}
		if (new File(System.getProperty("user.dir") + "/plugins/BIG/players.yml").exists()) {
			try {
				this.getConfig().load(System.getProperty("user.dir") + "/plugins/BIG/players.yml");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
		} else {
			if (new File(System.getProperty("user.dir") + "/plugins/BIG").exists()) {
				try {
					new File(System.getProperty("user.dir") + "/plugins/BIG/players.yml").createNewFile();
					this.getConfig().save(System.getProperty("user.dir") + "/plugins/BIG/players.yml");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				new File(System.getProperty("user.dir") + "/plugins/BIG/").mkdir();
				try {
					new File(System.getProperty("user.dir") + "/plugins/BIG/players.yml").createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		logger = this.getLogger();
		logger.info("©Tyrolyean 2016 Made for Ownworld");
		server = this.getServer();
		server_id = MYSQL_CONNECTOR_OPTIONS.get_id();
		String DATA[] = new String[2];
		String i = null;
		try {
			Socket s = new Socket("192.168.0.1", 80);
			i = s.getLocalAddress().getHostAddress();
			s.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (debug) {
			System.out.println("Retrievt ip =" + i);
		}
		DATA[0] = i;
		DATA[1] = Integer.toString(this.getServer().getPort());
		getServer().getPluginManager().registerEvents(this, this);
		this.getServer().broadcastMessage("The Server has been reloaded!");
		LOOP checker = new LOOP();
		checker.main(this.getServer(), this);
		// Register all the worlds that exists at the beginning to do the
		// startup faster
		List<String> list = MYSQL_CONNECTOR_OPTIONS.get_worlds();
		for (int i1 = 1; i1 < list.size() - 1; i1 += 6) {
			WorldCreator c = new WorldCreator(list.get(i1 + 5) + list.get(i1));
			if (list.get(i1 + 5).equals("normal"))
				c.type(WorldType.NORMAL);
			else if (list.get(i1 + 5).equals("flat")) {
				c.type(WorldType.FLAT);
			} else if (list.get(i1 + 5).equals("nether")) {
				c.environment(Environment.NETHER);
			} else if (list.get(i1 + 5).equals("end")) {
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
		} // End of for
	}

	@Override
	public void onDisable() {
		if (debug) {
			this.getServer().broadcastMessage("KONTROLL-PLUGIN WIRD DEAKTIVIERT! BITTE BLEIB RUHIG! ");
			this.getLogger().info("BIG plugin is going to be disabled! ");
			this.getLogger().info(MYSQL_CONNECTOR_UNREGISTER_SERVER.main());
		} else {
			MYSQL_CONNECTOR_UNREGISTER_SERVER.main();
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent event) {

		// register last position of player in config
		this.getConfig().set(
				"z" + event.getPlayer().getLocation().getWorld().getName() + event.getPlayer().getDisplayName(),
				event.getPlayer().getLocation().getZ());
		this.getConfig().set(
				"x" + event.getPlayer().getLocation().getWorld().getName() + event.getPlayer().getDisplayName(),
				event.getPlayer().getLocation().getX());
		this.getConfig().set(
				"y" + event.getPlayer().getLocation().getWorld().getName() + event.getPlayer().getDisplayName(),
				event.getPlayer().getLocation().getY());
		try {
			this.getConfig().save(System.getProperty("user.dir") + "/plugins/BIG/players.yml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void PlayerLogin(PlayerLoginEvent event) {
		MYSQL_CONNECTOR_LOGIN mysqllogin = new MYSQL_CONNECTOR_LOGIN();
		int id = 0;
		id = mysqllogin.main(event.getPlayer().getUniqueId().toString().replace('-', ' ').replaceAll("\\s", ""));
		if (id != 0) {
			event.getPlayer().sendMessage("[SERVER] Wilkommen" + event.getPlayer().getName());
			// set and get target world
			if (debug) {
				this.getLogger().info("[SERVER] " + event.getPlayer().getDisplayName() + " Hat die Welt betreten.");
			}
			if (this.getServer().getOnlinePlayers().size() > 5) {
				event.setKickMessage("Es befinden sich momentan zu viele Spieler in dieser Welt!");
			} else {
				this.getServer().broadcastMessage(event.getPlayer().getDisplayName() + " hat den Server betreten!");
				String target = MYSQL_CONNECTOR_OPTIONS.get_spawn_world(event.getPlayer());
				Location templocation = new Location(this.getServer().getWorld(target),
						this.getConfig().getInt("x" + target + event.getPlayer().getDisplayName()),
						this.getConfig().getInt("y" + target + event.getPlayer().getDisplayName()),
						this.getConfig().getInt("z" + target + event.getPlayer().getDisplayName()));
				event.getPlayer().teleport(templocation);
			}

		} else {
			event.setKickMessage("Du musst dich auf www.ownworld.at registrieren bevor du  Server betreten kannst!");
		}
	}
	// Commands

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {

		// Commands:
		// "Ping"
		if (cmd.getName().equalsIgnoreCase("ping")) {
			sender.sendMessage("pong");
		} else if (cmd.getName().equalsIgnoreCase("world")) {
			System.out.println(" s");
		}
		return true;
	}

	// Variables that are globally needed and defined first in the onEnable
	public static Server server;
	public static int server_id;
	public static List<String> worlds;

	// Events for Statistics (Enabled by first row of worlds list)
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		places++;
		if (debug) {
			this.getLogger()
					.info("Player " + event.getPlayer().getDisplayName() + " placed block "
							+ event.getBlock().getState().getData().getItemType().name() + " at "
							+ event.getBlock().getX() + " " + event.getBlock().getY() + " " + event.getBlock().getZ());
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		destroys++;
		if (debug) {
			this.getLogger()
					.info("Player " + event.getPlayer().getDisplayName() + " destroyed block "
							+ event.getBlock().getState().getData().getItemType().name() + " at "
							+ event.getBlock().getX() + " " + event.getBlock().getY() + " " + event.getBlock().getZ());
		}
	}

	@EventHandler
	public void onEntityKill(EntityDeathEvent event) {
		if (debug) {
			this.getLogger().info("Entitiy " + event.getEntityType().name() + " died  at "
					+ event.getEntity().getLocation().getBlockX() + " " + event.getEntity().getLocation().getBlockY()
					+ " " + event.getEntity().getLocation().getBlockZ());
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		// Prevent spawning in other worlds
		Entity killedE = event.getEntity();
		if (killedE instanceof Player) {
			Player killed = (Player) killedE;
			if (killed.getLocation() != killed.getBedSpawnLocation()) {
				killed.setBedSpawnLocation(killed.getLocation().getWorld().getSpawnLocation());
			}

		}
		// MYSQL statistics
		deaths++;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		joins++;
		PermissionAttachment attachment = event.getPlayer().addAttachment(this);
		permissions.put(event.getPlayer().getUniqueId(), attachment);
		OTHER_THINGS.update_player_permissions();
	}

	public static HashMap<UUID, PermissionAttachment> permissions =new HashMap<UUID, PermissionAttachment>();
	// Global Variables for stats
	public static int deaths;
	public static int joins;
	public static int places;
	public static int destroys;

}