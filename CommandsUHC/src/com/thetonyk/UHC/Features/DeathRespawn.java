package com.thetonyk.UHC.Features;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
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
		
		Player death = event.getEntity();
		
		new BukkitRunnable() {
			
			public void run() {
				
				death.spigot().respawn();
				
			}
			
		}.runTaskLater(Main.uhc, 10);
		
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		
		Status status = GameUtils.getStatus();
		Player player = event.getPlayer();
		
		if (status != Status.PLAY) return;
		
		event.setRespawnLocation(Bukkit.getWorld("lobby").getSpawnLocation().add(0.5, 0, 0.5));
		player.setGameMode(GameMode.ADVENTURE);
		player.setMaxHealth(20.0);
		
		//if (GameUtils.getDeath(player.getUniqueId())) return;
		
		player.sendMessage(Main.PREFIX + "Thanks for playing! Please don't rage or insult please.");
		
		ComponentBuilder text = Main.getPrefixComponent().append("Follow us on Twitter ").color(GRAY).append("@CommandsPVP").color(AQUA).italic(true);
		text.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to see our ").color(GRAY).append("Twitter").color(GREEN).append(".").color(GRAY).create()));
		text.event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://twitter.com/commandspvp"));
		text.append(" for next games.").retain(FormatRetention.NONE).color(GRAY);
		
		player.spigot().sendMessage(text.create());
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent event) {
		
		Status status = GameUtils.getStatus();
		
		if (status != Status.PLAY) return;
		
		LoginPlayer.updateVisibility();
		
	}
	
}
