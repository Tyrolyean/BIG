package bungee_plugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class big extends Plugin implements Listener{
	//Mysql-Database Location
	public static String mysql="192.168.0.2";
	//Global DEBUG
	public static boolean debug=true;
    @Override
    //on Enable
    public void onEnable() {
    	//Starting the Loop for CHecking for new Servers
    	LOOP.main(getProxy());
    	//Register Commands and events
    	getProxy().getPluginManager().registerCommand(this, new Command_Hub());
    	getProxy().getPluginManager().registerCommand(this, new Command_connect());
        getProxy().getPluginManager().registerListener(this, new bigListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, this);
        //End onEnable
    }
    @EventHandler
    public void onKick(ServerKickEvent event) {
    	String message = event.getKickReasonComponent().toString();
    	event.setCancelled(true);
    	ProxiedPlayer player = event.getPlayer();
    	player.connect(ProxyServer.getInstance().getServerInfo("hub"));
    	player.sendMessage(new ComponentBuilder(message)
					.color(ChatColor.RED).create());
    }
}