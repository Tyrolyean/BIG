package server_inner_part;

import java.util.logging.Level;

import org.bukkit.entity.Player;

public class Tools {

	public static String intToRank(int rankInt) {

		String rankString = new String();

		switch (rankInt) {

		case 0:
			rankString = "§4Administrator§f";

			break;
		case 1:
			rankString = "§6Weltenbesitzer§f";

			break;
		case 2:
			rankString = "§fSpieler§f";

			break;

		}

		return rankString;

	}
	
	public static void updatePlayerRanks(){
		
		for(Player p:JoinLeave.server.getOnlinePlayers()){
			int rank=MysqlConnectorPlayer.getRank(p);
			
			JoinLeave.ranks.put(p, rank);
			
			p.setDisplayName(intToRank(rank)+" | "+p.getName());
			p.setPlayerListName(intToRank(rank)+" | "+p.getName());
			p.setCustomName(intToRank(rank)+" | "+p.getName());
			p.setCustomNameVisible(true);
			Tools.disguisePlayer(p,intToRank(rank)+" | "+p.getName());
			if(JoinLeave.debug()){
				JoinLeave.logger.log(Level.INFO, "Set "+p.getName()+"'s Name to "+p.getDisplayName());
			}
			
		}
		
	}
	
	public static void disguisePlayer(Player p, String newName){
        
		ProfileLoader pl =new ProfileLoader(p.getUniqueId().toString(),newName);
		pl.loadProfile();
		
    }

}
