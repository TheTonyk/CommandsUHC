package com.thetonyk.UHC.Features.Options;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;

import com.thetonyk.UHC.Features.Options.OptionFeature.Option;
import com.thetonyk.UHC.Utils.GameUtils;

public class EndOption extends Option implements Listener {
	
	public EndOption() {
		
		super("The End", new ItemStack(Material.ENDER_STONE), false);
		
	}
	
	@EventHandler
	public void onPortal(PlayerPortalEvent event) {
		
		Location fromLocation = event.getFrom();
		World from = fromLocation.getWorld();
		TravelAgent agent = event.getPortalTravelAgent();
		
		if (!isEnabled()) {
			
			event.setCancelled(true);
			return;
			
		}
		
		Boolean portal = false;
		
		for (BlockFace face : BlockFace.values()) {
			
			if (fromLocation.getBlock().getRelative(face).getType() != Material.ENDER_PORTAL) continue;
			
			portal = true;
			break;
			
		}
		
		if (!portal) return;
		
		if (from.getEnvironment() == Environment.THE_END) {
			
			if (GameUtils.getWorld() != null && Bukkit.getWorld(GameUtils.getWorld()) != null) event.setTo(Bukkit.getWorld(GameUtils.getWorld()).getSpawnLocation());
			else event.setTo(Bukkit.getWorld("lobby").getSpawnLocation());
			
		}
		
		Location toLocation = getPortal(fromLocation, from, agent);
		
		if (toLocation == null) return;
		
		event.setTo(toLocation);
		
	}
	
	@EventHandler
	public void onEntityPortal(EntityPortalEvent event) {
		
		Location fromLocation = event.getFrom();
		World from = fromLocation.getWorld();
		TravelAgent agent = event.getPortalTravelAgent();
		
		if (!isEnabled()) {
			
			event.setCancelled(true);
			return;
			
		}
		
		if (fromLocation.getBlock().getType() != Material.ENDER_PORTAL) return;
		
		if (from.getEnvironment() == Environment.THE_END) {
			
			if (GameUtils.getWorld() != null && Bukkit.getWorld(GameUtils.getWorld()) != null) event.setTo(Bukkit.getWorld(GameUtils.getWorld()).getSpawnLocation());
			else event.setTo(Bukkit.getWorld("lobby").getSpawnLocation());
			
		}
		
		Location toLocation = getPortal(fromLocation, from, agent);
		
		if (toLocation == null) return;
		
		event.setTo(toLocation);
		
	}
	
	private Location getPortal(Location fromLocation, World from, TravelAgent agent) {
		
		World to = null;
		
		if (from.getEnvironment() == Environment.NETHER) {
			
			if (!from.getName().endsWith("_nether")) return null;
			
			to = Bukkit.getWorld(from.getName().substring(0, from.getName().length() - 7) + "_end");
			
		} else if (from.getEnvironment() == Environment.NORMAL) {
			
			to = Bukkit.getWorld(from.getName() + "_end");
			
		} else return null;
		
		if (to == null) return null;
		
		Location toLocation = agent.findOrCreate(new Location(to, 100, 50, 100));
		
		for (int x = toLocation.getBlockX() - 1; x < toLocation.getBlockX() + 2; x++) {
			
			for (int y = toLocation.getBlockY() - 1; y < toLocation.getBlockY() + 3; y++) {
				
				for (int z = toLocation.getBlockZ() - 1; z < toLocation.getBlockZ() + 2; z++) {
					
					to.getBlockAt(x, y, z).setType(y == toLocation.getBlockY() - 1 ? Material.OBSIDIAN : Material.AIR);
					
				}
				
			}
			
		}
		
		return toLocation;
		
	}

}
