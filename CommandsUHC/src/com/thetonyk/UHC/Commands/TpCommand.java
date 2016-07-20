package com.thetonyk.UHC.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Utils.GameUtils;
import com.thetonyk.UHC.Utils.GameUtils.Status;

public class TpCommand implements CommandExecutor, TabCompleter {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player = Bukkit.getPlayer(sender.getName());
		
		if (!GameUtils.getSpectate(player.getUniqueId()) && !sender.hasPermission("uhc.spectate.all")) {
			
			sender.sendMessage(Main.NO_PERMS);
			return true;
			
		}
		
		if (args.length < 1) {
			
			sender.sendMessage(Main.PREFIX + "Usage: /tp <player>" + (sender.hasPermission("uhc.spectate.all") ? " [player]" : ""));
			return true;
			
		}
		
		if (sender.hasPermission("uhc.spectate.all") && args.length > 2) {
			
			int x = 0;
			int y = 0;
			int z = 0;
			
			try {
				
				x = Integer.parseInt(args[0]);
				
			} catch (Exception exception) {
				
				player.sendMessage(Main.PREFIX + "Usage: /tp <x> <y> <z>");
				return true;
				
			}
			
			try {
				
				y = Integer.parseInt(args[1]);
				
			} catch (Exception exception) {
				
				player.sendMessage(Main.PREFIX + "Usage: /tp <x> <y> <z>");
				return true;
				
			}

			try {
				
				z = Integer.parseInt(args[2]);
				
			} catch (Exception exception) {
				
				player.sendMessage(Main.PREFIX + "Usage: /tp <x> <y> <z>");
				return true;
				
			}
			
			player.teleport(new Location(player.getWorld(), y, z, x));
			player.sendMessage(Main.PREFIX + "You were teleported to §6" + x + " " + y + " " + z + ".");
			return true;
			
		}
		
		Player teleport = Bukkit.getPlayer(args[0]);
		
		if (teleport == null && (args.length < 2 || !args[0].equalsIgnoreCase("*") || !sender.hasPermission("uhc.spectate.all"))) {
			
			sender.sendMessage(Main.PREFIX + "The player '§6" + args[0] + "§7' is not online.");
			return true;
			
		}
		
		if (args.length > 1 && sender.hasPermission("uhc.spectate.all")) {
			
			Player destination = Bukkit.getPlayer(args[1]);
			
			if (destination == null) {
				
				sender.sendMessage(Main.PREFIX + "The player '§6" + args[0] + "§7' is not online.");
				return true;
				
			}
			
			if (args[0].equalsIgnoreCase("*")) {
				
				for (Player online : Bukkit.getOnlinePlayers()) {
					
					online.teleport(destination);
					
				}
				
				Bukkit.broadcastMessage(Main.PREFIX + "All players were teleported to the player '§6" + destination.getName() + "§7'.");
				return true;
				
			}
			
			teleport.teleport(destination);
			
			if (!player.equals(teleport) && !player.equals(destination)) player.sendMessage(Main.PREFIX + "The player '§6" + teleport.getName() + "§7' was teleported to the player '§6" + destination.getName() + "§7'.");
			
			teleport.sendMessage(Main.PREFIX + "Your where teleported to the player '§6" + destination.getName() + "§7'.");
			
			if (teleport.equals(destination)) return true; 
			
			destination.sendMessage(Main.PREFIX + "Your where teleported to the player '§6" + teleport.getName() + "§7'.");
			
		}
		
		player.teleport(teleport);
		player.sendMessage(Main.PREFIX + "You where teleported to the player '§6" + teleport.getName() + "§7'.");
		return true;
		
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("uhc.spectate")) return null;
		
		List<String> complete = new ArrayList<String>();
		
		if (args.length == 1) {

			if (GameUtils.getStatus() == Status.NONE || GameUtils.getStatus() == Status.READY) {
				
				for (Player player : Bukkit.getOnlinePlayers()) {
					
					UUID uuid = player.getUniqueId();
					
					complete.add(Bukkit.getPlayer(uuid).getName());
					
				}
				
			} else {
				
				for (UUID player : GameUtils.getAlives()) {
				
					if (Bukkit.getPlayer(player) == null) continue;
					
					complete.add(Bukkit.getPlayer(player).getName());
				
				}
				
			}
			
		} else if (args.length == 2 && sender.hasPermission("uhc.spectate.all")) {

			for (Player player : Bukkit.getOnlinePlayers()) {
				
				UUID uuid = player.getUniqueId();
				
				complete.add(Bukkit.getPlayer(uuid).getName());
				
			}
			
		}
		
		List<String> tabCompletions = new ArrayList<String>();
		
		if (args[args.length - 1].isEmpty()) {
			
			for (String type : complete) {
				
				tabCompletions.add(type);
				
			}
			
		} else {
			
			for (String type : complete) {
				
				if (type.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) tabCompletions.add(type);
				
			}
			
		}
		
		return tabCompletions;
		
	}
	
}
