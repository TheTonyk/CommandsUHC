package com.thetonyk.UHC.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Utils.GameUtils;
import com.thetonyk.UHC.Utils.TeamsUtils;
import com.thetonyk.UHC.Utils.GameUtils.Status;
import com.thetonyk.UHC.Utils.GameUtils.TeamType;

import static net.md_5.bungee.api.ChatColor.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;

public class InviteCommand implements CommandExecutor, TabCompleter {
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("uhc.team")) {
			
			sender.sendMessage(Main.NO_PERMS);
			return true;
			
		}
		
		if (args.length > 0) {
			
			Status status = GameUtils.getStatus();
			Player player = Bukkit.getPlayer(sender.getName());
			Player invited = Bukkit.getPlayer(args[0]);
			String team = TeamsUtils.getTeam(player.getUniqueId());
			
			if (status == Status.TELEPORT || status == Status.PLAY || status == Status.END) {
				
				sender.sendMessage(Main.PREFIX + "The game has already started.");
				return true;
				
			}
			
			if (GameUtils.getTeamSize() < 2 || GameUtils.getTeamType() != TeamType.CHOSEN) {
				
				sender.sendMessage(Main.PREFIX + "You can't join a team in " + (GameUtils.getTeamType() == null ? "FFA" : GameUtils.getTeamType().name() + " teams") + ".");
				return true;
				
			}
				
			if (invited == null) {
				
				sender.sendMessage(Main.PREFIX + "The player '§6" + args[0] + "§7' is not online.");
				return true;
				
			}
			
			if (player.getName().equalsIgnoreCase(invited.getName())) {
				
				sender.sendMessage(Main.PREFIX + "You can't invite yourslef.");
				return true;
				
			}
			
			if (team == null) {
				
				if (TeamsUtils.getTeamsLeft() < 1) {
					
					sender.sendMessage(Main.PREFIX + "There are no more available teams.");
					return true;
					
				}
				
				TeamsUtils.createTeam(player.getUniqueId());
				
			} else {
				
				if (TeamsUtils.getTeamMembers(team).size() >= TeamCommand.size) {
					
					sender.sendMessage(Main.PREFIX + "Your team is already full.");
					return true;
					
				}
				
			}
			
			if (!TeamsUtils.invitations.containsKey(player.getUniqueId())) TeamsUtils.invitations.put(player.getUniqueId(), new ArrayList<UUID>());
			
			TeamsUtils.invitations.get(player.getUniqueId()).add(invited.getUniqueId());
			
			invited.sendMessage(Main.PREFIX + "You have received an invitation from '§6" + player.getName() + "§7'.");
			
			ComponentBuilder message = Main.getPrefixComponent().append("To join his team, ").color(GRAY).append("accept the invitation").color(AQUA).italic(true);
			message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to accept the invitation of ").color(GRAY).append(player.getName()).color(GREEN).append(".").color(GRAY).create()));
			message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/team accept " + player.getName()));
			message.append(".").retain(FormatRetention.NONE).color(GRAY);
			invited.spigot().sendMessage(message.create());
	        
			if (team == null) sender.sendMessage(Main.PREFIX + "The player '§6" + invited.getName() + "§7' was invited in the team.");
			else TeamsUtils.sendMessage(team, Main.PREFIX + "The player '§6" + invited.getName() + "§7' was invited in the team.");
			
	        return true;
			
		}	
			
		sender.sendMessage(Main.PREFIX + "Usage: /invite <player>");
		return true;
		
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("uhc.team")) return null;
		
		List<String> complete = new ArrayList<String>();
		
		if (args.length == 1) {

			for (Player player : Bukkit.getOnlinePlayers()) {
				
				if (player.getName().equalsIgnoreCase(sender.getName()) || GameUtils.getDeath(player.getUniqueId()) || GameUtils.getSpectate(player.getUniqueId())) continue;
				
				complete.add(player.getName());
				
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
