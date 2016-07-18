package com.thetonyk.UHC.Features;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.thetonyk.UHC.Main;

public class LobbyProtection implements Listener {
	
	public LobbyProtection() {
		
		new BukkitRunnable() {
			
			public void run() {
				
				for (Player player : Bukkit.getWorld("lobby").getPlayers()) {
					
					if (player.getFireTicks() < 1) continue;
					
					player.setFireTicks(0);
					
				}
				
			}
			
		}.runTaskTimer(Main.uhc, 20, 20);
		
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		
		World world = event.getWorld();
		
		if (!event.toWeatherState()) return;
		
		if (!world.getName().equalsIgnoreCase("lobby")) return;
			
		event.setCancelled(true);
		world.setStorm(false);
		world.setWeatherDuration(0);
		
	}
	
	@EventHandler
	public void onThunderChange(ThunderChangeEvent event) {
		
		World world = event.getWorld();
		
		if (!event.toThunderState()) return;
		
		if (!world.getName().equalsIgnoreCase("lobby")) return;
			
		event.setCancelled(true);
		world.setThundering(false);
		world.setThunderDuration(0);
		
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent event) {
	
		Player player = event.getPlayer();
		Block block = event.getBlock();
		World world = block.getWorld();
		
		if (!world.getName().equalsIgnoreCase("lobby")) return;
		
		if (player.hasPermission("global.build") && player.getGameMode() == GameMode.CREATIVE) return;
			
		event.setCancelled(true);
			
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		
		Player player = event.getPlayer();
		Block block = event.getBlock();
		World world = block.getWorld();
		
		if (!world.getName().equalsIgnoreCase("lobby")) return;
		
		if (player.hasPermission("global.build") && player.getGameMode() == GameMode.CREATIVE) return;
			
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		
		Player player = event.getPlayer();
		World world = player.getWorld();
		Action action = event.getAction();
	
		if (!world.getName().equalsIgnoreCase("lobby")) return;
		
		if (player.hasPermission("global.build") && player.getGameMode() == GameMode.CREATIVE) return;
		
		if (action == Action.PHYSICAL) {
				
			event.setCancelled(true);
			return;
			
		}
		
		if (event.getClickedBlock() == null) return;
		
		Material type = event.getClickedBlock().getType();
			
		switch(type) {
		
			case ANVIL:
			case BEACON:
			case BREWING_STAND:
			case CHEST:
			case WORKBENCH:
			case DISPENSER:
			case DROPPER:
			case ENCHANTMENT_TABLE:
			case ENDER_CHEST:
			case FURNACE:
			case BURNING_FURNACE:
			case HOPPER:
			case ITEM_FRAME:
			case LEVER:
			case BED_BLOCK:
			case TRAPPED_CHEST:
				event.setCancelled(true);
				return;
			default:
				break;
		
		}
		
	}
	
	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent event) {
		
		Player player = event.getPlayer();
		World world = player.getWorld();
		EntityType type = event.getRightClicked().getType();
		
		if (!world.getName().equalsIgnoreCase("lobby")) return;
		
		if (player.hasPermission("global.build") && player.getGameMode() == GameMode.CREATIVE) return;
				
		switch (type) {
		
			case ITEM_FRAME:
			case VILLAGER:
			case MINECART_CHEST:
			case MINECART_FURNACE:
			case MINECART_HOPPER:
			case ARMOR_STAND:
				event.setCancelled(true);
				return;
			default:
				break;
		
		}
		
	}
	
	@EventHandler
	public void onArmorStandInteract(PlayerArmorStandManipulateEvent event) {
		
		Player player = event.getPlayer();
		World world = player.getWorld();
		
		if (!world.getName().equalsIgnoreCase("lobby")) return;
		
		if (player.hasPermission("global.build") && player.getGameMode() == GameMode.CREATIVE) return;
			
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onHungerChange(FoodLevelChangeEvent event) {
			
		if (!(event.getEntity() instanceof Player)) return;
		
		Player player = (Player) event.getEntity();
		World world = player.getWorld();
		
		if (!world.getName().equalsIgnoreCase("lobby")) return;

		event.setCancelled(true);
		event.setFoodLevel(20);
		player.setSaturation(20f);
		player.setExhaustion(0f);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
	
		Player player = event.getPlayer();
		Location location = event.getTo();
		World world = location.getWorld();
		
		if (!world.getName().equalsIgnoreCase("lobby")) return;
		
		if (location.getY() > 0) return;
		
		player.teleport(world.getSpawnLocation().add(0.5, 0, 0.5));
		
	}
	
	@EventHandler
	public void onBedEnter(PlayerBedEnterEvent event) {
		
		Player player = event.getPlayer();
		World world = player.getWorld();
		
		if (!world.getName().equalsIgnoreCase("lobby")) return;
			
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		
		Player player = event.getPlayer();
		World world = player.getWorld();
		Block block = event.getBlockClicked();
		
		if (!world.getName().equalsIgnoreCase("lobby")) return;
		
		if (player.hasPermission("global.build") && player.getGameMode() == GameMode.CREATIVE) return;
			
		event.setCancelled(true);
		block.getState().update(true, true);
		
	}
	
	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent event) {
		
		Player player = event.getPlayer();
		World world = player.getWorld();
		Block block = event.getBlockClicked();
		
		if (!world.getName().equalsIgnoreCase("lobby")) return;
		
		if (player.hasPermission("global.build") && player.getGameMode() == GameMode.CREATIVE) return;
			
		event.setCancelled(true);
		block.getState().update(true, true);
		
	}
	
	@EventHandler
	public void onFish(PlayerFishEvent event) {
	
		Player player = event.getPlayer();
		World world = player.getWorld();
		
		if (!world.getName().equalsIgnoreCase("lobby")) return;
		
		event.setCancelled(true);
		event.getHook().remove();
		
	}
	
	@EventHandler
	public void onPortal(PlayerPortalEvent event) {
		
		Location location = event.getFrom();
		World world = location.getWorld();
		
		if (!world.getName().equalsIgnoreCase("lobby")) return;
			
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onShears(PlayerShearEntityEvent event) {
		
		Player player = event.getPlayer();
		World world = player.getWorld();
		
		if (!world.getName().equalsIgnoreCase("lobby")) return;
			
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		
		if (!(event.getWhoClicked() instanceof Player)) return;
		
		Player player = (Player) event.getWhoClicked();
		World world = player.getWorld();
		InventoryAction action = event.getAction();
		
		if (!world.getName().equalsIgnoreCase("lobby")) return;
		
		if (action != InventoryAction.HOTBAR_SWAP) return;

		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		
		Entity entity = event.getEntity();
		World world = entity.getWorld();
		
		if (!world.getName().equalsIgnoreCase("lobby")) return;
		
		event.setCancelled(true);
		
	}
	
}
