package com.thetonyk.UHC.Features;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.github.paperspigot.Title;

import com.thetonyk.UHC.Utils.DisplayUtils;
import com.thetonyk.UHC.Utils.GameUtils;
import com.thetonyk.UHC.Utils.PermissionsUtils;
import com.thetonyk.UHC.Utils.PlayerUtils;
import com.thetonyk.UHC.Utils.TeamsUtils;
import com.thetonyk.UHC.Utils.GameUtils.Status;

public class LoginPlayer implements Listener {
	
	public LoginPlayer() {
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			
			Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
			player.setScoreboard(scoreboard);
			PermissionsUtils.setPermissions(player);
			PermissionsUtils.updateBungeePermissions(player);
			
		}
		
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {

		Status status = GameUtils.getStatus();
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		
		if (player.isDead()) player.spigot().respawn();
		
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		player.setScoreboard(scoreboard);
		DisplayUtils.showCoord(player);
		player.setNoDamageTicks(0);
		
		event.setJoinMessage("§7[§a+§7] " + PlayerUtils.getRank(uuid).getPrefix() + ((TeamsUtils.getTeam(uuid) != null) ? TeamsUtils.getTeamPrefix(uuid) : "§7") + player.getName());
		
		if ((status == Status.TELEPORT || status == Status.PLAY || status == Status.END) && (!GameUtils.getDeath(uuid) || GameUtils.getSpectate(uuid))) return;
			
		Title title = new Title("§aUHC by CommandsPVP", "§7UHC §a" + (GameUtils.getTeamType() == null ? "FFA" : (GameUtils.getTeamSize() < 2 ? SpecInfo.formatName(GameUtils.getTeamType().name()) : GameUtils.getTeamType().getSyntax() + "To" + GameUtils.getTeamSize())) + (GameUtils.getHost() != null ? " §7⋯ Host §a" + PlayerUtils.getName(PlayerUtils.getId(GameUtils.getHost())) : ""), 0, 40, 10);
		player.sendTitle(title);
		
		Location spawn = Bukkit.getWorld("lobby").getSpawnLocation().add(0.5, 0, 0.5);
		player.teleport(spawn);
		player.setGameMode(GameMode.ADVENTURE);
		PlayerUtils.clearInventory(player);
		PlayerUtils.clearXp(player);
		PlayerUtils.feed(player);
		PlayerUtils.heal(player);
		PlayerUtils.clearEffects(player);
		player.setMaxHealth(20.0);
		
	}
	
	public static void updateVisibility() {
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			
			for (Player online : Bukkit.getOnlinePlayers()) {
				
				if (player.equals(online)) continue;
				
				if (GameUtils.getDeath(online.getUniqueId())) {
					
					player.hidePlayer(online);
					continue;
					
				}
				
				if (!GameUtils.getSpectate(player.getUniqueId()) && GameUtils.getSpectate(online.getUniqueId())) {
					
					player.hidePlayer(online);
					continue;
					
				}
				
				player.showPlayer(online);
				
			}
			
		}
		
	}

}
