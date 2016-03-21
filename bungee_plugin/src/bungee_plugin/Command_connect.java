package bungee_plugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Command_connect extends Command {
	public Command_connect() {
		super("connect");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		// Retrieving Information
		ProxiedPlayer player = (ProxiedPlayer) sender;
		Boolean arguments = false;
		try {
			sender.sendMessage(
					new ComponentBuilder("Verbinde mit dem Server auf dem sich Welt Nr. " + args[0] + "befindet!")
							.color(ChatColor.WHITE).create());
			arguments = true;
			if (big.debug) {
				System.out.println("Connecting to World!");
				Integer.parseInt(args[0]);
			}
		} catch (Exception e) {

			arguments = true;
		}
		ServerInfo target = null;
		// Get hub
		if (arguments) {
			// go on with Code
			Boolean hub_exists = false;
			try {

				if (player.getServer().getInfo().equals(ProxyServer.getInstance().getServerInfo("hub"))) {
					hub_exists = true;
				} else {
					hub_exists = false;
				}
			} catch (Exception e) {
				hub_exists = false;
			}
			if (hub_exists) {
				// Go on with code
				String info[];
				try {
					info = MYSQL_CONNECTOR_GET_WORLD_NAME.main(Integer.parseInt(args[0]));
				} catch (Exception e) {
					info = null;
				}
				if (big.debug) {// Show debug messages
					System.out.println("Got world information:");
					try {
						System.out.println(info[0]);
						System.out.println(info[1]);
						System.out.println(info[2]);
					} catch (Exception e) {
						System.out.println(e);
					}
				} // End debug messages
				if (!info[0].equals(null)) {
					try {
						target = ProxyServer.getInstance().getServerInfo("s" + info[1]);
					} catch (Exception e) {
						target = null;
					}
					if (target !=null) {// if server exists
						Boolean permission = false;
						try {// get permission for joining world
							permission = MYSQL_CONNECTOR_GET_PERMISSION.main(
									player.getUniqueId().toString().replace('-', ' ').replaceAll("\\s", ""),
									Integer.parseInt(args[0]));
						} catch (Exception e) {
							permission = false;
						}
						try{
						if (permission) {// if player is allowed to join world
							// Register transmission
							System.out.println(MYSQL_CONNECTOR_PLAYER_TRANSMISSION.main(player.getDisplayName(),
									info[0] + info[2]));
							// Connect to Server
							player.connect(target);

						} else {// send no permission error
							player.sendMessage(
									new ComponentBuilder("Dir wurde noch nicht die Erlaubnis zum joinen erteilt!")
											.color(ChatColor.WHITE).create());
						} // end permission check
						}catch(Exception e){//something always went wrong when checking for permission
							player.sendMessage(
									new ComponentBuilder("Dir wurde noch nicht die Erlaubnis zum joinen erteilt!")
											.color(ChatColor.WHITE).create());
						}

					} else {// send error-Message for not existing Server
						player.sendMessage(
								new ComponentBuilder("Der Server ist bisher noch nicht gestartet: " + "s" + info[1])
										.color(ChatColor.WHITE).create());
					} // end server exists
				} else {// Send error for not existing world_id
					player.sendMessage(new ComponentBuilder("Diese Welt ist bisher noch nicht registriert!")
							.color(ChatColor.WHITE).create());
				} // end world exists
			} else {
				// Send fatal error Message
				player.sendMessage(new ComponentBuilder("Bitte verwende diesen Command nur auf dem hub!")
						.color(ChatColor.WHITE).create());
			} // End hub_exists
		} else {
			// Send No input error
			player.sendMessage(new ComponentBuilder("Unter Umständen solltest du vielleicht eine Welt-ID angeben?")
					.color(ChatColor.WHITE).create());
		} // end connector
	}
}
