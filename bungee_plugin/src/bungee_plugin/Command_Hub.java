package bungee_plugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
		
public class Command_Hub extends Command {


    public Command_Hub(){
        super("hub");
    }
    @Override
    public void execute(CommandSender sender, String[] args){
    	ProxiedPlayer player = (ProxiedPlayer) sender;
    	ServerInfo target = ProxyServer.getInstance().getServerInfo("hub");
    	if (player.getServer()==target){
    		player.sendMessage(new ComponentBuilder("You are already connected to the Hub!").color(ChatColor.RED).create());
    	}
    	else{
    	player.connect(target);
    	}
    }

}
