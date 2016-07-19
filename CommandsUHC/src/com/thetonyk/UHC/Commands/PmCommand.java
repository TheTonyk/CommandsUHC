package com.thetonyk.UHC.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.thetonyk.UHC.Main;

public class PmCommand implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("uhc.team")) {
			
			sender.sendMessage(Main.NO_PERMS);
			return true;
			
		}
		
		sender.sendMessage(Main.PREFIX + "Team's commands:");
		sender.sendMessage("§8⫸ §6/t <message> §8- §7Talk with your team.");
		sender.sendMessage("§8⫸ §6/tc §8- §7Send your coords to your team.");
		return true;
		
	}

}

