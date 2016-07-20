package com.thetonyk.UHC.Features;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Utils.GameUtils;
import com.thetonyk.UHC.Utils.GameUtils.Status;
import com.thetonyk.UHC.Utils.PlayerUtils;
import com.thetonyk.UHC.Utils.TeamsUtils;

public class DeathMessage implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onDeath(PlayerDeathEvent event) {
		
		UUID uuid = event.getEntity().getUniqueId();
		String name = event.getEntity().getName();
		String oldMessage = event.getDeathMessage();
		Player killer = event.getEntity().getKiller();
		Status status = GameUtils.getStatus();
		
		event.setDeathMessage(null);
		
		if (status != Status.PLAY || GameUtils.getDeath(uuid) || GameUtils.getSpectate(uuid)) return;
		
		String victimName = PlayerUtils.getRank(uuid).getPrefix() + ((TeamsUtils.getTeam(uuid) != null) ? TeamsUtils.getTeamPrefix(uuid) : "§7") + name + "§7";
		String killerName = killer == null ? null : PlayerUtils.getRank(killer.getUniqueId()).getPrefix() + ((TeamsUtils.getTeam(killer.getUniqueId()) != null) ? TeamsUtils.getTeamPrefix(killer.getUniqueId()) : "§7") + killer.getName() + "§7";		
		String message = Main.PREFIX + "§7" + oldMessage.substring(0, oldMessage.contains("using") ? oldMessage.indexOf("using") : oldMessage.length()).replaceAll(name, victimName).replaceAll(killer != null ? killer.getName() : "", killer != null ? killerName : "");
		
		GameUtils.setDeath(uuid, true);
		event.getEntity().setWhitelisted(false);
		LoginPlayer.updateVisibility();
		
		Bukkit.broadcastMessage(message);
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			
			if (PlayerUtils.getNosoundState(player) == 1) continue;
			
			player.playSound(player.getLocation(), Sound.ZOMBIE_REMEDY, 1, 1);
			
		}
		
		Bukkit.broadcastMessage(Main.PREFIX + "There are §a" + GameUtils.getAlives().size() + " §7players alive.");
		
	}
	
}
