package com.thetonyk.UHC.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Features.DisplayTimers;
import com.thetonyk.UHC.Utils.DisplayUtils;
import com.thetonyk.UHC.Utils.GameUtils;
import com.thetonyk.UHC.Utils.GameUtils.Status;
import com.thetonyk.UHC.Utils.PlayerUtils;
import com.thetonyk.UHC.Utils.TeleportUtils;
import com.thetonyk.UHC.Utils.WorldUtils;

public class StartCommand implements CommandExecutor {
	
	private Boolean start = false;
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("uhc.start")) {
			
			sender.sendMessage(Main.NO_PERMS);
			return true;
			
		}
		
		if (GameUtils.getWorld() == null) {
			
			sender.sendMessage(Main.PREFIX + "You need to setup the game first.");
			return true;
			
		}
		
		if (GameUtils.getStatus() == Status.NONE) {
			
			sender.sendMessage(Main.PREFIX + "The game is not ready.");
			return true;
			
		}
		
		if (GameUtils.getStatus() == Status.READY) {
			
			sender.sendMessage(Main.PREFIX + "You need to teleport players first.");
			return true;
			
		}
		
		if (GameUtils.getStatus() != Status.TELEPORT) {
			
			sender.sendMessage(Main.PREFIX + "Game has already started.");
			return true;
			
		}
		
		if (start) {
			
			sender.sendMessage(Main.PREFIX + "You have already started the game.");
			return true;
			
		}
		
		start = true;
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			
			DisplayUtils.sendTitle(player, "§45", "", 0, 20, 0);
			player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
			
		}
		
		Bukkit.getWorld(GameUtils.getWorld()).setDifficulty(Difficulty.HARD);
		Bukkit.getWorld(GameUtils.getWorld()).setTime(0);
		
		new BukkitRunnable() {
			
			public void run() {
				
				for (Player player : Bukkit.getOnlinePlayers()) {
					
					DisplayUtils.sendTitle(player, "§c4", "", 0, 20, 0);
					player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
					
				}
				
			}
			
		}.runTaskLater(Main.uhc, 20);
		
		new BukkitRunnable() {
			
			public void run() {
				
				for (Player player : Bukkit.getOnlinePlayers()) {
					
					DisplayUtils.sendTitle(player, "§63", "", 0, 20, 0);
					player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
					
				}
				
			}
			
		}.runTaskLater(Main.uhc, 40);
		
		new BukkitRunnable() {
			
			public void run() {
				
				for (Player player : Bukkit.getOnlinePlayers()) {
					
					DisplayUtils.sendTitle(player, "§22", "", 0, 20, 0);
					player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
					
				}
				
			}
			
		}.runTaskLater(Main.uhc, 60);
		
		new BukkitRunnable() {
			
			public void run() {
				
				for (Player player : Bukkit.getOnlinePlayers()) {
					
					DisplayUtils.sendTitle(player, "§a1", "", 0, 20, 0);
					player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
					
				}
				
			}
			
		}.runTaskLater(Main.uhc, 80);
		
		new BukkitRunnable() {
			
			public void run() {
				
				for (int i = 0; i < 150; i++) {
					
					Bukkit.broadcastMessage("");
					
				}
				
				Bukkit.broadcastMessage(Main.PREFIX + "The game is starting now.");
				Bukkit.broadcastMessage(Main.PREFIX + "Timers:");
				Bukkit.broadcastMessage("§8⫸ §7Final Heal: §a60 seconds§7.");
				Bukkit.broadcastMessage("§8⫸ §7PVP: §a15 minutes§7.");
				Bukkit.broadcastMessage("§8⫸ §7Meetup: §a60 minutes§7.");
				Bukkit.broadcastMessage(Main.PREFIX + "Good luck & Have Fun!");
				
				Bukkit.getWorld(GameUtils.getWorld()).getWorldBorder().setSize(WorldUtils.getSize(GameUtils.getWorld()));
				Bukkit.getWorld(GameUtils.getWorld()).setPVP(false);
				GameUtils.setStatus(Status.PLAY);
				DisplayTimers.startTimer();
				start = false;
				
				for (Player player : Bukkit.getOnlinePlayers()) {
					
					DisplayUtils.sendTitle(player, "", "§7Go§a! §7Go§a! §7Go§a!", 5, 30, 5);
					player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
					
					player.setGameMode(GameMode.SURVIVAL);
					PlayerUtils.clearEffects(player);
					PlayerUtils.clearInventory(player);
					PlayerUtils.clearXp(player);
					PlayerUtils.feed(player);
					PlayerUtils.heal(player);
					
				}
				
				TeleportUtils.removeSpawns(TeleportCommand.locations);
				
			}
			
		}.runTaskLater(Main.uhc, 100);
			
		return true;
		
	}

}