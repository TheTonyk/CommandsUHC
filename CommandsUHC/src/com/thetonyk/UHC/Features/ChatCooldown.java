package com.thetonyk.UHC.Features;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.thetonyk.UHC.Main;

public class ChatCooldown implements Listener {

	List<UUID> cooldown = new ArrayList<UUID>();
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onChat (AsyncPlayerChatEvent event) {
		
		UUID uuid = event.getPlayer().getUniqueId();
		
		if (cooldown.contains(uuid)) {
			
			event.setCancelled(true);
			return;
			
		}
		
		cooldown.add(uuid);
		
		new BukkitRunnable() {
			
			public void run() {
				
				cooldown.remove(uuid);
				
			}
			
		}.runTaskLater(Main.uhc, 40);
		
	}
	
}
