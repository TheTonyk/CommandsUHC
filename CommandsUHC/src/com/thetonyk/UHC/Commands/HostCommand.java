package com.thetonyk.UHC.Commands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Inventories.GameInventory;

public class HostCommand implements CommandExecutor {
	
	public static Map<UUID, GameInventory> inventories = new HashMap<UUID, GameInventory>();
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sender.hasPermission("uhc.list")) {
			
			sender.sendMessage(Main.NO_PERMS);
			return true;
			
		}
		
		Player player = Bukkit.getPlayer(sender.getName());
		
		if (!HostCommand.inventories.containsKey(player.getUniqueId())) HostCommand.inventories.put(player.getUniqueId(), new GameInventory());
		
		player.openInventory(HostCommand.inventories.get(player.getUniqueId()).getInventory());
		return true;
		
	}

}
