package com.thetonyk.UHC.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Inventories.TeamsInventory;
import com.thetonyk.UHC.Utils.GameUtils;
import com.thetonyk.UHC.Utils.PlayerUtils;
import com.thetonyk.UHC.Utils.GameUtils.Status;
import com.thetonyk.UHC.Utils.GameUtils.TeamType;
import com.thetonyk.UHC.Utils.TeamsUtils;

import static net.md_5.bungee.api.ChatColor.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;

public class TeamCommand implements CommandExecutor, TabCompleter {
	
	public static int size = 2;
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("uhc.team")) {
			
			sender.sendMessage(Main.NO_PERMS);
			return true;
			
		}
		
		Status status = GameUtils.getStatus();
		
		if (args.length < 1) {
			
			sender.sendMessage(Main.PREFIX + "Usage of /team:");
			
			if (status != Status.TELEPORT && status != Status.PLAY && status != Status.END && GameUtils.getTeamSize() > 1 && GameUtils.getTeamType() == TeamType.CHOSEN) {
				
				sender.sendMessage("§8⫸ §6/" + label + " invite <player> §8- §7Invite a player in your team.");
				sender.sendMessage("§8⫸ §6/" + label + " accept <player> §8- §7Accept your invitations.");
				sender.sendMessage("§8⫸ §6/" + label + " leave §8- §7Leave your team.");
				
			}
			
			sender.sendMessage("§8⫸ §6/" + label + " list §8- §7List all teams.");
			sender.sendMessage("§8⫸ §6/" + label + " info [player] §8- §7Informations about a team.");
			
			if (sender.hasPermission("uhc.team.admin")) {
				
				sender.sendMessage("§8⫸ §6/" + label + " add <player> [player] §8- §7Add player to team of player.");
				sender.sendMessage("§8⫸ §6/" + label + " remove <player> §8- §7Remove player from his team.");
				sender.sendMessage("§8⫸ §6/" + label + " delete <player> §8- §7Delete team of the player.");
				sender.sendMessage("§8⫸ §6/" + label + " clear §8- §7Delete all teams.");
				sender.sendMessage("§8⫸ §6/" + label + " color §8- §7Re-color teams.");
				
			}
			
			sender.sendMessage("§8⫸ §6/t <message> §8- §7Talk with your team.");
			sender.sendMessage("§8⫸ §6/tc §8- §7Send your coords to your team.");
			return true;
			
		}
		
		Player player = Bukkit.getPlayer(sender.getName());
		String team = TeamsUtils.getTeam(player.getUniqueId());
		
		if (status != Status.TELEPORT && status != Status.PLAY && status != Status.END) {
			
			if (GameUtils.getTeamSize() < 2 || GameUtils.getTeamType() != TeamType.CHOSEN) {
				
				sender.sendMessage(Main.PREFIX + "You can't join a team in " + (GameUtils.getTeamType() == null ? "FFA" : GameUtils.getTeamType().name() + " teams") + ".");
				return true;
				
			}
			
			if (args[0].equalsIgnoreCase("invite")) {
				
				if (args.length < 2) {
					
					sender.sendMessage(Main.PREFIX + "Usage: /" + label + " invite <player>");
					return true;
					
				}
				
				Player invited = Bukkit.getPlayer(args[1]);
				
				if (invited == null) {
					
					sender.sendMessage(Main.PREFIX + "The player '§6" + args[1] + "§7' is not online.");
					return true;
					
				}
				
				String invitedTeam = TeamsUtils.getTeam(invited.getUniqueId());
				
				if (player.getUniqueId().equals(invited.getUniqueId())) {
					
					sender.sendMessage(Main.PREFIX + "You can't invite yourslef.");
					return true;
					
				}
				
				if (GameUtils.getSpectate(invited.getUniqueId())) {
					
					sender.sendMessage(Main.PREFIX + "You can't invite this player.");
					return true;
					
				}
				
				if (team != null && invitedTeam != null && team.equalsIgnoreCase(invitedTeam)) {
					
					sender.sendMessage(Main.PREFIX + "The player '§6" + invited.getName() + "§7' is already in your team.");
					return true;
					
				}
				
				if (team == null) {
					
					if (TeamsUtils.getTeamsLeft() < 1) {
						
						sender.sendMessage(Main.PREFIX + "There are no more available teams.");
						return true;
						
					}
					
					TeamsUtils.createTeam(player.getUniqueId());
					
				} else {
				
					if (TeamsUtils.getTeamMembers(team).size() >= size) {
						
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
			
			if (args[0].equalsIgnoreCase("accept")) {
				
				if (args.length < 2) {
					
					sender.sendMessage(Main.PREFIX + "Usage: /" + label + " accept <player>");
					return true;
					
				}
				
				UUID uuid = PlayerUtils.getUUID(args[1]);
				
				if (uuid == null) {
					
					sender.sendMessage(Main.PREFIX + "This player has not invited you.");
					return true;
					
				}
				
				String name = PlayerUtils.getName(PlayerUtils.getId(uuid));
				String invitedTeam = TeamsUtils.getTeam(uuid);
				
				if (team != null) {
					
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
				
				if (invitedTeam == null) {
					
					sender.sendMessage(Main.PREFIX + "This invitation was canceled.");
					return true;
					
				}
				
				if (GameUtils.getSpectate(uuid)) {
					
					sender.sendMessage(Main.PREFIX + "You can't join this player.");
					return true;
					
				}
				
				if (TeamsUtils.getTeamMembers(invitedTeam).size() >= size) {
					
					sender.sendMessage(Main.PREFIX + "This team is already full.");
					return true;
					
				}
				
				sender.sendMessage(Main.PREFIX + "You joined the team of '§6" + name + "§7'.");
				TeamsUtils.sendMessage(invitedTeam, Main.PREFIX + "The player '§6" + player.getName() + "§7' joined your team.");
				
				TeamsUtils.joinTeam(player.getUniqueId(), invitedTeam);
				TeamsUtils.invitations.get(uuid).remove(player.getUniqueId());
				return true;
				
			}
			
			if (args[0].equalsIgnoreCase("leave")) {
				
				if (team == null) {
					
					sender.sendMessage(Main.PREFIX + "You are not in a team.");
					return true;
					
				}
				
				TeamsUtils.leaveTeam(player.getUniqueId());
				
				if (TeamsUtils.invitations.containsKey(player.getUniqueId())) TeamsUtils.invitations.remove(player.getUniqueId());
				
				sender.sendMessage(Main.PREFIX + "You left your team.");
				
				final String finalTeam = team;
				final Player finalPlayer = player;
				
				new BukkitRunnable() {
				
					public void run() {
					
						TeamsUtils.sendMessage(finalTeam, Main.PREFIX + "The player '§6" + finalPlayer.getName() + "§7' left your team.");
				
					}
				
				}.runTaskLater(Main.uhc, 2);
				
				return true;
				
			}
			
			if (args[0].equalsIgnoreCase("create")) {
				
				ComponentBuilder message = Main.getPrefixComponent().append("Create a team is useless, use ").color(GRAY).append("/invite").color(GOLD);
				message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/invite"));
				message.append(".").color(GRAY);
				player.spigot().sendMessage(message.create());
				return true;
				
			}
			
		}
		
		if (args[0].equalsIgnoreCase("list")) {
			
			if (TeamsUtils.getTeamsLeft() == 75) {
				
				sender.sendMessage(Main.PREFIX + "There are no teams.");
				return true;
				
			}
			
			player.openInventory(TeamsInventory.getTeams(0));
			return true;
			
		}
		
		if (args[0].equalsIgnoreCase("info")) {
			
			String teamInfo = null;
			UUID uuid = null;
			
			if (args.length < 2) {
				
				if (team == null) {
					
					sender.sendMessage(Main.PREFIX + "You are not in a team.");
					return true;
					
				}
				
				teamInfo = team;
				uuid = player.getUniqueId();
			
			} else {
				
				uuid = PlayerUtils.getUUID(args[1]);
				
				if (uuid == null) {
					
					sender.sendMessage(Main.PREFIX + "This player is not in a team.");
					return true;
					
				}
				
				String name = PlayerUtils.getName(PlayerUtils.getId(uuid));
				teamInfo = TeamsUtils.getTeam(uuid);
			
				if (teamInfo == null) {
					
					sender.sendMessage(Main.PREFIX + "The player '§6" + name + "§7' is not in a team.");
					return true;
					
				}
				
			}
			
			List<UUID> members = TeamsUtils.getTeamMembers(teamInfo);
			int kills = 0;
			
			for (Map.Entry<UUID, Integer> kill : GameUtils.getKills().entrySet()) {
				
				if (!members.contains(kill.getKey())) continue;
				
				kills++;
				
			}
			
			sender.sendMessage(Main.PREFIX + "Informations about the team:");
			sender.sendMessage("§8⫸ §7Team: " + TeamsUtils.getTeamPrefix(uuid) + teamInfo + "§7.");
			sender.sendMessage("§8⫸ §7Kills: §a" + kills + "§7.");
			sender.sendMessage(Main.PREFIX + "Members of the team:");
			
			for (UUID member : members) {
				
				Player mate = Bukkit.getPlayer(member);
				if (mate != null) sender.sendMessage("§8⫸ " + (GameUtils.getDeath(member) ? "§c☠ " : "  ") + TeamsUtils.getTeamPrefix(member) + mate.getName() + "§8 - §f" + (int) ((mate.getHealth() / 2) * 10) + "§4♥");
				else sender.sendMessage("§8⫸ " + (GameUtils.getDeath(member) ? "§c☠ " : "  ") + TeamsUtils.getTeamPrefix(member) + PlayerUtils.getName(PlayerUtils.getId(member)) + "§8 - §cOFFLINE");
				
			}
			
			return true;
			
		}

		if (sender.hasPermission("uhc.team.admin")) {
			
			if (args[0].equalsIgnoreCase("add")) {
				
				if (args.length < 2) {
					
					sender.sendMessage(Main.PREFIX + "Usage: /" + label + " add <player> [player]");
					return true;
					
				}
				
				player = Bukkit.getPlayer(args[1]);
				
				if (player == null) {
					
					sender.sendMessage(Main.PREFIX + "The player '§6" + args[1] + "§7' is not online.");
					return true;
					
				}
				
				team = TeamsUtils.getTeam(player.getUniqueId());
				
				if (GameUtils.getSpectate(player.getUniqueId())) {
					
					sender.sendMessage(Main.PREFIX + "You can't add this player to a team.");
					return true;
					
				}
				
				if (team != null) {
					
					TeamsUtils.leaveTeam(player.getUniqueId());
					if (TeamsUtils.invitations.containsKey(player.getUniqueId())) TeamsUtils.invitations.remove(player.getUniqueId());
					
				}
				
				if (args.length == 2) {
					
					if (TeamsUtils.getTeamsLeft() < 1) {
						
						sender.sendMessage(Main.PREFIX + "There are no more available teams.");
						return true;
						
					}
					
					TeamsUtils.createTeam(player.getUniqueId());
					sender.sendMessage(Main.PREFIX + "The player '§6" + player.getName() + "§7' has been added to a team.");
					return true;
					
				}
				
				Player secondPlayer = Bukkit.getPlayer(args[2]);
				
				if (secondPlayer == null) {
					
					sender.sendMessage(Main.PREFIX + "The player '§6" + args[2] + "§7' is not online.");
					return true;
					
				}
				
				team = TeamsUtils.getTeam(secondPlayer.getUniqueId());
				
				if (team == null) {
					
					sender.sendMessage(Main.PREFIX + "The player '§6" + secondPlayer.getName() + "§7' is not in a team.");
					return true;
					
				}
				
				
				if (TeamsUtils.invitations.containsKey(secondPlayer.getUniqueId()) && TeamsUtils.invitations.get(secondPlayer.getUniqueId()).contains(player.getUniqueId())) TeamsUtils.invitations.get(secondPlayer.getUniqueId()).remove(player.getUniqueId());
				
				TeamsUtils.joinTeam(player.getUniqueId(), team);
				TeamsUtils.sendMessage(team, Main.PREFIX + "The player '§6" + player.getName() + "§7' joined your team.");
				sender.sendMessage(Main.PREFIX + "The player '§6" + player.getName() + "§7' has been added to the team.");
				return true;
				
			}
			
			if (args[0].equalsIgnoreCase("remove")) {
				
				if (args.length < 2) {
					
					sender.sendMessage(Main.PREFIX + "Usage: /" + label + " remove <player>");
					return true;
					
				}
				
				UUID uuid = PlayerUtils.getUUID(args[1]);
				
				if (uuid == null) {
					
					sender.sendMessage(Main.PREFIX + "This player is not in a team.");
					return true;
					
				}
				
				String name = PlayerUtils.getName(PlayerUtils.getId(uuid));
				team = TeamsUtils.getTeam(uuid);
			
				if (team == null) {
					
					sender.sendMessage(Main.PREFIX + "The player '§6" + name + "§7' is not in a team.");
					return true;
					
				}
				
				TeamsUtils.leaveTeam(uuid);
				
				if (TeamsUtils.invitations.containsKey(uuid)) TeamsUtils.invitations.remove(uuid);
				
				sender.sendMessage(Main.PREFIX + "The player '§6" + name + "§7' has been removed from his team.");
				
				final String finalTeam = team;
				final String finalName = name;
				
				new BukkitRunnable() {
				
					public void run() {
					
						TeamsUtils.sendMessage(finalTeam, Main.PREFIX + "The player '§6" + finalName + "§7' left your team.");
				
					}
				
				}.runTaskLater(Main.uhc, 2);
				
				return true;
				
			}
			
			if (args[0].equalsIgnoreCase("delete")) {
				
				if (args.length < 2) {
					
					sender.sendMessage(Main.PREFIX + "Usage: /" + label + " delete <player>");
					return true;
					
				}
				
				UUID uuid = PlayerUtils.getUUID(args[1]);
				
				if (uuid == null) {
					
					sender.sendMessage(Main.PREFIX + "This player is not in a team.");
					return true;
					
				}
				
				String name = PlayerUtils.getName(PlayerUtils.getId(uuid));
				team = TeamsUtils.getTeam(uuid);
			
				if (team == null) {
					
					sender.sendMessage(Main.PREFIX + "The player '§6" + name + "§7' is not in a team.");
					return true;
					
				}
				
				for (UUID member : TeamsUtils.getTeamMembers(team)) {
					
					TeamsUtils.leaveTeam(member);
					if (TeamsUtils.invitations.containsKey(member)) TeamsUtils.invitations.remove(member);
					
				}
				
				sender.sendMessage(Main.PREFIX + "The team of player '§6" + name + "§7' has been deleted.");
				return true;
				
			}
			
			if (args[0].equalsIgnoreCase("clear")) {
				
				TeamsUtils.config();
				
				Bukkit.broadcastMessage(Main.PREFIX + "All the teams are been deleted.");
				return true;
				
			}
			
			if (args[0].equalsIgnoreCase("color")) {
				
				TeamsUtils.setColors();
				
				Bukkit.broadcastMessage(Main.PREFIX + "Teams colors randomized.");
				return true;
				
			}
			
			if (args[0].equalsIgnoreCase("size")) {
				
				if (args.length < 2) {
					
					sender.sendMessage(Main.PREFIX + "Usage: /" + label + " size <number>");
					return true;
					
				}
				
				try {
					
					size = Integer.parseInt(args[1]);
					
				} catch (Exception exception) {
					
					sender.sendMessage(Main.PREFIX + "Incorrect number.");
					return true;
					
				}
				
				if (size < 2 || size > 10) {
					
					sender.sendMessage(Main.PREFIX + "Incorrect number.");
					return true;
					
				}
				
				Bukkit.broadcastMessage(Main.PREFIX + "New team size: §a" + size + "§7.");
				return true;
				
			}
			
		}
		
		sender.sendMessage(Main.PREFIX + "Usage of /team:");
		
		if (status != Status.TELEPORT && status != Status.PLAY && status != Status.END) {
			
			sender.sendMessage("§8⫸ §6/" + label + " invite <player> §8- §7Invite a player in your team.");
			sender.sendMessage("§8⫸ §6/" + label + " accept <player> §8- §7Accept your invitations.");
			sender.sendMessage("§8⫸ §6/" + label + " leave §8- §7Leave your team.");
			
		}
		
		sender.sendMessage("§8⫸ §6/" + label + " list §8- §7List all teams.");
		sender.sendMessage("§8⫸ §6/" + label + " info [player] §8- §7Informations about a team.");
		
		if (sender.hasPermission("uhc.team.admin")) {
			
			sender.sendMessage("§8⫸ §6/" + label + " add <player> [player] §8- §7Add player to team of player.");
			sender.sendMessage("§8⫸ §6/" + label + " remove <player> §8- §7Remove player from his team.");
			sender.sendMessage("§8⫸ §6/" + label + " delete <player> §8- §7Delete team of the player.");
			sender.sendMessage("§8⫸ §6/" + label + " clear §8- §7Delete all teams.");
			sender.sendMessage("§8⫸ §6/" + label + " color §8- §7Re-color teams.");
			
		}
		
		sender.sendMessage("§8⫸ §6/t <message> §8- §7Talk with your team.");
		sender.sendMessage("§8⫸ §6/tc §8- §7Send your coords to your team.");
		return true;
		
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("uhc.team")) return null;
		
		List<String> complete = new ArrayList<String>();
		
		if (args.length == 1) {

			complete.add("invite");
			complete.add("accept");
			complete.add("leave");
			complete.add("list");
			complete.add("info");
			
			if (sender.hasPermission("uhc.team.admin")) {
				
				complete.add("add");
				complete.add("remove");
				complete.add("delete");
				complete.add("clear");
				complete.add("color");
				
			}
			
		} else if (args.length == 2) {
			
			Player player = Bukkit.getPlayer(sender.getName());
			
			if (args[0].equalsIgnoreCase("accept")) {
				
				for (UUID invitations : TeamsUtils.invitations.keySet()) {
					
					if (TeamsUtils.invitations.get(invitations).contains(player.getUniqueId())) complete.add(PlayerUtils.getName(PlayerUtils.getId(invitations)));
					
				}
				
			}
			else if (args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("info")) {
				
				for (Player online : Bukkit.getOnlinePlayers()) {
					
					if (GameUtils.getSpectate(online.getUniqueId())) continue;
					
					complete.add(online.getName());
					
				}
				
			}
			else if (sender.hasPermission("uhc.team.admin") && args[0].equalsIgnoreCase("add")) {
				
				for (Player online : Bukkit.getOnlinePlayers()) {
					
					if (GameUtils.getSpectate(online.getUniqueId())) continue;
					
					complete.add(online.getName());
					
				}
				
			}
			else if (sender.hasPermission("uhc.team.admin") && (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete"))) {
				
				for (Player online : Bukkit.getOnlinePlayers()) {
					
					if (TeamsUtils.getTeam(online.getUniqueId()) == null) continue;
					
					complete.add(online.getName());
					
				}
				
			}
			
		} else if (args.length == 3 && sender.hasPermission("uhc.team.admin") && args[0].equalsIgnoreCase("add")) {
			
			for (Player online : Bukkit.getOnlinePlayers()) {
				
				if (GameUtils.getSpectate(online.getUniqueId())) continue;
				
				complete.add(online.getName());
				
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
