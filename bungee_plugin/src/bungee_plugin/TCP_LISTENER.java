package bungee_plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TCP_LISTENER {

	public static void start() {
		main_transmission();

	}

	private static void main_transmission() {

		new Thread() {
			@SuppressWarnings("resource")
			public void run() {
				try {
					ServerSocket welcome_socket = new ServerSocket(1945);

					// <the input comes like this:
					// Player uuid with -
					// target world
					while (true) {
						try{
						Socket conn = welcome_socket.accept();
						BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						String username = input.readLine();
						int world_id = Integer.parseInt(input.readLine());
						input.close();
						conn.close();
						if (big.debug) {
							System.out.println("Got some input: " + username + " " + world_id);

						}
						ProxiedPlayer p = big.server.getPlayer(username);
						connect(p,world_id);
					}catch(Exception e){
						e.printStackTrace();
					}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}.start();

	}

	private static void connect(ProxiedPlayer player,int world_id)
	{
		int server_id=MYSQL_CONNECTOR_PLAYER_TRANSMISSION.get_server(world_id);
		ServerInfo target = null;
		// Get hub
			Boolean hub_exists = false;
			try {

				if (player.getServer().getInfo().equals(big.server.getServerInfo("hub"))) {
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
					target = big.server.getServerInfo("s" + server_id);
				} catch (Exception e) {
					target = null;
				}
				if (target != null) {// if server exists
					Boolean permission = false;
					try {// get permission for joining world
						permission = MYSQL_CONNECTOR_GET_PERMISSION.main(
								player.getUniqueId().toString().replace('-', ' ').replaceAll("\\s", ""),
								world_id);
					} catch (Exception e) {
						permission = false;
					}
					try {
						if (permission) {// if player is allowed to join world
							// Registed Transmission to world with server_internal_number
							// Connect to Server
							MYSQL_CONNECTOR_PLAYER_TRANSMISSION.main(player.getDisplayName(), ""+world_id);
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
							new ComponentBuilder("Der Server ist bisher noch nicht gestartet: " + "s" + server_id)
									.color(ChatColor.WHITE).create());
				} // end server exists

			} else {
				// Send fatal error Message
				player.sendMessage(new ComponentBuilder("Bitte verwende diesen Command nur auf dem hub!")
						.color(ChatColor.WHITE).create());
			} // End hub_exists

	}

}
