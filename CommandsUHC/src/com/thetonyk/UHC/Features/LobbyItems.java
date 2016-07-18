package com.thetonyk.UHC.Features;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.thetonyk.UHC.Main;
import com.thetonyk.UHC.Inventories.RulesInventory;
import com.thetonyk.UHC.Inventories.TeamsInventory;
import com.thetonyk.UHC.Utils.GameUtils;
import com.thetonyk.UHC.Utils.ItemsUtils;
import com.thetonyk.UHC.Utils.TeamsUtils;
import com.thetonyk.UHC.Utils.GameUtils.Status;

public class LobbyItems implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		
		Status status = GameUtils.getStatus();
		ItemStack item = event.getItem();
		Player player = event.getPlayer();
			
		if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
		
		ItemMeta meta = item.getItemMeta();
				
		if (meta.getDisplayName().equals("§a§lThe Rules §7(Right-Click)")) {
			
			event.setCancelled(true);	
			player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
			
			if (status == Status.NONE) {
				
				player.sendMessage(Main.PREFIX + "The game is not ready.");
				return;
				
			}
			
			Inventory rules = RulesInventory.getRules();
			
			player.openInventory(rules);
			return;
			
		}
		
		if (meta.getDisplayName().equals("§6§lTeams List §7(Right-Click)")) {
			
			event.setCancelled(true);	
			player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
			
			if (TeamsUtils.getTeamsLeft() == 75) {
				
				player.sendMessage(Main.PREFIX + "There are no teams.");
				return;
				
			}
			
			Inventory teams = TeamsInventory.getTeams(0);
			
			player.openInventory(teams);
			return;
			
		}
		
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		
		Status status = GameUtils.getStatus();
		Item item = event.getItemDrop();
		Player player = event.getPlayer();
		
		if (item == null || !item.getItemStack().hasItemMeta() || !item.getItemStack().getItemMeta().hasDisplayName()) return;
		
		ItemMeta meta = item.getItemStack().getItemMeta();
				
		if (meta.getDisplayName().equals("§a§lThe Rules §7(Right-Click)")) {
			
			event.setCancelled(true);
			player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
			
			if (status == Status.NONE) {
				
				player.sendMessage(Main.PREFIX + "The game is not ready.");
				return;
				
			}
			
			Inventory rules = RulesInventory.getRules();
			
			player.openInventory(rules);
			return;
			
		}
		
		if (meta.getDisplayName().equals("§6§lTeams List §7(Right-Click)")) {
			
			event.setCancelled(true);
			player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
			
			if (TeamsUtils.getTeamsLeft() == 75) {
				
				player.sendMessage(Main.PREFIX + "There are no teams.");
				return;
				
			}
			
			Inventory teams = TeamsInventory.getTeams(0);
			
			player.openInventory(teams);
			return;
			
		}
		
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
				
		if (!(event.getWhoClicked() instanceof Player)) return;
		
		Status status = GameUtils.getStatus();
		ItemStack item = event.getCurrentItem();
		Player player = (Player) event.getWhoClicked();
		
		if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
		
		ItemMeta meta = item.getItemMeta();
		
		if (meta.getDisplayName().equals("§a§lThe Rules §7(Right-Click)")) {
				
			event.setCancelled(true);
			player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
			
			if (status == Status.NONE) {
				
				player.sendMessage(Main.PREFIX + "The game is not ready.");
				return;
				
			}
			
			Inventory rules = RulesInventory.getRules();
			
			player.openInventory(rules);
			return;
			
		}
		
		if (meta.getDisplayName().equals("§6§lTeams List §7(Right-Click)")) {
			
			event.setCancelled(true);
			player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
			
			if (TeamsUtils.getTeamsLeft() == 75) {
				
				player.sendMessage(Main.PREFIX + "There are no teams.");
				return;
				
			}
			
			Inventory teams = TeamsInventory.getTeams(0);
			
			player.openInventory(teams);
			return;
			
		}
			
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		
		Status status = GameUtils.getStatus();
		Player player = event.getPlayer();
		
		if ((status == Status.TELEPORT || status == Status.PLAY || status == Status.END) && !GameUtils.getDeath(player.getUniqueId()) && !GameUtils.getSpectate(player.getUniqueId())) return;
			
		giveItems(player);
		
	}
	
	@EventHandler
	public void onChangeWorld(PlayerChangedWorldEvent event) {
		
		Player player = event.getPlayer();
		World world = player.getWorld();
		
		if (!world.getName().equalsIgnoreCase("lobby") || GameUtils.getSpectate(player.getUniqueId())) return;
		
		giveItems(player);
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onRespawn(PlayerRespawnEvent event) {
		
		Player player = event.getPlayer();
		World world = event.getRespawnLocation().getWorld();
		
		if (!world.getName().equalsIgnoreCase("lobby")) return;
		
		giveItems(player);
		
	}
	
	public static void giveItems (Player player) {
		
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§7Click to see the rules.");
		
		ItemStack rules = ItemsUtils.createItem(Material.PAPER, "§a§lThe Rules §7(Right-Click)", 1, 0, lore);
		rules = ItemsUtils.addGlow(rules);
		
		player.getInventory().setItem(4, rules);
		
		lore = new ArrayList<String>();
		lore.add("§7Click to see teams.");
		
		ItemStack teams = ItemsUtils.createItem(Material.NAME_TAG, "§6§lTeams List §7(Right-Click)", 1, 0, lore);
		teams = ItemsUtils.addGlow(teams);
		
		player.getInventory().setItem(8, teams);
		
	}
	
}
