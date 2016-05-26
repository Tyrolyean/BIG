package bungee_plugin;

import java.util.Timer;
import java.util.TimerTask;

import net.md_5.bungee.api.ProxyServer;

public class LOOP {
	public static void main(ProxyServer proxyServer) {
		// Called first when the Program starts!
		// Used for getting Information/set online-state
		try {
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					// All things put in here are checked every 10 minutes!
					try{
					if (big.debug) {
						System.out.println("Checking for new Servers!");
					}
					if (big.debug) {
						System.out.println(MYSQL_CONNECTOR_CHECK_SERVER.get_Servers(proxyServer));
					} else {
						MYSQL_CONNECTOR_CHECK_SERVER.get_Servers(proxyServer);
					}

					if (big.debug) {
						System.out.println("Did it!");
					}
					}catch(Exception e){
						e.printStackTrace(System.out);
					}
					//If Minecraft-Services are offline disable online Mode
					//This will come in the next version
									
					
					// Here it stops to do this
				}
			}, 30000, 30000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
