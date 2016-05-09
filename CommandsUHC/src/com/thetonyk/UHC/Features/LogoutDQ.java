package com.thetonyk.UHC.Features;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Events.StartEvent;
import com.thetonyk.UHC.Utils.GameUtils;
import com.thetonyk.UHC.Utils.PlayerUtils;
import com.thetonyk.UHC.Utils.TeamsUtils;
import com.thetonyk.UHC.Utils.GameUtils.Status;

public class LogoutDQ implements Listener {

	private static Map<UUID, BukkitRunnable> offlineTimers = new HashMap<UUID, BukkitRunnable>();
	private static Map<UUID, Long> offlineTime = new HashMap<UUID, Long>();
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		
		if (GameUtils.getStatus() != Status.PLAY) return;
		
		if (!GameUtils.getWorld().equalsIgnoreCase(event.getPlayer().getWorld().getName()))
		
		if (!event.getPlayer().isWhitelisted()) return;
		
		startTimer(event.getPlayer());
		
	}
	
	@EventHandler
	public void onStart(StartEvent event) {
		
		for (OfflinePlayer player : Bukkit.getWhitelistedPlayers()) {
			
			if (player.isOnline()) continue;
			
			startTimer(player);
			
		}
		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		
		if (GameUtils.getStatus() != Status.PLAY) return;
		
		if (!event.getPlayer().isWhitelisted()) return;
		
		if (!offlineTimers.containsKey(event.getPlayer().getUniqueId()) || offlineTimers.get(event.getPlayer().getUniqueId()) == null) return;
		
		offlineTimers.get(event.getPlayer().getUniqueId()).cancel();
		offlineTimers.put(event.getPlayer().getUniqueId(), null);
		offlineTime.remove(event.getPlayer().getUniqueId());
		
	}
	
	public static void reset() {
		
		for (UUID uuid : offlineTimers.keySet()) {
			
			if (offlineTimers.get(uuid) == null) continue;
			
			offlineTimers.get(uuid).cancel();
			
		}
		
		offlineTimers.clear();
		offlineTime.clear();
		
	}
	
	private static void startTimer(OfflinePlayer player) {
		
		BukkitRunnable counter = new BukkitRunnable() {
		
			public void run() {
				
				player.setWhitelisted(false);
				GameUtils.players.get(player.getUniqueId()).put("death", "true");
				Bukkit.broadcastMessage(Main.PREFIX + PlayerUtils.getRank(player.getName()).getPrefix() + ((TeamsUtils.getTeam(player.getName()) != null) ? TeamsUtils.getTeamPrefix(player.getName()) : "§7") + player.getName() + "§7" + " died offline");
				DisplaySidebar.addPve();
				
				for (Player player : Bukkit.getOnlinePlayers()) {
					
					DisplaySidebar.update(player);
					
					if (PlayerUtils.getNosoundState(player) == 1) continue;
					
					player.playSound(player.getLocation(), Sound.ZOMBIE_REMEDY, 1, 1);
					
				}
				
				new BukkitRunnable() {
					
					public void run() {
						
						Bukkit.broadcastMessage(Main.PREFIX + "There are §a" + Bukkit.getWhitelistedPlayers().size() + " §7players alive.");
					
					}
					
				}.runTaskLater(Main.uhc, 1);
				
				offlineTimers.put(player.getUniqueId(), null);
				offlineTimers.remove(player.getUniqueId());
				offlineTime.remove(player.getUniqueId());
				
			}
			
		};
		
		int time = 0;
		if (DisplayTimers.getTimeLeftMeetup() > 300) time = 18000;
		else if (DisplayTimers.getTimeLeftMeetup() > 0) time = 12000;
		else time = 600;
		
		offlineTime.put(player.getUniqueId(), new Date().getTime());
		offlineTimers.put(player.getUniqueId(), counter);
		offlineTimers.get(player.getUniqueId()).runTaskLater(Main.uhc, time);
			
	}
	
	public static int offlineSince(UUID uuid) {
		
		if (!offlineTime.containsKey(uuid)) return 0;
		
		return (int) (new Date().getTime() - offlineTime.get(uuid)) / 1000;
		
	}
	
}
