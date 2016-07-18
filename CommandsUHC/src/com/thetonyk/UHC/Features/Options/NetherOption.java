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

public class NetherOption extends Option implements Listener {
	
	public NetherOption() {
		
		super("Nether", new ItemStack(Material.NETHERRACK), true);
		
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
			
			if (fromLocation.getBlock().getRelative(face).getType() != Material.PORTAL) continue;
			
			portal = true;
			break;
			
		}
		
		if (!portal) return;
		
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
		
		if (fromLocation.getBlock().getType() != Material.PORTAL) return;
		
		Location toLocation = getPortal(fromLocation, from, agent);
		
		if (toLocation == null) return;
		
		event.setTo(toLocation);
		
	}
	
	private Location getPortal(Location fromLocation, World from, TravelAgent agent) {
		
		World to = null;
		
		if (from.getEnvironment() == Environment.NETHER) {
			
			if (!from.getName().endsWith("_nether")) return null;
			
			to = Bukkit.getWorld(from.getName().substring(0, from.getName().length() - 7));
			
		} else if (from.getEnvironment() == Environment.NORMAL) {
			
			to = Bukkit.getWorld(from.getName() + "_nether");
			
		} else return null;
		
		if (to == null) return null;
		
		Location toLocation = null;
		
		if (from.getEnvironment() == Environment.NETHER) toLocation = agent.findOrCreate(new Location(to, fromLocation.getX() * 8, fromLocation.getY() * 8, fromLocation.getZ() * 8));
		else toLocation = agent.findOrCreate(new Location(to, fromLocation.getX() * 0.125, fromLocation.getY() * 0.125, fromLocation.getZ() * 0.125));
		
		double size = to.getWorldBorder().getSize();
		double posRadius = size / 2;
		double negRadius = size - (size * 1.5);
		double x = toLocation.getX();
		double z = toLocation.getZ();
		
		if (x > posRadius - 30 || x < negRadius + 30 || z > posRadius - 30  || z < negRadius + 30) {
			
			double newX = toLocation.getX();
			double newZ = toLocation.getZ();
			
			if (x > posRadius - 30 || x < negRadius + 30) newX = x > posRadius - 30 ? posRadius - 50 : negRadius + 50;
			if (z > posRadius - 30 || z < negRadius + 30) newZ = z > posRadius - 30 ? posRadius - 50 : negRadius + 50;
			
			
			agent.setCreationRadius(30);
			agent.setSearchRadius(30);
			
			toLocation = agent.findOrCreate(new Location(to, newX, toLocation.getY(), newZ));
			
		}
		
		return toLocation;
		
	}

}
