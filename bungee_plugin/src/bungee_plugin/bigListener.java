package bungee_plugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class bigListener implements Listener {
    @EventHandler
    public void onServerConnected(final ServerConnectedEvent event) {
        event.getPlayer().sendMessage(new ComponentBuilder("Welcome to " + event.getServer().getInfo().getName() + "! You have a ping of "+event.getPlayer().getPing()).color(ChatColor.GREEN).create());
    }
}