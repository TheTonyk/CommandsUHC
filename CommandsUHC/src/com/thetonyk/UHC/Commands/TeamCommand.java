package com.thetonyk.UHC.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Inventories.TeamsInventory;
import com.thetonyk.UHC.Utils.TeamsUtils;

import static net.md_5.bungee.api.ChatColor.*;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;

public class TeamCommand implements CommandExecutor, TabCompleter {
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("uhc.team")) {
			
			sender.sendMessage(Main.NO_PERMS);
			return true;
			
		}
		
		if (args.length > 0) {
		
			if (args[0].equalsIgnoreCase("invite")) {
				
				if (args.length < 2) {
					
					sender.sendMessage(Main.PREFIX + "Usage: /team invite <player>");
					return true;
					
				}
				
				if (Bukkit.getPlayer(args[1]) == null) {
					
					sender.sendMessage(Main.PREFIX + "The player '§6" + args[1] + "§7' is not online.");
					return true;
					
				}
				
				if (!Bukkit.getPlayer(args[1]).isOnline()) {
					
					sender.sendMessage(Main.PREFIX + "The player '§6" + Bukkit.getPlayer(args[1]) + "§7' is not online.");
					return true;
					
				}
				
				if (TeamsUtils.getTeam(sender.getName()) == null) {
					
					if (TeamsUtils.getTeamsLeft() < 1) {
						
						sender.sendMessage(Main.PREFIX + "There are no more available teams.");
						return true;
						
					}
					
					TeamsUtils.createTeam(sender.getName());
					
				}
				
				TeamsUtils.invitations.get(sender.getName()).add(Bukkit.getPlayer(args[1]).getName());
				
				Bukkit.getPlayer(args[1]).sendMessage(Main.PREFIX + "You have received an invitation from '§6" + sender.getName() + "§7'.");
				
				ComponentBuilder message = Main.PREFIX_COMPONENT.append("To join his team, ").color(GRAY).append("accept the invitation").color(AQUA).italic(true);
				message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to accept the invitation of ").color(GRAY).append(sender.getName()).color(GREEN).append(".").color(GRAY).create()));
				message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/team accept " + sender.getName()));
				message.append(".").retain(FormatRetention.NONE).color(GRAY);
				Bukkit.getPlayer(args[1]).spigot().sendMessage(message.create());
		        
		        sender.sendMessage(Main.PREFIX + "Invitation send to player '§6" + Bukkit.getPlayer(args[1]).getName() + "§7'.");
		        return true;
				
			}
			else if (args[0].equalsIgnoreCase("accept")) {
				
				if (args.length < 2) {
					
					sender.sendMessage(Main.PREFIX + "Usage: /team accept <player>");
					return true;
					
				}
				
				if (TeamsUtils.getTeam(sender.getName()) != null) {
					
					ComponentBuilder message = Main.PREFIX_COMPONENT.append("You are already in a team, ").color(GRAY).append("leave it first").color(AQUA).italic(true);
					message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click on this text to leave your team.").color(GRAY).create()));
					message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/team leave"));
					message.append(".").retain(FormatRetention.NONE).color(GRAY);
			        Bukkit.getPlayer(sender.getName()).spigot().sendMessage(message.create());
					
					return true;
					
				}
				
				if (!TeamsUtils.invitations.containsKey(Bukkit.getPlayer(args[1]).getName())) {
					
					sender.sendMessage(Main.PREFIX + "This player has not invited you.");
					return true;
					
				}
				
				if (!TeamsUtils.invitations.get(Bukkit.getPlayer(args[1]).getName()).contains(sender.getName())) {
					
					sender.sendMessage(Main.PREFIX + "This player has not invited you.");
					return true;
					
				}
				
				if (TeamsUtils.getTeam(args[1]) == null) {
					
					sender.sendMessage(Main.PREFIX + "This invitation was canceled.");
					return true;
					
				}
				
				sender.sendMessage(Main.PREFIX + "You joined the team of '§6" + Bukkit.getPlayer(args[1]).getName() + "§7'.");
				TeamsUtils.sendMessage(TeamsUtils.getTeam(args[1]), Main.PREFIX + "The player '§6" + sender.getName() + "§7' joined your team.");
				
				TeamsUtils.joinTeam(sender.getName(), TeamsUtils.getTeam(args[1]));
				TeamsUtils.invitations.get(args[1]).remove(sender.getName());
				return true;
				
			}
			else if (args[0].equalsIgnoreCase("leave")) {
				
				if (TeamsUtils.getTeam(sender.getName()) == null) {
					
					sender.sendMessage(Main.PREFIX + "You are not in a team.");
					return true;
					
				}
				
				String team = TeamsUtils.getTeam(sender.getName());
				
				TeamsUtils.leaveTeam(sender.getName());
				TeamsUtils.invitations.remove(sender.getName());
				
				sender.sendMessage(Main.PREFIX + "You left your team.");
				TeamsUtils.sendMessage(team, Main.PREFIX + "The player '§6" + sender.getName() + "§7' left your team.");
				return true;
				
			}
			else if (args[0].equalsIgnoreCase("list")) {
				
				if (TeamsUtils.getTeamsLeft() == 75) {
					
					sender.sendMessage(Main.PREFIX + "There are no teams.");
					return true;
					
				}
				
				Bukkit.getPlayer(sender.getName()).openInventory(TeamsInventory.getTeams(1));
				return true;
				
			}
			else if (args[0].equalsIgnoreCase("create")) {
				
				ComponentBuilder message = Main.PREFIX_COMPONENT.append("Create a team is useless, use ").color(GRAY).append("/invite").color(GOLD);
				message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/invite"));
				message.append(".").color(GRAY);
				Bukkit.getPlayer(sender.getName()).spigot().sendMessage(message.create());
				return true;
				
			}
			else if (args[0].equalsIgnoreCase("info")) {
				
				String name;
				
				if (args.length < 2) {
					
					name = sender.getName();
				
				} else {
				
					if (TeamsUtils.getTeam(args[1]) == null) {
						
						String nameMsg = Bukkit.getPlayer(args[1]) != null ? Bukkit.getPlayer(args[1]).getName() : args[1];
						
						sender.sendMessage(Main.PREFIX + "The player '§6" + nameMsg + "§7' is not in a team.");
						return true;
						
					}
					
					name = Bukkit.getPlayer(args[1]) != null ? Bukkit.getPlayer(args[1]).getName() : args[1];
					
				}
				
				if (TeamsUtils.getTeam(name) == null) {
					
					sender.sendMessage(Main.PREFIX + "You are not in a team.");
					return true;
					
				}
				
				sender.sendMessage(Main.PREFIX + "Informations about the team:");
				sender.sendMessage("§8⫸ §7Team: " + TeamsUtils.getTeamPrefix(name) + TeamsUtils.getTeam(name) + "§7.");
				sender.sendMessage(Main.PREFIX + "Members of the team:");
				
				for (String member : TeamsUtils.getTeamMembers(TeamsUtils.getTeam(name))) {
					
					if (Bukkit.getPlayer(member) != null && Bukkit.getPlayer(member).isOnline()) sender.sendMessage("§8⫸ " + TeamsUtils.getTeamPrefix(member) + member + " §8- §7" + (int) ((Bukkit.getPlayer(member).getHealth()) / 2) * 10 + "§4♥\n");
					else sender.sendMessage("§8⫸ " + TeamsUtils.getTeamPrefix(member) + member + " §8- §cOFFLINE\n");
					
				}
				
				return true;
				
			}
			else if (sender.hasPermission("uhc.team.admin")) {
				
				if (args[0].equalsIgnoreCase("add")) {
					
					if (args.length < 2) {
						
						sender.sendMessage(Main.PREFIX + "Usage: /team add <player> [player]");
						return true;
						
					}
					
					if (Bukkit.getPlayer(args[1]) == null) {
						
						sender.sendMessage(Main.PREFIX + "The player '§6" + args[1] + "§7' is not online.");
						return true;
						
					}
					
					if (TeamsUtils.getTeam(Bukkit.getPlayer(args[1]).getName()) != null) {
						
						TeamsUtils.leaveTeam(Bukkit.getPlayer(args[1]).getName());
						TeamsUtils.invitations.remove(Bukkit.getPlayer(args[1]).getName());
						
					}
					
					if (args.length == 2) {
						
						if (TeamsUtils.getTeamsLeft() < 1) {
							
							sender.sendMessage(Main.PREFIX + "There are no more available teams.");
							return true;
							
						}
						
						TeamsUtils.createTeam(Bukkit.getPlayer(args[1]).getName());
						sender.sendMessage(Main.PREFIX + "The player '§6" + Bukkit.getPlayer(args[1]).getName() + "§7' has been added to a team.");
						return true;
						
					}
					
					if (TeamsUtils.getTeam(args[2]) == null) {
						
						String name = Bukkit.getPlayer(args[2]) != null ? Bukkit.getPlayer(args[2]).getName() : args[3];
						
						sender.sendMessage(Main.PREFIX + "The player '§6" + name + "§7' is not in a team.");
						return true;
						
					}
					
					TeamsUtils.joinTeam(args[1], TeamsUtils.getTeam(args[2]));
					TeamsUtils.invitations.get(args[1]).remove(sender.getName());
					sender.sendMessage(Main.PREFIX + "The player '§6" + Bukkit.getPlayer(args[1]).getName() + "§7' has been added to the team.");
					return true;
					
				}
				else if (args[0].equalsIgnoreCase("remove")) {
					
					if (args.length < 2) {
						
						sender.sendMessage(Main.PREFIX + "Usage: /team remove <player>");
						return true;
						
					}
					
					String name = Bukkit.getPlayer(args[1]) != null ? Bukkit.getPlayer(args[1]).getName() : args[1];
					
					if (TeamsUtils.getTeam(args[1]) == null) {
						
						sender.sendMessage(Main.PREFIX + "The player '§6" + name + "§7' is not in a team.");
						return true;
						
					}
					
					TeamsUtils.leaveTeam(Bukkit.getPlayer(args[1]).getName());
					TeamsUtils.invitations.remove(Bukkit.getPlayer(args[1]).getName());
					
					sender.sendMessage(Main.PREFIX + "The player '§6" + name + "§7' has been removed from his team.");
					return true;
					
				}
				else if (args[0].equalsIgnoreCase("delete")) {
					
					if (args.length < 2) {
						
						sender.sendMessage(Main.PREFIX + "Usage: /team delete <player>");
						return true;
						
					}
					
					String name = Bukkit.getPlayer(args[1]) != null ? Bukkit.getPlayer(args[1]).getName() : args[1];
					
					if (TeamsUtils.getTeam(args[1]) == null) {
						
						sender.sendMessage(Main.PREFIX + "The player '§6" + name + "§7' is not in a team.");
						return true;
						
					}
					
					for (String member : TeamsUtils.getTeamMembers(TeamsUtils.getTeam(name))) {
						
						TeamsUtils.leaveTeam(member);
						TeamsUtils.invitations.remove(member);
						
					}
					
					sender.sendMessage(Main.PREFIX + "The team of player '§6" + name + "§7' has been deleted.");
					return true;
					
				}
				else if (args[0].equalsIgnoreCase("clear")) {
					
					TeamsUtils.config();
					
					Bukkit.broadcastMessage(Main.PREFIX + "All the teams are been deleted.");
					return true;
					
				}
				else if (args[0].equalsIgnoreCase("color")) {
					
					TeamsUtils.setColors();
					
					Bukkit.broadcastMessage(Main.PREFIX + "Teams colors randomized.");
					return true;
					
				}
				
			}
			
		}	
			
		sender.sendMessage(Main.PREFIX + "Usage of /team:");
		sender.sendMessage("§8⫸ §6/team invite <player> §8- §7Invite a player in your team.");
		sender.sendMessage("§8⫸ §6/team accept <player> §8- §7Accept your invitations.");
		sender.sendMessage("§8⫸ §6/team leave §8- §7Leave your team.");
		sender.sendMessage("§8⫸ §6/team list §8- §7List all teams.");
		sender.sendMessage("§8⫸ §6/team info [player] §8- §7Informations about a team.");
		
		if (sender.hasPermission("uhc.team.admin")) {
			
			sender.sendMessage("§8⫸ §6/team add <player> [player] §8- §7Add player to team of player.");
			sender.sendMessage("§8⫸ §6/team remove <player> §8- §7Remove player from his team.");
			sender.sendMessage("§8⫸ §6/team delete <player> §8- §7Delete team of the player.");
			sender.sendMessage("§8⫸ §6/team clear §8- §7Delete all teams.");
			sender.sendMessage("§8⫸ §6/team color §8- §7Re-color teams.");
			
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
			
			if (args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("info")) {
				
				for (Player player : Bukkit.getOnlinePlayers()) {
					
					complete.add(player.getName());
					
				}
				
			}
			
			if (sender.hasPermission("uhc.team.admin") && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete"))) {
				
				for (Player player : Bukkit.getOnlinePlayers()) {
					
					complete.add(player.getName());
					
				}
				
			}
			
		} else if (args.length == 3 && sender.hasPermission("uhc.team.admin") && args[0].equalsIgnoreCase("add")) {
			
			for (Player player : Bukkit.getOnlinePlayers()) {
				
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
