package com.thetonyk.UHC.Features;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.thetonyk.UHC.Game;
import com.thetonyk.UHC.Game.Status;

public class ScatterProtection implements Listener {

	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		
		if (Game.getStatus() != Status.TELEPORT) return;
			
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		
		if (Game.getStatus() != Status.TELEPORT) return;
			
		event.setCancelled(true);

	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		
		if (Game.getStatus() != Status.TELEPORT) return;
			
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		
		if (Game.getStatus() != Status.TELEPORT) return;
			
		if (event.getAction() == Action.PHYSICAL) {
			
			event.setCancelled(true);
			return;
			
		}
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			
			switch(event.getClickedBlock().getType()) {
			
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
		
	}
	
	@EventHandler
	public void onArmorStandInteract(PlayerArmorStandManipulateEvent event) {
		
		if (Game.getStatus() != Status.TELEPORT) return;
			
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void onHungerChange(FoodLevelChangeEvent event) {
		
		if (Game.getStatus() != Status.TELEPORT) return;
			
		event.setCancelled(true);
		event.setFoodLevel(20);
		
	}
	
	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		
		if (Game.getStatus() != Status.TELEPORT) return;
			
		event.setCancelled(true);
		event.getBlockClicked().getState().update(true, true);
		
	}
	
	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent event) {
		
		if (Game.getStatus() != Status.TELEPORT) return;
			
		event.setCancelled(true);
		event.getBlockClicked().getState().update(true, true);
		
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent event) {
		
		if (Game.getStatus() != Status.TELEPORT) return;
		
		event.setCancelled(true);
		
	}
	
}