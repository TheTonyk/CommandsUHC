package com.thetonyk.UHC.Features;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Utils.GameUtils;
import com.thetonyk.UHC.Utils.GameUtils.Status;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;

import static net.md_5.bungee.api.ChatColor.*;

public class DeathRespawn implements Listener {

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		
		new BukkitRunnable() {
			
			public void run() {
				
				event.getEntity().spigot().respawn();
				
			}
			
		}.runTaskLater(Main.uhc, 2);
		
		if (GameUtils.getStatus() != Status.PLAY || !GameUtils.getWorld().equalsIgnoreCase(event.getEntity().getWorld().getName())) return;
		
		event.getEntity().setWhitelisted(false);
		GameUtils.players.get(event.getEntity().getUniqueId()).put("death", "true");
		
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		
		if (GameUtils.getStatus() != Status.PLAY) return;
		
		event.setRespawnLocation(Bukkit.getWorld("lobby").getSpawnLocation().add(0.5, 0, 0.5));
		event.getPlayer().setGameMode(GameMode.ADVENTURE);
		event.getPlayer().setMaxHealth(20.0);
		event.getPlayer().sendMessage(Main.PREFIX + "Thanks for playing! Please don't rage or spoil please.");
		
		ComponentBuilder text = Main.getPrefixComponent().append("Follow us on Twitter ").color(GRAY).append("@CommandsPVP").color(AQUA).italic(true);
		text.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to see our ").color(GRAY).append("Twitter").color(GREEN).append(".").color(GRAY).create()));
		text.event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://twitter.com/commandspvp"));
		text.append(" for next games.").retain(FormatRetention.NONE).color(GRAY);
		
		event.getPlayer().spigot().sendMessage(text.create());
		
	}
	
}
