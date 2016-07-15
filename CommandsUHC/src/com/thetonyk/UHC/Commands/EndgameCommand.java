package com.thetonyk.UHC.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Utils.GameUtils;
import com.thetonyk.UHC.Utils.PlayerUtils;
import com.thetonyk.UHC.Utils.TeamsUtils;

import static net.md_5.bungee.api.ChatColor.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

import com.thetonyk.UHC.Utils.GameUtils.Status;

public class EndgameCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("uhc.end")) {
			
			sender.sendMessage(Main.NO_PERMS);
    		return true;
    		
		}
		
		Status status = GameUtils.getStatus();
		
		switch (status) {
		
			case NONE:
			case OPEN:
				sender.sendMessage(Main.PREFIX + "The game is not ready.");
				return true;
			case READY:
			case TELEPORT:
				sender.sendMessage(Main.PREFIX + "The game has not started.");
				return true;
			case END:
				sender.sendMessage(Main.PREFIX + "Game is already ended.");
				return true;
			default:
				break;
	
		}
		
		List<Map.Entry<String, ?>> players = new ArrayList<Map.Entry<String, ?>>();
		
		for (UUID alive : GameUtils.getAlives()) {
			
			List<String> teamsDone = new ArrayList<String>();
			
			for (Map.Entry<String, ?> entry : players) {
				
				if (!entry.getKey().equalsIgnoreCase("team")) continue;
				
				teamsDone.add((String) entry.getValue());
				
			}
			
			String playerTeam = TeamsUtils.getTeam(alive);
			
			if (playerTeam != null) {
				
				if (teamsDone.contains(playerTeam)) continue;
				
				Map.Entry<String, String> team = new Map.Entry<String, String>() {
					
					String team = playerTeam;

					@Override
					public String getKey() {
						
						return "team";
						
					}

					@Override
					public String getValue() {
						
						return this.team;
						
					}

					@Override
					public String setValue(String team) {
					
						this.team = team;
						return this.team;
						
					}
					
				};
				
				players.add(team);
				continue;
				
			}
			
			Map.Entry<String, UUID> uuid = new Map.Entry<String, UUID>() {
				
				UUID uuid = alive;

				@Override
				public String getKey() {
					
					return "uuid";
					
				}

				@Override
				public UUID getValue() {
					
					return this.uuid;
					
				}

				@Override
				public UUID setValue(UUID uuid) {
				
					this.uuid = uuid;
					return this.uuid;
					
				}
				
			};
			
			players.add(uuid);
			
		}
		
		if (args.length > 0) {
			
			Boolean find = false;
			
			for (Map.Entry<String, ?> entry : players) {
				
				if (entry.getKey().equalsIgnoreCase("team")) {
					
					String team = (String) entry.getValue();
					
					if (team.equalsIgnoreCase(args[0])) {
						
						find = true;
						break;
						
					}
					
					continue;
					
				}
				
				UUID player = (UUID) entry.getValue();
				
				if (PlayerUtils.getName(PlayerUtils.getId(player)).equalsIgnoreCase(args[0])) {
					
					find = true;
					break;
					
				}
				
			}
			
			if (find == false) {
				
				sender.sendMessage(Main.PREFIX + "This player/team is not alive.");
				return true;
				
			}
			
		}
		
		if (players.size() < 1) {
			
			sender.sendMessage(Main.PREFIX + "There are no teams/players alive.");
			return true;
			
		}
		
		if (players.size() > 1 && args.length < 1) {
			
			sender.sendMessage(Main.PREFIX + "There are multiples teams/players alives.");
			sender.sendMessage(Main.PREFIX + "Choose the winner(s): §8(§7Click on it§8)");
			
			for (Map.Entry<String, ?> entry : players) {
				
				if (entry.getKey().equalsIgnoreCase("team")) {
					
					String team = (String) entry.getValue();
					ComponentBuilder message = new ComponentBuilder("⫸ ").color(DARK_GRAY);
					List<UUID> members = TeamsUtils.getTeamMembers(team);
					int i = 1;
					
					for (UUID member : members) {
						
						String prefix = TeamsUtils.getTeamPrefix(member);
						
						message.append(PlayerUtils.getName(PlayerUtils.getId(member))).color(ChatColor.getByChar(prefix.charAt(1))).bold(prefix.contains("l")).italic(prefix.contains("o")).underlined(prefix.contains("n")).strikethrough(prefix.contains("m"));
						message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " " + team));
						message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to choose.").color(GRAY).create()));
						
						if (members.size() > 1 && i < members.size()) message.append("§7, ").color(GRAY);
							
						i++;
						
					}
					
					Bukkit.getPlayer(sender.getName()).spigot().sendMessage(message.create());
					continue;
					
				}
				
				UUID player = (UUID) entry.getValue();
				ComponentBuilder message = new ComponentBuilder("⫸ ").color(DARK_GRAY);
				message.append(PlayerUtils.getName(PlayerUtils.getId(player))).color(GRAY);
				message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + label + " " + PlayerUtils.getName(PlayerUtils.getId(player))));
				message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to choose.").color(GRAY).create()));
				Bukkit.getPlayer(sender.getName()).spigot().sendMessage(message.create());
				
			}
			
			return true;
			
		}
		
		List<UUID> winners = new ArrayList<UUID>();
		
		if (args.length > 0) {
			
			if (args[0].startsWith("UHC")) {
				
				for (UUID member : TeamsUtils.getTeamMembers(args[0])) {
					
					winners.add(member);
					
				}
				
			} else {
				
				UUID player = PlayerUtils.getUUID(args[0]);
				
				if (player == null) return true;
				
				winners.add(player);
				
			}
			
		} else {
			
			if (players.get(0).getKey().equalsIgnoreCase("team")) {
				
				for (UUID member : TeamsUtils.getTeamMembers((String) players.get(0).getValue())) {
					
					winners.add(member);
					
				}
				
			} else {
				
				UUID player = (UUID) players.get(0).getValue();
				
				winners.add(player);
				
			}
			
		}
		
		Bukkit.broadcastMessage(" ");
		Bukkit.broadcastMessage(Main.PREFIX + "The game is over!");
		Bukkit.broadcastMessage(" ");
		
		if (winners.size() < 2) {
			
			Bukkit.broadcastMessage(Main.PREFIX + "The winner is " + (TeamsUtils.getTeam(winners.get(0)) == null ? "§7" : TeamsUtils.getTeamPrefix(winners.get(0))) + PlayerUtils.getName(PlayerUtils.getId(winners.get(0))) + " §8(§a" + (GameUtils.getKills().containsKey(winners.get(0)) ? GameUtils.getKills().get(winners.get(0)) : 0) + " §7kill(s)§8)");
			
		} else {
			
			Bukkit.broadcastMessage(Main.PREFIX + "The winners:");
			
			int total = 0;
			
			for (UUID winner : winners) {
			
				int kills = GameUtils.getKills().containsKey(winner) ? GameUtils.getKills().get(winner) : 0;
				total += kills;
				
				Bukkit.broadcastMessage("§8⫸ " + (TeamsUtils.getTeam(winner) == null ? "§7" : TeamsUtils.getTeamPrefix(winner)) + PlayerUtils.getName(PlayerUtils.getId(winner)) + " §8(§a" + kills + " §7kill(s)§8)");
			
			}
			
			Bukkit.broadcastMessage(Main.PREFIX + "With §a" + total + " §7kills.");
			
		}
		
		Bukkit.broadcastMessage(" ");
		Bukkit.broadcastMessage(Main.PREFIX + "Thanks all for playing.");

		GameUtils.setStatus(Status.END);
		return true;
		
	}

}
