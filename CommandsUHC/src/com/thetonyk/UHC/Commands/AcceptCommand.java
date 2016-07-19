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
import com.thetonyk.UHC.Utils.GameUtils;
import com.thetonyk.UHC.Utils.TeamsUtils;
import com.thetonyk.UHC.Utils.GameUtils.Status;
import com.thetonyk.UHC.Utils.GameUtils.TeamType;
import com.thetonyk.UHC.Utils.PlayerUtils;

import static net.md_5.bungee.api.ChatColor.*;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;

public class AcceptCommand implements CommandExecutor, TabCompleter {
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("uhc.team")) {
			
			sender.sendMessage(Main.NO_PERMS);
			return true;
			
		}
		
		if (args.length > 0) {
			
			Status status = GameUtils.getStatus();
			Player player = Bukkit.getPlayer(sender.getName());
			UUID uuid = PlayerUtils.getUUID(args[0]);
			String name = PlayerUtils.getName(PlayerUtils.getId(uuid));
			String team = TeamsUtils.getTeam(uuid);
			
			if (status == Status.TELEPORT || status == Status.PLAY || status == Status.END) {
				
				sender.sendMessage(Main.PREFIX + "The game has already started.");
				return true;
				
			}
			
			if (GameUtils.getTeamSize() < 2 || GameUtils.getTeamType() != TeamType.CHOSEN) {
				
				sender.sendMessage(Main.PREFIX + "You can't join a team in " + (GameUtils.getTeamType() == null ? "FFA" : GameUtils.getTeamType().name() + " teams") + ".");
				return true;
				
			}
			
			if (uuid == null) {
				
				sender.sendMessage(Main.PREFIX + "This player has not invited you.");
				return true;
				
			}
			
			if (TeamsUtils.getTeam(player.getUniqueId()) != null) {
				
				ComponentBuilder message = Main.getPrefixComponent().append("You are already in a team, ").color(GRAY).append("leave it first").color(AQUA).italic(true);
				message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click on this text to leave your team.").color(GRAY).create()));
				message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/team leave"));
				message.append(".").retain(FormatRetention.NONE).color(GRAY);
		        player.spigot().sendMessage(message.create());
				return true;
				
			}
			
			if (!TeamsUtils.invitations.containsKey(uuid) || !TeamsUtils.invitations.get(uuid).contains(player.getUniqueId())) {
				
				sender.sendMessage(Main.PREFIX + "This player has not invited you.");
				return true;
				
			}
			
			if (team == null) {
				
				sender.sendMessage(Main.PREFIX + "This invitation was canceled.");
				return true;
				
			}
			
			if (GameUtils.getSpectate(uuid)) {
				
				sender.sendMessage(Main.PREFIX + "You can't join this player.");
				return true;
				
			}
			
			if (TeamsUtils.getTeamMembers(team).size() >= TeamCommand.size) {
				
				sender.sendMessage(Main.PREFIX + "This team is already full.");
				return true;
				
			}
			
			sender.sendMessage(Main.PREFIX + "You joined the team of '§6" + name + "§7'.");
			TeamsUtils.sendMessage(team, Main.PREFIX + "The player '§6" + player.getName() + "§7' joined your team.");
			
			if (TeamsUtils.invitations.containsKey(player.getUniqueId())) TeamsUtils.invitations.remove(player.getUniqueId());
			TeamsUtils.joinTeam(player.getUniqueId(), team);
			TeamsUtils.invitations.get(uuid).remove(player.getUniqueId());
			return true;
			
		}	
			
		sender.sendMessage(Main.PREFIX + "Usage: /" + label + " <player>");
		return true;
		
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("uhc.team")) return null;
		
		List<String> complete = new ArrayList<String>();
		
		if (args.length == 1) {
			
			Player player = Bukkit.getPlayer(sender.getName());

			for (UUID uuid : TeamsUtils.invitations.keySet()) {
				
				String name = PlayerUtils.getName(PlayerUtils.getId(uuid));
				
				if (TeamsUtils.invitations.get(uuid).contains(player.getUniqueId())) complete.add(name);
				
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
