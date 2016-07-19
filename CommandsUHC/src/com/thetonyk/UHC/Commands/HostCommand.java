package com.thetonyk.UHC.Commands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Inventories.GameInventory;
import com.thetonyk.UHC.Utils.GameUtils;
import com.thetonyk.UHC.Utils.GameUtils.Status;

public class HostCommand implements CommandExecutor {
	
	public static Map<UUID, GameInventory> inventories = new HashMap<UUID, GameInventory>();
	private static Map<UUID, BukkitTask> check = new HashMap<UUID, BukkitTask>();
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("uhc.list")) {
			
			sender.sendMessage(Main.NO_PERMS);
			return true;
			
		}
		
		Player player = Bukkit.getPlayer(sender.getName());
		
		if (args.length > 0 && args[0].equalsIgnoreCase("reset")) {
			
			if (HostCommand.check.containsKey(player.getUniqueId())) {
				
				HostCommand.check.get(player.getUniqueId()).cancel();
				HostCommand.check.clear();
				GameUtils.resetGame();
				return true;
				
			}
			
			BukkitTask timer = new BukkitRunnable() {
				
				public void run() {
					
					if (!HostCommand.check.containsKey(player.getUniqueId())) return;
					
					HostCommand.check.remove(player.getUniqueId());
					
					cancel();
					
				}
				
			}.runTaskLater(Main.uhc, 30);
			
			HostCommand.check.put(player.getUniqueId(), timer);
			sender.sendMessage(Main.PREFIX + "Are you sure? It will reset all the game and players will loose everything.");
			sender.sendMessage(Main.PREFIX + "Type §6/" + label + " reset §7another time to confirm.");
			return true;
			
		}
		
		if (GameUtils.getStatus() != Status.NONE) {
			
			sender.sendMessage(Main.PREFIX + "You can't schedule a game when another game is running");
			return true;
			
		}
		
		if (!HostCommand.inventories.containsKey(player.getUniqueId())) HostCommand.inventories.put(player.getUniqueId(), new GameInventory());
		
		player.openInventory(HostCommand.inventories.get(player.getUniqueId()).getInventory());
		return true;
		
	}

}
