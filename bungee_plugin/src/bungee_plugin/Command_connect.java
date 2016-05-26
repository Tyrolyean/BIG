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
				// Go on with cODE
				try {
					target = ProxyServer.getInstance().getServerInfo("s" + MYSQL_CONNECTOR_PLAYER_TRANSMISSION.get_server(Integer.parseInt( args[0])));
				} catch (Exception e) {
					target = null;
				}
				if (target != null) {// if server exists
					Boolean permission = false;
					try {// get permission for joining world
						permission = MYSQL_CONNECTOR_GET_PERMISSION.main(
								player.getUniqueId().toString().replace('-', ' ').replaceAll("\\s", ""),
								Integer.parseInt(args[0]));
					} catch (Exception e) {
						permission = false;
					}
					try {
						if (permission) {// if player is allowed to join world
							// Registed Transmission to world with server_internal_number
							// Connect to Server
							MYSQL_CONNECTOR_PLAYER_TRANSMISSION.main(player.getDisplayName(), args[0]);
							player.connect(target);

						} else {// send no permission error
							player.sendMessage(
									new ComponentBuilder("Dir wurde noch nicht die Erlaubnis zum joinen erteilt!")
											.color(ChatColor.WHITE).create());
						} // end permission check
					} catch (Exception e) {// something always went wrong when
											// checking for permission
						player.sendMessage(
								new ComponentBuilder("Dir wurde noch nicht die Erlaubnis zum joinen erteilt!")
										.color(ChatColor.WHITE).create());
					}

				} else {// send error-Message for not existing Server
					player.sendMessage(
							new ComponentBuilder("Der Server ist bisher noch nicht gestartet: " + "s" + args[0])
									.color(ChatColor.WHITE).create());
				} // end server exists

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
