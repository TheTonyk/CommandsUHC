package com.thetonyk.UHC.Features;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Utils.GameUtils;
import com.thetonyk.UHC.Utils.GameUtils.Status;

public class DeathMessage implements Listener {

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		
		if (GameUtils.getStatus() != Status.PLAY) return;
		
		if (!GameUtils.getWorld().equalsIgnoreCase(event.getEntity().getWorld().getName())) return;
		
		event.setDeathMessage(Main.PREFIX + "§6" + event.getDeathMessage());
		
		new BukkitRunnable() {
			
			public void run() {
				
				Bukkit.broadcastMessage(Main.PREFIX + "There are §a" + Bukkit.getWorld(GameUtils.getWorld()).getPlayers().size() + " §7players alive.");
			
			}
			
		}.runTaskLater(Main.uhc, 1);
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			
			player.playSound(player.getLocation(), Sound.ZOMBIE_REMEDY, 1, 1);
			
		}
		
	}
	
}
