package server_inner_part;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.entity.Player;

public class Loop {

	public static void main() {
		// Called first when the Programm starts!
		// Explanation:
		// Run Task Later is used in order to Run the world-Creation
		// Syncroniously because else the Server isn't creaing the world
		// Creating every world hapens on the Server-Start. Every 30 Seconds the
		// worlds are updated with an mysql-Databse
		// In an earlyer Version of this Plugin MultiVerse Core was used so if
		// you find something from Multiverse please delete it
		//
		//
		//
		//
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				//Setting Ranks
				JoinLeave jl =new JoinLeave();
				for(Player p:jl.getServer().getOnlinePlayers()){
					
					
					
				}
				
				
				
				
				
				
				
				
				
				
				
			}
		}, 30000, 30000);

	}
}