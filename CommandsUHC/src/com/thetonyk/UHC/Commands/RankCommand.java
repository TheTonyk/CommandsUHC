package com.thetonyk.UHC.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Utils.PlayerUtils;
import com.thetonyk.UHC.Utils.PlayerUtils.Rank;

public class RankCommand implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			
		if (!sender.hasPermission("global.rank")) {
			
			sender.sendMessage(Main.NO_PERMS);
			return true;
			
		}
		
		if (args.length < 1) {
			
			sender.sendMessage(Main.PREFIX + "Usage: /" + label + " <player> <rank>");
			return true;
			
		}
		
		if (args.length < 2) {
				
			sender.sendMessage(Main.PREFIX + PlayerUtils.getRanks());
			return true;
				
		}
		
		UUID uuid = PlayerUtils.getUUID(args[0]);
		Player player = Bukkit.getPlayer(uuid);
		Rank rank = null;
		
		if (uuid == null) {
			
			sender.sendMessage(Main.PREFIX + "This player is not known on this server");
			return true;
		
		}
		
		try {
			
			rank = Rank.valueOf(args[1].toUpperCase());
			
		} catch (Exception exception) {
			
			sender.sendMessage(Main.PREFIX + PlayerUtils.getRanks());
			return true;
			
		}
		
		if (rank == null) {
			
			sender.sendMessage(Main.PREFIX + PlayerUtils.getRanks());
			return true;
			
		}
			
		PlayerUtils.setRank(uuid, rank);
		
		sender.sendMessage(Main.PREFIX + "The rank of '§6" + args[0] + "§7' has been set to '§6" + rank.toString().toLowerCase() + "§7'.");
		
		if (player == null) return true;
		
		if (sender.getName() != player.getName()) player.sendMessage(Main.PREFIX + "Your rank was set to '§6" + rank.toString().toLowerCase() + "§7'.");
		
		return true;
		
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("global.rank")) return null;
		
		List<String> complete = new ArrayList<String>();
		
		if (args.length == 1) {

			for (Player player : Bukkit.getOnlinePlayers()) {
				
				complete.add(player.getName());
				
			}
			
		} else if (args.length == 2) {
			
			for (Rank rank : Rank.values()) {
				
				complete.add(rank.name().toLowerCase());
				
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
