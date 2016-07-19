package com.thetonyk.UHC.Commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Utils.DatabaseUtils;
import com.thetonyk.UHC.Utils.GameUtils;
import com.thetonyk.UHC.Utils.GameUtils.Status;
import com.thetonyk.UHC.Utils.WorldUtils;

public class WorldCommand implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			
		if (!sender.hasPermission("uhc.world")) {
			
			sender.sendMessage(Main.NO_PERMS);
			return true;
			
		}
		
		if (args.length < 1) {
			
			sender.sendMessage(Main.PREFIX + "Usage of /world:");
			sender.sendMessage("§8⫸ §6/world create §8- §7Create a world.");
			sender.sendMessage("§8⫸ §6/world delete §8- §7Delete a world.");
			sender.sendMessage("§8⫸ §6/world reload §8- §7Reload a world.");
			sender.sendMessage("§8⫸ §6/world list §8- §7List all worlds.");
			sender.sendMessage("§8⫸ §6/world tp §8- §7Teleport to a world.");
			return true;
			
		}
					
		if (args[0].equalsIgnoreCase("create")) {
			
			if (args.length < 3) {
				
				sender.sendMessage(Main.PREFIX + "Usage: /world create <name> <size> [nether] [end] [seed]");
				return true;
				
			}
			
			String name = args[1];
				
			if (WorldUtils.exist(name)) {
				
				sender.sendMessage(Main.PREFIX + "The world '§6" + name + "§7' is already created.");
				return true;
				
			}
			
			if (name.equalsIgnoreCase("world") || name.equalsIgnoreCase("lobby")) {
				
				sender.sendMessage(Main.PREFIX + "The world '§6" + name + "§7' is already created.");
				return true;
				
			}
			
			if (name.endsWith("_end") || name.endsWith("_nether")) {
				
				sender.sendMessage(Main.PREFIX + "You can't created Nether or End world by yourself.");
				return true;
				
			}
			
			int radius = 2000;
			
			try {
				
				radius = Integer.parseInt(args[2]);
				
			} catch (Exception exception) {
				
				sender.sendMessage(Main.PREFIX + "The radius is incorrect.");
				return true;
				
			}
			
			Boolean nether = false;
			Boolean end = false;
			long seed = new Random().nextLong();
			
			if (args.length > 3) {
				
				try {
					
					nether = Boolean.parseBoolean(args[3]);
					
				} catch (Exception exception) {
					
					sender.sendMessage(Main.PREFIX + "Nether can only be '§6true§7' or '§6false§7'.");
					return true;
					
				}
				
			}
			
			if (args.length > 4) {
				
				try {
					
					end = Boolean.parseBoolean(args[4]);
					
				} catch (Exception exception) {
					
					sender.sendMessage(Main.PREFIX + "The End can only be '§6true§7' or '§6false§7'.");
					return true;
					
				}
				
			}
		
			if (args.length > 5) {
				
				try {
					
					seed = Long.parseLong(args[5]);
					
				} catch (Exception exception) {
					
					sender.sendMessage(Main.PREFIX + "This seed is incorrect.");
					return true;
					
				}
				
			}
			
			Bukkit.broadcastMessage(Main.PREFIX + "Creation of world '§6" + name + "§7'...");
			Bukkit.broadcastMessage("§8⫸ §7Size: §a" + radius);
			Bukkit.broadcastMessage("§8⫸ §7Seed: §a" + seed);
			Bukkit.broadcastMessage("§8⫸ §7Nether: §a" + nether);
			Bukkit.broadcastMessage("§8⫸ §7End: §a" + end);
			
			WorldUtils.createWorld(name, Environment.NORMAL, seed, WorldType.NORMAL, radius);
			
			if (nether) WorldUtils.createWorld(name + "_nether", Environment.NETHER, seed, WorldType.NORMAL, radius);	
			
			if (end) WorldUtils.createWorld(name + "_end", Environment.THE_END, seed, WorldType.NORMAL, radius);	
			
			sender.sendMessage(Main.PREFIX + "The world '§6" + name + "§7' has been created.");
			return true;
				
		}			
			
		if (args[0].equalsIgnoreCase("delete")) {
						
			if (args.length < 2) {
				
				sender.sendMessage(Main.PREFIX + "Usage: /world delete <world>");
				return true;
				
			}
			
			String name = args[1];
							
			if (name.contains("_end") || name.contains("_nether")) {
				
				sender.sendMessage(Main.PREFIX + "You can't delete Nether or End world by yourself.");
				return true;
				
			}
			
			if (!WorldUtils.exist(name)) {
				
				sender.sendMessage(Main.PREFIX + "The world '§6" + name + "§7' doesn't exist.");
				return true;
				
			}
									
			if (name.equalsIgnoreCase("lobby")) {
				
				sender.sendMessage(Main.PREFIX + "You can't delete the lobby.");
				return true;
				
			}
										
			WorldUtils.deleteWorld(name);
										
			sender.sendMessage(Main.PREFIX + "The world '§6" + name + "§7' has been deleted.");	
			
			if (WorldUtils.exist(name + "_nether")) WorldUtils.deleteWorld(name + "_nether");
			if (WorldUtils.exist(name + "_end")) WorldUtils.deleteWorld(name + "_end");
			return true;						
						
		}
			
		if (args[0].equalsIgnoreCase("reload")) {
						
			if (args.length < 2) {
				
				sender.sendMessage(Main.PREFIX + "Usage: /world reload <world>");
				return true;
				
			}
			
			String name = args[1];
			
			if (!WorldUtils.exist(name)) {
				
				sender.sendMessage(Main.PREFIX + "The world '§6" + name + "§7' doesn't exist.");
				return true;
				
			}
			
			if (Bukkit.getWorld(name) != null) {
				
				WorldUtils.unloadWorld(name);
				
			}
								
			WorldUtils.loadWorld(name);
									
			sender.sendMessage(Main.PREFIX + "The world '§6" + name + "§7' has been reloaded.");
			return true;					
						
		}
			
		if (args[0].equalsIgnoreCase("list")) {
						
			sender.sendMessage(Main.PREFIX + "Existing worlds:");
						
			World lobby = Bukkit.getWorld("lobby");
			
			if (lobby != null) sender.sendMessage(Main.PREFIX + "§7lobby §8- §a" + lobby.getEnvironment().name() + "§7.");
			else sender.sendMessage(Main.PREFIX + "§clobby §8- §aNORMAL§7.");
			
			try {
				
				ResultSet worlds = DatabaseUtils.sqlQuery("SELECT * FROM uhc_worlds;");
				
				while (worlds.next()) {
					
					String world = worlds.getString("name");
					String color = "§7";
					
					switch (worlds.getString("environment")) {
					
						case "NORMAL":
							color = "§a";
							break;
						case "NETHER":
							color = "§c";
							break;
						case "THE_END":
							color = "§9";
							break;
						default:
							break;
						
					}
					
					String worldColor = Bukkit.getWorld(world) != null ? "§7" : "§c";
					
					sender.sendMessage(Main.PREFIX + worldColor + world + " §8- " + color + worlds.getString("environment") + "§7.");							
					
				}
				
				worlds.close();
			
			} catch (SQLException exception) {
				
				Bukkit.getLogger().severe("[WorldUtils] Error to fetch informations of worlds in DB.");
				sender.sendMessage(Main.PREFIX + "§cError to fetch informations of worlds in DB.");
				
			}
			
			return true;
						
		}
			
		if (args[0].equalsIgnoreCase("tp")) {
						
			if (args.length < 2) {
				
				sender.sendMessage(Main.PREFIX + "Usage: /world tp <world>");
				return true;
				
			}
			
			String name = args[1];
			
			if (!WorldUtils.exist(name) && !name.equalsIgnoreCase("lobby")) {
				
				sender.sendMessage(Main.PREFIX + "The world '§6" + name + "§7' doesn't exist.");
				return true;
				
			}
							
			if (Bukkit.getWorld(name) == null) {
					
				sender.sendMessage(Main.PREFIX + "The world '§6" + name + "§7' is not loaded.");
				
			}
			
			World world = Bukkit.getWorld(name);
			Player player = Bukkit.getPlayer(sender.getName());
			
			player.teleport(world.getSpawnLocation());
			sender.sendMessage(Main.PREFIX + "You have been teleported to world '§6" + name + "§7'.");
			return true;
						
		}
			
		if (args[0].equalsIgnoreCase("game")) {
			      
			if (args.length < 2) {
				
				sender.sendMessage(Main.PREFIX + "Usage: /world game <world>");
				return true;
				
			}
			
			String name = args[1];
			
			if (!WorldUtils.exist(name) && !name.equalsIgnoreCase("lobby")) {
				
				sender.sendMessage(Main.PREFIX + "The world '§6" + name + "§7' doesn't exist.");
				return true;
				
			}
							
			if (Bukkit.getWorld(name) == null) {
					
				sender.sendMessage(Main.PREFIX + "The world '§6" + name + "§7' is not loaded.");
				
			}
			
			if (name.equalsIgnoreCase("lobby")) {
				
				sender.sendMessage(Main.PREFIX + "You can't use the lobby world.");
		        return true;
				
			}
			
			Status status = GameUtils.getStatus();
			
			if (status == Status.TELEPORT || status == Status.PLAY || status == Status.END) {
				
				sender.sendMessage(Main.PREFIX + "You can't change the world during the game.");
		        return true;
				
			}
		      
		    GameUtils.setWorld(name);
		    GameUtils.setStatus(Status.READY);
		    sender.sendMessage(Main.PREFIX + "The world '§6" + name + "§7' is now the game world.");
		    return true;
		            
		    } 
		
		sender.sendMessage(Main.PREFIX + "Usage of /world:");
		sender.sendMessage("§8⫸ §6/world create §8- §7Create a world.");
		sender.sendMessage("§8⫸ §6/world delete §8- §7Delete a world.");
		sender.sendMessage("§8⫸ §6/world reload §8- §7Reload a world.");
		sender.sendMessage("§8⫸ §6/world list §8- §7List all worlds.");
		sender.sendMessage("§8⫸ §6/world tp §8- §7Teleport to a world.");
		return true;
		
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("uhc.world")) return null;
		
		List<String> complete = new ArrayList<String>();
		
		if (args.length == 1) {

			complete.add("create");
			complete.add("delete");
			complete.add("reload");
			complete.add("list");
			complete.add("tp");
			
		} else if (args.length == 2) {
			
			if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("tp")) {
				
				for (World world : Bukkit.getWorlds()) {
					
					if (world.getName().equalsIgnoreCase("lobby")) {
						
						continue;
						
					}
					
					complete.add(world.getName());
					
				}
				
			}
			
		} else if (args.length == 4 || args.length == 5) {
			
			if (args[0].equalsIgnoreCase("create")) {
				
				complete.add("true");
				complete.add("false");
				
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
