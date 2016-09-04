package server_inner_part;

import java.util.Timer;
import java.util.TimerTask;

public class LOOP {

	public void main() {
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
				
				
				
				
				
				
				
				
				
			}
		}, 30000, 30000);

	}
}