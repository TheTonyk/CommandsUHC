package com.thetonyk.UHC.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Features.DisplayTimers;
import com.thetonyk.UHC.Utils.GameUtils;
import com.thetonyk.UHC.Utils.GameUtils.Status;

public class TimeleftCommand implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	
		if (!sender.hasPermission("uhc.timeleft")) {
			
			sender.sendMessage(Main.NO_PERMS);
			return true;
			
		}
		
		if (DisplayTimers.timer == null) {
			
			sender.sendMessage(Main.PREFIX + "The game has not started.");
			return true;
			
		}
		
		if (GameUtils.getStatus() == Status.END) {
			
			sender.sendMessage(Main.PREFIX + "The game is over");
			return true;
			
		}
		
		int time = DisplayTimers.getTime();
		int finalHeal = 45 - time;
		int pvp = DisplayTimers.getTimeLeftPVP();
		int meetup = DisplayTimers.getTimeLeftMeetup();
		
		sender.sendMessage(Main.PREFIX + "Time left:");
		sender.sendMessage("§8⫸ §7Since the game has started: §a" + DisplayTimers.getFormatedTime(time) + "§7.");
		sender.sendMessage("§8⫸ §7Final Heal: §a" + (finalHeal > 0 ? DisplayTimers.getFormatedTime(finalHeal) : "Already given") + "§7.");
		sender.sendMessage("§8⫸ §7PVP: §a" + (pvp > 0 ? DisplayTimers.getFormatedTime(pvp) : "ON") + "§7.");
		sender.sendMessage("§8⫸ §7Meetup: §a" + (meetup > 0 ? DisplayTimers.getFormatedTime(meetup) : "Now") + "§7.");
		return true;
		
	}

}
