package server_inner_part;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.nio.file.Path;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

public class Create_Server {
	public static boolean main(int Port, int id, String name, String type, String path, FTPClient server,
			String username, String password) throws IOException {
		// find out which Folder has the highest Number
		Path path_under = new File(path + id).toPath();
		server.setFileTransferMode(FTP.BINARY_FILE_TYPE);
		server.setFileType(FTP.BINARY_FILE_TYPE);
		server.changeWorkingDirectory(path);

		server.makeDirectory("" + id);
		Path files = new File(new File(System.getProperty("user.dir")).toPath().getParent().toString() + "/0/")
				.toPath();
		// server.storeFile(remote, local); Syntax for storing file
		String tDir = System.getProperty("java.io.tmpdir");// Where the files
															// are temporarily
															// stored
		if (JoinLeave.debug()) {
			System.out.println("Creating Spigot-Server at " + server.getRemoteAddress().getHostAddress()
					+ " with Server-id " + id + " and port " + Port);
		}
		// Create Server
		PrintWriter writer;
		try {
			if (JoinLeave.debug()) {
				System.out.println("Creating File start.sh");
			}
			// Create start.sh for Restarting the Server and for the first Start
			writer = new PrintWriter(tDir + "/start.sh");
			writer.println("#!/bin/bash");
			writer.println("# /etc/init.d/minecraft");
			writer.println("# version _APLPHA.0.1 2016-02-28 (YYYY-MM-DD)");
			writer.println("#");
			writer.println("### BEGIN INIT INFO");
			writer.println("# Provides:   minecraft");
			writer.println("# Required-Start: $local_fs $remote_fs screen-cleanup");
			writer.println("# Required-Stop:  $local_fs $remote_fs");
			writer.println("# Should-Start:   $network");
			writer.println("# Should-Stop:    $network");
			writer.println("# Default-Start:  2 3 4 5");
			writer.println("# Default-Stop:   0 1 6");
			writer.println("# Short-Description:    Minecraft server");
			writer.println("#Description:    Starts the minecraft server for the OwnWorld Network");
			writer.println("### END INIT INFO");
			writer.println("#Settings");
			writer.println("USERNAME='server'");
			writer.println("SCREENNAME='s" + id + "'");
			if (JoinLeave.debug()) {
				System.out.println("SCRENNAME='s" + id + "'");
			}
			writer.println("MCPATH='" + path_under.toString() + "'");
			if (JoinLeave.debug()) {
				System.out.println("MCPATH='" + path_under.toString() + "");
			}
			writer.println("MAXHEAP=2048");
			writer.println("MINHEAP=512");
			writer.println("HISTORY=1024");
			writer.println("INVOCATION=\"java -Xmx${MAXHEAP}M -Xms${MINHEAP}M -jar spigot.jar nogui\"");
			writer.println("ME=`whoami`");
			writer.println("as_user() {");
			writer.println("  if [ \"$ME\" = \"$USERNAME\" ] ; then");
			writer.println("   bash -c \"$1\"");
			writer.println("  else");
			writer.println("    su - \"$USERNAME\" -c \"$1\"");
			writer.println("  fi");
			writer.println("}");
			writer.println("mc_start() {");
			writer.println("    echo \"Starting Minecraft...\"");
			writer.println("    cd $MCPATH");
			writer.println("    as_user \"cd $MCPATH && screen  -dmS ${SCREENNAME} $INVOCATION\"");
			writer.println("    if pgrep -u $USERNAME > /dev/null");
			writer.println("    then");
			writer.println("      echo \"running.\"");
			writer.println("    exit 0");
			writer.println("    else");
			writer.println("    exit 1");
			writer.println("      echo \"Error! Could not start Server\"");
			writer.println("    fi");
			writer.println("}");
			writer.println("mc_command() {");
			writer.println("  command=\"$1\";");
			writer.println("  if pgrep -u $USERNAME -f $SERVICE > /dev/null");
			writer.println("  then");
			writer.println("    pre_log_len=`wc -l \"$MCPATH/logs/latest.log\" | awk '{print $1}'`");
			writer.println("    as_user \"screen -p 0 -S ${SCREENNAME} -X eval 'stuff \"$command\"\015'\"");
			writer.println(
					"    sleep .1 # assumes that the command will run and print to the log file in less than .1 seconds");
			writer.println("    # print output");
			writer.println(
					"    tail -n $[`wc -l \"$MCPATH/logs/latest.log\" | awk '{print $1}'`-$pre_log_len] \"$MCPATH/logs/latest.log\"");
			writer.println("  fi");
			writer.println("}");
			writer.println("#Start-Stop here");
			writer.println("case \"$1\" in");
			writer.println("  first)");
			writer.println("    mc_start");
			writer.println("    mc_command \"mv create $* normal\"");
			writer.println("    mc_command \"mv unload world\"");
			writer.println("    mc_command \"mv unoad world_nether\"");
			writer.println("    mc_command \"mv unoad world_the_end\"");
			writer.println("    ;;");
			writer.println("  start)");
			writer.println("    mc_start");
			writer.println("    ;;");
			writer.println("  stop)");
			writer.println("    mc_stop");
			writer.println("    ;;");
			writer.println("  restart)");
			writer.println("    mc_stop");
			writer.println("    mc_start");
			writer.println("    ;;");
			writer.println("  backup)");
			writer.println("    mc_backup");
			writer.println("    ;;");
			writer.println("  command)");
			writer.println("    if [ $# -gt 1 ]; then");
			writer.println("      shift");
			writer.println("      mc_command \"$*\"");
			writer.println("    else");
			writer.println("      echo \"Must specify server command (try 'help'?)\"");
			writer.println("    fi");
			writer.print("    ;;");
			writer.println("  *)");
			writer.println("      mc_start");
			writer.println("    ;;");
			writer.println("esac");
			writer.println("exit 0");
			writer.close();
			// Send sh-file to Server
			if (JoinLeave.debug()) {
				System.out.println(
						server.storeFile(path + "/" + id + "/start.sh", new FileInputStream(tDir + "/start.sh")));
			} else {
				server.storeFile(path + "/" + id, new FileInputStream(tDir + "/start.sh"));

			}
			if (JoinLeave.debug()) {
				System.out.println(new File(tDir + "/start.sh").delete());
				System.out.println("Created file start.sh");
			} else {
				new File(tDir + "/start.sh").delete();

			} // Delete temp file
				// End of

			if (JoinLeave.debug()) {
				System.out.println("Creating File eula.txt");
			}
			// Create Eula.txt
			writer = new PrintWriter(tDir + "/eula.txt");
			writer.println(
					"#By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).");
			writer.println("#Sun Feb 21 11:20:57 CET 2016");
			writer.println("eula=true");
			writer.close();
			if (JoinLeave.debug()) {
				System.out.println("Created Eula.txt");
				System.out.println(
						server.storeFile(path + "/" + id + "/eula.txt", new FileInputStream(tDir + "/eula.txt")));
				new File(tDir + "/eula.txt").delete();
			} else {
				server.storeFile(path + "/" + id + "/eula.txt", new FileInputStream(tDir + "/eula.txt"));
				new File(tDir + "/eula.txt").delete();
			}
			// Create Spigot.yml
			if (JoinLeave.debug()) {
				System.out.println("Creating spigot.yml");
			}
			writer = new PrintWriter(tDir + "/spigot.yml");
			writer.println("# This is the main configuration file for Spigot.");
			writer.println("# As you can see, there's tons to configure. Some options may impact gameplay, so use");
			writer.println("# with caution, and make sure you know what each option does before configuring.");
			writer.println("# For a reference for any variable inside this file, check out the Spigot wiki at");
			writer.println("# http://www.spigotmc.org/wiki/spigot-configuration/");
			writer.println("#");
			writer.println("# If you need help with the configuration or have any questions related to Spigot,");
			writer.println("# join us at the IRC or drop by our forums and leave a post.");
			writer.println("#");
			writer.println("# IRC: #spigot @ irc.spi.gt ( http://www.spigotmc.org/pages/irc/ )");
			writer.println("# Forums: http://www.spigotmc.org/");
			writer.println("");
			writer.println("config-version: 8");
			writer.println("settings:");
			writer.println("  save-user-cache-on-stop-only: false");
			writer.println("  bungeecord: true");
			writer.println("  late-bind: false");
			writer.println("  player-shuffle: 0");
			writer.println("  filter-creative-items: true");
			writer.println("  user-cache-size: 1000");
			writer.println("  int-cache-limit: 1024");
			writer.println("  moved-wrongly-threshold: 0.0625");
			writer.println("  moved-too-quickly-threshold: 100.0");
			writer.println("  timeout-time: 60");
			writer.println("  restart-on-crash: true");
			writer.println("  restart-script: ./start.sh");
			writer.println("  netty-threads: 4");
			writer.println("  attribute:");
			writer.println("    maxHealth:");
			writer.println("      max: 2048.0");
			writer.println("    movementSpeed:");
			writer.println("      max: 2048.0");
			writer.println("    attackDamage:");
			writer.println("      max: 2048.0");
			writer.println("  sample-count: 12");
			writer.println("  debug: false");
			writer.println("commands:");
			writer.println("  tab-complete: 0");
			writer.println("  log: true");
			writer.println("  spam-exclusions:");
			writer.println("  - /skill");
			writer.println("  silent-commandblock-console: false");
			writer.println("  replace-commands:");
			writer.println("  - setblock");
			writer.println("  - summon");
			writer.println("  - testforblock");
			writer.println("  - tellraw");
			writer.println("messages:");
			writer.println("  whitelist: Du darfst diesen Server nicht betreten.");
			writer.println("  unknown-command: Dieser Command ist nicht Registriert!");
			writer.println("  server-full: Dieser Server ist voll!");
			writer.println("  outdated-client: Wir verwenden die 1.9!");
			writer.println("  outdated-server: Wir verwenden die 1.9!");
			writer.println("  restart: Täglicher Neustart des Servers! Bitte Warten sie!");
			writer.println("stats:");
			writer.println("  disable-saving: false");
			writer.println("  forced-stats: {}");
			writer.println("world-settings:");
			writer.println("  default:");
			writer.println("    verbose: true");
			writer.println("    mob-spawn-range: 4");
			writer.println("    nerf-spawner-mobs: false");
			writer.println("    growth:");
			writer.println("      cactus-modifier: 100");
			writer.println("      cane-modifier: 100");
			writer.println("      melon-modifier: 100");
			writer.println("      mushroom-modifier: 100");
			writer.println("      pumpkin-modifier: 100");
			writer.println("      sapling-modifier: 100");
			writer.println("      wheat-modifier: 100");
			writer.println("      netherwart-modifier: 100");
			writer.println("    entity-activation-range:");
			writer.println("      animals: 32");
			writer.println("      monsters: 32");
			writer.println("      misc: 16");
			writer.println("    entity-tracking-range:");
			writer.println("      players: 48");
			writer.println("      animals: 48");
			writer.println("      monsters: 48");
			writer.println("      misc: 32");
			writer.println("      other: 64");
			writer.println("    ticks-per:");
			writer.println("      hopper-transfer: 8");
			writer.println("      hopper-check: 8");
			writer.println("    hopper-amount: 1");
			writer.println("    random-light-updates: false");
			writer.println("    save-structure-info: true");
			writer.println("    max-entity-collisions: 8");
			writer.println("    dragon-death-sound-radius: 0");
			writer.println("    seed-village: 10387312");
			writer.println("    seed-feature: 14357617");
			writer.println("    hunger:");
			writer.println("      walk-exhaustion: 0.2");
			writer.println("      sprint-exhaustion: 0.8");
			writer.println("      combat-exhaustion: 0.3");
			writer.println("      regen-exhaustion: 3.0");
			writer.println("    max-tnt-per-tick: 100");
			writer.println("    max-tick-time:");
			writer.println("      tile: 50");
			writer.println("      entity: 50");
			writer.println("    item-despawn-rate: 6000");
			writer.println("    merge-radius:");
			writer.println("      item: 2.5");
			writer.println("      exp: 3.0");
			writer.println("    arrow-despawn-rate: 1200");
			writer.println("    enable-zombie-pigmen-portal-spawns: true");
			writer.println("    wither-spawn-sound-radius: 0");
			writer.println("    view-distance: 10");
			writer.println("    hanging-tick-frequency: 100");
			writer.println("    zombie-aggressive-towards-villager: true");
			writer.close();
			if (JoinLeave.debug()) {
				System.out.println("Created Spigot.yml!");
				System.out.println(
						server.storeFile(path + "/" + id + "/spigot.yml", new FileInputStream(tDir + "/spigot.yml")));
				new File(tDir + "/spigot.yml").delete();
			} else {
				server.storeFile(path + "/" + id + "/spigot.yml", new FileInputStream(tDir + "/spigot.yml"));
				new File(tDir + "/spigot.yml").delete();
			}
			// Create Bukkit.yml
			if (JoinLeave.debug()) {
				System.out.println("Creating File bukkit.yml");
			}
			writer = new PrintWriter(tDir + "/bukkit.yml");
			writer.println("# This is the main configuration file for Bukkit.");
			writer.println("# As you can see, there's actually not that much to configure without any plugins.");
			writer.println("# For a reference for any variable inside this file, check out the Bukkit Wiki a");
			writer.println("# http://wiki.bukkit.org/Bukkit.yml");
			writer.println("#");
			writer.println("# If you need help on this file, feel free to join us on irc or leave a message");
			writer.println("# on the forums asking for advice.");
			writer.println("#");
			writer.println("# IRC: #spigot @ irc.spi.gt");
			writer.println("#    (If this means nothing to you, just go to http://www.spigotmc.org/pages/irc/ )");
			writer.println("# Forums: http://www.spigotmc.org/");
			writer.println("# Bug tracker: http://www.spigotmc.org/go/bugs");
			writer.println("");
			writer.println("");
			writer.println("settings:");
			writer.println("  allow-end: false");
			writer.println("  warn-on-overload: true");
			writer.println("  permissions-file: permissions.yml");
			writer.println("  update-folder: update");
			writer.println("  plugin-profiling: false");
			writer.println("  connection-throttle: 4000");
			writer.println("  query-plugins: true");
			writer.println("  deprecated-verbose: default");
			writer.println("  shutdown-message: Server closed");
			writer.println("spawn-limits:");
			writer.println("  monsters: 70");
			writer.println("  animals: 15");
			writer.println("  water-animals: 5");
			writer.println("  ambient: 15");
			writer.println("chunk-gc:");
			writer.println("  period-in-ticks: 600");
			writer.println("  load-threshold: 0");
			writer.println("ticks-per:");
			writer.println("  animal-spawns: 400");
			writer.println("  monster-spawns: 1");
			writer.println("  autosave: 6000");
			writer.println("aliases: now-in-commands.yml");
			writer.println("database:");
			writer.println("  username: bukkit");
			writer.println("  isolation: SERIALIZABLE");
			writer.println("  driver: com.mysql.jdbc.Driver");
			writer.println("  password: bukkit");
			writer.println("  url: jdbc:mysql:/" + "/192.168.0.2:3306/bukkit");
			writer.close();
			if (JoinLeave.debug()) {
				System.out.println("Created bukkit.yml!");
				System.out.println(
						server.storeFile(path + "/" + id + "/bukkit.yml", new FileInputStream(tDir + "/bukkit.yml")));
				new File(tDir + "/bukkit.yml").delete();
			} else {
				server.storeFile(path + "/" + id + "/bukkit.yml", new FileInputStream(tDir + "/bukkit.yml"));
				new File(tDir + "/bukkit.yml").delete();
			}
			// Create Server.properties
			if (JoinLeave.debug()) {
				System.out.println("Creating File server.properties");
			}

			writer = new PrintWriter(tDir + "/server.properties");
			writer.println("#Minecraft server properties");
			writer.println("#Sun Feb 21 11:21:29 CET 2016");
			writer.println("generator-settings=");
			writer.println("op-permission-level=4");
			writer.println("allow-nether=false");
			writer.println("resource-pack-hash=");
			writer.println("level-name=world");
			writer.println("enable-query=true");
			writer.println("allow-flight=false");
			writer.println("announce-player-achievements=true");
			writer.println("server-port=" + Port);
			writer.println("max-world-size=29999984");
			writer.println("level-type=SUPERFLAT");
			writer.println("enable-rcon=true");
			writer.println("level-seed=");
			writer.println("force-gamemode=true");
			writer.println("server-ip=");
			writer.println("network-compression-threshold=256");
			writer.println("max-build-height=256");
			writer.println("spawn-npcs=true");
			writer.println("white-list=false");
			writer.println("spawn-animals=true");
			writer.println("hardcore=false");
			writer.println("snooper-enabled=true");
			writer.println("online-mode=false");
			writer.println("resource-pack=");
			writer.println("pvp=true");
			writer.println("difficulty=1");
			writer.println("enable-command-block=false");
			writer.println("gamemode=0");
			writer.println("player-idle-timeout=0");
			writer.println("max-players=20");
			writer.println("spawn-monsters=true");
			writer.println("generate-structures=true");
			writer.println("view-distance=10");
			writer.println("motd=Not relevant");
			writer.close();
			if (JoinLeave.debug()) {
				System.out.println("Created server.properties!");
				System.out.println(server.storeFile(path + "/" + id + "/server.properties",
						new FileInputStream(tDir + "/server.properties")));
				new File(tDir + "/server.properties").delete();
			} else {
				server.storeFile(path + "/" + id + "/server.properties",
						new FileInputStream(tDir + "/server.properties"));
				new File(tDir + "/server.properties").delete();
			}

			// Create Directory /plugins
			if (JoinLeave.debug()) {
				System.out.println(server.makeDirectory(path + "/" + id + "/plugins/"));
			} else {
				server.makeDirectory(path + "/" + id + "/plugins/");
			}
			// create Directory for Permissions Ex

			if (JoinLeave.debug()) {
				System.out.println(server.makeDirectory(path + "/" + id + "/plugins/PermissionsEx/"));
			} else {
				server.makeDirectory(path + "/" + id + "/plugins/PermissionsEx/");
			}
			// Create PermissionsEx config

			if (JoinLeave.debug()) {
				System.out.println("Creating File config.yml for Permissions Ex");
			}
			writer = new PrintWriter(tDir + "/config.yml");
			writer.println("multiserver:");
			writer.println("  use-netevents: true");
			writer.println("permissions:");
			writer.println("  debug: false");
			writer.println("  allowOps: false");
			writer.println("  user-add-groups-last: false");
			writer.println("  log-players: false");
			writer.println("  createUserRecords: false");
			writer.println("  backend: mysql");
			writer.println("  informplayers:");
			writer.println("    changes: false");
			writer.println("  basedir: plugins/PermissionsEx");
			writer.println("  backends:");
			writer.println("    file:");
			writer.println("      type: file");
			writer.println("      file: permissions.yml");
			writer.println("    mysql:");
			writer.println("      uri: mysql:/" + "/192.168.0.2:3306/permissionsex");
			writer.println("      user: permissionsex");
			writer.println("      password: permissionsex");
			writer.println("      type: sql");
			writer.println("updater: true");
			writer.println("alwaysUpdate: false");
			writer.close();

			if (JoinLeave.debug()) {
				System.out.println("Created config.yml!");
				System.out.println(server.storeFile(path + "/" + id + "/plugins/PermissionsEx/config.yml",
						new FileInputStream(tDir + "/config.yml")));
				new File(tDir + "/config.yml").delete();
			} else {
				server.storeFile(path + "/" + id + "/plugins/PermissionsEx/config.yml",
						new FileInputStream(tDir + "/config.yml"));
				new File(tDir + "/config.yml").delete();
			}

			// The firs world will be created because of an MYSQL_Entry
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Copy files and Start Server
		try {
			// Copy Spigot
			server.mode(FTP.BINARY_FILE_TYPE);

			if (JoinLeave.debug()) {
				System.out.println("now uploading spigot .yml with Options:");
				System.out.println(server.getStatus());
			}
			server.storeFile(path + "/" + id + "/spigot.jar",
					new FileInputStream(new File(files.toString() + "/spigot.jar")));

			// Copy outer Part of this Plugin
			server.storeFile(path + "/" + id + "/plugins/outer_part.jar",
					new FileInputStream(files.toString() + "/outer_part.jar"));
			// Copy PermissionsEx
			server.storeFile(path + "/" + id + "/plugins/PermissionsEx.jar",
					new FileInputStream(files.toString() + "/PermissionsEx.jar"));
			if (JoinLeave.debug()) {
				System.out.println("Copyed all Files from 0 Directory. Attempting to Start start.sh file!");
			}

			// Set the Programms to Executable
			// Start the Server
			if (JoinLeave.debug()) {
				System.out.println("Setting programm to executable ");
			}
			Boolean did = server.sendSiteCommand("chmod 777 ./" + id + "/start.sh");
			if (JoinLeave.debug()) {
				System.out.println("Did it: " + did);
			}
			InetAddress host = server.getRemoteAddress();
			server.disconnect();
			if (JoinLeave.debug()) {
				System.out.println("Exited FTP-Conection " + server.getControlEncoding());
			}

			JoinLeave.server.getScheduler().runTaskLater(JoinLeave.getPlugin(null), new Runnable() {
				public void run() {
					
					ChannelExec channel = null;
					Session sessie = null;
					try {
						JSch ssh = new JSch();
						sessie = ssh.getSession(username, host.getHostAddress());
						java.util.Properties config = new java.util.Properties();
						config.put("StrictHostKeyChecking", "no");
						sessie.setConfig(config);
						sessie.setPassword(password);
						sessie.connect();
						channel = (ChannelExec) sessie.openChannel("exec");
						channel.setCommand(path + id + "/start.sh");
						BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
						channel.connect();
						if (JoinLeave.debug()) {
							System.out.println(
									"command for starting server was executed with code: " + channel.getExitStatus());
						}
						if (channel.getExitStatus() == 0 || channel.isClosed() || channel.isEOF()) {
							if (JoinLeave.debug()) {

								System.out.println("Successfully connected to the Client. Command was executed!");
							}
						}
						try {
							in.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (JSchException e) {
						System.out.println(
								"SSH-Connection to Host " + server.getRemoteAddress() + " but got exception " + e);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} finally {
						if (channel != null)
							channel.disconnect();
						if (sessie != null)
							sessie.disconnect();
					}
				}
			}, 10000);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
