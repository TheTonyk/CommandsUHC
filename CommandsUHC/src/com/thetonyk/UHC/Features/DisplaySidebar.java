package com.thetonyk.UHC.Features;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Utils.GameUtils;
import com.thetonyk.UHC.Utils.PlayerUtils;
import com.thetonyk.UHC.Utils.GameUtils.Status;
import com.thetonyk.UHC.Utils.TeamsUtils;

public class DisplaySidebar implements Listener {
	
	public DisplaySidebar() {
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			
			update(player);
			
		}
		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {

		update(event.getPlayer());
		
	}
	
	@EventHandler
	public void onEnable(PluginEnableEvent event) {
		
		for (Player player : Bukkit.getOnlinePlayers()) {
					
			DisplaySidebar.update(player);
			
		}
		
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		
		UUID uuid = event.getEntity().getUniqueId();
		Player killer = event.getEntity().getKiller();
		
		if (GameUtils.getStatus() != Status.PLAY || GameUtils.getDeath(uuid)) return;
		
		new BukkitRunnable() {
			
			public void run() {
				
				for (Player player : Bukkit.getOnlinePlayers()) {
					
					update(player);
					
				}
				
			}
			
		}.runTaskLater(Main.uhc, 10);
		
		if (killer == null) {
			
			addPVE();
			return;
			
		}
		
		String deathTeam = TeamsUtils.getTeam(uuid);
		String killerTeam = TeamsUtils.getTeam(killer.getUniqueId());
		
		if (deathTeam != null && killerTeam != null && deathTeam.equalsIgnoreCase(killerTeam)) return;
		
		if (killer.equals(event.getEntity())) return;
		
		Map<UUID, Integer> kills = GameUtils.getKills();
		int kill = kills.containsKey(killer.getUniqueId()) ? kills.get(killer.getUniqueId()) + 1 : 1;
		
		kills.put(killer.getUniqueId(), kill);
		GameUtils.setKills(kills);
		
	}
	
	public static void update(Player player) {
		
		Status status = GameUtils.getStatus();
		Scoreboard scoreboard = player.getScoreboard();
		Objective sidebar = scoreboard.getObjective("sidebar");
		
		for (String entry : scoreboard.getEntries()) {
			
			if (!entry.startsWith("  ")) continue;
			
			scoreboard.resetScores(entry);
			
		}
		
		if (sidebar == null) {
		
			sidebar = scoreboard.registerNewObjective("sidebar", "dummy");
			sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
			sidebar.setDisplayName("§a§lUHC §8⫸ §7CommandsPVP");
		
		}
		
		if (status != Status.PLAY && status != Status.END) {

			int i = 0;

			sidebar.getScore("  §b@CommandsPVP").setScore(i++);
			sidebar.getScore(" ").setScore(i++);
			sidebar.getScore("    §8⫸ §7Vanilla+").setScore(i++);
			sidebar.getScore("  §6Scenarios").setScore(i++);
			sidebar.getScore("  ").setScore(i++);
			sidebar.getScore("    §8⫸ §7/rules").setScore(i++);
			sidebar.getScore("    §8⫸ §7/helpop").setScore(i++);
			sidebar.getScore("  §6Help").setScore(i++);
			sidebar.getScore("   ").setScore(i++);
			sidebar.getScore("    §8⫸ §7" + (GameUtils.getTeamType() == null ? "FFA" : GameUtils.getTeamType()) + (GameUtils.getTeamSize() > 1 ? GameUtils.getTeamSize() + " " : "")).setScore(i++);
			sidebar.getScore("  §6Team Size").setScore(i++);
			sidebar.getScore("    ").setScore(i++);
			
		} else {
		
			sidebar.getScore("  ").setScore(99);
			sidebar.getScore("  §6PVE §8⫸ §a" + GameUtils.getPVE()).setScore(98);
			
			Map<UUID, Integer> kills = GameUtils.getKills();
			
			for (UUID killer : kills.keySet()) {
				
				String name = PlayerUtils.getName(PlayerUtils.getId(killer));
				String score = "  " + (GameUtils.getDeath(killer) ? "§c☠ " : "  ") + PlayerUtils.getRank(killer).getPrefix() + ((TeamsUtils.getTeam(killer) != null) ? TeamsUtils.getTeamPrefix(killer) : "§7") + name;
				sidebar.getScore(score).setScore(kills.get(killer));
				
			}
			
			sidebar.getScore(" ").setScore(-1);
			
		
		}
		
	}
	
	public static void addPVE() {
		
		int pve = GameUtils.getPVE();
		GameUtils.setPVE(pve + 1);
		
	}

}
