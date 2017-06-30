package server_outer_part;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.nio.file.Files;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.scheduler.BukkitRunnable;

public class LOOP {

	public void main(Server server, Person_splitter person_splitter) {
		// Called first when the Programm starts!
		// Explanation:
		// Run Task Later is used in order to Run the world-Creation
		// Syncroniously because else the Server isn't creaing the world
		// Creating every world hapens on the Server-Start. Every 30 Seconds the
		// worlds are updated with an mysql-Databse
		// In an earlyer Version of this Plugin MultiVerse Core was used so if
		// you find something from Multiverse please delete it
		// RAM is controled over MYSQL and the Restart SCRIPT, if a
		// RAM-Change is detected
		// the server has to restart which happens over the script and the
		// command-line
		//
		//
		//
		Timer timer2 = new Timer();
		timer2.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				// Things put here are sent every hour
				// Made for statistics
				MYSQL_CONNECTOR_STATISTIC.send();

			}

		}, 0, 3600000);
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				OTHER_THINGS.update_player_permissions();
				// All things put in here are checked every 30 Seconds!
				// Get RAM at the moment and which should be
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
				Long ram = MYSQL_CONNECTOR_OPTIONS.getRAM();
				if (maxheap != ram && ram != 0) {
					if (Person_splitter.debug) {
						Person_splitter.logger.info(" Ram was updated from " + maxheap + " to " + ram);
					}
					File sh = new File(System.getProperty("user.dir") + "/start.sh");
					try {
						List<String> content = Files.readAllLines(sh.toPath());
						// Get row where Maxheap is
						Boolean foundrow = true;
						int i = 0;
						int row_ = 0;
						for (; foundrow; i++) {
							char[] row = content.get(i).toLowerCase().toCharArray();
							if (row[0] == 'm' && row[1] == 'a' && row[2] == 'x' && row[3] == 'h' && row[4] == 'e'
									&& row[5] == 'a' && row[6] == 'p') {
								foundrow = false;
								row_ = i;
							}
						} // End FOR

						if (Person_splitter.debug) {
							Person_splitter.logger.info("Got Row where MAXHAEP is defined: " + row_);
						}
						// SET ROW TO NEW HEAP
						content.set(row_, "MAXHEAP=" + ram);
						//
						Files.delete(sh.toPath());
						sh.createNewFile();
						PrintWriter writer = new PrintWriter(sh);
						for (String str : content) {// Write old file to new
													// File with changed RAM
							writer.println(str);
						}
						writer.close();
						sh.setExecutable(true);
						Person_splitter.server.dispatchCommand(Person_splitter.server.getConsoleSender(), "restart");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				// Get world list
				Person_splitter.worlds = MYSQL_CONNECTOR_OPTIONS.get_worlds();
				List<String> worlds = Person_splitter.worlds;
				if (Person_splitter.debug) {
					person_splitter.getLogger().info("Now Checking for information for worlds again!");
				}
				try {

					for (int i1 = 1; i1 < worlds.size() - 1; i1 += 6) {

						Boolean worldexists = false;

						try {
							World worldtemp = Person_splitter.server.getWorld(worlds.get(i1 + 5) + worlds.get(i1));
							if (worldtemp != null) {
								worldexists = true;
							}
						} catch (Exception e) {
							worldexists = false;
						}
						if (!worldexists) {
							WorldCreator c = new WorldCreator(worlds.get(i1 + 5) + worlds.get(i1));
							if (worlds.get(i1 + 5).equals("normal"))
								c.type(WorldType.NORMAL);
							else if (worlds.get(i1 + 5).equals("flat")) {
								c.type(WorldType.FLAT);
							} else if (worlds.get(i1 + 5).equals("nether")) {
								c.environment(Environment.NETHER);
							} else if (worlds.get(i1 + 5).equals("end")) {
								c.environment(Environment.THE_END);
							}
							// Create the world async
							server.getScheduler().runTaskLater(person_splitter, new Runnable() {
								@Override
								public void run() {
									if (Person_splitter.debug) {
										person_splitter.getLogger().info("Creating world " + c.name());
										person_splitter.getLogger().info("" + server.createWorld(c));
										person_splitter.getLogger().info("Done!");
									} else {
										server.createWorld(c);
									}
								}
							}, 0L);// End of creation
						} else {
							if (Person_splitter.debug) {
								Person_splitter.logger
										.info("world " + worlds.get(i1 + 5) + worlds.get(i1) + " already existing!");
							}
						}
					} // End of for
					if (Person_splitter.debug) {
						person_splitter.getLogger()
								.info("Created all new worlds! Becasuse of run task later creation may be later!");
					}
					// Unloading of the world happens in the
					// MYSQL_CONNECTOR_OPTIONS.worlds() automatically
				} catch (Exception e) {
					e.printStackTrace();
				}
				// Update the per-world options
				// This can only be set by the server-Administrator
				// Aaaand here comes the Hashmap!!!!!
				HashMap<String, ArrayList<Object>> options = MYSQL_CONNECTOR_OPTIONS.getAllOptions();

				// Update world_settings for every world.

				for (World world : Person_splitter.server.getWorlds()) {

					if (Person_splitter.debug) {
						Person_splitter.logger.info("Checking for world " + world.getName());
					}

					ArrayList<Object> world_options = options.get(world.getName());
					if (world_options == null) {
						continue;
					}
					// Set Difficulty
					if ((Short) world_options.get(0) == 0) {
						world.setDifficulty(Difficulty.PEACEFUL);
					} else if ((Short) world_options.get(0) == 1) {
						world.setDifficulty(Difficulty.EASY);
					} else if ((Short) world_options.get(0) == 2) {
						world.setDifficulty(Difficulty.NORMAL);
					} else if ((Short) world_options.get(0) == 3) {
						world.setDifficulty(Difficulty.HARD);
					} else {
						world.setDifficulty(Difficulty.NORMAL);
					}
					// Set default Gamemode
					if ((Short) world_options.get(1) == 0) {
						/*
						 * if (Person_splitter.gamemodes.get(world) != null) {
						 * Person_splitter.gamemodes.replace(world,
						 * GameMode.SURVIVAL); } else {
						 * Person_splitter.gamemodes.put(world,
						 * GameMode.SURVIVAL); }
						 */
						Person_splitter.gamemodes.put(world, GameMode.SURVIVAL);
					} else if ((Short) world_options.get(1) == 1) {
						/*
						 * if (Person_splitter.gamemodes.get(world) != null) {
						 * Person_splitter.gamemodes.replace(world,
						 * GameMode.CREATIVE); } else {
						 * Person_splitter.gamemodes.put(world,
						 * GameMode.CREATIVE); }
						 */
						Person_splitter.gamemodes.put(world, GameMode.CREATIVE);
					} else if ((Short) world_options.get(1) == 2) {
						/*
						 * if (Person_splitter.gamemodes.get(world) != null) {
						 * Person_splitter.gamemodes.replace(world,
						 * GameMode.ADVENTURE); } else {
						 * Person_splitter.gamemodes.put(world,
						 * GameMode.ADVENTURE); }
						 */
						Person_splitter.gamemodes.put(world, GameMode.ADVENTURE);
					} else if ((Short) world_options.get(1) == 3) {
						/*
						 * if (Person_splitter.gamemodes.get(world) != null) {
						 * Person_splitter.gamemodes.replace(world,
						 * GameMode.SPECTATOR); } else {
						 * Person_splitter.gamemodes.put(world,
						 * GameMode.SPECTATOR); }
						 */
						Person_splitter.gamemodes.put(world, GameMode.SPECTATOR);
					} else {
						/*
						 * if (Person_splitter.gamemodes.get(world) != null) {
						 * Person_splitter.gamemodes.replace(world,
						 * GameMode.SURVIVAL); } else {
						 * Person_splitter.gamemodes.put(world,
						 * GameMode.SURVIVAL); }
						 */
						Person_splitter.gamemodes.put(world, GameMode.SURVIVAL);
					}
					// animal and Monster-Spawnrate
					world.setTicksPerAnimalSpawns((int) world_options.get(2));
					world.setTicksPerMonsterSpawns((int) world_options.get(3));
					if (!(boolean) world_options.get(4)) {
						world.setThundering(false);
						world.setStorm(false);
						world.setWeatherDuration(6000);
					}
					// set if the players can hurt eachother
					world.setPVP((boolean) world_options.get(5));
					Person_splitter.spawns.clear();

					if ((int) world_options.get(6) == 0 && (int) world_options.get(7) == 0
							&& (int) world_options.get(7) == 0) {
						// Here if the Admin says that the persons should spawn,
						// where they left the last time.
						// Putting into Hashmap
						/*
						 * if (Person_splitter.spawns.get(world) != null) {
						 * Person_splitter.spawns.replace(world, new
						 * Location(world, 0, 0, 0)); } else {
						 * Person_splitter.spawns.put(world, new Location(world,
						 * 0, 0, 0)); }
						 */
						Person_splitter.spawns.put(world, new Location(world, 0, 0, 0));
					} else {
						/*
						 * if (Person_splitter.spawns.get(world) != null) {
						 * Person_splitter.spawns.replace(world, new
						 * Location(world, (int) world_options.get(6), (int)
						 * world_options.get(7), (int) world_options.get(8))); }
						 * else { Person_splitter.spawns.put(world, new
						 * Location(world, (int) world_options.get(6), (int)
						 * world_options.get(7), (int) world_options.get(8))); }
						 */
						Person_splitter.spawns.put(world, new Location(world, (int) world_options.get(6),
								(int) world_options.get(7), (int) world_options.get(8)));
					}

				}
				MYSQL_CONNECTOR_REGISTER_SERVER.update_server_id();

			}

		}, 30000, 30000);

	}

	public BukkitRunnable createWorld(WorldCreator c, Server server) {
		try {
			server.createWorld(c);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}